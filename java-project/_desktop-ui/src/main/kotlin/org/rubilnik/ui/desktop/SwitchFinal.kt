package org.rubilnik.ui.desktop

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication

import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.OffsetEffect
import androidx.compose.ui.graphics.vector.PathNode
import javax.swing.JDialog
import javax.swing.JOptionPane

@Composable
fun MySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = { JOptionPane.showMessageDialog(null,"switched to ${it}") },
    scale: Float = 1f
){
    val bg = loadSvgPainter( Global::class.java.getResourceAsStream("/drawable/switch-bg.svg"), LocalDensity.current )
    val thumb = loadSvgPainter( Global::class.java.getResourceAsStream("/drawable/custom-thumb.svg"), LocalDensity.current )

    var isOn by remember { mutableStateOf(checked) }
    var offset = if (isOn) Offset(64f, 8f) else Offset(8f, 8f)
    var offsetState = animateOffsetAsState(offset, tween(durationMillis = 500))

    Box(Modifier.size(106.dp*scale,50.dp*scale).clickable {
        isOn = !isOn
        onCheckedChange(isOn)
    }){
        Image(bg, "bg")

        Box(Modifier.size(34.dp*scale,34.dp*scale).offset(offsetState.value.x.dp*scale,offsetState.value.y.dp*scale)){
            Image(thumb,"thumb")
        }
    }
}

fun main() = singleWindowApplication (
    title = "Rubilnik Launcher",
    icon = BitmapPainter(useResource("icons/app-icon.png", ::loadImageBitmap))// ("app-icon.png") //BitmapPainter( loadImageBitmap() //File("./src/main/resources/app-icon.png").inputStream()) )
)   {
    MySwitch(true, {})
}