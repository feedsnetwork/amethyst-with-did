package com.vitorpamplona.amethyst.ui.screen

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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
import com.vitorpamplona.amethyst.ui.actions.NewDIDView
import com.vitorpamplona.amethyst.ui.actions.RestoreDIDView
import com.vitorpamplona.amethyst.ui.actions.RestoreDIDViewModel

@Composable
fun DidLoginScreen(accountViewModel: AccountStateViewModel, layoutInflater: LayoutInflater, intent: Intent, startingPage: String?) {
    val restoreDIDViewModel: RestoreDIDViewModel = viewModel()
    val TAG = "wangran"
    val key = remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }
    val acceptedTerms = remember { mutableStateOf(false) }
    var termsAcceptanceIsRequired by remember { mutableStateOf("") }
    val uri = LocalUriHandler.current
    lateinit var barcodeView: DecoratedBarcodeView
    val text = MutableLiveData("")
    var hideAll by remember {
        mutableStateOf(false)
    }
    var wantNewDID by remember {
        mutableStateOf(false)
    }
    var showScanner by remember {
        mutableStateOf(false)
    }

    var restoreDID by remember {
        mutableStateOf(false)
    }

    if (wantNewDID){
        NewDIDView(onFinish = {type, didString ->
            run {
                if (type == 1) {
                    accountViewModel.login(didString)
                    hideAll = true
                }
                wantNewDID = false
            }
        })
    }


    if (restoreDID)
        RestoreDIDView (restoreDIDViewModel, onFinish = {type,didString ->
            run {
                if (type == 1) {
                    accountViewModel.login(didString)
                    hideAll = true
                }
                restoreDID = false
            }
        })


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
    }else if (!hideAll){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xff161C24)),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Box(modifier = Modifier.fillMaxSize()){
                    Image(
                        painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "Web3 社交网络" ,
                        fontSize = TextUnit(22f, TextUnitType.Sp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(0.dp, 0.dp, 0.dp, 40.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
            ) {
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xff161C24)),
                    color = Color(0xff323B45),
                    shape = RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp),
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(45.dp, 0.dp, 45.dp, 0.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                        ) {
                            Text(
                                text = "登录" ,
                                color = Color.White,
                                fontSize = TextUnit(21f, TextUnitType.Sp),
                                modifier = Modifier.align(Alignment.BottomCenter),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                                .weight(1f),
                        ) {
                            Text(
                                text = "请选择应用登录方式",
                                color = Color.LightGray,
                                fontSize = TextUnit(14f, TextUnitType.Sp),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(0.dp, 0.dp, 0.dp, 20.dp)
                                .weight(1f),
                        ) {
                            TextButton(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(40))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xff7624FE),
                                                Color(0xff368BFF),
                                            )
                                        )
                                    ),
                                onClick = {
                                    showScanner = true
                                }
                            ) {
                                Text(
                                    text = "导入Elastos DID",
                                    modifier = Modifier.padding(5.dp),
                                    color = Color.White,
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .padding(0.dp, 0.dp, 0.dp, 20.dp)
                        ) {
                            TextButton(
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(2.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xff7624FE),
                                            Color(0xff368BFF),
                                        )
                                    )
                                ),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth(),

                                onClick = {
                                    wantNewDID = true
                                }
                            ){
                                Text(
                                    text = "新人登录",
                                    modifier = Modifier.padding(5.dp),
                                    color = Color.White,
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(0.dp, 0.dp, 0.dp, 25.dp)
                                .weight(1f),
                        ) {
                            TextButton(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth(),

                                onClick = {}
                            ){
                                Text(
                                    text = "了解更多",
                                    modifier = Modifier.padding(5.dp),
                                    color = Color(0xFF368BFF),
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(0.dp, 0.dp, 0.dp, 10.dp)
                                .weight(1f),
                        ) {
                            Row(modifier = Modifier.align(Alignment.TopCenter)) {
                                Text(
                                    text = "登录表明您同意我们的",
                                    color = Color.White,
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "条款、隐私政策",
                                    color = Color(0xFFC4C4C4),
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
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