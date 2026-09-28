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

package com.goldenankh.purewave.ui.components.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.routing.Routes

@Composable
fun PureWaveDrawer(
    selectedRoute: String?,
    onNavigate: (String) -> Unit
) {
    val menuItems = listOf(
        DrawerMenuItem(
            route = Routes.TRACKS,
            title = R.string.menu_tracks,
            icon = R.drawable.ic_icon_tracks
        ),
        DrawerMenuItem(
            route = Routes.SAMPLES,
            title = R.string.menu_samples,
            icon = R.drawable.ic_icon_sample,
            badge = "5"
        ),
        DrawerMenuItem(
            route = Routes.CURVES,
            title = R.string.menu_curves,
            icon = R.drawable.ic_icon_curves,
            badge = "2"
        ),
        DrawerMenuItem(
            route = Routes.SETTINGS,
            title = R.string.menu_settings,
            icon = R.drawable.ic_icon_settings
        )
    )

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp),
        drawerContainerColor = Color(0xFFE0E0E0),
        drawerContentColor = Color.Black,
        drawerShape = MaterialTheme.shapes.extraSmall
    ) {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color(0xFFE0E0E0))
        ) {

            // =========================================================
            // LOGO
            // =========================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(top = 8.dp, start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo_gradient),
                    contentDescription = null
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFD6D6E0))
            )

            Spacer(Modifier.height(14.dp))

            // =========================================================
            // MENU ITEMS
            // =========================================================

            menuItems.forEach { item ->

                NavigationDrawerItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 5.dp
                        )
                        .height(64.dp),

                    label = {
                        Text(
                            text = stringResource(item.title),
                            fontSize = 20.sp,
                            color = Color(0xFF111111)
                        )
                    },

                    icon = {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = stringResource(item.title),
                            modifier = Modifier.width(28.dp),
                            tint = Color(0xFF111111)
                        )
                    },

                    badge = {
                        item.badge?.let { count ->
                            Text(
                                text = count,
                                fontSize = 16.sp,
                                color = Color(0xFF111111)
                            )
                        }
                    },

                    selected = selectedRoute == item.route,

                    onClick = {
                        onNavigate(item.route)
                    },

                    colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0x40373BE8),
                            unselectedContainerColor = Color.Transparent,
                            selectedIconColor = Color(0xFF111111),
                            unselectedIconColor = Color(0xFF111111),
                            selectedTextColor = Color(0xFF111111),
                            unselectedTextColor = Color(0xFF111111)
                        ),

                    shape = MaterialTheme.shapes.large
                )
            }
        }
    }
}

@Preview
@Composable
private fun PureWaveDrawerPreview() {
    PureWaveDrawer("tracks", {})
}