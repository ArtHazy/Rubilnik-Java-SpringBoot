package org.rubilnik.ui.desktop

import java.io.File
import java.net.NetworkInterface
import java.net.ServerSocket
import java.util.*
import kotlin.jvm.optionals.getOrNull

fun myStartServices(auth_service_port: String, room_service_port: String){
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

fun myStopServices(){
    auth_service_process?.destroy()
    room_service_process?.destroy()
}
fun myGetFreePort():Int {
    ServerSocket(0).use {
            socket -> return socket.localPort
    }
}


// Operating system specific
data class WifiNetData (val encryption:String, val ssid:String, val password:String)
fun myGetCurrentWifiNetData():WifiNetData {
    var os_name = System.getProperty("os.name")
    val logFile = File.createTempFile("logFile",".txt")

    val logFilePath = logFile.absolutePath //"C:\\Users\\artem\\OneDrive\\Desktop\\logFile.txt"
    println("logfile-path: ${logFilePath}")
    // if Windows
    if (os_name.contains("Win")){
        val scriptTempFile = File.createTempFile("wifi-win",".ps1")
        val scriptPath = scriptTempFile.absolutePath
        Global::class.java.getResourceAsStream("/native-scripts/wifi-win.ps1").bufferedReader().use { reader ->
            scriptTempFile.writer().use { writer -> reader.copyTo(writer) }
        }
        val command = "Start-Process powershell.exe -ArgumentList '-File \"${scriptPath}\" -logFile \"${logFilePath}\"' -Verb RunAs"
        val process = ProcessBuilder("powershell.exe","-Command",command)
        val result = process.redirectOutput(ProcessBuilder.Redirect.PIPE).start().waitFor()
        Thread.sleep(3000)
    }
    // if MacOS
    else if (os_name.contains("Mac")) {
        println("   os: mac")
        val scriptTempFile = File.createTempFile("wifi-mac",".sh")
        val scriptPath = scriptTempFile.absolutePath
        Global::class.java.getResourceAsStream("/native-scripts/wifi-mac.sh").bufferedReader().use { reader ->
            scriptTempFile.writer().use { writer ->
                reader.copyTo(writer)
            }
        }
        //val command = listOf("sudo", scriptPath)// "sudo ${scriptPath}"
        //println("command: ${command}")
        val command = listOf("bash", scriptPath, logFilePath)
        val process = ProcessBuilder(command).start()
        val exitCode = process.waitFor()
//        val process = ProcessBuilder("sudo", "-S", scriptPath, logFilePath)
//        val result = process.inheritIO().start().waitFor()
        println("result ${exitCode}")
        Thread.sleep(3000)
    } else {
        println("undef OS")
        return WifiNetData("","","")
    }

    val lines = logFile.readLines()
    println("lines ${lines}")
    if (lines.size>=3){
        val ssid = lines[0]
        val password = lines[1]
        println("encryption: "+lines[2])
        var encryption = ""
        if (lines[2].contains("WPA")) encryption = "WPA"
        else if (lines[2].contains("WEP")) encryption = "WEP"
        else encryption = "nopass"
        return WifiNetData(encryption,ssid,password)
    }
    return WifiNetData("","","")
}


fun getLocalIP(): String {
    try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val iface = interfaces.nextElement()
            if (iface.isUp && !iface.isLoopback) {
                val interfaceAddresses = iface.interfaceAddresses
                for (address in interfaceAddresses) {
                    if (address.address.isSiteLocalAddress && address.address.hostAddress.contains("192.168.0")) return address.address.hostAddress // getCanonicalHostName() // "user_pc"
                }
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
    return "failed"
}