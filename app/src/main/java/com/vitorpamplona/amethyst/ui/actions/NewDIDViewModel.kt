package com.vitorpamplona.amethyst.ui.actions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var publishFinish = false;
    var did = ""
    fun create(onSuccess: (didString: String) -> Unit) {//TODO ONFINISH

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {
                delay(100)
                val didHelper = DIDHelper()
                val didDocument = didHelper.createNewDid()
                val didString = didDocument.subject.toString()
                Log.d(TAG, "create: ..."+didString)
                publishFinish = true
                onSuccess(didString)
            }catch (e: Exception){
                Log.d(TAG, "create error : ..."+e.toString())
            }
        }
    }


}