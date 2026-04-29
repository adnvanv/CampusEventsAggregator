package com.example.campusevents.ui.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campusevents.model.CampusEvent
import com.example.campusevents.ui.detail.EventDetailScreen
import com.example.campusevents.ui.feed.EventFeedScreen
import com.example.campusevents.ui.saved.SavedEventsScreen

@Composable
fun CampusEventsApp(
    viewModel: CampusEventsViewModel = viewModel(factory = CampusEventsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    val requestNotificationPermission = remember(context, notificationPermissionLauncher) {
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val onToggleSaved: (CampusEvent) -> Unit = { event ->
        if (event.id !in uiState.savedEventIds) {
            requestNotificationPermission()
        }
        viewModel.toggleSaved(event)
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = currentDestination?.route?.startsWith(DETAIL_ROUTE) != true

    Scaffold(
        topBar = {
            CampusEventsTopBar(
                currentDestination = currentDestination,
                savedCount = uiState.savedEventIds.size,
                onBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                CampusEventsBottomBar(
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FEED_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(FEED_ROUTE) {
                EventFeedScreen(
                    uiState = uiState,
                    onCategorySelected = viewModel::selectCategory,
                    onTagSelected = viewModel::selectTag,
                    onClearFilters = viewModel::clearFilters,
                    onEventSelected = { eventId ->
                        navController.navigate(detailRoute(eventId))
                    },
                    onToggleSaved = onToggleSaved
                )
            }

            composable(SAVED_ROUTE) {
                SavedEventsScreen(
                    uiState = uiState,
                    onEventSelected = { eventId ->
                        navController.navigate(detailRoute(eventId))
                    },
                    onToggleSaved = onToggleSaved
                )
            }

            composable(
                route = "$DETAIL_ROUTE/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { entry ->
                val eventId = entry.arguments?.getString("eventId").orEmpty()
                val event = uiState.allEvents.firstOrNull { it.id == eventId }
                if (event != null) {
                    EventDetailScreen(
                        event = event,
                        isSaved = event.id in uiState.savedEventIds,
                        contentPadding = PaddingValues(0.dp),
                        onToggleSaved = { onToggleSaved(event) }
                    )
                } else {
                    Text(
                        text = "This event is unavailable right now.",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampusEventsTopBar(
    currentDestination: NavDestination?,
    savedCount: Int,
    onBack: () -> Unit
) {
    val route = currentDestination?.route.orEmpty()
    val title = when {
        route.startsWith(DETAIL_ROUTE) -> "Event details"
        route == SAVED_ROUTE -> "Saved events"
        else -> "Campus events"
    }
    val subtitle = when {
        route.startsWith(DETAIL_ROUTE) -> "Event info and reminders"
        route == SAVED_ROUTE -> "$savedCount reminders tracked"
        else -> "Meetings, activities, and free food around campus"
    }

    TopAppBar(
        title = {
            androidx.compose.foundation.layout.Column {
                Text(text = title)
                Text(text = subtitle)
            }
        },
        navigationIcon = {
            if (route.startsWith(DETAIL_ROUTE)) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Go back"
                    )
                }
            }
        }
    )
}

@Composable
private fun CampusEventsBottomBar(
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val items = listOf(
        TopLevelDestination(FEED_ROUTE, "Feed", Icons.Filled.Event),
        TopLevelDestination(SAVED_ROUTE, "Saved", Icons.Filled.Bookmark)
    )

    NavigationBar {
        items.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination.isTopLevelDestinationInHierarchy(destination.route),
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) }
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { destination -> destination.route == route } == true
}

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private const val FEED_ROUTE = "feed"
private const val SAVED_ROUTE = "saved"
private const val DETAIL_ROUTE = "detail"

private fun detailRoute(eventId: String): String = "$DETAIL_ROUTE/$eventId"
