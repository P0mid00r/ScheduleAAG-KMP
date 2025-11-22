package com.pomidorka.scheduleaag.ad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pomidorka.scheduleaag.ui.Green

expect object AdManager {
    fun showOpenAppAd()

    @Composable
    fun AdBannerMainScreen(
        modifier: Modifier = Modifier,
        backgroundColor: Color
    )

    @Composable
    fun AdBannerScheduleScreen(
        modifier: Modifier = Modifier,
        backgroundColor: Color
    )

    @Composable
    fun AdBannerAnyScreen(
        modifier: Modifier = Modifier,
        backgroundColor: Color
    )
}

@Composable
fun DefaultAdBanner(
    modifier: Modifier = Modifier,
    backgroundColor: Color
) {
    if (true) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(backgroundColor),
//            .clip(RoundedCornerShape(24.dp))
//            .background(Green)
//            .innerShadow(
//                shape = RoundedCornerShape(24.dp),
//                block = {
//                    radius = 30f
//                }
//            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = modifier.align(Alignment.Center),
            text = "Отчисляйся из ААГ",
//            text = "Тут может бы быть ваша реклама",
            maxLines = 1,
            color = Color.White
        )
    }
}