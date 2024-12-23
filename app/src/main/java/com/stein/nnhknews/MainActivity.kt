package com.stein.nnhknews

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stein.nnhknews.common.Resource
import com.stein.nnhknews.nhk.NewsView
import com.stein.nnhknews.nhk.NewsViewColumn
import com.stein.nnhknews.nhk.NhkHtmlModel
import com.stein.nnhknews.nhk.NhkNews
import com.stein.nnhknews.nhk.NhkViewModel
import com.stein.nnhknews.settings.DessertReleaseViewModel
import com.stein.nnhknews.ui.theme.MahoyinkuimaTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

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
    val nhkHtml: NhkHtmlModel = viewModel()

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = OverViewScreen.Main.route) {
        composable(OverViewScreen.Main.route) { MainView(nhkHtml, navController) }
        composable(OverViewScreen.News.route) { WebViewScreen(nhkHtml, navController) }
    }
}

@Composable
fun MainView(htmlModel: NhkHtmlModel, upNavController: NavHostController) {
    val navController = rememberNavController()
    val nhkViewModel: NhkViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val settingsViewModel: DessertReleaseViewModel =
            viewModel(factory = AppViewModelProvider.Factory)
    // NOTE: just hack it now
    MaterialTheme {
        Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
            NavHost(navController = navController, startDestination = BottomBarScreen.Home.route) {
                composable(BottomBarScreen.Home.route) {
                    NhkNewsList(
                            settingViewModel = settingsViewModel,
                            nhkViewModel = nhkViewModel,
                            htmlModel = htmlModel,
                            upNavController = upNavController,
                            dp = padding
                    )
                }

                composable(BottomBarScreen.About.route) { AboutPage(padding) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(dp: PaddingValues? = null) {
    var showDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val remoteUrl = "https://github.com/Decodetalkers/nnhk_client"
    val modifier =
            Modifier.fillMaxSize().let done@{
                if (dp == null) return@done it
                it.padding(dp)
            }
    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    text = "About",
                                    modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Right
                            )
                        }
                )
            },
            modifier = modifier,
    ) { padding ->
        Column(
                modifier = Modifier.padding(padding).fillMaxHeight().padding(all = 30.dp),
        ) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                    modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
                    text = "nnhknews -> A nhk news client made with jetpack compose",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                    modifier =
                            Modifier.clickable { uriHandler.openUri(remoteUrl) }
                                    .fillMaxWidth()
                                    .padding(10.dp)
            ) {
                Text(text = "Github", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                        text = "View the source code",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                    modifier =
                            Modifier.clickable { showDialog = true }.fillMaxWidth().padding(10.dp)
            ) {
                Text(text = "License", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "MIT", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
    if (showDialog) {
        DialogLicense { showDialog = false }
    }
}

const val MITLICENSE =
        """
MIT License
Copyright (c) 2023 Decodetalkers

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
"""

@Composable
fun DialogLicense(
        onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
                modifier = Modifier.fillMaxWidth().height(475.dp).padding(16.dp),
                shape = RoundedCornerShape(16.dp),
        ) {
            LazyColumn {
                item {
                    Text(
                            text = MITLICENSE,
                            modifier = Modifier.padding(16.dp),
                    )
                }
                item {
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                                onClick = { onDismissRequest() },
                                modifier = Modifier.padding(8.dp),
                        ) { Text("Ok") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(onDateRangeSelected: (Pair<Long?, Long?>) -> Unit, onDismiss: () -> Unit) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                        onClick = {
                            onDateRangeSelected(
                                    Pair(
                                            dateRangePickerState.selectedStartDateMillis,
                                            dateRangePickerState.selectedEndDateMillis
                                    )
                            )
                            onDismiss()
                        }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DateRangePicker(
                state = dateRangePickerState,
                title = {},
                showModeToggle = false,
                modifier = Modifier.fillMaxWidth().height(500.dp)
        )
    }
}

@Composable
fun NetworkError(padding: PaddingValues = PaddingValues(all = 0.dp), error: String) {
    Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = Color.DarkGray)

        Image(
                painterResource(R.drawable.error),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(30.dp).height(30.dp)
        )
    }
}

@Preview
@Composable
fun NetworkErrorPreview() {
    NetworkError(error = "network error")
}

@Composable
fun NewsCard(
        modifier: Modifier,
        htmlModel: NhkHtmlModel,
        upNavController: NavHostController,
        isLinearLayout: Boolean,
        news: List<NhkNews>
) {

    if (isLinearLayout) {
        LazyColumn(modifier = modifier) {
            items(news) { data ->
                data.NewsView {
                    htmlModel.setData(data)

                    upNavController.navigate(OverViewScreen.News.route)
                }
            }
        }
    } else {
        LazyVerticalGrid(
                modifier = modifier,
                columns = GridCells.Fixed(2),
                verticalArrangement =
                        Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
                horizontalArrangement =
                        Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            items(news) { data ->
                data.NewsViewColumn {
                    htmlModel.setData(data)

                    upNavController.navigate(OverViewScreen.News.route)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NhkNewsList(
        settingViewModel: DessertReleaseViewModel,
        nhkViewModel: NhkViewModel,
        htmlModel: NhkHtmlModel,
        upNavController: NavHostController,
        dp: PaddingValues? = null
) {
    val uiState = settingViewModel.uiState.collectAsState().value
    val isLinearLayout = uiState.isLinearLayout
    val cachedNews by nhkViewModel.cachedValues.collectAsState()
    val state by nhkViewModel.state
    val glModifier =
            Modifier.fillMaxSize().let done@{
                if (dp == null) return@done it
                it.padding(dp)
            }
    var showRangeModal by remember { mutableStateOf(false) }
    Scaffold(
            modifier = glModifier,
            topBar = {
                CenterAlignedTopAppBar(
                        colors =
                                TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                        title = {
                            Text(
                                    nhkViewModel.modelTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                            )
                        },
                        actions = {
                            IconButton(
                                    onClick = { settingViewModel.selectLayout(!isLinearLayout) }
                            ) {
                                Icon(
                                        painter = painterResource(uiState.toggleIcon),
                                        contentDescription =
                                                stringResource(uiState.toggleContentDescription),
                                        tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            IconButton(onClick = { showRangeModal = true }) {
                                Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Localized description"
                                )
                            }
                        }
                )
            }
    ) { padding ->
        when (val smartCastData = state) {
            is Resource.Success ->
                    NewsCard(
                            modifier = Modifier.padding(padding),
                            news = smartCastData.data,
                            htmlModel = htmlModel,
                            isLinearLayout = isLinearLayout,
                            upNavController = upNavController
                    )
            is Resource.Failure ->
                    if (cachedNews.isEmpty()) {
                        Column(
                                modifier = Modifier.padding(padding),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) { Text(text = smartCastData.message, fontWeight = FontWeight.Bold) }
                    } else {
                        NewsCard(
                                modifier = Modifier.padding(padding),
                                news = cachedNews,
                                htmlModel = htmlModel,
                                isLinearLayout = isLinearLayout,
                                upNavController = upNavController
                        )
                    }
            else ->
                    CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
        }
    }
    if (showRangeModal) {
        DateRangePickerModal(
                onDateRangeSelected = date@{
                            val (end_l, start_l) = it

                            if (start_l == null || end_l == null) return@date
                            var start =
                                    LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(start_l),
                                            ZoneId.systemDefault()
                                    )
                            val end =
                                    LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(end_l),
                                            ZoneId.systemDefault()
                                    )
                            nhkViewModel.clearState()
                            nhkViewModel.syncNews(start, end)
                            showRangeModal = false
                        },
                onDismiss = { showRangeModal = false }
        )
    }
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
                                    playIcon = Icons.Filled.Done
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

    val screens = listOf(BottomBarScreen.Home, BottomBarScreen.About)

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
