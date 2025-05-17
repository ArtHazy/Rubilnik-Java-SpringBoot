package org.rubilnik.ui.desktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication

@Composable
fun MySwitch(
    state: Boolean
){
    val bg = loadSvgPainter( Global::class.java.getResourceAsStream("/drawable/switch-bg.svg"), LocalDensity.current )
    val bg_on = loadSvgPainter( Global::class.java.getResourceAsStream("/drawable/switch-bg-on.svg"), LocalDensity.current )
    val bg_off = loadSvgPainter( Global::class.java.getResourceAsStream("/drawable/switch-bg-off.svg"), LocalDensity.current )
    val thumb = loadSvgPainter( Global::class.java.getResourceAsStream("/drawable/custom-thumb.svg"), LocalDensity.current )
    val thumb2 = loadSvgPainter( Global::class.java.getResourceAsStream("/drawable/custom-thumb-state.svg"), LocalDensity.current )

    Row {
        Box(modifier = Modifier.width(64.dp).height(64.dp)){
            Image(bg, "bg")
        }
        Box(modifier = Modifier.width(64.dp).height(64.dp)){
            Image(bg_on, "bg_on")
        }
        Box(modifier = Modifier.width(64.dp).height(64.dp)){
            Image(bg_off, "bg_off")
        }
        Box(modifier = Modifier.width(64.dp).height(64.dp)){
            Image(thumb, "thumb")
        }
        Box(modifier = Modifier.width(64.dp).height(64.dp)){
            Image(thumb2, "thumb2")
        }
    }
    Box(
        modifier = Modifier.width(64.dp).height(64.dp)
    ){
//        Image(bg_on, "bg_on")
//        Image(thumb, "thumb")
//        Image(bg, "bg")
    }
}
@Composable
fun MySwitchDemo(){
    MySwitch(true)
}


fun main() = singleWindowApplication (
    title = "Rubilnik Launcher",
    icon = BitmapPainter(useResource("icons/app-icon.png", ::loadImageBitmap))// ("app-icon.png") //BitmapPainter( loadImageBitmap() //File("./src/main/resources/app-icon.png").inputStream()) )
)   {
    MySwitchDemo()
}