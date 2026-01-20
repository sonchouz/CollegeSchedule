package com.example.collegeschedule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.GroupsDto
import com.example.collegeschedule.data.network.FavoritesDataStore
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    // -------- data -------
    var groups by remember { mutableStateOf<List<GroupsDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // -------- favorites (DataStore) -------
    val context = LocalContext.current
    val favStore = remember { FavoritesDataStore(context) }
    val scope = rememberCoroutineScope()

    val favoriteGroupNames by favStore.favoritesFlow.collectAsState(initial = emptySet())

    // Загружаем группы один раз
    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            groups = RetrofitInstance.group.getGroups()
        } catch (e: Exception) {
            error = e.message ?: "Ошибка загрузки групп"
        } finally {
            loading = false
        }
    }


    val favoriteGroups = remember(groups, favoriteGroupNames) {
        groups.filter { it.groupName in favoriteGroupNames }
            .sortedBy { it.groupName }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Избранные группы", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        when {
            loading -> {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
            }

            error != null -> {
                Text("Ошибка: $error")
            }

            favoriteGroupNames.isEmpty() -> {
                Text("Избранных групп нет")
            }

            favoriteGroups.isEmpty() -> {
                Text("Не удалось найти избранные группы в списке с сервера.")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(favoriteGroups, key = { it.groupName }) { group ->
                        FavoriteGroupItem(
                            groupName = group.groupName,
                            onOpen = {

                            },
                            onRemove = {
                                scope.launch { favStore.toggle(group.groupName) }
                            }
                        )


                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
