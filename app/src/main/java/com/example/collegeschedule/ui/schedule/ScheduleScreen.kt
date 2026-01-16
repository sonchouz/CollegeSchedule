package com.example.collegeschedule.ui.schedule

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.utils.getWeekDateRange


@Composable
fun ScheduleScreen(groupName: String?){
    var schedule by remember {
        mutableStateOf<List<ScheduleByDateDto>>(emptyList())}
    var loading by remember{ mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(groupName) {
        if (groupName.isNullOrBlank()) return@LaunchedEffect
        val (start, end) = getWeekDateRange()
        try{
            schedule = RetrofitInstance.api.getSchedule(
                groupName = groupName,
                start = start,
                end = end
            )
        }catch(e: Exception)
        {
            error = e.message
        }finally{
            loading = false
        }
    }
    when{
        groupName.isNullOrBlank() -> Text("Выбери группу, чтобы увидеть расписание")
        loading -> CircularProgressIndicator()
        error != null -> Text("Ошибка: $error")
        else -> ScheduleList(schedule)
    }
}