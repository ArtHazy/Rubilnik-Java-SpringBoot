package org.rubilnik.ui.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import java.awt.Color
import java.awt.Label

val myTextFieldModifier = Modifier
    .width(256.dp)

@Composable
fun MyCentredRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        content()
    }
}
@Composable
fun MyTextField(
    modifier: Modifier = Modifier.width(256.dp),
    label: @Composable () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    MaterialTheme{

    }
    TextField(value, onValueChange, modifier, label = label, visualTransformation = visualTransformation, singleLine = true)
}
@Composable
fun myTextH1(
    value: String,
){
    Text(text = value, style = myTypography.h1 ,modifier = Modifier.padding(0.dp,8.dp))
}

@Composable
fun myTextSplit1(
    value: String,
){
    Text(text = value, style = myTypography.h1 ,modifier = Modifier.padding(0.dp,8.dp), color = myColorCyanLight)
}
@Composable
fun myTextSplit2(
    value: String,
){
    Text(text = value, style = myTypography.h1 ,modifier = Modifier.padding(0.dp,8.dp), color = myColorYellowLight)
}




@Composable
fun myTextH2(value: String){
    Text(value, style = myTypography.h2, modifier = Modifier.padding(0.dp,8.dp))
}

val myColorBGDark = Color(0xFF242424)
val myColorPrime = Color(0xFFB0B0B0)
val myColorLight = Color(0xFFD9D9D9)
val myColorYellowLight = Color(0xFFD6BF81)
val myColorCyanLight = Color(0xFF0FD4CD)
val myColorPrimeDark = Color(0xFF2C2C2C)

val myDarkColors = Colors(
    primary = myColorPrimeDark, // button bg
    primaryVariant = myColorYellowLight, // not used
    onPrimary = myColorLight,

    secondary = myColorYellowLight, // not used
    secondaryVariant = myColorYellowLight, // not used
    onSecondary = myColorYellowLight, // not used

    surface = myColorYellowLight, // not used
    onSurface = myColorLight, //myColorPrime, // textfield bg

    background = myColorBGDark,
    onBackground = myColorYellowLight, // not used

    error = myColorYellowLight, // not used
    onError = myColorYellowLight, // not used

    isLight = false
)
val myTextStyle = TextStyle(
    color = myColorLight
)

val russoOne = FontFamily(
    Font("fonts/RussoOne-Regular.ttf", FontWeight.Normal)
)
val robotoFlex = FontFamily(
    Font("fonts/RobotoFlex-VariableFont.ttf", FontWeight.Normal)
)

val myTypography = Typography(
    defaultFontFamily = FontFamily.Default,
    h1 = TextStyle(color = myColorCyanLight, fontSize = 36.sp, fontWeight = FontWeight.Bold, fontFamily = russoOne),
    h2 = TextStyle(color = myColorYellowLight, fontFamily = russoOne), //color = myColorYellowLight),
    h3 = myTextStyle,
    h4 = myTextStyle,
    h5 = TextStyle(color = myColorLight, fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = robotoFlex),
    h6 = myTextStyle,
    subtitle1 = myTextStyle,
    subtitle2 = myTextStyle,
    body1 = TextStyle(color = myColorLight, fontWeight = FontWeight.Normal, fontSize = 16.sp, letterSpacing = 0.5.sp, fontFamily = robotoFlex), // textfield
    body2 = myTextStyle,
    button = TextStyle.Default.copy( fontFamily = robotoFlex ),
    caption = TextStyle.Default.copy( color = myColorLight, fontFamily = robotoFlex ), // textfield caption
    overline = myTextStyle,
)

@Composable
fun MyMaterialTheme(
    content: @Composable () -> Unit
){
    MaterialTheme(
        colors = myDarkColors,
        typography = myTypography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}