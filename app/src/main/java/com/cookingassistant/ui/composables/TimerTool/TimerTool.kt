import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun TimerTool(viewModel: TimerViewModel = TimerViewModel(LocalContext.current)) {
    var inputMinutes by remember { mutableStateOf("") }
    val isRunning = viewModel.isTimerRunning
    val timeRemaining by viewModel.timeRemainingInSeconds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Display remaining time in minutes and seconds
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .clip(RoundedCornerShape(50))
            .clickable { viewModel.toggleTimer() }) {
            Box(modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .fillMaxSize()
                .align(Alignment.Center))
            Text(
                text = "${timeRemaining / 60}m : ${timeRemaining % 60}s",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 50.sp
            )
            Icon(imageVector = if(isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = "play", modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(60.dp), tint = MaterialTheme.colorScheme.primary)
        }

        // Numeric TextField to input minutes
        OutlinedTextField(
            value = inputMinutes,
            onValueChange = { newInput ->
                // Allow only numeric input
                if (newInput.all { it.isDigit() } || newInput.isEmpty()) {
                    inputMinutes = newInput
                }
            },
            label = { Text("Enter minutes") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Set the timer when "Done" is pressed
                    viewModel.setTimerFromInput(inputMinutes)
                }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.setTimerFromInput(inputMinutes) },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            Text("Start", color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Start/Pause Button
        Text("Protip: click timer to pause / unpause", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    TimerTool()
}
