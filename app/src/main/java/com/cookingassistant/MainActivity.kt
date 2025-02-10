package com.cookingassistant

import TimerTool
import TimerViewModel
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cookingassistant.compose.AppTheme
import com.cookingassistant.data.ShoppingProducts
import com.cookingassistant.data.network.RetrofitClient
import com.cookingassistant.data.objects.ScreenControlManager
import com.cookingassistant.data.objects.ShakeDetector
import com.cookingassistant.data.repositories.TokenRepository
import com.cookingassistant.services.AuthService
import com.cookingassistant.services.RecipeService
import com.cookingassistant.services.ReviewService
import com.cookingassistant.services.UserService
import com.cookingassistant.ui.composables.topappbar.TopAppBar
import com.cookingassistant.ui.composables.topappbar.TopAppBarViewModel
import com.cookingassistant.ui.screens.RecipesList.RecipeList
import com.cookingassistant.ui.screens.RecipesList.RecipesListViewModel
import com.cookingassistant.ui.screens.editor.EditorScreen
import com.cookingassistant.ui.screens.editor.EditorScreenViewModel
import com.cookingassistant.ui.screens.editor.authorization.AuthorizationScreen
import com.cookingassistant.ui.screens.editor.authorization.AuthorizationScreenViewModel
import com.cookingassistant.ui.screens.home.HomeScreen
import com.cookingassistant.ui.screens.home.HomeScreenViewModel
import com.cookingassistant.ui.screens.home.LoginViewModel
import com.cookingassistant.ui.screens.login.LoginScreen
import com.cookingassistant.ui.screens.profile.ProfileScreen
import com.cookingassistant.ui.screens.profile.ProfileScreenViewModel
import com.cookingassistant.ui.screens.recipescreen.RecipeScreen
import com.cookingassistant.ui.screens.recipescreen.RecipeScreenViewModel
import com.cookingassistant.ui.screens.registration.RegistrationScreen
import com.cookingassistant.ui.screens.registration.RegistrationViewModel
import com.cookingassistant.ui.screens.reviews.ReviewList
import com.cookingassistant.ui.screens.reviews.ReviewViewModel
import com.cookingassistant.util.VoiceToTextParser
import java.io.File
import kotlin.math.sqrt


class MainActivity : ComponentActivity() {

    // Sensor gestures
    private lateinit var sensorManager : SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var sensorEventListener: SensorEventListener

    // Declare the permission request contract
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, proceed with your logic
            println("Permission granted")
        } else {
            // Permission denied, handle accordingly
            println("Permission denied")
        }
    }

    val voiceToTextParser by lazy {
        VoiceToTextParser(application)
    }

    private fun startSensorListener() {
        // Initialize the sensor event listener and the callback for accelerometer data
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    // Calculate the acceleration
                    val acceleration = sqrt(x * x + y * y + z * z)

                    // Set the threshold for shake detection
                    val shakeDetected = acceleration > 30

                    ShakeDetector.detectedShake.value = shakeDetected
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the sensor manager and accelerometer
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

        // Start the sensor listener
        startSensorListener()



        // Check and request permission if not already granted
        checkAndRequestPermission()

        //Retrofit HTTP Client creation (singleton)
        val tokenRepository = TokenRepository(applicationContext)
        val apiRepository = RetrofitClient(tokenRepository).retrofit
        // Create services
        val authService = AuthService(apiRepository)
        val userService = UserService(apiRepository)
        val reviewService = ReviewService(apiRepository)
        val recipeService = RecipeService(apiRepository)
        // Pdf directory
        val destinationDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        setContent {
            var canRecord by remember {
                mutableStateOf(false)
            }

            val recordAudioLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {isGranted ->
                    canRecord = isGranted
                }
            )
            LaunchedEffect(key1 = recordAudioLauncher) {
                recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            AppNavigator(authService,userService,reviewService,recipeService ,tokenRepository, destinationDir, voiceToTextParser) // inject services here
        }

        ShoppingProducts.loadProducts(this)
    }

    override fun onResume() {
        super.onResume()
        // Register the sensor listener to listen to accelerometer data
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the listener when the app goes to the background
        sensorManager.unregisterListener(sensorEventListener)
    }

    override fun onBackPressed() { //Deprecated but wbo asked?
        if (ScreenControlManager.activeTool == "") {
            if(ScreenControlManager.hasLoggedIn && ScreenControlManager.topAppBarViewModel.onAppTryExit()) {
                moveTaskToBack(true)
            } else {
                super.onBackPressed()
            }
        } else {
            ScreenControlManager.topAppBarViewModel.onDeselctTool()
        }
    }

    private fun checkAndRequestPermission() {
        val permissionsToRequest = mutableListOf<String>()
        // Powiadomienia (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        // Wibracje/dźwięki (Android 6+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.VIBRATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.VIBRATE)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) ==
                        PackageManager.PERMISSION_GRANTED -> {

                    println("Permission already granted")
                }
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES) -> {

                    println("Need permission to access images")
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
                else -> {

                    permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        } else {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted, proceed with file access
                    println("Permission already granted")

                }
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // Show rationale if necessary and request permission
                    println("Need permission to access storage")
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                else -> {
                    // Request the permission
                    permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
        // Poproś o brakujące uprawnienia
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                101
            )
        }
    }
}

