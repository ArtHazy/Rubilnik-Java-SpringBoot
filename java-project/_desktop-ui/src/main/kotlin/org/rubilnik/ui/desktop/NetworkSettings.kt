package org.rubilnik.ui.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import java.util.Optional

@Composable
fun networkSettings() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        var encryption by remember { mutableStateOf("") }
        var ssid by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var qrstr by remember { mutableStateOf(Optional.of("")) }

//        Text("Network settings", )

        Text("Network settings", style = myTypography.h5, modifier = Modifier.padding(8.dp))
        MyTextField(label = { Text("encryption") }, value = encryption, onValueChange = {encryption=it})
        MyTextField(label = { Text("name") }, value = ssid, onValueChange = {ssid=it})
        MyTextField(label = { Text("password") }, value = password, onValueChange = {password=it}, visualTransformation = PasswordVisualTransformation())
        MyCentredRow(modifier = Modifier.fillMaxWidth()){
            Button(onClick = {
                val (encryption_, ssid_, password_) = myGetCurrentWifiNetData()
                encryption=encryption_
                ssid=ssid_
                password=password_
                qrstr=Optional.of("WIFI:T:${encryption};S:${ssid};P:${password};H:false;;")
                Global.lanQrString=qrstr
            }) {
                Text("Use current network")
            }
        }
    }
}