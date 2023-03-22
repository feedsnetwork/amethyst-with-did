package com.vitorpamplona.amethyst.ui.actions

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitorpamplona.amethyst.R
import com.vitorpamplona.amethyst.model.Account
import com.vitorpamplona.amethyst.model.Channel
import com.vitorpamplona.amethyst.ui.screen.AccountStateViewModel
import com.vitorpamplona.amethyst.ui.screen.loggedIn.AccountViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RestoreDIDView(restoreDIDViewModel: RestoreDIDViewModel, onClose: () -> Unit) {
    LaunchedEffect(Unit) {
    }

    Dialog(
        onDismissRequest = { onClose() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
        ) {
            var prepareRestore by remember { mutableStateOf(false) }
            if (prepareRestore) {
                Column(modifier = Modifier
                    .padding(10.dp)
                    .height(350.dp)
                    .fillMaxWidth()
                ) {
//                    Column(modifier = Modifier.align(Alignment.End)) {
//                        Text(text = "waitting...")
//                    }
                    Text(text = "waitting...")
                    Text(text = "waitting...")
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onClose()
//                            if (isFinish) {
//                                onClose()
//                            }
                        },
                        colors = ButtonDefaults
                            .buttonColors(
                                backgroundColor = MaterialTheme.colors.primary
                            )
                    ) {
                        Text(text = "Done", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
                    }
                }
            }else {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(350.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.align(Alignment.End)) {
                        CloseButton(onCancel = {
                            onClose()
                        })
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("wait")
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Column(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
//                        CloseButton(onCancel = {
////                            postViewModel.clear()
//                            onClose()
//                        })
                        Log.d(TAG, "RestoreDIDView: "+restoreDIDViewModel.mnemonic)
                        RestoreDIDButton(
                            onConfirm = {
                                restoreDIDViewModel.restore()
                                prepareRestore = true
                            },
                            restoreDIDViewModel.mnemonic.isNotBlank()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestoreDIDButton(onConfirm: () -> Unit = {}, isActive: Boolean, modifier: Modifier = Modifier.fillMaxWidth()) {
    Button(
        modifier = modifier,
        onClick = {
            if (isActive) {
                onConfirm()
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults
            .buttonColors(
                backgroundColor = if (isActive) MaterialTheme.colors.primary else Color.Gray
            )
    ) {
        Text(text = "Create", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
    }
}
