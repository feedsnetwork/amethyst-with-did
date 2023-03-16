package com.vitorpamplona.amethyst.ui.actions

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.vitorpamplona.amethyst.service.DIDHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "wangran"
class NewDIDViewModel: ViewModel() {

    val userName = mutableStateOf("")

    fun create() {

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {
                delay(100)
                val didHelper: DIDHelper =
                    DIDHelper()
                didHelper.init()
                didHelper.initDid()
                Log.d(TAG, "create: ...")
            }catch (e: Exception){
                Log.d(TAG, "create error : ..."+e.toString())
            }


//            try {
//
//            } finally {
////                withContext(NonCancellable) {
////                    handlerWaiting.set(false)
////                }
//            }
        }
    }


}