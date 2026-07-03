package it.unipi.di.sam.moveit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.unipi.di.sam.moveit.ui.theme.MoveItTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(onBack: () -> Unit) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impostazioni") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Text(
                text = "Tema",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp),
            ) {
                RadioButton(onClick = { /*TODO*/ }, selected = true)
                Spacer(modifier = Modifier.width(8.dp))
                Text( modifier = Modifier.align(Alignment.CenterVertically), text = "tema scuro", style = MaterialTheme.typography.bodyLarge)
            }

            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp),
            ) {
                RadioButton(onClick = { /*TODO*/ }, selected = true)
                Spacer(modifier = Modifier.width(8.dp))
                Text( modifier = Modifier.align(Alignment.CenterVertically), text = "tema scuro", style = MaterialTheme.typography.bodyLarge)
            }

            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp),
            ) {
                RadioButton(onClick = { /*TODO*/ }, selected = true)
                Spacer(modifier = Modifier.width(8.dp))
                Text( modifier = Modifier.align(Alignment.CenterVertically), text = "tema chiaro", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingScreenPreview() {
    MoveItTheme() {
        SettingScreen(
            onBack = {}
        )
    }

}