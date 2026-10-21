package com.pomidorka.scheduleaag.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
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
    val keyboardController = LocalSoftwareKeyboardController.current
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
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
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
    val qTokens = remember(query) { tokenize(query) }

    val filtered = if (qTokens.isEmpty()) filtersList
    else filtersList
        .mapNotNull { item -> scoreOf(qTokens, item.data)?.let { item to it } }
        .sortedByDescending { it.second }
        .map { it.first }

    LazyColumn {
        items(filtered, key = { it.hash }) { item ->
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

private val SEPARATORS = Regex("[^\\p{L}\\p{N}]+")

private fun normalize(text: String): String =
    text.lowercase()
        .replace('ё', 'е')
        .replace(SEPARATORS, " ")
        .trim()

private fun tokenize(text: String): List<String> =
    normalize(text).split(' ').filter { it.isNotEmpty() }

private fun maxDistFor(len: Int): Int = when {
    len <= 3 -> 0
    len <= 5 -> 1
    len <= 8 -> 2
    else     -> 3
}

private fun levenshtein(a: String, b: String, maxDist: Int): Int? {
    if (kotlin.math.abs(a.length - b.length) > maxDist) return null
    val la = a.length; val lb = b.length
    var prev = IntArray(lb + 1) { it }
    var curr = IntArray(lb + 1)
    for (i in 1..la) {
        curr[0] = i
        var rowMin = i
        for (j in 1..lb) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            curr[j] = minOf(prev[j] + 1, curr[j - 1] + 1, prev[j - 1] + cost)
            if (curr[j] < rowMin) rowMin = curr[j]
        }
        if (rowMin > maxDist) return null
        val t = prev; prev = curr; curr = t
    }
    return if (prev[lb] <= maxDist) prev[lb] else null
}

/**
 * Совпадение в любую сторону: "ул"↔"улица", "корп"↔"корпус", "титов"↔"титова".
 * Токены короче 2 символов не участвуют — защита от оверматчинга
 * ("г"→"город", "к"→"корпус").
 */
private fun prefixMatch(a: String, b: String): Boolean {
    if (a.length < 2 || b.length < 2) return false
    return a.startsWith(b) || b.startsWith(a)
}

private fun scoreToken(q: String, tokens: List<String>, joined: String): Int? {
    val exact = tokens.indexOfFirst { it == q }
    if (exact >= 0) return 100 - exact

    val prefix = tokens.indexOfFirst { prefixMatch(it, q) }
    if (prefix >= 0) return 60 - prefix

    if (q.length >= 2) {
        val contains = tokens.indexOfFirst { it.length >= 2 && it.contains(q) }
        if (contains >= 0) return 30 - contains
    }

    if (q.length >= 2 && joined.contains(q)) return 15

    val md = maxDistFor(q.length)
    if (md > 0) {
        var best: Int? = null
        for (t in tokens) {
            val d = levenshtein(q, t, md) ?: continue
            if (best == null || d < best) best = d
            if (d == 0) break
        }
        if (best != null) return 20 - best * 5
    }
    return null
}

private fun scoreOf(qTokens: List<String>, target: String): Int? {
    if (qTokens.isEmpty()) return 0
    val tokens = tokenize(target)
    if (tokens.isEmpty()) return null
    val joined = tokens.joinToString("")
    var total = 0
    for (q in qTokens) {
        total += scoreToken(q, tokens, joined) ?: return null
    }
    return total
}