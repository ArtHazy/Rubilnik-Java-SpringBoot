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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import java.io.File
import java.util.Optional

@Composable
fun networkSettings() {
    Column {
        var encryption by remember { mutableStateOf("WPA") }
        var ssid by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var qrstr by remember { mutableStateOf(Optional.of("")) }

        TextField(label = { Text("encryption") }, value = encryption, onValueChange = {encryption=it})
        TextField(label = { Text("name") }, value = ssid, onValueChange = {ssid=it})
        TextField(label = { Text("password") }, value = password, onValueChange = {password=it}, visualTransformation = PasswordVisualTransformation())
        Button(onClick = {
            val (encryption_, ssid_, password_) = getCurrentWifiNetData()
            encryption=encryption_
            ssid=ssid_
            password=password_

            qrstr=Optional.of("WIFI:T:${encryption};S:${ssid};P:${password};H:false;;")
            Global.lanQrString=qrstr

        }){ Text("Use current network") }
//        Text(qrstr.getOrNull().toString())

    }
}

// Operating system specific
data class WifiNetData (val encryption:String, val ssid:String, val password:String)
fun getCurrentWifiNetData():WifiNetData {
    println("func")
    var os_name = System.getProperty("os.name")
    val logFile = File.createTempFile("logFile",".txt")
    val logFilePath = logFile.absolutePath //"C:\\Users\\artem\\OneDrive\\Desktop\\logFile.txt"
    println("logFilePath ${logFilePath}")
    // if Windows
    if (os_name.contains("Win")){
        println("windows")
//        val scriptURL = Global::class.java.getResource("native-scripts/wifi-win.ps1") //Paths.get("src","main","resources","native-scripts","wifi-win.ps1").toAbsolutePath().toString()
//        val logFileURL = Global::class.java.getResource("native-scripts/logFile.txt") //Paths.get("src","main","resources", "native-scripts", "logFile.txt").toAbsolutePath().toString()
////        val logFile = File(logFilePath.path)
//        val scriptPath = Paths.get(scriptURL.toURI()).toAbsolutePath()
//        val logFilePath = Paths.get(logFileURL.toURI()).toAbsolutePath()
        val scriptTempFile = File.createTempFile("wifi-win",".ps1")
        val scriptPath = scriptTempFile.absolutePath
        println("scriptPath ${scriptPath}")
//        Global::class.java.getResourceAsStream("native-scripts/wifi-win.ps1").bufferedReader().use { reader ->
//        object {}.javaClass.getResourceAsStream()
        Global::class.java.getResourceAsStream("/native-scripts/wifi-win.ps1").bufferedReader().use { reader ->
            scriptTempFile.writer().use { writer ->
                reader.copyTo(writer)
            }
        }
//        val process = ProcessBuilder(("powershell.exe -ExecutionPolicy Bypass -File "+scriptPath).split(" "))
        val command = "Start-Process powershell.exe -ArgumentList '-File \"${scriptPath}\" -logFile \"${logFilePath}\"' -Verb RunAs"
        println("command ${command}")
        val process = ProcessBuilder("powershell.exe","-Command",command)
        val result = process.redirectOutput(ProcessBuilder.Redirect.PIPE).start().waitFor()
        println("result ${result}")
        Thread.sleep(1000)
//        println("ssid ${ssid}\npassword ${password}")
    }
    // if MacOS
    else if (os_name.contains("Mac")) { // TODO
        println("mac")
        val scriptTempFile = File.createTempFile("wifi-mac",".sh")
        val scriptPath = scriptTempFile.absolutePath
        Global::class.java.getResourceAsStream("/native-scripts/wifi-mac.sh").bufferedReader().use { reader ->
            scriptTempFile.writer().use { writer ->
                reader.copyTo(writer)
            }
        }
        val command = "sudo ${scriptPath}"
        println("command: ${command}")
        val process = ProcessBuilder("powershell.exe","-Command",command)
        val result = process.redirectOutput(ProcessBuilder.Redirect.PIPE).start().waitFor()
        println("result ${result}")
        Thread.sleep(1000)
    } else {
        println("undef OS")
        return WifiNetData("","","")
    }

    val lines = logFile.readLines()
    val ssid = lines[0]
    val password = lines[1]
    var encryption = ""
    if (lines[2].contains("WPA")) encryption = "WPA"
    else if (lines[2].contains("WEP")) encryption = "WEP"
    else encryption = "nopass"
    return WifiNetData(encryption,ssid,password)
}



