package com.example.collegeschedule.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.GroupsDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.ui.schedule.ScheduleScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var groups by remember { mutableStateOf<List<GroupsDto>>(emptyList()) }
    var groupsLoading by remember { mutableStateOf(true) }
    var groupsError by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(false) }
    var selectedGroup by remember { mutableStateOf<GroupsDto?>(null) }


    var searchQuery by remember { mutableStateOf("") }

    val filteredGroups by remember(groups, searchQuery) {
        mutableStateOf(
            if (searchQuery.isBlank()) groups
            else groups.filter { it.groupName.contains(searchQuery, ignoreCase = true) }
        )
    }


    LaunchedEffect(Unit) {
        groupsLoading = true
        groupsError = null
        try {
            groups = RetrofitInstance.group.getGroups()
        } catch (e: Exception) {
            groupsError = e.message ?: "Ошибка загрузки групп"
        } finally {
            groupsLoading = false
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Группа")
        Spacer(Modifier.height(8.dp))

        when {
            groupsLoading -> CircularProgressIndicator()
            groupsError != null -> Text("Ошибка: $groupsError")
            groups.isEmpty() -> Text("Групп нет")
            else -> {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedGroup?.groupName.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Выбери группу") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                            searchQuery = ""
                        }
                    ) {

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )

                        if (filteredGroups.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Ничего не найдено") },
                                onClick = {},
                                enabled = false
                            )
                        } else {
                            filteredGroups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group.groupName) },
                                    onClick = {
                                        selectedGroup = group
                                        expanded = false
                                        searchQuery = ""
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        ScheduleScreen(groupName = selectedGroup?.groupName)
    }
}
