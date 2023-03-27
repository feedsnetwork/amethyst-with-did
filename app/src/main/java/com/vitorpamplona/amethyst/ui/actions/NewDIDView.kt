package com.vitorpamplona.amethyst.ui.actions

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewDIDView(onFinish: (type: Int, didString: String) -> Unit) {
    //type 0 cancel, 1 finish
    val newDIDViewModel: NewDIDViewModel = viewModel()
    val cachedDID = MutableLiveData("")

    var createFinish by remember {
        mutableStateOf(false)
    }

    var createError by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
    }

    Dialog(
        onDismissRequest = { onFinish(0,"") },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
        ) {
            var prepareCreate by remember { mutableStateOf(false) }
            if (prepareCreate) {
                Column(modifier = Modifier
                    .padding(10.dp)
                    .height(350.dp)
                ) {
                    val state = cachedDID.observeAsState()
                    state.value?.let {
                        if (it == ""){
                            Text(text = "Creating...")
                        }else{
                            Text(text = "New DID created")
                            Text(text = "DID: $it")
                        }
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                              if (createFinish){
                                  cachedDID.value?.let { onFinish(1, it) }
                              }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults
                            .buttonColors(
                                backgroundColor = if (createFinish) MaterialTheme.colors.primary else Color.Gray
                            )
                    ) {
                        Text(text = "Start", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
                    }
                }
            }else if (createError){
                Column(modifier = Modifier
                    .padding(10.dp)
                    .height(350.dp)
                ) {
                    Text(text = "Creat DID error, Please try again later")
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onFinish(0, "")
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults
                            .buttonColors(
                                backgroundColor =  MaterialTheme.colors.primary
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
                ) {
                    Column(modifier = Modifier.align(Alignment.End)) {
                        CloseButton(onCancel = {
                            onFinish(0,"")
                        })
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            label = { Text(text = "Username") },
                            modifier = Modifier.weight(1f),
                            value = newDIDViewModel.userName.value,
                            onValueChange = { newDIDViewModel.userName.value = it },
                            placeholder = {
                                Text(
                                    text = "My username",
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
                                )
                            },
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Column(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        ReadyCreateDIDButton(
                            onConfirm = {
                                newDIDViewModel.create { type, didString ->
                                    run {

                                        Log.d(TAG, "NewDIDView: type is $type, didString is $didString")
                                        if (type == 1){
                                            cachedDID.postValue(didString)
                                            Log.d(TAG, "NewDIDView: did is $didString")
                                            createFinish = true
                                        }else{
                                            prepareCreate = false
                                            createError = true

                                        }
                                    }
                                }
                                prepareCreate = true
                            },
                            newDIDViewModel.userName.value.isNotBlank()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReadyCreateDIDButton(onConfirm: () -> Unit = {}, isActive: Boolean, modifier: Modifier = Modifier.fillMaxWidth()) {
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
