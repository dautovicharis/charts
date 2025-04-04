import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.github_url
import chartsproject.app.generated.resources.github_url_content_description
import chartsproject.app.generated.resources.ic_check
import chartsproject.app.generated.resources.ic_dark_mode
import chartsproject.app.generated.resources.ic_github
import chartsproject.app.generated.resources.themes_content_description
import io.github.dautovicharis.charts.app.BuildConfig
import io.github.dautovicharis.charts.app.ChartScreen
import io.github.dautovicharis.charts.app.ChartSubmenuItem
import io.github.dautovicharis.charts.app.Navigation
import io.github.dautovicharis.charts.app.ui.theme.AppTheme
import io.github.dautovicharis.charts.app.ui.theme.LocalHasDynamicColorFeature
import io.github.dautovicharis.charts.app.ui.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    KoinContext {
        val themeState = viewModel.themeState.collectAsStateWithLifecycle()
        val menuState = viewModel.menuState.collectAsStateWithLifecycle()
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val canNavigateBack =
            currentBackStackEntry?.destination?.route != ChartScreen.MainScreen.ROUTE

        AppTheme(
            theme = themeState.value.selectedTheme,
            useDynamicColors = themeState.value.useDynamicColors,
            darkTheme = viewModel.resolveDarkTheme(isSystemInDarkTheme())
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            if (canNavigateBack) {
                                IconButton(onClick = { navController.popBackStack() }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    )
                }
            ) {
                Navigation(
                    navController = navController,
                    themeState = themeState,
                    menuState = menuState,
                    onThemeSelected = { viewModel.onThemeSelected(it) },
                    onSubmenuSelected = { viewModel.onSubmenuSelected(it) },
                    onMenuToggle = { viewModel.onMenuToggle(it) },
                    onDarkModeToggle = { viewModel.toggleDarkMode() },
                    onDynamicToggle = { viewModel.toggleDynamicColor() }
                )
            }
        }

        LaunchedEffect(menuState.value.selectedSubmenu) {
            menuState.value.selectedSubmenu?.let {
                viewModel.onSubmenuUnselected()
                navController.navigate(it.route)
            }
        }
    }
}

@Composable
fun MainScreenContent(
    themeState: State<ThemesState>,
    menuState: State<MenuState>,
    onThemeSelected: (Theme) -> Unit,
    onSubmenuSelected: (selected: ChartSubmenuItem) -> Unit,
    onMenuToggle: (index: Int) -> Unit,
    onDarkModeToggle: () -> Unit,
    onDynamicToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AddGithubIcon(themeState, onDarkModeToggle)

        AddThemes(
            themeState = themeState,
            onDynamicToggle = onDynamicToggle
        ) {
            onThemeSelected(it)
        }

        AddMenuItems(
            menuState = menuState,
            onSubmenuSelected = onSubmenuSelected,
            onMenuToggle = onMenuToggle
        )
    }
}

@Composable
fun AddMenuItems(
    menuState: State<MenuState>,
    onSubmenuSelected: (selected: ChartSubmenuItem) -> Unit,
    onMenuToggle: (index: Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(modifier = Modifier.widthIn(min = 150.dp, max = 300.dp)) {
            items(items = menuState.value.menuItems) { chartItem ->
                val itemIndex = menuState.value.menuItems.indexOf(chartItem)
                ChartTypeItem(
                    item = chartItem,
                    onClick = {
                        onMenuToggle(itemIndex)
                    }
                )
                AnimatedVisibility(
                    visible = menuState.value.expandedMenuIndex == itemIndex,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    SubMenuItems(
                        chartItem = chartItem,
                        menuState = menuState,
                        onSubmenuClick = {
                            onSubmenuSelected(it)
                        }
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Version: ${BuildConfig.DEMO_VERSION_NAME}\n" +
                                "Build: ${BuildConfig.DEMO_VERSION_CODE}\n" +
                                "Charts: ${BuildConfig.CHARTS_VERSION}",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}


@Composable
private fun SubMenuItems(
    chartItem: ChartScreen,
    menuState: State<MenuState>,
    onSubmenuClick: (ChartSubmenuItem) -> Unit
) {
    Column(modifier = Modifier.padding(15.dp)) {
        chartItem.submenus.forEach { submenuItem ->
            val isSelected = submenuItem == menuState.value.selectedSubmenu

            TextButton(
                onClick = {
                    onSubmenuClick(submenuItem)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = when (isSelected) {
                        true -> MaterialTheme.colorScheme.primary
                        false -> MaterialTheme.colorScheme.onSurface
                    }
                )
            ) {
                Text(
                    text = stringResource(submenuItem.title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (isSelected) {
                        true -> MaterialTheme.colorScheme.primary
                        false -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}


@Composable
fun AddGithubIcon(
    themeState: State<ThemesState>,
    onDarkModeToggle: () -> Unit
) {
    val githubUrl = stringResource(Res.string.github_url)
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(20.dp)
                .clickable { onDarkModeToggle() }
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_dark_mode),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = themeState.value.darkMode.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(
            modifier = Modifier
                .padding(20.dp)
                .size(35.dp),
            onClick = {
                uriHandler.openUri(githubUrl)
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_github),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = stringResource(Res.string.github_url_content_description)
            )
        }
    }
}

@Composable
fun AddThemes(
    themeState: State<ThemesState>,
    onDynamicToggle: () -> Unit,
    onThemeSelected: (selected: Theme) -> Unit
) {
    val hasDynamicColors = LocalHasDynamicColorFeature.current
    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        if (themeState.value.useDynamicColors.not()) {
            items(items = themeState.value.themes) { theme ->
                val isSelectedTheme = themeState.value.selectedTheme == theme
                FilledIconToggleButton(
                    checked = isSelectedTheme,
                    onCheckedChange = {
                        onThemeSelected(theme)
                    },
                    modifier = Modifier.padding(10.dp),
                    content = {
                        if (isSelectedTheme) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_check),
                                contentDescription = stringResource(Res.string.themes_content_description),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        containerColor = theme.colors.md_theme_light_primary
                    ),
                    shape = CircleShape
                )
            }
        }

        if (hasDynamicColors) {
            item {
                FilledTonalButton(
                    onClick = onDynamicToggle,
                    modifier = Modifier
                        .padding(10.dp),
                    shape = CircleShape,
                ) {
                    Text(
                        text = "Dynamic: ${themeState.value.useDynamicColors}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ChartTypeItem(item: ChartScreen, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(item.icon),
                contentDescription = stringResource(item.title)
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(item.title),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
