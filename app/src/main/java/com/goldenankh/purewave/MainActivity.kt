/*
The MIT License

Copyright (c) 2026 DigitalWorldPure

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.goldenankh.purewave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.goldenankh.purewave.ui.components.drawer.PureWaveDrawer
import com.goldenankh.purewave.ui.routing.Routes.CURVES
import com.goldenankh.purewave.ui.routing.Routes.SAMPLES
import com.goldenankh.purewave.ui.routing.Routes.SETTINGS
import com.goldenankh.purewave.ui.routing.Routes.SPLASH
import com.goldenankh.purewave.ui.routing.Routes.TRACKS
import com.goldenankh.purewave.ui.screens.CurvesScreen
import com.goldenankh.purewave.ui.screens.SamplesScreen
import com.goldenankh.purewave.ui.screens.SettingsScreen
import com.goldenankh.purewave.ui.screens.SplashScreen
import com.goldenankh.purewave.ui.screens.TracksScreen
import com.goldenankh.purewave.ui.theme.PureWaveTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PureWaveTheme {

                val navController = rememberNavController()

                val drawerState = DrawerState(
                    initialValue = DrawerValue.Closed
                )

                val scope = rememberCoroutineScope()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val isDrawerAvailable = currentRoute != SPLASH

                val windowSizeClass = calculateWindowSizeClass(this)

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = isDrawerAvailable,
                    drawerContent = {
                        if (isDrawerAvailable) {
                            PureWaveDrawer(
                                selectedRoute = currentRoute,
                                onNavigate = { route ->

                                    scope.launch {
                                        drawerState.close()
                                    }

                                    if (currentRoute != route) {
                                        navController.navigate(route) {
                                            popUpTo(
                                                navController.graph
                                                    .findStartDestination().id
                                            ) {
                                                saveState = true
                                            }

                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = SPLASH,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val openMenuAction: () -> Unit = {
                            scope.launch {
                                drawerState.open()
                            }
                        }

                        composable(SPLASH) {
                            SplashScreen(
                                onFinished = {
                                    navController.navigate(TRACKS) {
                                        popUpTo(SPLASH) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                windowSizeClass
                            )
                        }

                        composable(TRACKS) {
                            TracksScreen(
                                onMenuClick = openMenuAction
                            )
                        }

                        composable(SAMPLES) {
                            SamplesScreen(
                                onMenuClick = openMenuAction
                            )
                        }

                        composable(CURVES) {
                            CurvesScreen(
                                onMenuClick = openMenuAction
                            )
                        }

                        composable(SETTINGS) {
                            SettingsScreen(
                                onMenuClick = openMenuAction
                            )
                        }
                    }
                }
            }
        }
    }
}