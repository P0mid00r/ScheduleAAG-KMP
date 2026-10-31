package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pomidorka.scheduleaag.Strings
import com.pomidorka.scheduleaag.ad.AdManager
import com.pomidorka.scheduleaag.schedule.PdfLoadingState
import com.pomidorka.scheduleaag.schedule.rememberPdfDownloader
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.NavigationBar
import com.pomidorka.scheduleaag.ui.components.TopAppBarWithToolBar
import com.pomidorka.scheduleaag.ui.components.alertdialogs.*
import com.pomidorka.scheduleaag.ui.components.schedule.PdfViewer
import com.pomidorka.scheduleaag.utils.currentPlatform
import kotlin.math.roundToInt

@Composable
fun SchedulePdfViewerScreen(
    navController: NavHostController,
    url: String
) {
    val isNotDesktopOrWeb = !(currentPlatform().type.isDesktop || currentPlatform().type.isWeb)
    val pdfDownloader = rememberPdfDownloader(url)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var searchBarVisible by rememberSaveable { mutableStateOf(false) }
    var isShowPdfViewer by rememberSaveable { mutableStateOf(true) }
    val loadingDialogController = LoadingDialogController(Strings.PROGRESS_DIALOG_SCHEDULE)
    val errorDialogController = rememberErrorDialogController(
        onConfirm = {
            it.hideDialog()
            isShowPdfViewer = false
            navController.popBackStack()
        }
    )

    var searchTextState by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val progressState by pdfDownloader.pdfLoadingState.collectAsState()
    var progress by remember { mutableIntStateOf(-1) }
    val showProgressBar by remember {
        derivedStateOf { progress in 0..100 }
    }

    if (showProgressBar && !loadingDialogController.isVisible) {
        ProgressDialog(
            progress = progress,
        )
    }
    LoadingDialog(loadingDialogController)
    ErrorDialog(errorDialogController)

    LaunchedEffect(progressState) {
        when (val state = progressState) {
            PdfLoadingState.Initialization -> loadingDialogController.showDialog()
            PdfLoadingState.Started -> loadingDialogController.hideDialog()
            is PdfLoadingState.Loading -> progress = state.progress.roundToInt()
            PdfLoadingState.Loaded -> {
                loadingDialogController.hideDialog()
                progress = -1
            }
            is PdfLoadingState.Failed -> {
                progress = -1
                loadingDialogController.hideDialog()
                errorDialogController.showDialog("Произошла ошибка при скачивании файла")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBarWithToolBar(
                title = "Расписание",
                onBackClick = {
                    isShowPdfViewer = false
                    navController.popBackStack()
                },
                actions = {
                    if (isNotDesktopOrWeb) {
                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                searchBarVisible = !searchBarVisible
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(50.dp),
                                imageVector = if (searchBarVisible) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                },
                showedToolBar = searchBarVisible,
                toolBarContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Green),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            singleLine = true,
                            onValueChange = { searchQuery = it.replace("\n", "") },
                            label = { Text("Введите запрос") },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                    searchTextState = searchQuery
                                    keyboardController?.hide()
                                }
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                focusedLabelColor = Color.White,
                                focusedBorderColor = Color.White,

                                unfocusedTextColor = Color.Black,
                                unfocusedLabelColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                            )
                        )

                        IconButton(
                            onClick = {
                                focusManager.clearFocus()
                                searchTextState = searchQuery
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(50.dp),
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column {
                AdManager.AdBannerScheduleScreen(
                    backgroundColor = Green
                )
                NavigationBar(
                    color = Green,
                )
            }
        }
    ) { paddings ->
        BackgroundCells(
            Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            if (isShowPdfViewer) {
                PdfViewer(
                    modifier = Modifier.fillMaxSize(),
                    searchTextState = if (searchBarVisible) searchTextState else "",
                    downloader = pdfDownloader,
                )
            }
        }
    }
}