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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun NavBar(modifier: Modifier = Modifier, vm: BookViewModel = viewModel()) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(Color.LightGray),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home Button"
            )
        }

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(Color.LightGray),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favourite Button"
            )
        }

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(Color.LightGray),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.bookmark_border_icon),
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