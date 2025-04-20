package com.example.eventcountdown.presentation.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.eventcountdown.R

object MyFonts {
    val robotoSlab = FontFamily(
        Font(R.font.robotoslab_regular),
        Font(R.font.robotoslab_bold, weight = FontWeight.Bold),
        Font(R.font.robotoslab_medium, weight = FontWeight.Medium),
        Font(R.font.robotoslab_light, weight = FontWeight.Light)
    )
}