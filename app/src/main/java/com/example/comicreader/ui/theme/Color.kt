package com.example.comicreader.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Custom color palette inspired by Marvel and DC comic book aesthetics.
 */
val ComicBlack = Color(0xFF0F0F0F)
val ComicDarkGrey = Color(0xFF1A1A1A)
val ComicDrawerGrey = Color(0xFF1E1E1E)
val ComicRed = Color(0xFFE23636) // Marvel Red
val ComicBlue = Color(0xFF0052CC) // DC Blue
val ComicWhite = Color(0xFFFFFFFF)
val ComicGrey = Color(0xFFB0B0B0)
val ComicLightGrey = Color(0xFFF5F5F5)
val ComicLightBackground = Color(0xFFECEFF1) // Light Blue-Grey background for Day mode

/**
 * Standard Material 3 color mappings for the dark mode theme.
 */
val PrimaryDark = ComicRed
val OnPrimaryDark = Color.White
val PrimaryContainerDark = Color(0xFF930006)
val OnPrimaryContainerDark = Color(0xFFFFDAD4)

val SecondaryDark = ComicBlue
val OnSecondaryDark = Color.White
val SecondaryContainerDark = Color(0xFF004494)
val OnSecondaryContainerDark = Color(0xFFD6E2FF)

val BackgroundDark = ComicBlack
val OnBackgroundDark = ComicWhite
val SurfaceDark = ComicDarkGrey
val OnSurfaceDark = ComicWhite
val SurfaceVariantDark = Color(0xFF2C2C2C)
val OnSurfaceVariantDark = ComicGrey

/**
 * Standard Material 3 color mappings for the light mode theme.
 */
val PrimaryLight = ComicRed
val OnPrimaryLight = Color.White
val PrimaryContainerLight = Color(0xFFFFDAD4)
val OnPrimaryContainerLight = Color(0xFF410002)

val SecondaryLight = ComicBlue
val OnSecondaryLight = Color.White
val SecondaryContainerLight = Color(0xFFD6E2FF)
val OnSecondaryContainerLight = Color(0xFF001A40)

val BackgroundLight = ComicLightBackground // Changed from White to a Blue-Grey
val OnBackgroundLight = ComicBlack
val SurfaceLight = ComicWhite // Keeping surface white for contrast against background
val OnSurfaceLight = ComicBlack
val SurfaceVariantLight = Color(0xFFCFD8DC) // Darker variant of the light background
val OnSurfaceVariantLight = Color(0xFF455A64)
