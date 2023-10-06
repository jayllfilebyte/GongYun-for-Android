package com.dart.campushelper.di

import android.util.Log
import com.dart.campushelper.api.ChaoxingService
import com.dart.campushelper.data.UserPreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ChaoxingModule {

    @Singleton
    @Provides
    fun provideChaoxingService(
        userPreferenceRepository: UserPreferenceRepository
    ): ChaoxingService {

        val client = OkHttpClient.Builder()
            .followRedirects(false)
            .cookieJar(ChaoxingCookieJar(userPreferenceRepository))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            // Handle HTTP request error only
            .addInterceptor(Interceptor { chain ->
                val req = chain.request()
                try {
                    Log.d("okhttp.OkHttpClient", "provideChaoxingService: ${req.url}")
                    val res = chain.proceed(req)
                    res
                } catch (e: Exception) {
                    Log.d("okhttp.OkHttpClient", "provideChaoxingService: ${e.message}")
                    Response.Builder()
                        .request(req)
                        .protocol(Protocol.HTTP_1_1)
                        .code(502)
                        .message("Failed to connect to Internet")
                        .body("".toResponseBody())
                        .build()
                }
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ChaoxingService.BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ChaoxingService::class.java)
    }
}

class ChaoxingCookieJar @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
) : CookieJar {

    val scope = CoroutineScope(Dispatchers.IO)

    private var cookies: List<Cookie>

    private val cookiesStateFlow = userPreferenceRepository.observeCookies().stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = runBlocking { userPreferenceRepository.observeCookies().first() }
    )

    init {
        cookies = runBlocking { cookiesStateFlow.first() }
        scope.launch {
            Log.d("okhttp.OkHttpClient", "init: cookies.size: ${cookies.size}")
            userPreferenceRepository.observeCookies().collect {
                cookies = it
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        Log.d(
            "okhttp.OkHttpClient",
            "loadForRequest: cookies.size: ${cookies.size}"
        )
        return cookies
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val count = cookies.count { it.name == "rememberMe" }
        if (url.toString() == "${ChaoxingService.BASE_URL}login" && count == 2) {
            Log.d("okhttp.OkHttpClient", "saveFromResponse: cookies.size: ${cookies.size}")
            this@ChaoxingCookieJar.cookies = cookies
            scope.launch {
                this@ChaoxingCookieJar.userPreferenceRepository.changeCookies(cookies)
            }
        }
    }
}
