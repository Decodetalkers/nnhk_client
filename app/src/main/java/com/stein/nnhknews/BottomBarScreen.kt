package com.stein.nnhknews

sealed class BottomBarScreen(val route: String, val title: Int, val icon: Int) {
    data object Home : BottomBarScreen(route = "NEWS", title = R.string.news, icon = R.drawable.news)

    data object About :
            BottomBarScreen(route = "ABOUT", title = R.string.about, icon = R.drawable.about)
}

sealed class OverViewScreen(val route: String) {
    data object Main : OverViewScreen("MAIN")
    data object News : OverViewScreen("News")
}
