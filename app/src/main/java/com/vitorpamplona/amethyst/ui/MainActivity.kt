package com.vitorpamplona.amethyst.ui

import android.Manifest
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import com.vitorpamplona.amethyst.LocalPreferences
import com.vitorpamplona.amethyst.ServiceManager
import com.vitorpamplona.amethyst.service.Nip19
import com.vitorpamplona.amethyst.service.relays.Client
import com.vitorpamplona.amethyst.ui.screen.AccountScreen
import com.vitorpamplona.amethyst.ui.screen.AccountStateViewModel
import com.vitorpamplona.amethyst.ui.theme.AmethystTheme

class MainActivity : ComponentActivity() {
  private val requestPermission =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//      if (isGranted) {
//        barcodeView.resume()
//      }
    }



//  fun pauseBarcodeView(){
//    barcodeView.pause()
//  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")

    val nip19 = Nip19().uriToRoute(intent?.data?.toString())
    val startingPage = when (nip19?.type) {
      Nip19.Type.USER -> "User/${nip19.hex}"
      Nip19.Type.NOTE -> "Note/${nip19.hex}"
      else -> null
    }

    Coil.setImageLoader {
      ImageLoader.Builder(this).components {
        if (SDK_INT >= 28) {
          add(ImageDecoderDecoder.Factory())
        } else {
          add(GifDecoder.Factory())
        }
        add(SvgDecoder.Factory())
      }
        .respectCacheHeaders(false)
        .build()
    }

    setContent {
      AmethystTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          Log.d("wangran", "onCreate: applicationContext.dataDir.absoluteFile "+applicationContext.getExternalFilesDir(null))
          var didStorePathFile = applicationContext.getExternalFilesDir(null)

          val accountViewModel: AccountStateViewModel = viewModel {
            AccountStateViewModel(LocalPreferences(applicationContext), didStorePathFile)
          }

          if (didStorePathFile != null){
            AccountScreen(accountStateViewModel = accountViewModel, layoutInflater, intent, startingPage = startingPage, didStorePathFile)
          }else{
            didStorePathFile =  Environment.getExternalStorageDirectory();
            AccountScreen(accountStateViewModel = accountViewModel, layoutInflater, intent, startingPage = startingPage, didStorePathFile)
          }
        }
      }
    }

    Client.lenient = true
  }

  override fun onResume() {
    super.onResume()
    requestPermission.launch(Manifest.permission.CAMERA)
    requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    ServiceManager.start()
  }

  override fun onPause() {
    ServiceManager.pause()
//    barcodeView.pause()
    super.onPause()
  }


}
