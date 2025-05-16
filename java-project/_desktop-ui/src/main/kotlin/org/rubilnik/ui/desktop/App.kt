package org.rubilnik.ui.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.ui.window.singleWindowApplication
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.awt.Dimension

// for "by remember"
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.ServerSocket
import java.net.URI
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

//

var auth_service_process: Process? = null
var room_service_process: Process? = null

object Global {
    var lanQrString: Optional<String> = Optional.empty<String>();
}

val textFieldModifier = Modifier
    .width(256.dp)

fun main() = singleWindowApplication (
    title = "Rubilnik Launcher",
    icon = BitmapPainter(useResource("icons/app-icon.png", ::loadImageBitmap))// ("app-icon.png") //BitmapPainter( loadImageBitmap() //File("./src/main/resources/app-icon.png").inputStream()) )
)   {
    var web_ui_service_port by remember { mutableStateOf("0") }
    var auth_service_port by remember { mutableStateOf("null") }
    var room_service_port by remember { mutableStateOf("null") }

    var encryption by remember { mutableStateOf("WPA") }
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    window.minimumSize = Dimension(300,300)
    window.maximumSize = Dimension(450,450)
    window.setSize(window.maximumSize)

    MaterialTheme{
        Column (Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            Column () {
                TextField(enabled = false, label = {Text("web-ui-service-port")}, singleLine = true, value = web_ui_service_port, onValueChange = { web_ui_service_port = it }, modifier = textFieldModifier )
//                Spacer(Modifier.size(8.dp))
//                TextField(label = {Text("auth-service-port")}, singleLine = true, value = auth_service_port, onValueChange = { auth_service_port = it })
//                Spacer(Modifier.size(8.dp))
//                TextField(label = {Text("room-service-port")}, singleLine = true, value = room_service_port, onValueChange = { room_service_port = it })

            }
            Spacer(Modifier.size(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick= {
                    auth_service_port = getFreePort().toString()
                    room_service_port = getFreePort().toString()
                    web_ui_service_port = auth_service_port+""

                    startServices(auth_service_port,room_service_port, WifiNetData(encryption,ssid,password))

                }){
                    Text("Start")
                }
                Spacer(Modifier.size(8.dp))
                Button(onClick= {
                    stopServices()
                    auth_service_port="null"
                    room_service_port="null"
                    web_ui_service_port="0"
                }){
                    Text("Stop")
                }
    //            Switch(onCheckedChange = {  })
            }
//            Column {
//                Text("auth_service_port: ${auth_service_port}")
//                Text("room_service_port: ${room_service_port}")
//            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(URI("http://localhost:${web_ui_service_port}"))
                }) {
                    Text("Open")
                }
            }
            networkSettings()
//            Text(Global.lanQrString.getOrNull().toString())
        }
    }
}

fun startServices(auth_service_port: String, room_service_port: String, netData: WifiNetData){
    // auth-service
    // org.rubilnik.auth_service.App.main(arrayOf("--server.port=8083","--rubilnik.def-usr.name=admin", "--rubilnik.def-usr.email=admin", "--rubilnik.def-usr.password=admin"))
    val jarStreamAuth = object {}.javaClass.getResourceAsStream("/jars/_auth-service.jar")
    if (jarStreamAuth==null){
        println("_auth-service.jar not found")
    } else {
        // copy to temp file
        val tempJar = kotlin.io.path.createTempFile("_auth-service-temp",".jar").toFile()
        jarStreamAuth.use { input->tempJar.outputStream().use { output->input.copyTo(output) } }
        val command = mutableListOf("java","-jar",tempJar.absolutePath)+arrayOf("--server.port=${auth_service_port}","--rubilnik.def-usr.name=admin", "--rubilnik.def-usr.email=admin", "--rubilnik.def-usr.password=admin","--rubilnik.lan.qr-string=${Global.lanQrString.getOrNull()}", "\"--rubilnik.lan.roomPort=${room_service_port}")
        auth_service_process = ProcessBuilder(command).inheritIO().start()
    }
//    Thread.sleep(1000)
    val jarStreamRoom = object {}.javaClass.getResourceAsStream("/jars/_room-service.jar")
    if (jarStreamRoom==null){
        println("_auth-service.jar not found")
    } else {
        // copy to temp file
        val tempJar = kotlin.io.path.createTempFile("_room-service-temp",".jar").toFile()
        jarStreamRoom.use { input->tempJar.outputStream().use { output->input.copyTo(output) } }
        val command = mutableListOf("java","-jar",tempJar.absolutePath)+arrayOf("--server.port=${room_service_port}","--rubilnik.auth-service-url=http://localhost:${auth_service_port}")
        room_service_process = ProcessBuilder(command).inheritIO().start()
    }
}

fun stopServices(){
    auth_service_process?.destroy()
    room_service_process?.destroy()
}
fun getFreePort():Int {
    ServerSocket(0).use {
        socket -> return socket.localPort
    }
}