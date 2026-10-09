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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.goldenankh.domain.model.common.RGBColor
import com.goldenankh.purewave.ui.components.colorpicker.ColorPickerDialog
import com.goldenankh.purewave.ui.components.drawer.PureWaveDrawer
import com.goldenankh.purewave.ui.routing.Routes.CURVE
import com.goldenankh.purewave.ui.routing.Routes.CURVES
import com.goldenankh.purewave.ui.routing.Routes.CURVE_EDITOR
import com.goldenankh.purewave.ui.routing.Routes.SAMPLE
import com.goldenankh.purewave.ui.routing.Routes.SAMPLES
import com.goldenankh.purewave.ui.routing.Routes.SAMPLE_EDIT_PROGRAM
import com.goldenankh.purewave.ui.routing.Routes.SETTINGS
import com.goldenankh.purewave.ui.routing.Routes.SPLASH
import com.goldenankh.purewave.ui.routing.Routes.TRACKS
import com.goldenankh.purewave.ui.screens.curves.CurveEditorScreen
import com.goldenankh.purewave.ui.screens.curves.CurveScreen
import com.goldenankh.purewave.ui.screens.curves.CurvesScreen
import com.goldenankh.purewave.ui.screens.curves.CurvesTabletScreen
import com.goldenankh.purewave.ui.screens.samples.SampleEditProgramScreen
import com.goldenankh.purewave.ui.screens.samples.SampleScreen
import com.goldenankh.purewave.ui.screens.samples.SamplesScreen
import com.goldenankh.purewave.ui.screens.samples.SamplesTabletScreen
import com.goldenankh.purewave.ui.screens.SettingsScreen
import com.goldenankh.purewave.ui.screens.SplashScreen
import com.goldenankh.purewave.ui.screens.TracksScreen
import com.goldenankh.purewave.ui.theme.PureWaveTheme
import com.goldenankh.purewave.viewmodel.samples.SampleViewModel
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

                val isTablet = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

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




                        composable(CURVES) {
                            if (isTablet) {
                                CurvesTabletScreen(
                                    onMenuClick = openMenuAction,
                                    onEditCurve = { id ->
                                        navController.navigate(
                                            "${CURVE_EDITOR}?id=$id"
                                        )
                                    }
                                )
                            } else {
                                CurvesScreen(
                                    onMenuClick = openMenuAction,
                                    onAddClick = {
                                        navController.navigate("${CURVE}?id=-1")
                                    },
                                    onCurveClick = { id ->
                                        navController.navigate(
                                            "${CURVE_EDITOR}?id=$id"
                                        )
                                    },
                                    onEditClick = { id ->
                                        navController.navigate(
                                            "${CURVE}?id=$id"
                                        )
                                    }
                                )
                            }
                        }

                        composable(
                            route = "${CURVE}?id={id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                }
                            )
                        ) { entry ->
                            val id = entry.arguments?.getLong("id") ?: -1L
                            if (isTablet) {
                                CurvesTabletScreen(
                                    initialSelectedId = id.takeIf { it >= 0L },
                                    initiallyEditing = true,
                                    onInitialDetailBack = { navController.popBackStack() },
                                    onMenuClick = openMenuAction,
                                    onEditCurve = { id ->
                                        navController.navigate(
                                            "${CURVE_EDITOR}?id=$id"
                                        )
                                    }
                                )
                            } else {
                                CurveScreen(
                                    curveId = id.takeIf { it >= 0L },
                                    onBack = { navController.popBackStack() },
                                    onEditCurve = { id ->
                                        navController.navigate(
                                            "${CURVE_EDITOR}?id=$id"
                                        )
                                    }
                                )
                            }
                        }

                        composable(
                            route = "${CURVE_EDITOR}?id={id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                }
                            )
                        ) { entry ->
                            if (isTablet) {
                                CurvesTabletScreen(
                                    initialSelectedId = entry.arguments?.getLong("id"),
                                    onMenuClick = openMenuAction,
                                    onEditCurve = { id ->
                                        navController.navigate(
                                            "${CURVE_EDITOR}?id=$id"
                                        )
                                    }
                                )
                            } else {
                                CurveEditorScreen(
                                    curveId = entry.arguments?.getLong("id"),
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                        composable(SAMPLES) {
                            if (isTablet) {
                                SamplesTabletScreen(
                                    onMenuClick = openMenuAction,
                                    onDialogClick = { color ->
                                        navController.navigate(color)
                                    }
                                )
                            } else {
                                SamplesScreen(
                                    onMenuClick = openMenuAction,
                                    onAddClick = {
                                        navController.navigate(
                                            "${SAMPLE}?id=-1"
                                        )
                                    },
                                    onSampleClick = { id ->
                                        navController.navigate(
                                            "${SAMPLE}?id=$id"
                                        )
                                    },
                                    onEditClick = { id ->
                                        navController.navigate(
                                            "${SAMPLE}?id=$id"
                                        )
                                    }
                                )
                            }
                        }

                        composable(
                            route = "${SAMPLE}?id={id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                }
                            )
                        ) { entry ->
                            val id = entry.arguments?.getLong("id") ?: -1L
                            if (isTablet) {
                                SamplesTabletScreen(
                                    initialSelectedId = id.takeIf { it >= 0L },
                                    initiallyEditing = true,
                                    onInitialDetailBack = { navController.popBackStack() },
                                    onMenuClick = openMenuAction,
                                    onDialogClick = { color ->
                                        navController.navigate(color)
                                    }
                                )
                            } else {
                                SampleScreen(
                                    sampleId = id.takeIf { it >= 0L },
                                    onBack = { navController.popBackStack() },
                                    onEditProgram = { sampleId ->
                                        navController.navigate(
                                            "${SAMPLE_EDIT_PROGRAM}?id=${sampleId ?: -1L}"
                                        )
                                    },
                                    onDialogClick = { color ->
                                        navController.navigate(color)
                                    }
                                )
                            }
                        }

                        composable(
                            route = "${SAMPLE_EDIT_PROGRAM}?id={id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                }
                            )
                        ) { entry ->
                            val id = entry.arguments?.getLong("id") ?: -1L
                            if (isTablet) {
                                SamplesTabletScreen(
                                    initialSelectedId = id.takeIf { it >= 0L },
                                    onMenuClick = openMenuAction,
                                    onDialogClick = { color ->
                                        navController.navigate(color)
                                    }
                                )
                            } else {
                                SampleEditProgramScreen(
                                    sampleId = id.takeIf { it >= 0L },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                        composable(SETTINGS) {
                            SettingsScreen(
                                onMenuClick = openMenuAction
                            )
                        }

                        dialog<RGBColor> { backStackEntry ->
                            val initialColor = backStackEntry.toRoute<RGBColor>()
                            val parentEntry = remember(backStackEntry) {
                                val isSampleScreen = navController.visibleEntries.value.any { entry ->
                                    val route = entry.destination.route
                                    route != SAMPLES && route?.startsWith(SAMPLE) == true }
                                navController.getBackStackEntry(
                                    if (isSampleScreen)
                                        SAMPLE
                                    else
                                        SAMPLES
                                )
                            }
                            val sampleViewModel = hiltViewModel<SampleViewModel>(parentEntry)
                            ColorPickerDialog(
                                initialColor = with(initialColor) { Color(r, g, b) },
                                windowSizeClass = windowSizeClass,
                                onDismiss = { navController.popBackStack() },
                                onColorSelected = {
                                    sampleViewModel.setColor(RGBColor((255 * it.red).toInt(), (255 * it.green).toInt(), (255 * it.blue).toInt()))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}