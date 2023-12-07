package com.dart.campushelper.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dart.campushelper.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A layout that can load data from network and display different content according to the
 * loading state.
 * @param dataSource The data source to load data from network.
 * @param pullRefreshEnabled Whether to enable pull refresh.
 * @param loadData The function to load data from network.
 * @param autoLoadingArgs The arguments to trigger auto loading.
 * @param contentWhenDataSourceIsEmpty The content to display when data source is empty.
 * @param contentWhenDataSourceIsNotEmpty The content to display when data source is not empty.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : List<Any>> LoadOnlineDataLayout(
    dataSource: Result<T>,
    pullRefreshEnabled: Boolean = true,
    loadData: suspend () -> Unit,
    autoLoadingArgs: Array<Any?> = arrayOf(Unit),
    contentWhenDataSourceIsEmpty: @Composable (() -> Unit)? = null,
    contentWhenDataSourceIsNotEmpty: @Composable ((items: T) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(true) }
    fun onRefresh() = scope.launch {
        isRefreshing = true
        withContext(Dispatchers.IO) {
            loadData()
        }
        isRefreshing = false
    }
    LaunchedEffect(*autoLoadingArgs) {
        onRefresh()
    }

    val refreshState = rememberPullRefreshState(isRefreshing, ::onRefresh)
    Box(modifier = if (pullRefreshEnabled) Modifier.pullRefresh(refreshState) else Modifier) {
        if (isRefreshing) {
            // Waiting for data to load from network
            Box(Modifier.fillMaxSize())
        } else if (dataSource.data == null) {
            // Fail to load data from network
            FailToLoadPlaceholder(::onRefresh)
        } else {
            // Data loaded successfully
            if (dataSource.isDataEmpty) {
                contentWhenDataSourceIsEmpty?.let { it() }
            } else {
                contentWhenDataSourceIsNotEmpty?.let { it(dataSource.data) }
            }
        }
        if (pullRefreshEnabled) {
            PullRefreshIndicator(
                isRefreshing,
                refreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
