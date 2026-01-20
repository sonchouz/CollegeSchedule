package com.example.collegeschedule.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.GroupsDto
import com.example.collegeschedule.data.network.FavoritesDataStore
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // ---------- data ----------
    var groups by remember { mutableStateOf<List<GroupsDto>>(emptyList()) }
    var groupsLoading by remember { mutableStateOf(true) }
    var groupsError by remember { mutableStateOf<String?>(null) }

    // ---------- UI state ----------
    var expanded by remember { mutableStateOf(false) }
    var selectedGroup by remember { mutableStateOf<GroupsDto?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // ---------- favorites (DataStore) ----------
    val context = LocalContext.current
    val favStore = remember { FavoritesDataStore(context) }
    val scope = rememberCoroutineScope()

    // ВАЖНО: если у тебя красное подчёркивание тут — не хватает импорта:
    // import androidx.compose.runtime.collectAsState
    val favoriteGroupNames by favStore.favoritesFlow.collectAsState(initial = emptySet())

    fun toggleFavorite(groupName: String) {
        scope.launch { favStore.toggle(groupName) }
    }

    val filteredGroups = remember(groups, searchQuery) {
        if (searchQuery.isBlank()) groups
        else groups.filter { it.groupName.contains(searchQuery, ignoreCase = true) }
    }

    // ---------- load groups once ----------
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
                // поиск
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    label = { Text("Поиск группы") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedGroup?.groupName.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Выбери группу") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            if (filteredGroups.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Ничего не найдено") },
                                    onClick = {},
                                    enabled = false
                                )
                            } else {
                                filteredGroups.forEach { group ->
                                    val isFav = favoriteGroupNames.contains(group.groupName)

                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = group.groupName,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Icon(
                                                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedGroup = group
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(8.dp))


                    val selectedName = selectedGroup?.groupName
                    val selectedIsFav = selectedName != null && favoriteGroupNames.contains(selectedName)

                    IconButton(
                        onClick = {
                            if (selectedName != null) toggleFavorite(selectedName)
                        },
                        enabled = selectedName != null
                    ) {
                        Icon(
                            imageVector = if (selectedIsFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Избранное"
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // расписание по выбранной группе
        ScheduleScreen(groupName = selectedGroup?.groupName)
    }
}
