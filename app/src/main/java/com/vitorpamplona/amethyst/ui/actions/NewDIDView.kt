package com.vitorpamplona.amethyst.ui.actions

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitorpamplona.amethyst.R


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NewDIDView(onFinish: (type: Int, didString: String) -> Unit) {
    //type 0 cancel, 1 finish
    val newDIDViewModel: NewDIDViewModel = viewModel()
    val cachedDID = MutableLiveData("")
    var showDIDIcon by remember { mutableStateOf(false) }

    var showGuide by remember { mutableStateOf(true) }
    var creatingDID by remember { mutableStateOf(false) }
    var prepareCreate by remember { mutableStateOf(false) }
    var createFinish by remember { mutableStateOf(false) }
    var createError by remember { mutableStateOf(false) }
    var guideStep by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        showDIDIcon = true
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
            if (showGuide){
                when(guideStep){
                    1 ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(550.dp)
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

                                Spacer(modifier = Modifier.height(120.dp))

                                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                    AnimatedVisibility(
                                        visible = showDIDIcon,
                                        enter = scaleIn(),
                                        exit = scaleOut()
                                    ) {
                                        Image(
                                            painterResource(id = R.drawable.did),
                                            contentDescription = "Create did",
                                            contentScale = ContentScale.Inside
                                        )
                                    }
                                }


                                Spacer(modifier = Modifier.height(20.dp))
                                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                    Text(
                                        text = "欢迎来到" ,
                                        fontSize = TextUnit(16f, TextUnitType.Sp),
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(7.dp))
                                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                    Text(
                                        text = "我的第一个身份" ,
                                        fontSize = TextUnit(21f, TextUnitType.Sp),
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(15.dp),
                                shape = RoundedCornerShape(20.dp),
                                onClick = {
                                    guideStep = 2
                                }) {
                                Text(text = "下一步", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
                            }
                        }
                    2 ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(550.dp)
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

                                Spacer(modifier = Modifier.height(120.dp))

                                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                    Image(
                                        painterResource(id = R.drawable.did),
                                        contentDescription = "Create did",
                                        contentScale = ContentScale.Inside
                                    )
                                }


                                Column(modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth(0.8f)) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = "此应用程序使用去中心身份（DID）。" ,
                                        fontSize = TextUnit(13f, TextUnitType.Sp),
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "使用去中心身份，您拥有自己的身份和数据。" ,
                                        fontSize = TextUnit(13f, TextUnitType.Sp),
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))

                                    Text(
                                        text = "因此，您似乎还不知道这是什么，或者您从未创建自己的身份？ 我们在这里为您提供帮助，以下步骤将自动为您创建和发布全新的Elastos身份和存储空间。" ,
                                        fontSize = TextUnit(13f, TextUnitType.Sp),
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(15.dp),
                                shape = RoundedCornerShape(20.dp),
                                onClick = { guideStep = 3 }) {
                                Text(text = "下一步", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
                            }
                        }
                    3 ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(550.dp)
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

                                Spacer(modifier = Modifier.height(120.dp))

                                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                    Image(
                                        painterResource(id = R.drawable.did),
                                        contentDescription = "Create did",
                                        contentScale = ContentScale.Inside
                                    )
                                }
                                Column(modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth(0.8f)) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = "将来，如果您想更好地控制或在其他支持DID的应用程序中使用此身份，可以将其导出到第三方钱包应用程序，例如Elastos Essential。" ,
                                        fontSize = TextUnit(13f, TextUnitType.Sp),
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(15.dp),
                                shape = RoundedCornerShape(20.dp),
                                onClick = { showGuide = false }) {
                                Text(text = "下一步", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
                            }
                        }
                }
            }else if (prepareCreate) {
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
            }else if (creatingDID) {
                Column(modifier = Modifier
                    .padding(10.dp)
                    .height(350.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(550.dp)
                    ){
                        Row(

                        ) {
                            Column {

                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            CloseDialog(
                                modifier = Modifier.align(Alignment.End),
                                onCancel = {
                                    onFinish(0,"")
                                })

                            Spacer(modifier = Modifier.height(120.dp))

                            Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Image(
                                    painterResource(id = R.drawable.did),
                                    contentDescription = "Create did",
                                    contentScale = ContentScale.Inside
                                )
                            }
                            Column(modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(0.8f)) {
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "将来，如果您想更好地控制或在其他支持DID的应用程序中使用此身份，可以将其导出到第三方钱包应用程序，例如Elastos Essential。" ,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Button(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(15.dp),
                            shape = RoundedCornerShape(20.dp),
                            onClick = { showGuide = false }) {
                            Text(text = "下一步", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
                        }
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

                Box(
                    modifier = Modifier
                        .height(230.dp)
                        .fillMaxWidth(0.9f)
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

//                        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            OutlinedTextField(
                                label = { Text(text = "Username") },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
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
//                        }

                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    ReadyCreateDIDButton(
                        onConfirm = {
                            newDIDViewModel.create { type, didString ->
                                run {

                                    Log.d(TAG, "NewDIDView: type is $type, didString is $didString")
                                    if (type == 1){
                                        cachedDID.postValue(didString)
                                        Log.d(TAG, "NewDIDView: did is $didString")
                                        prepareCreate = false
                                        prepareCreate = true
                                        createFinish = true
                                    }else{
                                        prepareCreate = false
                                        createError = true
                                    }
                                }
                            }
                            creatingDID = true
                        },
                        newDIDViewModel.userName.value.isNotBlank(),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(15.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun CloseDialog(modifier: Modifier, onCancel: () -> Unit) {
    OutlinedButton(
        modifier = modifier,
        onClick = {
            onCancel()
        },
        colors = ButtonDefaults
            .outlinedButtonColors(backgroundColor = Color.Transparent),
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Image(
            painterResource(id = R.drawable.close),
            contentDescription = "Close dialog",
            contentScale = ContentScale.Inside,
        )
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
        Text(text = "继续", color = Color.White, fontSize = TextUnit(17f, TextUnitType.Sp))
    }
}