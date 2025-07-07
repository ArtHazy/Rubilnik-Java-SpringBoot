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
import java.io.IOException
import java.net.ServerSocket
import java.net.URI
import java.util.Locale
import java.util.Optional
import java.util.ResourceBundle


//




val locale_en = Locale("en","EN")
val locale_ru = Locale("ru","RU")
val locale =  if (Locale.getDefault().language.equals("ru")) locale_ru else locale_en

val bundle = ResourceBundle.getBundle("text", locale, ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES))

object Global {
    var lanQrString: Optional<String> = Optional.empty<String>();
//    val bundle: ResourceBundle =
}

var auth_service_process: Process? = null
var room_service_process: Process? = null
fun main() = singleWindowApplication (
    title = bundle.getString("Rubilnik_Launcher"),
    icon = BitmapPainter(useResource("icons/app-icon.png", ::loadImageBitmap))// ("app-icon.png") //BitmapPainter( loadImageBitmap() //File("./src/main/resources/app-icon.png").inputStream()) )
)   {
    var web_ui_service_port by remember { mutableStateOf("null") }
    var auth_service_port by remember { mutableStateOf("null") }
    var room_service_port by remember { mutableStateOf("null") }
    var isStarted by remember { mutableStateOf(false) }
//    var encryption by remember { mutableStateOf("") }
//    var ssid by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }

    window.minimumSize = Dimension(300,300)
    window.maximumSize = Dimension(450,450)
    window.size = window.maximumSize

    MyMaterialTheme{
        Column (Modifier.fillMaxSize().background(myColorBGDark), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            MyCentredRow {
                myTextSplit1(bundle.getString("RUBIL"))
                myTextSplit2(bundle.getString("NIK"))
            }
            Spacer(Modifier.size(16.dp))
            MyCentredRow {
                MySwitch(isStarted, { newState ->
                    isStarted = newState
                    if (isStarted==true){
                        // TODO remove debug
                        try {
                            ServerSocket(8081).use {  }
                            ServerSocket(8082).use {  }
                            auth_service_port = "8081"
                            room_service_port = "8082"
                        } catch (e:IOException) {
                            auth_service_port = myGetFreePort().toString()
                            room_service_port = myGetFreePort().toString()
                        }
                        web_ui_service_port = auth_service_port+""
                        myStartServices(auth_service_port,room_service_port)
                    } else {
                        myStopServices()
                        auth_service_port="null"
                        room_service_port="null"
                        web_ui_service_port="0"
                    }
                }, 0.7f)
//                Spacer(Modifier.size(8.dp))
            }
            AnimatedVisibility(isStarted){
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    //TextField(enabled = false, label = {Text("web-ui-service-port")}, singleLine = true, value = web_ui_service_port, onValueChange = { web_ui_service_port = it }, modifier = myTextFieldModifier )
                    MyCentredRow {
                        Text(bundle.getString("started_on_port"), style =  myTypography.h5, modifier = Modifier.padding(8.dp))
                        Text(web_ui_service_port, style = myTypography.h5, color = myColorYellowLight)
                    }
                    MyCentredRow {
                        Button(onClick = {
                            if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(URI("http://${getLocalIP()}:${web_ui_service_port}"))
                        }) {
                            Text(bundle.getString("open"))
                        }
                    }
                }
            }
            AnimatedVisibility(!isStarted){
                networkSettings()
            }
        }
        Text("v1.0.0-alpha")
    }
}