package com.vitorpamplona.amethyst.ui.actions

import android.util.Log
import androidx.lifecycle.ViewModel
import com.vitorpamplona.amethyst.service.DIDHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class RestoreDIDViewModel(private val storePathFile: File): ViewModel() {
    var mnemonic = ""
//    fun restore(onSuccess: (didString: String) -> Unit) {
    fun restore(onSuccess: (didString: String) -> Unit) {
        Log.d(TAG, "restore: mnemonic"+mnemonic)
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {
                delay(100)
                val didHelper = DIDHelper(storePathFile)
                val didString = didHelper.restore(mnemonic)
                Log.d(TAG, "Restore: ...")
                onSuccess(didString)
            }catch (e: Exception){
                Log.d(TAG, "restore error : ..."+e.toString())
            }
        }

    }


}