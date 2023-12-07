package com.dart.campushelper.viewmodel

import androidx.compose.material3.TooltipState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dart.campushelper.data.DataStoreRepository
import com.dart.campushelper.data.NetworkRepository
import com.dart.campushelper.data.Result
import com.dart.campushelper.model.Classroom
import com.dart.campushelper.model.Course
import com.dart.campushelper.model.PlannedCourse
import com.dart.campushelper.model.ScheduleNoteItem
import com.dart.campushelper.utils.DateUtils
import com.dart.campushelper.utils.getCurrentNode
import com.dart.campushelper.utils.getWeekCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ScheduleUiState(
    val courses: Result<List<Course>> = Result(),
    val currentWeek: Int? = null,
    val browsedWeek: Int? = null,
    // Indicate the day of week, 1 for Monday, 7 for Sunday
    val dayOfWeek: Int,
    // Indicate the node of the current day, 1 for 8:20 - 9:05, 2 for 9:10 - 9:55, etc.
    val currentNode: Int = 1,
    val isCourseDetailDialogOpen: Boolean = false,
    val isShowWeekSliderDialog: Boolean = false,
    val nodeHeaders: IntRange = (1..10),
    val contentInCourseDetailDialog: List<Course> = emptyList(),
    val nodeStartHeaders: List<String> = DateUtils.nodeEnds.map {
        LocalTime.of(
            it.split(":")[0].toInt(),
            it.split(":")[1].toInt()
        ).minusMinutes(45).format(DateTimeFormatter.ofPattern("HH:mm"))
    },
    val nodeEndHeaders: List<String> = DateUtils.nodeEnds,
    val isOtherCourseDisplay: Boolean = false,
    val isYearDisplay: Boolean = false,
    val isDateDisplay: Boolean = false,
    val isTimeDisplay: Boolean = false,
    val semesters: List<String> = emptyList(),
    val browsedSemester: String? = null,
    val currentSemester: String? = null,
    val startLocalDate: LocalDate? = null,
    val teachingClassrooms: Result<List<Course>> = Result(),
    val buildingNames: List<String>? = null,
    val emptyClassrooms: Result<List<Classroom>> = Result(),
    val holdingCourseTooltipState: TooltipState = TooltipState(isPersistent = true),
    val holdingSemesterTooltipState: TooltipState = TooltipState(isPersistent = true),
    val scheduleNotes: Result<List<ScheduleNoteItem>> = Result(),
    val isShowScheduleNotesSheet: Boolean = false,
    val plannedSchedule: Result<List<PlannedCourse>> = Result(),
    val isShowPlannedScheduleSheet: Boolean = false,
    val isShowTeachingClassroomSheet: Boolean = false,
    val isShowEmptyClassroomSheet: Boolean = false,
    val dayOfWeekOnHoldingCourse: Int = 0,
    val nodeNoOnHoldingCourse: Int = 0,
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(
        ScheduleUiState(
            dayOfWeek = LocalDate.now().dayOfWeek.value,
            currentNode = getCurrentNode(),
        )
    )
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val yearAndSemesterStateFlow =
        dataStoreRepository.observeYearAndSemester().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = runBlocking {
                dataStoreRepository.observeYearAndSemester().first()
            }
        )

    private val enterUniversityYearStateFlow =
        dataStoreRepository.observeEnterUniversityYear().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = runBlocking {
                dataStoreRepository.observeEnterUniversityYear().first()
            }
        )

    private val isOtherCourseDisplayStateFlow =
        dataStoreRepository.observeIsOtherCourseDisplay().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            runBlocking {
                dataStoreRepository.observeIsOtherCourseDisplay().first()
            }
        )

    private val isYearDisplayStateFlow = dataStoreRepository.observeIsYearDisplay().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        runBlocking {
            dataStoreRepository.observeIsYearDisplay().first()
        }
    )

    private val isDateDisplayStateFlow = dataStoreRepository.observeIsDateDisplay().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        runBlocking {
            dataStoreRepository.observeIsDateDisplay().first()
        }
    )

    private val isTimeDisplayStateFlow = dataStoreRepository.observeIsTimeDisplay().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        runBlocking {
            dataStoreRepository.observeIsTimeDisplay().first()
        }
    )

    init {
        viewModelScope.launch {
            isOtherCourseDisplayStateFlow.collect { value ->
                _uiState.update {
                    it.copy(isOtherCourseDisplay = value)
                }
            }
        }
        viewModelScope.launch {
            isYearDisplayStateFlow.collect { value ->
                _uiState.update {
                    it.copy(isYearDisplay = value)
                }
            }
        }
        viewModelScope.launch {
            isDateDisplayStateFlow.collect { value ->
                _uiState.update {
                    it.copy(isDateDisplay = value)
                }
            }
        }
        viewModelScope.launch {
            isTimeDisplayStateFlow.collect { value ->
                _uiState.update {
                    it.copy(isTimeDisplay = value)
                }
            }
        }
        viewModelScope.launch {
            combine(
                yearAndSemesterStateFlow,
                enterUniversityYearStateFlow
            ) { yearAndSemester, enterUniversityYear ->
                listOf(yearAndSemester, enterUniversityYear)
            }.collect { data ->
                if (data[0].isNotEmpty() && data[1].isNotEmpty()) {
                    _uiState.update { it.copy(currentSemester = data[0]) }
                }
            }
        }
    }

    fun setBrowsedSemester(value: String?) {
        _uiState.update {
            it.copy(
                browsedSemester = value
            )
        }
    }

    fun setBrowsedWeek(value: Int) {
        _uiState.update {
            it.copy(
                browsedWeek = value
            )
        }
    }

    fun setIsCourseDetailDialogOpen(value: Boolean) {
        _uiState.update {
            it.copy(isCourseDetailDialogOpen = value)
        }
    }

    fun setIsShowWeekSliderDialog(value: Boolean) {
        _uiState.update {
            it.copy(isShowWeekSliderDialog = value)
        }
    }

    fun setContentInCourseDetailDialog(value: List<Course>) {
        _uiState.update {
            it.copy(contentInCourseDetailDialog = value)
        }
    }

    suspend fun loadSchedule(yearAndSemester: String? = null) {
        val semesterYearStart = enterUniversityYearStateFlow.value.toInt()
        val semesterYearEnd = yearAndSemesterStateFlow.value.take(4).toInt()
        val semesterNoEnd = yearAndSemesterStateFlow.value.last().toString().toInt()
        val semesters = mutableListOf<String>()
        (semesterYearStart..semesterYearEnd).forEach { year ->
            (1..2).forEach { no ->
                if (year == semesterYearEnd && no > semesterNoEnd) {
                    return@forEach
                }
                semesters.add("$year-${year + 1}-$no")
            }
        }
        _uiState.update {
            it.copy(
                browsedSemester = yearAndSemester ?: yearAndSemesterStateFlow.value,
                semesters = semesters,
                startLocalDate = networkRepository.getCalendar(yearAndSemester)?.firstOrNull()
                    ?.let { found ->
                        (found.monday ?: (found.tuesday ?: (found.wednesday
                            ?: (found.thursday ?: (found.friday ?: (found.saturday
                                ?: found.sunday))))))?.let { day ->
                            LocalDate.parse(
                                found.yearAndMonth + "-" + (if (day.toInt() < 10) "0$day" else day),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            )
                        }
                    },
            )
        }
        if (_uiState.value.browsedSemester == semesters.last()) {
            val currentWeek = getWeekCount(_uiState.value.startLocalDate, LocalDate.now())
            _uiState.update { uiState ->
                uiState.copy(currentWeek = currentWeek, browsedWeek = currentWeek)
            }
        }
        _uiState.update { it.copy(courses = Result(networkRepository.getSchedule(yearAndSemester))) }
    }

    suspend fun loadTeachingClassrooms(dayOfWeek: Int, node: Int) {
        _uiState.update { it.copy(buildingNames = null) }
        val startNode = node * 2 - 1
        val result = _uiState.value.browsedSemester?.let {
            networkRepository.getGlobalSchedule(
                yearAndSemester = it,
                startWeekNo = _uiState.value.browsedWeek.toString(),
                endWeekNo = _uiState.value.browsedWeek.toString(),
                startDayOfWeek = dayOfWeek.toString(),
                endDayOfWeek = dayOfWeek.toString(),
                startNode = startNode.toString(),
                endNode = (node * 2).toString(),
            )
        }
        var teachingClassrooms: List<Course>? = null
        result?.forEach { course ->
            if (course.classroom.split(", ").size > 1) {
                course.sksjdd!!.split("\n").forEach {
                    val items = it.split(" ")
                    if (toMachineReadableWeekNoList(items[0]).contains(_uiState.value.browsedWeek)) {
                        if (when (items[1]) {
                                "周一" -> 1
                                "周二" -> 2
                                "周三" -> 3
                                "周四" -> 4
                                "周五" -> 5
                                "周六" -> 6
                                "周日" -> 7
                                else -> 0
                            } == dayOfWeek
                        ) {
                            if (items[2].replace("小节", "").split("-")[0].toInt() == startNode) {
                                val tmp = Course().copy(
                                    courseNameHtml = course.courseNameHtml,
                                    classroomNameHtml = items[3],
                                    teacherNameHtml = course.teacherNameHtml,
                                    classNameHtml = course.classNameHtml,
                                )
                                teachingClassrooms = if (teachingClassrooms == null) {
                                    listOf(tmp)
                                } else {
                                    teachingClassrooms!!.plus(tmp)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (result != null && teachingClassrooms == null) {
            teachingClassrooms = emptyList()
        }
        _uiState.update {
            it.copy(
                buildingNames = teachingClassrooms?.map { course ->
                    course.classroomName?.split("-")?.get(0) ?: ""
                }?.distinct()?.sorted(),
                teachingClassrooms = Result(teachingClassrooms?.distinct()
                    ?.sortedBy { item -> item.classroomName }),
            )
        }
    }

    suspend fun loadEmptyClassroom(dayOfWeek: Int, node: Int) {
        _uiState.update { it.copy(buildingNames = null) }
        val result = _uiState.value.browsedWeek?.let {
            networkRepository.getEmptyClassroom(
                weekNo = listOf(it),
                dayOfWeekNo = listOf(dayOfWeek),
                nodeNo = listOf(node),
            )
        }
        _uiState.update {
            it.copy(
                buildingNames = result?.map { course ->
                    course.buildingName ?: ""
                }?.distinct()?.sorted(),
                emptyClassrooms = Result(result),
            )
        }
    }

    private fun toMachineReadableWeekNoList(humanReadableWeekNoList: String): List<Int> {
        val list = mutableListOf<Int>()
        humanReadableWeekNoList.replace("周", "").split(",").forEach { item ->
            if (item.contains("-")) {
                val start = item.split("-")[0].toInt()
                val end = item.split("-")[1].toInt()
                (start..end).forEach { list.add(it) }
            } else {
                list.add(item.toInt())
            }
        }
        return list
    }

    suspend fun loadScheduleNotes() {
        _uiState.update {
            it.copy(
                scheduleNotes = Result(
                    networkRepository.getScheduleNotes(
                        _uiState.value.browsedSemester
                    )
                ),
            )
        }
    }

    fun setIsShowScheduleNotesSheet(value: Boolean) {
        _uiState.update {
            it.copy(isShowScheduleNotesSheet = value)
        }
    }

    suspend fun loadPlannedSchedule() {
        _uiState.update {
            it.copy(
                isShowPlannedScheduleSheet = true,
            )
        }
        _uiState.update {
            it.copy(
                plannedSchedule = Result(networkRepository.getPlannedSchedule()),
            )
        }
    }

    fun setIsShowPlannedScheduleSheet(value: Boolean) {
        _uiState.update {
            it.copy(isShowPlannedScheduleSheet = value)
        }
    }

    fun setIsShowNonEmptyClassroomSheet(value: Boolean) {
        _uiState.update {
            it.copy(isShowTeachingClassroomSheet = value)
        }
    }

    fun setIsShowEmptyClassroomSheet(value: Boolean) {
        _uiState.update {
            it.copy(isShowEmptyClassroomSheet = value)
        }
    }

    fun setDayOfWeekOnHoldingCourse(value: Int) {
        _uiState.update {
            it.copy(dayOfWeekOnHoldingCourse = value)
        }
    }

    fun setNodeNoOnHoldingCourse(value: Int) {
        _uiState.update {
            it.copy(nodeNoOnHoldingCourse = value)
        }
    }
}
