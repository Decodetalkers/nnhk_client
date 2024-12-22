package com.stein.nnhknews

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stein.nnhknews.common.Resource
import com.stein.nnhknews.nhk.NewsView
import com.stein.nnhknews.nhk.NhKViewModel
import com.stein.nnhknews.nhk.NhkHtmlModel
import com.stein.nnhknews.ui.theme.MahoyinkuimaTheme

import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MahoyinkuimaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        // color = MaterialTheme.colorScheme.background
                        ) { OverAllView() }
            }
        }
    }
}

@Composable
fun OverAllView() {
    val nhkhtml: NhkHtmlModel = viewModel()

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = OverViewScreen.Main.route) {
        composable(OverViewScreen.Main.route) { MainView(nhkhtml, navController) }
        composable(OverViewScreen.News.route) { WebViewScreen(nhkhtml, navController) }
    }
}

@Composable
fun MainView(htmlModel: NhkHtmlModel, upNavController: NavHostController) {
    val navController = rememberNavController()
    val nhkView: NhKViewModel = viewModel()
    nhkView.syncNews()

    // NOTE: just hack it now
    MaterialTheme {
        Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
            NavHost(navController = navController, startDestination = BottomBarScreen.Home.route) {
                composable(BottomBarScreen.Home.route) {
                    NhkNewsList(
                            model = nhkView,
                            htmlModel = htmlModel,
                            upNavController = upNavController,
                            dp = padding
                    )
                }

                composable(BottomBarScreen.Settings.route) {
                    Column(
                            modifier = Modifier.padding(padding).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                    ) { Text(text = "A Phone Info shown app", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
fun NhkNewsList(
        model: NhKViewModel,
        htmlModel: NhkHtmlModel,
        upNavController: NavHostController,
        dp: PaddingValues? = null
) {
    val state by model.state
    val glModifier =
            Modifier.fillMaxSize().let done@{
                if (dp == null) return@done it
                it.padding(dp)
            }
    when (val smartCastData = state) {
        is Resource.Success ->
                LazyColumn(modifier = glModifier) {
                    items(smartCastData.data) { data ->
                        data.NewsView {
                            htmlModel.setData(data)

                            upNavController.navigate(OverViewScreen.News.route)
                        }
                    }
                }
        else ->
                Column(
                        modifier = glModifier,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                ) { Text(text = "Loading....", fontWeight = FontWeight.Bold) }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MahoyinkuimaTheme { /*Greeting("Android") */}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(nhkHtml: NhkHtmlModel, upNavController: NavHostController) {
    val mediaPlayer = MediaPlayer()
    val htmlString = nhkHtml.html
    val title = nhkHtml.title
    val audioUrl = nhkHtml.audioUrl
    var playIcon by remember { mutableStateOf(Icons.Filled.PlayArrow) }

    Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                        onClick = play@{
                                    if (audioUrl == null) return@play
                                    if (mediaPlayer.isPlaying) {
                                        playIcon = Icons.Filled.Done
                                        mediaPlayer.stop()
                                        mediaPlayer.reset()
                                        return@play
                                    }
                                    mediaPlayer.setAudioAttributes(
                                            AudioAttributes.Builder()
                                                    .setContentType(
                                                            AudioAttributes.CONTENT_TYPE_MUSIC
                                                    )
                                                    .build()
                                    )

                                    // on below line we are running a try and catch block
                                    // for our media player.
                                    try {
                                        // on below line we are setting audio source
                                        // as audio url on below line.
                                        mediaPlayer.setDataSource(audioUrl)

                                        // on below line we are preparing
                                        // our media player.
                                        mediaPlayer.prepare()

                                        // on below line we are starting
                                        // our media player.
                                        mediaPlayer.start()
                                    } catch (e: Exception) {

                                        // on below line we are
                                        // handling our exception.
                                        e.printStackTrace()
                                    }
                                    playIcon = Icons.Filled.PlayArrow
                                }
                ) { Icon(playIcon, "Floating action button.") }
            },
            topBar = {
                TopAppBar(
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                        title = { Text(text = title) },
                        navigationIcon = {
                            IconButton(
                                    onClick = {
                                        mediaPlayer.stop()
                                        mediaPlayer.reset()
                                        mediaPlayer.release()
                                        upNavController.navigateUp()
                                    }
                            ) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Localized description"
                                )
                            }
                        },
                )
            }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                    factory = {
                        WebView(it).apply {
                            layoutParams =
                                    ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                        }
                    },
                    update = {
                        it.loadDataWithBaseURL(null, htmlString, "text/html", "utf-8", null)
                    }
            )
        }
    }
}

@Composable
private fun BottomBar(
        navController: NavHostController,
) {
    var selectedDestination by remember { mutableIntStateOf(0) }

    val screens = listOf(BottomBarScreen.Home, BottomBarScreen.Settings)

    val callback =
            NavController.OnDestinationChangedListener end@{ _, destination, _ ->
                if (destination.route == null) return@end
                val index = screens.withIndex().first { destination.route == it.value.route }.index
                if (index >= 0) selectedDestination = index
            }
    navController.addOnDestinationChangedListener(callback)
    NavigationBar {
        screens.forEachIndexed { index, screen ->
            AddItem(
                    screen = screen,
                    isSelected = index == selectedDestination,
                    navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
        screen: BottomBarScreen,
        isSelected: Boolean,
        navController: NavHostController
) {
    NavigationBarItem(
            label = { Text(text = screen.title) },
            icon = { Icon(imageVector = screen.icon, contentDescription = "Navigation Icon") },
            selected = isSelected,
            onClick = { navController.navigate(screen.route) }
    )
}
