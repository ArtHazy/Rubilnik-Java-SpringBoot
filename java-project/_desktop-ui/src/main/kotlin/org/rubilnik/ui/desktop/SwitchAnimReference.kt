package org.rubilnik.ui.desktop

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
//import androidx.compose.material3.LocalTextStyle
//import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomThemeSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation for thumb position
    val thumbOffset by animateDpAsState(
        targetValue = if (isChecked) 80.dp else 0.dp,
        animationSpec = tween(durationMillis = 200)
    )

    // Track colors
    val trackColor = if (isChecked) Color(0xFF4285F4) else Color(0xFFE0E0E0)
    val thumbColor = Color.White

    // Text colors
    val leftTextColor = if (isChecked) Color.LightGray else Color.DarkGray
    val rightTextColor = if (isChecked) Color.DarkGray else Color.LightGray

    Box(
        modifier = modifier
            .width(150.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(trackColor)
            .clickable { onCheckedChange(!isChecked) }
    ) {
        // Thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(70.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(thumbColor)
                .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
        )

        // Left text (ON)
        Text(
            text = "ON",
            color = leftTextColor,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp),
            style = TextStyle(
                fontSize = 26.sp,
                fontFamily = FontFamily.Default // Replace with your font
            )
        )

        // Right text (OFF)
        Text(
            text = "OFF",
            color = rightTextColor,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            style = TextStyle(
                fontSize = 26.sp,
                fontFamily = FontFamily.Default // Replace with your font
            )
        )
    }
}

// Usage example
@Composable
fun SwitchDemo() {
    var isChecked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomThemeSwitch(
            isChecked = isChecked,
            onCheckedChange = { isChecked = it }
        )

        Text("Current state: ${if (isChecked) "ON" else "OFF"}")
    }
}