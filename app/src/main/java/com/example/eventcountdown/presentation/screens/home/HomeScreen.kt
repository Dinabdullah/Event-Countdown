package com.example.eventcountdown.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventcountdown.R
import com.example.eventcountdown.presentation.activity.EventViewModel
import com.example.eventcountdown.presentation.componants.CombinedEventList
import com.example.eventcountdown.presentation.componants.EmptyStateView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: EventViewModel,
    onSignOut: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val events by viewModel.events.collectAsState()
    val holidays by viewModel.holidays.collectAsState()
    val now = remember { System.currentTimeMillis() }
    var currentTime by remember { mutableStateOf(now) }
    val isAddingHolidays by viewModel.isAddingHolidays.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val scrollState = rememberLazyListState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Drawer Header
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            "Eventat",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Navigation Items
                    NavigationDrawerItem(
                        label = { Text("Home") },
                        icon  = { Icon(Icons.Default.Home, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("home") { popUpTo("home") }
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    NavigationDrawerItem(
                        label    = { Text("Settings") },
                        icon     = { Icon(Icons.Default.Settings, contentDescription = null) },
                        selected = false,
                        onClick  = {
                            scope.launch { drawerState.close() }
                            navController.navigate("settings")
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )


                    NavigationDrawerItem(
                        label = { Text("Sign Out") },
                        icon  = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onSignOut()
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = MaterialTheme.colorScheme.surface,
                            unselectedTextColor      = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    title = {
                            Text(
                                text = "Eventat",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    ),
                    actions = {}
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("addEvent") },
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(16.dp)
                        .shadow(8.dp, shape = CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (events.isEmpty() && holidays.isEmpty()) {
                    EmptyStateView(modifier = Modifier.padding(padding))
                } else {
                    CombinedEventList(
                        events = events,
                        holidays = holidays,
                        currentTime = currentTime,
                        onEventClick = { navController.navigate("countdownEvent/${it.id}") },
                        onEdit = { navController.navigate("updateEvent/${it.id}") },
                        onDelete = viewModel::deleteEvent,
                        scrollState = scrollState,
                        modifier = Modifier.padding(padding)
                    )
                }

                if (isAddingHolidays) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}