package com.example.eventcountdown.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventcountdown.R
import com.example.eventcountdown.presentation.EventViewModel
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
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = R.string.nav_drawer_label_1)) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("home") { popUpTo("home") }
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = R.string.nav_drawer_label_2)) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("settings")
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Inside the ModalDrawerSheet in HomeScreen
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = R.string.nav_drawer_label_3),) },
                        icon = { Icon(Icons.Default.Check, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("pastEvents")
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = R.string.nav_drawer_label_4)) },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_about_us),
                                contentDescription = null
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("about")
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Sign Out") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_logout_24), // Replace with your sign-out icon resource
                                contentDescription = null
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onSignOut()
                        },
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
                            text = stringResource(id = R.string.app_name),
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
