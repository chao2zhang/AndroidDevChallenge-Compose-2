/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(
        color = MaterialTheme.colors.background
    ) {
        var totalTime by remember { mutableStateOf(0) }
        var countingDown by remember { mutableStateOf(false) }
        if (countingDown) {
            TimingScreen(totalTime = totalTime) {
                countingDown = false
            }
        } else {
            SetupScreen { totalDuration ->
                totalTime = totalDuration
                countingDown = true
            }
        }
    }
}

@Composable
fun TimingScreen(
    totalTime: Int,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var countingDown by remember { mutableStateOf(false) }
        var finished by remember { mutableStateOf(false) }
        val countDownTime by animateIntAsState(
            targetValue = if (countingDown) 1 else totalTime,
            animationSpec = tween(durationMillis = totalTime * 1000, easing = LinearEasing),
            finishedListener = {
                finished = true
            }
        )
        // Hack: Use a fixed key to run launch effect only once
        LaunchedEffect(0) {
            countingDown = true
        }
        Text(
            text = if (finished) "Time is up \uD83C\uDF89" else renderTime(countDownTime),
            fontSize = 60.sp
        )
        Row {
            Text(
                text = "❌️️",
                fontSize = 60.sp,
                modifier = Modifier
                    .clickable { onStop() }
            )
        }
    }
}

fun renderTime(durationSeconds: Int): String {
    val seconds = durationSeconds % 60
    val minutes = (durationSeconds / 60) % 60
    val hours = (durationSeconds / 3600)
    return "${hours}h ${minutes}m ${seconds}s"
}

@Composable
fun SetupScreen(onStart: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (totalHours, setTotalHours) = remember { mutableStateOf(0) }
        val (totalMinutes, setTotalMinutes) = remember { mutableStateOf(0) }
        val (totalSeconds, setTotalSeconds) = remember { mutableStateOf(0) }
        Row {
            DurationColumn(
                totalDuration = totalHours,
                setTotalDuration = setTotalHours,
                scale = "h",
                range = IntRange(0, 23)
            )
            DurationColumn(
                totalDuration = totalMinutes,
                setTotalDuration = setTotalMinutes,
                scale = "m",
                range = IntRange(0, 59)
            )
            DurationColumn(
                totalDuration = totalSeconds,
                setTotalDuration = setTotalSeconds,
                scale = "s",
                range = IntRange(0, 59)
            )
        }
        Text(
            text = "▶️",
            fontSize = 60.sp,
            modifier = Modifier
                .clickable { onStart(totalHours * 3600 + totalMinutes * 60 + totalSeconds) }
        )
    }
}

@Composable
fun DurationColumn(
    totalDuration: Int,
    setTotalDuration: (Int) -> Unit,
    scale: String,
    range: IntRange
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "+",
            fontSize = 48.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable { setTotalDuration(totalDuration.incrementWithinRange(range)) }
                .width(120.dp)
        )
        Text(
            fontSize = 60.sp,
            text = "$totalDuration$scale",
            textAlign = TextAlign.Center,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = "-",
            fontSize = 48.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable { setTotalDuration(totalDuration.decrementWithinRange(range)) }
                .width(120.dp)
        )
    }
}

fun Int.incrementWithinRange(range: IntRange) = if (this == range.last) {
    range.first
} else {
    this + 1
}

fun Int.decrementWithinRange(range: IntRange) = if (this == range.first) {
    range.last
} else {
    this - 1
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
