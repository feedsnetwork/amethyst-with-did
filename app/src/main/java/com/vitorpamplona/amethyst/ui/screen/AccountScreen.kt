package com.vitorpamplona.amethyst.ui.screen

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitorpamplona.amethyst.ui.screen.loggedIn.AccountViewModel

@Composable
fun AccountScreen(accountStateViewModel: AccountStateViewModel, layoutInflater: LayoutInflater, intent: Intent, startingPage: String?) {
  val accountState by accountStateViewModel.accountContent.collectAsState()

  Column() {
    Crossfade(targetState = accountState, animationSpec = tween(durationMillis = 100)) { state ->
      when (state) {
        is AccountState.LoggedOff -> {
          DidLoginScreen(accountStateViewModel, layoutInflater, intent, startingPage)
          Log.d("wangran", "AccountScreen: 111111111111111")
        }
        is AccountState.LoggedIn -> {
          MainScreen(AccountViewModel(state.account), accountStateViewModel, startingPage)
          Log.d("wangran", "AccountScreen: 2222222222222222")
          Log.d("wangran", "AccountScreen: state.account"+state.account.loggedIn.petName)
          Log.d("wangran", "AccountScreen: state.account"+String(state.account.loggedIn.pubKey))
          Log.d("wangran", "AccountScreen: state.account"+String(state.account.loggedIn.privKey))
        }
        is AccountState.LoggedInViewOnly -> {
          MainScreen(AccountViewModel(state.account), accountStateViewModel, startingPage)
          Log.d("wangran", "AccountScreen: 333333333333"+state.account)
        }
      }
    }
  }
}

