package io.github.dautovicharis.charts.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.cd_navigate_back
import chartsproject.app.generated.resources.cd_open_settings
import io.github.dautovicharis.charts.app.library.BuildConfig
import io.github.dautovicharis.charts.app.ui.theme.AppTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val themeState by viewModel.themeState.collectAsStateWithLifecycle()
    val menuState by viewModel.menuState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateBack = currentBackStackEntry?.destination?.route != ChartDestination.MainScreen.ROUTE

    AppTheme(
        theme = themeState.selectedTheme,
        useDynamicColors = themeState.useDynamicColors,
        darkTheme = viewModel.resolveDarkTheme(isSystemInDarkTheme()),
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                SettingsDrawerContent(
                    themeState = themeState,
                    onThemeSelected = viewModel::onThemeSelected,
                    onDarkModeToggle = viewModel::toggleDarkMode,
                    onDynamicToggle = viewModel::toggleDynamicColor,
                    onClose = { scope.launch { drawerState.close() } },
                )
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            if (canNavigateBack) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(Res.string.cd_navigate_back),
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = stringResource(Res.string.cd_open_settings),
                                )
                            }
                        },
                    )
                },
            ) { innerPadding ->
                Navigation(
                    navController = navController,
                    menuState = menuState,
                    onSubmenuSelected = viewModel::onSubmenuSelected,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }

    LaunchedEffect(menuState.selectedSubmenu) {
        menuState.selectedSubmenu?.let {
            viewModel.onSubmenuUnselected()
            navController.navigate(it.route)
        }
    }
}

@Composable
fun MainScreenContent(
    menuState: MenuState,
    onSubmenuSelected: (selected: ChartSubmenuItem) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        ChartGallery(
            menuState = menuState,
            onSubmenuSelected = onSubmenuSelected,
            versionLabel = "Charts: ${BuildConfig.CHARTS_VERSION}",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
        )
    }
}
