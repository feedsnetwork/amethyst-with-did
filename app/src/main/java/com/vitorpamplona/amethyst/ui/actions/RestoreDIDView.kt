package com.vitorpamplona.amethyst.ui.actions

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import com.vitorpamplona.amethyst.R

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
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
            val state = cachedDID.observeAsState()
            state.value?.let {
                Log.d(TAG, "RestoreDIDView: "+restoreDIDViewModel.mnemonic)
                if (it != null && it != ""){
                    Log.d(TAG, "RestoreDIDView: finish")
                    restoreFinish = true
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(320.dp)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    CloseDialog(
                        modifier = Modifier.align(Alignment.End),
                        onCancel = {
                            onFinish(0,"")
                        })

                    Spacer(modifier = Modifier.height(15.dp))

                    Column(modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .align(Alignment.CenterHorizontally)) {

                            AnimatedVisibility(
                                visible = restoreFinish,
                                enter = slideInVertically(tween(2000)) + fadeIn(tween(2000)) + scaleIn(tween(2000)),
                                exit = scaleOut()
                            ) {
                                Image(
                                    painterResource(id = R.drawable.baseline_task_alt_24),
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(80.dp),
                                    contentDescription = "Create did",
                                    contentScale = ContentScale.FillBounds,
                                    colorFilter = ColorFilter.tint(color = Color(0xFF00B000))
                                )
                            }
                        if (!restoreFinish){
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(80.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        if (restoreFinish){
                            Text(
                                text = "全部完成" ,
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontWeight = FontWeight.Bold
                            )
                        }else{
                            Text(
                                text = "正在获取" ,
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        if (restoreFinish){
                            Text(
                                text = "您的身份已经完全准备好了，您可以开始使用Feeds" ,
                                fontSize = TextUnit(14f, TextUnitType.Sp),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontWeight = FontWeight.Bold
                            )

                        }else{
                            Text(
                                text = "正在获取身份信息，请稍后..." ,
                                fontSize = TextUnit(14f, TextUnitType.Sp),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (restoreFinish) {
                    Button(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(15.dp),
                        shape = RoundedCornerShape(20.dp),
                        onClick = {
                            cachedDID.value?.let { onFinish(1, it) }
                        }) {
                        Text(
                            text = "继续",
                            color = Color.White,
                            fontSize = TextUnit(17f, TextUnitType.Sp)
                        )
                    }
                }
            }





//            Column(
//                modifier = Modifier
//                    .padding(10.dp)
//                    .height(350.dp)
//                    .fillMaxWidth()
//            ) {
//                Column(modifier = Modifier.align(Alignment.End)) {
//                    CloseButton(onCancel = {
//                        onFinish(0, "")
//                    })
//                }
//
//                Spacer(modifier = Modifier.height(100.dp))
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                ){
//                    val state = cachedDID.observeAsState()
//                    state.value?.let {
//                        if (it == ""){
//                            Text("Restoring")
//                        }else{
//                            Text("Restore Finish")
//                            Text("DID: $it")
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(30.dp))
//
//                Column(
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                ) {
//                    val state = cachedDID.observeAsState()
//                    state.value?.let {
//                        Log.d(TAG, "RestoreDIDView: "+restoreDIDViewModel.mnemonic)
//                        RestoreDIDButton(
//                            onConfirm = {
//                                onFinish(1, it)
//                                restoreFinish = true
//                            },
//                            it.isNotBlank()
//                        )
//                    }
//                }
//            }
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
