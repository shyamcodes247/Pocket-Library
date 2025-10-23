package com.example.pocket_library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue



@Composable
fun NavBar(modifier: Modifier = Modifier, vm: BookViewModel = viewModel()) {

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = {vm.screen = 0},
            colors = ButtonDefaults.buttonColors(Color.LightGray),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = if (vm.screen == 0) Icons.Default.Home else Icons.Outlined.Home,
                contentDescription = "Home Button"
            )
        }

        Button(
            onClick = { vm.screen = 1 },
            colors = ButtonDefaults.buttonColors(Color.LightGray),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = if (vm.screen == 1) painterResource(R.drawable.favourite_icon) else painterResource(R.drawable.favourite_outline_icon) ,
                contentDescription = "Favourite Button"
            )
        }

        Button(
            onClick = { vm.screen = 2 },
            colors = ButtonDefaults.buttonColors(Color.LightGray),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = if (vm.screen == 2) painterResource(R.drawable.bookmark_icon) else painterResource(R.drawable.bookmark_border_icon),
                contentDescription = "Favourite Button"
            )
        }
    }

}

@Preview
@Composable
fun NavBarPreview() {
    val vm: BookViewModel = viewModel()
    NavBar(modifier = Modifier, vm)
}