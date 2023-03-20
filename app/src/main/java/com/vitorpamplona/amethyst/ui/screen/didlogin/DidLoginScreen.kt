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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.MutableLiveData
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
import com.vitorpamplona.amethyst.ui.qrcode.DIDQrCodeScanner
import com.vitorpamplona.amethyst.ui.screen.loggedIn.AccountViewModel

@Composable
fun DidLoginScreen(accountViewModel: AccountStateViewModel, layoutInflater: LayoutInflater, intent: Intent, startingPage: String?) {
    val TAG = "wangran"
    val key = remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }
    val acceptedTerms = remember { mutableStateOf(false) }
    var termsAcceptanceIsRequired by remember { mutableStateOf("") }
    val uri = LocalUriHandler.current
    lateinit var barcodeView: DecoratedBarcodeView
    val text = MutableLiveData("")
    var wantNewDID by remember {
        mutableStateOf(false)
    }
    var entryMainScreen by remember {
        mutableStateOf(false)
    }

    if (wantNewDID)
        NewDIDView({ wantNewDID = false },{entryMainScreen = true})


    var dialogOpen by remember {
        mutableStateOf(false)
    }
    if(entryMainScreen){
//        val accountState by accountViewModel.accountContent.collectAsState()
        Log.d(TAG, "DidLoginScreen: "+entryMainScreen)
        AccountScreen(accountViewModel, startingPage)
    }
    if (dialogOpen){
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
            ScanQRCodeBox(barcodeLayoutView, it)
        }
//        Column(
//            modifier = Modifier
//                .background(MaterialTheme.colors.background)
//                .verticalScroll(rememberScrollState())
//                .fillMaxSize(),
//        ) {
////            // The first child is glued to the top.
////            // Hence we have nothing at the top, an empty box is used.
////            Box(modifier = Modifier.height(0.dp))
////
////            // The second child, this column, is centered vertically.
////            Column(
////                modifier = Modifier
////                    .padding(20.dp)
////                    .fillMaxSize(),
////                horizontalAlignment = Alignment.CenterHorizontally,
////            ) {
////                CloseButton(onCancel = {
////                    dialogOpen = false
////                })
////
////
////                Column(
////                    modifier = Modifier
////                        .padding(40.dp)
////                        .fillMaxSize(),
////                    horizontalAlignment = Alignment.CenterHorizontally,
////                ) {
////                    DIDQrCodeScanner(onScan = { result ->
////                        run {
////                            Log.d("TAG", "DidLoginScreen: " + result)
////                        }
////                    })
////                }
////            }
//
//
//        }
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

//            var showPassword by remember {
//                mutableStateOf(false)
//            }
//
//            OutlinedTextField(
//                value = key.value,
//                onValueChange = { key.value = it },
//                keyboardOptions = KeyboardOptions(
//                    autoCorrect = false,
//                    keyboardType = KeyboardType.Ascii,
//                    imeAction = ImeAction.Go
//                ),
//                placeholder = {
//                    Text(
//                        text = "nsec / npub / hex private key",
//                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
//                    )
//                },
//                trailingIcon = {
//                    IconButton(onClick = { showPassword = !showPassword }) {
//                        Icon(
//                            imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
//                            contentDescription = if (showPassword) "Show Password" else "Hide Password"
//                        )
//                    }
//                },
//                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
//                keyboardActions = KeyboardActions(
//                    onGo = {
//                        try {
//                            accountViewModel.login(key.value.text)
//                        } catch (e: Exception) {
//                            errorMessage = "Invalid key"
//                        }
//                    }
//                )
//            )
//            if (errorMessage.isNotBlank()) {
//                Text(
//                    text = errorMessage,
//                    color = MaterialTheme.colors.error,
//                    style = MaterialTheme.typography.caption
//                )
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Checkbox(
//                    checked = acceptedTerms.value,
//                    onCheckedChange = { acceptedTerms.value = it }
//                )
//
//                Text(text = "I accept the ")
//
//                ClickableText(
//                    text = AnnotatedString("terms of use"),
//                    onClick = { runCatching { uri.openUri("https://github.com/vitorpamplona/amethyst/blob/main/PRIVACY.md") } },
//                    style = LocalTextStyle.current.copy(color = MaterialTheme.colors.primary),
//                )
//            }
//
//            if (termsAcceptanceIsRequired.isNotBlank()) {
//                Text(
//                    text = termsAcceptanceIsRequired,
//                    color = MaterialTheme.colors.error,
//                    style = MaterialTheme.typography.caption
//                )
//            }

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            dialogOpen = true
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
fun ScanQRCodeBox(root: View, value: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
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
        }
    }
}