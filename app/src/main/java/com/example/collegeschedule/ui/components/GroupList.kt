package com.example.collegeschedule.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegeschedule.data.dto.GroupsDto
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
public fun GroupList(data: List<GroupsDto>) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Список групп")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (data.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Групп нет") },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                data.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group.groupName) },
                        onClick = {
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

