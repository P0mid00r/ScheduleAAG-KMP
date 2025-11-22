package com.pomidorka.scheduleaag.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pomidorka.scheduleaag.schedule.Result
import com.pomidorka.scheduleaag.schedule.interactive.FilterData
import com.pomidorka.scheduleaag.schedule.interactive.FilterType
import com.pomidorka.scheduleaag.schedule.interactive.ScheduleInteractiveApi.getFilters
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialogController
import kotlinx.coroutines.launch

var groups by mutableStateOf(emptyList<FilterData>())
var prep by mutableStateOf(emptyList<FilterData>())

@Composable
fun ExpandingSearchFiltersScreen(
    filterType: FilterType,
    onExpandedChanged: (Boolean) -> Unit,
    onSelectedItem: (FilterData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val errorDialogController = ErrorDialogController(
        onConfirm = {
            it.hideDialog()
            onExpandedChanged(false)
        }
    )
    ErrorDialog(errorDialogController)

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            filterType.getFilters().let {
                val filters = when (it) {
                    is Result.Success -> {
                        if (it.data.isEmpty()) {
                            errorDialogController.showDialog("Сервер не доступен, попробуйте позже!")
                        }

                        it.data
                    }
                    is Result.Failure -> {
                        errorDialogController.showDialog("Сервер не доступен, попробуйте позже!")

                        emptyList()
                    }
                }

                when (filterType) {
                    FilterType.Group -> groups = filters
                    FilterType.Prep -> prep = filters
                    FilterType.Aud -> TODO()
                }
            }
        }
    }

    ExpandingSearchFiltersScreen(
        filtersList = when(filterType) {
            FilterType.Group -> groups
            FilterType.Prep -> prep
            FilterType.Aud -> TODO()
        },
        onExpandedChanged = { onExpandedChanged(it) },
        onSelectedItem = { onSelectedItem(it) },
        modifier = modifier
    )
}

@Composable
fun ExpandingSearchFiltersScreen(
    filtersList: List<FilterData>,
    onExpandedChanged: (Boolean) -> Unit,
    onSelectedItem: (FilterData) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = "Поиск",
            onBackClick = { onExpandedChanged(false) }
        )
        SearchBarFilters(
            filtersList = filtersList,
            onExpandedChanged = { onExpandedChanged(false) },
            onSelectedItem = {
                onSelectedItem(it)
                onExpandedChanged(false)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarFilters(
    filtersList: List<FilterData>,
    onExpandedChanged: (Boolean) -> Unit,
    onSelectedItem: (FilterData) -> Unit,
) {
    var query by remember { mutableStateOf("") }

    SearchBar(
        modifier = Modifier,
        colors = SearchBarColors(
            containerColor = Color.White,
            dividerColor = Green,
        ),
        inputField = {
            TextField(
                modifier = Modifier.fillMaxSize(),
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                placeholder = { Text("Поиск...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
        },
        expanded = true,
        onExpandedChange = {  },
        windowInsets = WindowInsetsNotPadding(),
        shape = SearchBarDefaults.fullScreenShape
    ) {
        FiltersList(
            filtersList = filtersList,
            query = query,
            onSelectedItem = {
                onSelectedItem(it)
                onExpandedChanged(false)
            }
        )
    }
}

@Composable
private fun FiltersList(
    filtersList: List<FilterData>,
    query: String,
    onSelectedItem: (item: FilterData) -> Unit
) {
    LazyColumn {
        items(filtersList.filter {
            it.data
                .lowercase()
                .filterNot { char -> char == '-' }
                .startsWith(
                    query
                        .lowercase()
                        .filterNot { char -> char == '-' }
                )
        }) { item ->
            ListItem(
                modifier = Modifier.clickable {
                    onSelectedItem(item)
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.White
                ),
                headlineContent = {
                    Text(text = item.data)
                }
            )

            HorizontalDivider()
        }
    }
}