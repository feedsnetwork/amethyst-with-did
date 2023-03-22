package com.vitorpamplona.amethyst.ui.screen

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.vitorpamplona.amethyst.R
import com.vitorpamplona.amethyst.buttons.NewNoteButton
import com.vitorpamplona.amethyst.ui.MainActivity
import com.vitorpamplona.amethyst.ui.actions.CloseButton
import com.vitorpamplona.amethyst.ui.actions.NewDIDView
import com.vitorpamplona.amethyst.ui.actions.RestoreDIDView
import com.vitorpamplona.amethyst.ui.actions.RestoreDIDViewModel
import com.vitorpamplona.amethyst.ui.qrcode.DIDQrCodeScanner
import com.vitorpamplona.amethyst.ui.screen.loggedIn.AccountViewModel

@Composable
fun DidLoginScreen(accountViewModel: AccountStateViewModel, layoutInflater: LayoutInflater, intent: Intent, startingPage: String?) {
    val restoreDIDViewModel: RestoreDIDViewModel = viewModel()
    val TAG = "wangran"
    val key = remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }
    val acceptedTerms = remember { mutableStateOf(false) }
    var termsAcceptanceIsRequired by remember { mutableStateOf("") }
//    var mnemonic = ""
    val uri = LocalUriHandler.current
    lateinit var barcodeView: DecoratedBarcodeView
    val text = MutableLiveData("")
    var wantNewDID by remember {
        mutableStateOf(false)
    }
    var showScanner by remember {
        mutableStateOf(false)
    }
    var entryMainScreen by remember {
        mutableStateOf(false)
    }

    var restoreDID by remember {
        mutableStateOf(false)
    }

    if (wantNewDID)
        NewDIDView({ wantNewDID = false },{entryMainScreen = true})

    if (restoreDID)
        RestoreDIDView (restoreDIDViewModel, { restoreDID = false })


    if(entryMainScreen){
//        val accountState by accountViewModel.accountContent.collectAsState()
        Log.d(TAG, "DidLoginScreen: "+entryMainScreen)
        AccountScreen(accountViewModel, startingPage)
    }
    if (showScanner){
        val barcodeLayoutView = layoutInflater.inflate(R.layout.layout, null)
        barcodeView = barcodeLayoutView.findViewById(R.id.barcode_scanner)
        val formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.initializeFromIntent(intent)
        val callback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if (result.text == null || result.text == text.value) {
                    return
                }
                text.value = result.text
            }
        }
        barcodeView.decodeContinuous(callback)
        barcodeView.resume()
        val state = text.observeAsState()
        state.value?.let {
            ScanQRCodeBox(barcodeLayoutView, it,
                onCloseScanner = {
                    Log.d(TAG, "DidLoginScreen: close");
                    showScanner = false;
                    barcodeView.pause()
                },
                onFinish ={
                    restoreDIDViewModel.mnemonic = it
                    showScanner = false
                    restoreDID = true
                    barcodeView.pause()
                    Log.d(TAG, "DidLoginScreen: "+restoreDIDViewModel.mnemonic)
                })
        }
    }else if (!entryMainScreen){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // The first child is glued to the top.
            // Hence we have nothing at the top, an empty box is used.
            Box(modifier = Modifier.height(0.dp))

            // The second child, this column, is centered vertically.
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Image(
                    painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Inside
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(text = "Web3 社交网络")

                Spacer(modifier = Modifier.height(40.dp))

                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            showScanner = true
                        },
                        shape = RoundedCornerShape(35.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults
                            .buttonColors(
                                backgroundColor = if (acceptedTerms.value) Color(999999) else Color.Blue
                            )
                    ) {
                        Text(text = "导入Elastos DID",color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            wantNewDID = true
                        },
                        shape = RoundedCornerShape(35.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults
                            .buttonColors(
                                backgroundColor = if (acceptedTerms.value) Color(999999) else Color.Blue
                            )
                    ) {
                        Text(text = "新人登录",color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            if (!acceptedTerms.value) {
                                termsAcceptanceIsRequired = "Acceptance of terms is required"
                            }

                            if (key.value.text.isBlank()) {
                                errorMessage = "Key is required"
                            }

                            if (acceptedTerms.value && key.value.text.isNotBlank()) {
                                try {
                                    accountViewModel.login(key.value.text)
                                } catch (e: Exception) {
                                    errorMessage = "Invalid key"
                                }
                            }
                        },
                        shape = RoundedCornerShape(35.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults
                            .buttonColors(
                                backgroundColor = if (acceptedTerms.value) Color(999999) else Color.Blue
                            ),
                    ) {
                        Text(text = "了解更多",color = Color.White)
                    }
                }
            }

            // The last child is glued to the bottom.
//        ClickableText(
//            text = AnnotatedString("Generate a new key"),
//            modifier = Modifier
//                .padding(20.dp)
//                .fillMaxWidth(),
//            onClick = {
//                if (acceptedTerms.value) {
//                    accountViewModel.newKey()
//                } else {
//                    termsAcceptanceIsRequired = "Acceptance of terms is required"
//                }
//            },
//            style = TextStyle(
//                fontSize = 14.sp,
//                textDecoration = TextDecoration.Underline,
//                color = MaterialTheme.colors.primary,
//                textAlign = TextAlign.Center
//            )
//        )
        }
    }
    fun initBarcodeView(){

    }
}

@Composable
fun ScanQRCodeBox(root: View, value: String, onCloseScanner: () -> Unit = {},onFinish: (scannerResult: String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = {
                    onCloseScanner()
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults
                    .buttonColors(
                        backgroundColor = Color.Gray
                    )
            ) {
                Text(text = "close", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
            }
            AndroidView(modifier = Modifier.fillMaxSize(),
                factory = {
                    root
                })
            if (value.isNotBlank()) {
                Log.d("wangran", "ScanQRCodeBox: "+value)

                Text(
                    modifier = Modifier.padding(16.dp),
                    text = value,
                    color = Color.White,
                    style = MaterialTheme.typography.h4
                )
                onFinish(value)
            }
        }
    }
}