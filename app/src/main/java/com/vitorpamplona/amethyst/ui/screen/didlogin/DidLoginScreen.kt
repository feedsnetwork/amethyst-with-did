package com.vitorpamplona.amethyst.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.vitorpamplona.amethyst.R
import com.vitorpamplona.amethyst.model.LocalCache
import com.vitorpamplona.amethyst.model.User
import com.vitorpamplona.amethyst.model.decodePublicKey
import com.vitorpamplona.amethyst.model.toHexKey
import com.vitorpamplona.amethyst.ui.actions.CloseButton
import com.vitorpamplona.amethyst.ui.navigation.ShowQRDialog
import com.vitorpamplona.amethyst.ui.qrcode.DIDQrCodeScanner
import com.vitorpamplona.amethyst.ui.qrcode.QrCodeScanner

@Composable
fun DidLoginScreen(accountViewModel: AccountStateViewModel) {
    val key = remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }
    val acceptedTerms = remember { mutableStateOf(false) }
    var termsAcceptanceIsRequired by remember { mutableStateOf("") }
    val uri = LocalUriHandler.current
    // store the dialog open or close state
    var dialogOpen by remember {
        mutableStateOf(false)
    }

    if (dialogOpen){
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
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

                CloseButton(onCancel = {
                    dialogOpen = false
                })


                Column(
                    modifier = Modifier
                        .padding(40.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DIDQrCodeScanner(onScan = { result ->
                        run {
                            Log.d("TAG", "DidLoginScreen: " + result)
                        }
                    })
                }
            }
//            Column(
//                modifier = Modifier
//                    .padding(20.dp)
//                    .fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {

//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(10.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//                }
//
//                Row(
////                horizontalArrangement = Arrangement.Center,
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(30.dp)
//                ) {
//
//                }
//            }

        }
    }else{
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

}