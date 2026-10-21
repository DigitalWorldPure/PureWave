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

package com.goldenankh.purewave.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass
) {
    val navController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isDrawerAvailable = currentRoute != SPLASH

    val isTablet by remember { derivedStateOf { windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact } }

    val selectedSampleIdState = rememberSaveable { mutableStateOf<Long?>(null) }
    val editSampleModeState = rememberSaveable { mutableStateOf(false) }

    val selectedCurveIdState = rememberSaveable { mutableStateOf<Long?>(null) }
    val editCurveModeState = rememberSaveable { mutableStateOf(false) }


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
                                popUpTo(navController.graph.findStartDestination().id) {
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
            startDestination = TRACKS,
            modifier = Modifier.fillMaxSize()
        ) {
            val openMenuAction: () -> Unit = {
                scope.launch {
                    drawerState.open()
                }
            }

            composable(TRACKS) {
                TracksScreen(
                    onMenuClick = openMenuAction
                )
            }




            composable(CURVES) {
                if (isTablet) {
                    CurvesTabletScreen(
                        editMode = editCurveModeState,
                        selectedId = selectedCurveIdState,
                        onMenuClick = openMenuAction,
                        windowSizeClass = windowSizeClass,
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
                    if (editCurveModeState.value) {
                        if (navController.currentDestination?.route != "${CURVE}?id={id}") {
                            navController.navigate(
                                "${CURVE}?id=${selectedCurveIdState.value}"
                            )
                        }
                    }
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
                        initiallyEditing = true,
                        editMode = editCurveModeState,
                        selectedId = selectedCurveIdState,
                        onInitialDetailBack = { navController.popBackStack() },
                        onMenuClick = openMenuAction,
                        windowSizeClass = windowSizeClass,
                        onEditCurve = { id ->
                            navController.navigate(
                                "${CURVE_EDITOR}?id=$id"
                            )
                        },
                    )
                } else if (editCurveModeState.value) {
                    CurveScreen(
                        curveId = id.takeIf { it >= 0L },
                        onBack = {
                            editCurveModeState.value = false
                            selectedCurveIdState.value = null
                            navController.popBackStack()
                        },
                        onEditCurve = { id ->
                            navController.navigate(
                                "${CURVE_EDITOR}?id=$id"
                            )
                        }
                    )
                }
                else {
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
                        editMode = editCurveModeState,
                        selectedId = selectedCurveIdState,
                        onMenuClick = openMenuAction,
                        windowSizeClass = windowSizeClass,
                        onEditCurve = { id ->
                            navController.navigate(
                                "${CURVE_EDITOR}?id=$id"
                            )
                        },
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
                        selectedId = selectedSampleIdState,
                        editMode = editSampleModeState,
                        onMenuClick = openMenuAction,
                        windowSizeClass = windowSizeClass
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
                    if (editSampleModeState.value) {
                        if (navController.currentDestination?.route != "${SAMPLE}?id={id}") {
                            navController.navigate(
                                "${SAMPLE}?id=${selectedSampleIdState.value}"
                            )
                        }
                    }
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
                    selectedSampleIdState.value = id.takeIf { it >= 0L }
                    editSampleModeState.value = true
                    SamplesTabletScreen(
                        initiallyEditing = true,
                        selectedId = selectedSampleIdState,
                        editMode = editSampleModeState,
                        onInitialDetailBack = { navController.popBackStack() },
                        onMenuClick = openMenuAction,
                        windowSizeClass = windowSizeClass
                    )
                } else if (editSampleModeState.value) {
                    SampleScreen(
                        sampleId = id.takeIf { it >= 0L },
                        onBack = {
                            editSampleModeState.value = false
                            selectedSampleIdState.value = null
                            navController.popBackStack()
                        },
                        onEditProgram = { sampleId ->
                            navController.navigate(
                                "${SAMPLE_EDIT_PROGRAM}?id=${sampleId ?: -1L}"
                            )
                        },
                        windowSizeClass = windowSizeClass
                    )
                }
                else {
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
                    selectedSampleIdState.value = id.takeIf { it >= 0L }
                    SamplesTabletScreen(
                        selectedId = selectedSampleIdState,
                        editMode = editSampleModeState,
                        onMenuClick = openMenuAction,
                        windowSizeClass = windowSizeClass
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
        }
    }
}