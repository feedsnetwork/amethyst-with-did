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
import java.io.File

const val TAG = "wangran"
class NewDIDViewModel(private val didStorePathFile: File) : ViewModel() {
    val userName = mutableStateOf("")
    var did = ""
    fun create(onFinish: (type: Int, didString: String) -> Unit) {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {
                delay(100)
                val didHelper = DIDHelper(didStorePathFile)
                Log.d(TAG, "create: 1111111111")
                val didDocument = didHelper.createNewDid()
                Log.d(TAG, "create: didDocument 22222$didDocument")
                val didString = didDocument.subject.toString()
                Log.d(TAG, "create: ..."+didString)
                onFinish(1, didString)
            }catch (e: Exception){
                Log.d(TAG, "create error : ..."+e.toString())
                onFinish(-1, "")
            }
        }
    }

}