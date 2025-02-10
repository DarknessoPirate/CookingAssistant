import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import com.cookingassistant.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class TimerViewModel(context: Context) : ViewModel() {
    // Timer state
    var timeInMinutes by mutableStateOf(0)
    val timeRemainingInSeconds = MutableStateFlow<Int>(0)
    var isTimerRunning by mutableStateOf(false)
    val context : MutableStateFlow<Context?> = MutableStateFlow<Context?>(context)

    private val timerScope = kotlinx.coroutines.MainScope()
    private var job : Job? = null

    // Start or pause the timer
    fun toggleTimer() {
        if (isTimerRunning) {
            stopTimer()
        } else {
            if(timeRemainingInSeconds.value > 0)
                startTimer()
        }
    }

    fun createNotification(context: Context?) {
        if (context == null) {
            return
        }

        // Sprawdzenie uprawnień (API 33+ wymaga dodatkowych zgód na notyfikacje)
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Tworzenie kanału notyfikacji dla API 26+ (Oreo i nowsze)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Timer"
                val descriptionText = "Provides notification when time runs out"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(
                    "Timer_notifications",
                    name,
                    importance
                ).apply {
                    description = descriptionText
                }

                // Uzyskanie instancji NotificationManager
                val notificationManager =
                    context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }

            // Budowanie notyfikacji
            val notificationBuilder = NotificationCompat.Builder(context, "Timer_notifications")
                .setSmallIcon(R.drawable.projectlogo2kcircular) // Zmień na właściwy zasób
                .setContentTitle("It's time!")
                .setContentText("Timer got to 0!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            // Uzyskanie instancji NotificationManager
            val notificationManager =
                context.getSystemService(NotificationManager::class.java)

            // Wyświetlanie notyfikacji
            notificationManager.notify(1, notificationBuilder.build())
        } else {
            // Jeśli nie masz uprawnień, tutaj możesz zainicjować prośbę o nie
            // (jeśli ta funkcja jest w ViewModel, konieczne będzie zarządzanie uprawnieniami w aktywności/fragmencie)
        }
    }


    // Start the timer
    private fun startTimer() {
        if(isTimerRunning) return
        if (timeInMinutes <= 0) return // Do nothing if time is not set
        isTimerRunning = true
        job?.cancel()
        job = timerScope.launch {
            while (isTimerRunning && timeRemainingInSeconds.value > 0) {
                delay(1000)
                if (isActive && isTimerRunning) {
                    timeRemainingInSeconds.value -= 1
                }
            }
            if(isTimerRunning && timeRemainingInSeconds.value <= 0) {
                createNotification(context.value)
                job?.cancel()
            }
        }
    }

    // Stop the timer
    private fun stopTimer() {
        isTimerRunning = false
    }

    // Set the timer to a specific time when done on keyboard
    fun setTimerFromInput(minutes: String) {
        stopTimer()
        job?.cancel()
        timeInMinutes = minutes.toIntOrNull() ?: 0
        if (timeInMinutes > 0) {
            timeRemainingInSeconds.value = timeInMinutes * 60
            startTimer()
        }
    }
}
