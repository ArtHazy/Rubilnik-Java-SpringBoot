package org.rubilnik.ui.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.window.singleWindowApplication
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import java.io.File

fun main() = singleWindowApplication {
    MaterialTheme{
        Column {
            Button(onClick={
                // auth-service
                // org.rubilnik.auth_service.App.main(arrayOf("--server.port=8083","--rubilnik.def-usr.name=admin", "--rubilnik.def-usr.email=admin", "--rubilnik.def-usr.password=admin"))
                val jarStream = object {}.javaClass.getResourceAsStream("/_auth-service.jar")
                if (jarStream==null){
                    println("_auth-service.jar not found")
                } else {
                    // copy to temp file
                    val tempJar = kotlin.io.path.createTempFile("_auth-service-temp",".jar").toFile()
                    jarStream.use { input->tempJar.outputStream().use { output->input.copyTo(output) } }
                    var command = mutableListOf("java","-jar",tempJar.absolutePath)+arrayOf("--server.port=8083","--rubilnik.def-usr.name=admin", "--rubilnik.def-usr.email=admin", "--rubilnik.def-usr.password=admin")
                    ProcessBuilder(command).inheritIO().start()
                }
            }){
                Text("Start auth-service")
            }
            Button(onClick={
                // room-service
                // org.rubilnik.room_service.App.main(arrayOf("--server.port=8084","--rubilnik.auth-service-url=http://localhost:8083"))
                val jarStream = object {}.javaClass.getResourceAsStream("/_room-service.jar")
                if (jarStream==null){
                    println("_auth-service.jar not found")
                } else {
                    // copy to temp file
                    val tempJar = kotlin.io.path.createTempFile("_room-service-temp",".jar").toFile()
                    jarStream.use { input->tempJar.outputStream().use { output->input.copyTo(output) } }
                    var command = mutableListOf("java","-jar",tempJar.absolutePath)+arrayOf("--server.port=8084","--rubilnik.auth-service-url=http://localhost:8083")
                    ProcessBuilder(command).inheritIO().start()
                }

            }){
                Text("Start room-service")
            }
        }
    }
}