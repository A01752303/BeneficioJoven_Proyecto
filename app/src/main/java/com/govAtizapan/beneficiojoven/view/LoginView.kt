package com.govAtizapan.beneficiojoven.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens

@Composable
fun LoginView(modifier: Modifier = Modifier, navController: NavController) {

}

@Preview(showBackground = true)
@Composable
fun LoginPreview(modifier: Modifier = Modifier) {
    LoginView(navController = rememberNavController())
}