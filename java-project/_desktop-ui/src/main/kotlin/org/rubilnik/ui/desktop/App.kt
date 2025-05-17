package org.rubilnik.ui.desktop

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.window.singleWindowApplication
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import java.awt.Dimension

// for "by remember"
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.URI
import java.util.Optional


//

var auth_service_process: Process? = null
var room_service_process: Process? = null

object Global {
    var lanQrString: Optional<String> = Optional.empty<String>();
}

fun main() = singleWindowApplication (
    title = "Rubilnik Launcher",
    icon = BitmapPainter(useResource("icons/app-icon.png", ::loadImageBitmap))// ("app-icon.png") //BitmapPainter( loadImageBitmap() //File("./src/main/resources/app-icon.png").inputStream()) )
)   {
    var web_ui_service_port by remember { mutableStateOf("0") }
    var auth_service_port by remember { mutableStateOf("null") }
    var room_service_port by remember { mutableStateOf("null") }

    var isStarted by remember { mutableStateOf(false) }

    var encryption by remember { mutableStateOf("WPA") }
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    window.minimumSize = Dimension(300,300)
    window.maximumSize = Dimension(450,450)
    window.size = window.maximumSize



    MyMaterialTheme{
        Column (Modifier.fillMaxSize().background(myColorBGDark), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            MyCentredRow {
                myTextSplit1("RUBIL")
                myTextSplit2("NIK")
            }
            Spacer(Modifier.size(16.dp))
            MyCentredRow {
                Switch(
                    checked = isStarted,
                    onCheckedChange = { newState ->
                        isStarted = newState
                        if (isStarted==true){
                            auth_service_port = myGetFreePort().toString()
                            room_service_port = myGetFreePort().toString()
                            web_ui_service_port = auth_service_port+""
                            myStartServices(auth_service_port,room_service_port, WifiNetData(encryption,ssid,password))
                        } else {
                            myStopServices()
                            auth_service_port="null"
                            room_service_port="null"
                            web_ui_service_port="0"
                        }
                    }
                )
//                Spacer(Modifier.size(8.dp))
            }
            AnimatedVisibility(isStarted){
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    //TextField(enabled = false, label = {Text("web-ui-service-port")}, singleLine = true, value = web_ui_service_port, onValueChange = { web_ui_service_port = it }, modifier = myTextFieldModifier )
                    MyCentredRow {
                        Text("started on port:", style =  myTypography.h5)
                        Text(web_ui_service_port, style = myTypography.h5, color = myColorYellowLight)
                    }
                    MyCentredRow {
                        Button(onClick = {
                            if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(URI("http://localhost:${web_ui_service_port}"))
                        }) {
                            Text("Open")
                        }
                    }
                }
            }
            AnimatedVisibility(!isStarted){
                networkSettings()
            }
        }
        Text("v0.0.0-alpha")
    }
}