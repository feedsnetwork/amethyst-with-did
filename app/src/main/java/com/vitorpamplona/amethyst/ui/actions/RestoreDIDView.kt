package com.vitorpamplona.amethyst.ui.actions

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RestoreDIDView(restoreDIDViewModel: RestoreDIDViewModel, onFinish: (type: Int, didString: String) -> Unit) {
    val cachedDID = MutableLiveData("")
    var restoreFinish by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        restoreDIDViewModel.restore {
            cachedDID.postValue(it)
        }
    }

    Dialog(
        onDismissRequest = { onFinish(0,"") },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface() {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .height(350.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.align(Alignment.End)) {
                    CloseButton(onCancel = {
                        onFinish(0, "")
                    })
                }

                Spacer(modifier = Modifier.height(100.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ){
                    val state = cachedDID.observeAsState()
                    state.value?.let {
                        if (it == ""){
                            Text("Restoring")
                        }else{
                            Text("Restore Finish")
                            Text("DID: $it")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Column(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    val state = cachedDID.observeAsState()
                    state.value?.let {
                        Log.d(TAG, "RestoreDIDView: "+restoreDIDViewModel.mnemonic)
                        RestoreDIDButton(
                            onConfirm = {
                                onFinish(1, it)
                                restoreFinish = true
                            },
                            it.isNotBlank()
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
        Text(text = "Start", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
    }
}
