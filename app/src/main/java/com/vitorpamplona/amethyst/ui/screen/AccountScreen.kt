package com.vitorpamplona.amethyst.ui.screen

import android.content.Intent
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
        }
        is AccountState.LoggedIn -> {
          MainScreen(AccountViewModel(state.account), accountStateViewModel, startingPage)
        }
        is AccountState.LoggedInViewOnly -> {
          MainScreen(AccountViewModel(state.account), accountStateViewModel, startingPage)
        }
      }
    }
  }
}

