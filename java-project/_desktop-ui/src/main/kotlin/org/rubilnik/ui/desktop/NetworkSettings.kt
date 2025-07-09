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

        Text(bundle.getString("network_settings"), style = myTypography.h5, modifier = Modifier.padding(8.dp))
        MyTextField(label = { Text(bundle.getString("encryption")) }, value = encryption, onValueChange = {encryption=it; updQrString(encryption,ssid,password) })
        MyTextField(label = { Text(bundle.getString("name")) }, value = ssid, onValueChange = {ssid=it; updQrString(encryption,ssid,password)})
        MyTextField(label = { Text(bundle.getString("password")) }, value = password, onValueChange = {password=it; updQrString(encryption,ssid,password)}, visualTransformation = PasswordVisualTransformation())
        MyCentredRow(modifier = Modifier.fillMaxWidth()){
            Button(onClick = {
                val (encryption_, ssid_, password_) = myGetCurrentWifiNetData()
                println("ESP: ${encryption_}, ${ssid_}, ${password_}")
                encryption=encryption_
                ssid=ssid_
                password=password_
                updQrString(encryption,ssid,password)
            }) {
                Text(bundle.getString("use_current_network"))
            }
        }
    }
}

fun updQrString(encryption: String, ssid: String, password: String){
    Global.lanQrString=Optional.of("WIFI:T:${encryption};S:${ssid};P:${password};H:false;;")
}