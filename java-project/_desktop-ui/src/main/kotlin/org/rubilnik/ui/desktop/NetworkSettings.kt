package org.rubilnik.ui.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.singleWindowApplication
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@Composable
fun networkSettings() {
    Column {
        var encryption by remember { mutableStateOf("WPA") }
        var ssid by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var qrstr by remember { mutableStateOf(Optional.of("")) }

        TextField(value = encryption, onValueChange = {encryption=it})
        TextField(value = ssid, onValueChange = {ssid=it})
        TextField(value = password, onValueChange = {password=it})
        Button(onClick = {
            qrstr=Optional.of("WIFI:T:${encryption};S:${ssid};P:${password};H:false;;")
            Globar.localNetworkConnectionString=qrstr
        }){ Text("save settings") }
        Text(qrstr.getOrNull().toString())

    }
}


