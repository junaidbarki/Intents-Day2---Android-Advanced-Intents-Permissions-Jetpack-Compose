package com.example.intentsday2

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register activity result for taking a picture
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    Toast.makeText(this, "Image saved at: $imageUri", Toast.LENGTH_SHORT).show()

                }

            }
        // Initialize the image URI
        val imageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg")
        imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", imageFile)

        enableEdgeToEdge()
        setContent {

//            CaptureImageScreen { takePictureLauncher.launch(imageUri) }
            Surface(
                modifier = Modifier.fillMaxSize().systemBarsPadding()
            ) {
                val navController = rememberNavController()
                AppNavigation(navController)
            }

        }
    }
}

@Composable
fun CaptureImageScreen(onCaptureClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onCaptureClick,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Capture Image")
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("camera") { CameraScreen(navController) }
        composable("gallery") { GalleryScreen(navController) }
        composable("document") { DocumentPickerScreen(navController) }
        composable("permission") { PermissionScreen(navController) }
        // Define deep link navigation
        composable(
            "details/{itemId}",
            deepLinks = listOf(navDeepLink { uriPattern = "https://example.com/details/{itemId}" })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            DetailsScreen(itemId)
        }
    }
}
@Composable
fun DetailsScreen(itemId: String?) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Details Screen")
        Text(text = "Item ID: $itemId", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("camera") }) { Text("Open Camera") }
        Button(onClick = { navController.navigate("gallery") }) { Text("Pick Image from Gallery") }
        Button(onClick = { navController.navigate("document") }) { Text("Pick Document") }
        Button(onClick = { navController.navigate("permission") }) { Text("Request Permission") }
        Button(onClick = { navController.navigate("details/456") }) { Text("Implicit Deep Link") }
    }
}

@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Toast.makeText(context, "Image Captured: ${imageUri.value}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg")
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    imageUri.value = uri

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { takePictureLauncher.launch(uri) }) { Text("Capture Image") }
        Button(onClick = { navController.popBackStack() }) { Text("Back") }
    }
}

@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Toast.makeText(context, "Selected Image: $it", Toast.LENGTH_SHORT).show()
            }
        }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { pickImageLauncher.launch("image/*") }) { Text("Pick Image from Gallery") }
        Button(onClick = { navController.popBackStack() }) { Text("Back") }
    }
}

@Composable
fun DocumentPickerScreen(navController: NavController) {
    val context = LocalContext.current

    val pickFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                Toast.makeText(context, "Selected Document: $it", Toast.LENGTH_SHORT).show()
            }
        }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            pickFileLauncher.launch(arrayOf("*/*"))
        }) { Text("Pick a File") }
        Button(onClick = { navController.popBackStack() }) { Text("Back") }
    }
}

@Composable
fun PermissionScreen(navController: NavController) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
    }


    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }) { Text("Request Camera Permission") }
        Button(onClick = { navController.popBackStack() }) { Text("Back") }
    }
}


