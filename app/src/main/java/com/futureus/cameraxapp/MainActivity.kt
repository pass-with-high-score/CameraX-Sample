package com.futureus.cameraxapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.futureus.cameraxapp.ui.theme.CameraXAppTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.futureus.cameraxapp.presentation.camera.CameraScreen
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.rememberNavHostEngine
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseClient: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, 0)
        }

        enableEdgeToEdge()
        setContent {
            CameraXAppTheme {
                val navController = rememberNavController()
                val navHostEngine = rememberNavHostEngine()

                val newBackStackEntry by navController.currentBackStackEntryAsState()
                val route = newBackStackEntry?.destination?.route
                DestinationsNavHost(
                    navController = navController,
                    engine = navHostEngine,
                    navGraph = NavGraphs.root,
                    dependenciesContainerBuilder = {
                        dependency(this@MainActivity)
                    }
                )
            }
        }
    }

     fun arePermissionsGranted(): Boolean {
        return CAMERA_PERMISSION.all { permission ->
            ContextCompat.checkSelfPermission(
                applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        val CAMERA_PERMISSION = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}

