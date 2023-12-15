package se.jzonite.teststartprinter

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.starmicronics.stario10.InterfaceType
import com.starmicronics.stario10.StarDeviceDiscoveryManager
import com.starmicronics.stario10.StarDeviceDiscoveryManagerFactory
import com.starmicronics.stario10.StarPrinter
import se.jzonite.teststartprinter.ui.theme.TestStartPrinterTheme

class MainActivity : ComponentActivity() {

    var deviceDiscoveryManager: StarDeviceDiscoveryManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    search()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED -> {
                search()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.BLUETOOTH_CONNECT) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
//                showInContextUI(...)
                requestPermissionLauncher.launch(
                    android.Manifest.permission.BLUETOOTH_CONNECT)
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    android.Manifest.permission.BLUETOOTH_CONNECT)
            }
        }


        setContent {
            TestStartPrinterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    fun search() {
        val interfaceTypes = mutableListOf<InterfaceType>()
        interfaceTypes += InterfaceType.Bluetooth
        interfaceTypes += InterfaceType.Usb
        interfaceTypes += InterfaceType.Lan
        deviceDiscoveryManager = StarDeviceDiscoveryManagerFactory.create(interfaceTypes, this)
        deviceDiscoveryManager?.callback = object : StarDeviceDiscoveryManager.Callback {
            override fun onDiscoveryFinished() {
                print("StarIOPrinter: onDiscoveryFinished")
            }

            override fun onPrinterFound(printer: StarPrinter) {
                println("StarIOPrinter: onPrinterFound: ${printer}")
            }
        }
        deviceDiscoveryManager?.startDiscovery()
    }
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        TestStartPrinterTheme {
            Greeting("Android")
        }
    }
}

