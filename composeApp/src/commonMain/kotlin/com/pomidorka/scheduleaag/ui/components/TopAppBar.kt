package com.pomidorka.scheduleaag.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.utils.getVibrator

@Composable
fun TopAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    onBackClick: () -> Unit
) {
    val vibrator = getVibrator()
    var isNavigating by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        modifier = Modifier
            .dropShadow(
                shape = RectangleShape,
                block = {
                    radius = 20f
                }
            ),
        title = title,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Green,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    if (!isNavigating) {
                        isNavigating = true
                        onBackClick.invoke()
                        vibrator.vibrateClick()
                    }
                }
            ) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
    )

    LaunchedEffect(Unit) {
        isNavigating = false
    }
}