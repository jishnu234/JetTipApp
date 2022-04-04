package com.example.jettipapp.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.jettipapp.R


data class HomeScreenStates (
    val isValid: Boolean = false,
    val sliderValue: Float = 0f,
    var splitCount: Int = 1,
    val tipValue: Float = 0f,
    val perHead: Float = 0f,
)

@ExperimentalComposeUiApi
@Preview
@Composable
fun HomeScreen() {
    var states by remember {
        mutableStateOf(HomeScreenStates())
    }
    var textFieldState by remember {
        mutableStateOf(TextFieldValue())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = Color(0xFFE2D0E6),
            elevation = 12.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Person",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%.2f".format(states.perHead),
                        color = Color.Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFB9B8B8), RoundedCornerShape(15.dp)),
            backgroundColor = Color.White,
            shape = RoundedCornerShape(15.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter Bill") },
                    singleLine = true,
                    keyboardActions = KeyboardActions {
                        keyboardController?.hide()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) ,
                    leadingIcon = { Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.dollar_sign),
                        contentDescription = "Amount" ) },
                    value = textFieldState,
                    onValueChange = {
                        textFieldState = it
                        states = if(it.text.isNotBlank()) {

                            val perHead = (it.text.toFloat() + states.tipValue)/states.splitCount
                            states.copy(
                                isValid = true,
                                sliderValue = 0f,
                                tipValue = 0f,
                                perHead = perHead
                            )
                        }
                        else states.copy(isValid = false)

                    })
                if (states.isValid) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Split",
                            style = MaterialTheme.typography.subtitle1
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RoundedCard(icon = R.drawable.minus){
                                var splitMinus = states.splitCount
                            if (states.splitCount > 1)
                                splitMinus -= 1
                                states = states.copy(splitCount = splitMinus)
                                val perHead = (textFieldState.text.toFloat() +
                                            states.tipValue) / states.splitCount
                                states = states.copy(perHead = perHead)
                            }
                            Text(text = "${states.splitCount}")
                            RoundedCard(icon = R.drawable.plus){
                                var splitPlus = states.splitCount
                                if(splitPlus<20) splitPlus += 1
                            states = states.copy( splitCount = splitPlus)
                                val perHead = (textFieldState.text.toFloat() +
                                        states.tipValue) / states.splitCount
                                states = states.copy(perHead = perHead)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Tip")
                        Text(text = "%.2f".format(states.tipValue))
                    }
                    Text(text = "${(states.sliderValue * 100).toInt()} %")
                    Slider(
                        value = states.sliderValue,
                        onValueChange = {
                            val tipValue = textFieldState.text.toFloat() * it
                            val perHead =
                                (textFieldState.text.toFloat() + states.tipValue) / states.splitCount
                        states = states.copy(
                            sliderValue = it,
                            tipValue = tipValue,
                            perHead = perHead
                        )

                            Log.d("HomeScreen", "TipField: ${states.sliderValue}")
                        },
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF4BB8E9),
                            inactiveTrackColor = Color(0xFFB4AFAF),
                            activeTrackColor = Color(0xFFCFDFBD),
                        )
                    )
                }

            }

        }

    }
}

@Composable
fun RoundedCard(icon: Int = R.drawable.plus, changeSplitCount: (Int) -> Unit){
    Card(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable {
                changeSplitCount(icon)
            },
        backgroundColor = Color(0xFFCCCCCC),
        elevation = 8.dp,
        shape = CircleShape,
    ) {
        Icon(
            modifier = Modifier.padding(12.dp),
            painter = painterResource(id = icon),
            contentDescription = "Add icon" )
    }
}