@Composable
// modify this code to inject services
fun AppNavigator(authService: AuthService,userService: UserService,reviewService: ReviewService, recipeService: RecipeService, tokenRepository: TokenRepository, destinationDir: File, voiceToTextParser: VoiceToTextParser){
    val navController = rememberNavController()
    NavGraph(navController = navController,
        authService = authService,
        userService = userService,
        reviewService = reviewService,
        recipeService = recipeService,
        tokenRepository = tokenRepository,
        destinationDir = destinationDir,
        voiceToTextParser = voiceToTextParser
    )
}

@Composable
fun NavGraph(navController: NavHostController, authService: AuthService, userService: UserService,reviewService: ReviewService,recipeService: RecipeService, tokenRepository: TokenRepository, destinationDir: File, voiceToTextParser: VoiceToTextParser) {
    AppTheme(true) {
        // Control over the life cycle of viewmodels!!!
        val reviewViewModel = ReviewViewModel(reviewService)
        val rsvm = RecipeScreenViewModel(recipeService, userService, reviewService, navController, reviewViewModel)
        val esvm = EditorScreenViewModel(recipeService)
        val recipeListViewModel = RecipesListViewModel(recipeService,userService)
        val homeScreenViewModel = HomeScreenViewModel(recipeService, rsvm, navController)
        val pvm = ProfileScreenViewModel(userService,tokenRepository)
        val timerToolViewModel = TimerViewModel(LocalContext.current)
        val topBarViewModel = TopAppBarViewModel(recipeService, rsvm, navController, recipeListViewModel, voiceToTextParser,pvm,esvm,timerToolViewModel)

        val loginViewModel = LoginViewModel(authService, tokenRepository)
        ScreenControlManager.topAppBarViewModel=topBarViewModel

        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                // create viewModel and inject service
                // TODO: Implement factories later
                //val loginViewModel: LoginViewModel = ViewModelProvider(LoginViewModelFactory(userService))
                LoginScreen(navController, loginViewModel)
            }
            composable("home") {
                TopAppBar(topAppBarviewModel = topBarViewModel) {
                    HomeScreen(homeScreenViewModel)
                }
            }
            composable("test") {//For testing purposes
                //val reviewViewModel = ReviewViewModel(reviewService)
                //ReviewList(reviewViewModel)
            }

            composable("timer") {
                TopAppBar(topBarViewModel) {
                    TimerTool(timerToolViewModel)
                }
            }

            composable("recipeReviews") {
                TopAppBar(topAppBarviewModel = topBarViewModel) {
                    ReviewList(reviewViewModel)
                }
            }

            composable("recipeList") {
                TopAppBar(topAppBarviewModel = topBarViewModel) {
                    RecipeList(navController,rsvm,esvm,recipeListViewModel)
                }
            }
            composable("registration") {
                val registrationViewModel = RegistrationViewModel(authService)
                RegistrationScreen(navController, registrationViewModel)
            }
            composable("recipeScreen") {
                TopAppBar(topAppBarviewModel = topBarViewModel) {
                    RecipeScreen(rsvm, destinationDir)
                }
            }
            composable("editor"){
                TopAppBar(topAppBarviewModel = topBarViewModel) {
                    EditorScreen(navController, esvm)
                }
            }
            composable("profile"){
                TopAppBar(topAppBarviewModel = topBarViewModel) {
                    ProfileScreen(navController,recipeListViewModel,pvm,loginViewModel)
                }
            }
            composable("authorization"){
                val avm = AuthorizationScreenViewModel(userService)
                AuthorizationScreen(navController,avm)
            }
        }
    }
}

