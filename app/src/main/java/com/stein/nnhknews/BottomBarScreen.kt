package com.stein.nnhknews

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomBarScreen(route = "NEWS", title = "NEWS", icon = Icons.Default.Home)

    data object About :
            BottomBarScreen(route = "ABOUT", title = "ABOUT", icon = Icons.Default.Settings)
}

sealed class OverViewScreen(val route: String) {
    data object Main : OverViewScreen("MAIN")
    data object News : OverViewScreen("News")
}
