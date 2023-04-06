package com.vitorpamplona.amethyst.model

import android.content.res.Resources
import android.util.Log
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.LiveData
import com.vitorpamplona.amethyst.did.DIDPersona
import com.vitorpamplona.amethyst.service.DIDHelper
import com.vitorpamplona.amethyst.service.model.ChannelCreateEvent
import com.vitorpamplona.amethyst.service.model.ChannelMessageEvent
import com.vitorpamplona.amethyst.service.model.ChannelMetadataEvent
import com.vitorpamplona.amethyst.service.model.ReactionEvent
import com.vitorpamplona.amethyst.service.model.ReportEvent
import com.vitorpamplona.amethyst.service.model.RepostEvent
import com.vitorpamplona.amethyst.service.relays.Client
import com.vitorpamplona.amethyst.service.relays.Constants
import com.vitorpamplona.amethyst.service.relays.FeedType
import com.vitorpamplona.amethyst.service.relays.Relay
import com.vitorpamplona.amethyst.service.relays.RelayPool
import com.vitorpamplona.amethyst.test.MyEvent
import com.vitorpamplona.amethyst.ui.actions.NewRelayListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nostr.postr.Contact
import nostr.postr.Utils
import nostr.postr.events.ContactListEvent
import nostr.postr.events.Event
import nostr.postr.events.MetadataEvent
import nostr.postr.events.PrivateDmEvent
import nostr.postr.toHex
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

val DefaultChannels = setOf(
  "25e5c82273a271cb1a840d0060391a0bf4965cafeb029d5ab55350b418953fbb", // -> Anigma's Nostr //Nostr public channel id: 25e5c82273a271cb1a840d0060391a0bf4965cafeb029d5ab55350b418953fbb
  "42224859763652914db53052103f0b744df79dfc4efef7e950fc0802fc3df3c5"  // -> Amethyst's Group
)

fun getLanguagesSpokenByUser(): Set<String> {
  val languageList = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration())
  val codedList = mutableSetOf<String>()
  for (i in 0 until languageList.size()) {
    languageList.get(i)?.let { codedList.add(it.language) }
  }
  return codedList
}

class Account(
  val loggedIn: DIDPersona,
  var followingChannels: Set<String> = DefaultChannels,
  var hiddenUsers: Set<String> = setOf(),
  var localRelays: Set<NewRelayListViewModel.Relay> = Constants.defaultRelays.toSet(),
  var dontTranslateFrom: Set<String> = getLanguagesSpokenByUser(),
  var translateTo: String = Locale.getDefault().language
) {
  fun userProfile(): User {
//    return LocalCache.getOrCreateUser(loggedIn.pubKey.toHexKey())
        return LocalCache.getOrCreateUser(loggedIn.pubKey.toHexKey())
  }

  fun followingChannels(): List<Channel> {
    return followingChannels.map { LocalCache.getOrCreateChannel(it) }
  }

  fun hiddenUsers(): List<User> {
    return hiddenUsers.map { LocalCache.getOrCreateUser(it) }
  }

  fun isWriteable(): Boolean {
    return loggedIn.privKey != null
  }

  fun sendNewRelayList(relays: Map<String, ContactListEvent.ReadWrite>) {
    if (!isWriteable()) return

    val lastestContactList = userProfile().latestContactList
    val event = if (lastestContactList != null) {
      ContactListEvent.create(
        lastestContactList.follows,
        relays,
        loggedIn.privKey!!)
    } else {
      ContactListEvent.create(listOf(), relays, loggedIn.privKey!!)
    }

    Client.send(event)
    LocalCache.consume(event)
  }

  fun sendNewUserMetadata(toString: String) {
    if (!isWriteable()) return

    loggedIn.privKey?.let {
//      val didString = String(it);

      val createdAt = Date().time / 1000
      val content = toString
      val pubKey = Utils.pubkeyCreate(it)
      val tags = listOf<List<String>>()
      val id = Event.generateId(pubKey, createdAt, MetadataEvent.kind, tags, content)
//      val sig = Utils.sign(id, it)
      val sig = DIDHelper.signData(id)
      val sigByteArray = sig.toByteArray()

      val event = MetadataEvent(id, pubKey, createdAt, tags, content, sigByteArray)

      Client.send(event)
      LocalCache.consume(event)
    }
  }

  fun reactTo(note: Note) {
    if (!isWriteable()) return

    if (note.hasReacted(userProfile(), "+")) {
      // has already liked this note
      return
    }

    note.event?.let {
      val event = ReactionEvent.createLike(it, loggedIn.privKey!!)
      Client.send(event)
      LocalCache.consume(event)
    }
  }

  fun report(note: Note, type: ReportEvent.ReportType) {
    if (!isWriteable()) return

    if (note.hasReacted(userProfile(), "⚠️")) {
      // has already liked this note
      return
    }

    note.event?.let {
      val event = ReactionEvent.createWarning(it, loggedIn.privKey!!)
      Client.send(event)
      LocalCache.consume(event)
    }

    note.event?.let {
      val event = ReportEvent.create(it, type, loggedIn.privKey!!)
      Client.send(event)
      LocalCache.consume(event)
    }
  }

  fun report(user: User, type: ReportEvent.ReportType) {
    if (!isWriteable()) return

    if (user.hasReport(userProfile(), type)) {
      // has already reported this note
      return
    }

    val event = ReportEvent.create(user.pubkeyHex, type, loggedIn.privKey!!)
    Client.send(event)
    LocalCache.consume(event)
  }

  fun boost(note: Note) {
    if (!isWriteable()) return

    if (note.hasBoosted(userProfile())) {
      // has already bosted in the past 5mins 
      return
    }

    note.event?.let {
      val event = RepostEvent.create(it, loggedIn.privKey!!)
      Client.send(event)
      LocalCache.consume(event)
    }
  }

  fun broadcast(note: Note) {
    note.event?.let {
      Client.send(it)
    }
  }

  fun follow(user: User) {
    if (!isWriteable()) return

    val lastestContactList = userProfile().latestContactList
    val event = if (lastestContactList != null) {
      ContactListEvent.create(
        lastestContactList.follows.plus(Contact(user.pubkeyHex, null)),
        userProfile().relays,
        loggedIn.privKey!!)
    } else {
      val relays = Constants.defaultRelays.associate { it.url to ContactListEvent.ReadWrite(it.read, it.write) }
      ContactListEvent.create(
        listOf(Contact(user.pubkeyHex, null)),
        relays,
        loggedIn.privKey!!
      )
    }

    Client.send(event)
    LocalCache.consume(event)
  }

  fun unfollow(user: User) {
    if (!isWriteable()) return

    val lastestContactList = userProfile().latestContactList
    if (lastestContactList != null) {
      val event = ContactListEvent.create(
        lastestContactList.follows.filter { it.pubKeyHex != user.pubkeyHex },
        userProfile().relays,
        loggedIn.privKey!!)
      Client.send(event)
      LocalCache.consume(event)
    }
  }

  fun sendPost(message: String, replyTo: List<Note>?, mentions: List<User>?) {
    if (!isWriteable()) return
    val repliesToHex = replyTo?.map { it.idHex }
    val mentionsHex = mentions?.map { it.pubkeyHex }

    Log.d("wangran", "message: ====>"+message);
    val didTags = listOf<String>("did")

    thread {
      val signedEvent = MyEvent.create(
        privateKey = loggedIn.privKey,
        kind = 1,
        tags = listOf(didTags),
        content = message
      )
      Log.d("wangran", "sendPost: ====>"+signedEvent.toJson().toString());
      Client.send(signedEvent)
      LocalCache.consume(signedEvent)
    }
  }

  fun sendChannelMeesage(message: String, toChannel: String, replyingTo: Note? = null, mentions: List<User>?) {
    if (!isWriteable()) return

    val repliesToHex = listOfNotNull(replyingTo?.idHex).ifEmpty { null }
    val mentionsHex = mentions?.map { it.pubkeyHex }

    val signedEvent = ChannelMessageEvent.create(
      message = message,
      channel = toChannel,
      replyTos = repliesToHex,
      mentions = mentionsHex,
      privateKey = loggedIn.privKey!!
    )

    Log.d("wangran", "sendChannelMeesage: ====>"+signedEvent.toString());
    Client.send(signedEvent)
    LocalCache.consume(signedEvent, null)
  }

  fun sendPrivateMeesage(message: String, toUser: String, replyingTo: Note? = null) {
    if (!isWriteable()) return
    val user = LocalCache.users[toUser] ?: return

    val signedEvent = PrivateDmEvent.create(
      recipientPubKey = user.pubkey,
      publishedRecipientPubKey = user.pubkey,
      msg = message,
      privateKey = loggedIn.privKey!!,
      advertiseNip18 = false
    )
    Client.send(signedEvent)
    LocalCache.consume(signedEvent, null)
  }

  fun sendCreateNewChannel(name: String, about: String, picture: String) {
    if (!isWriteable()) return

    val metadata = ChannelCreateEvent.ChannelData(
      name, about, picture
    )

    val event = ChannelCreateEvent.create(
      channelInfo = metadata,
      privateKey = loggedIn.privKey!!
    )

    Client.send(event)
    LocalCache.consume(event)

    joinChannel(event.id.toHex())
  }

  fun joinChannel(idHex: String) {
    followingChannels = followingChannels + idHex
    invalidateData(live)
  }

  fun leaveChannel(idHex: String) {
    followingChannels = followingChannels - idHex
    invalidateData(live)
  }

  fun hideUser(pubkeyHex: String) {
    hiddenUsers = hiddenUsers + pubkeyHex
    invalidateData(live)
  }

  fun showUser(pubkeyHex: String) {
    hiddenUsers = hiddenUsers - pubkeyHex
    invalidateData(live)
  }

  fun sendChangeChannel(name: String, about: String, picture: String, channel: Channel) {
    if (!isWriteable()) return

    val metadata = ChannelCreateEvent.ChannelData(
      name, about, picture
    )

    val event = ChannelMetadataEvent.create(
      newChannelInfo = metadata,
      originalChannelIdHex = channel.idHex,
      privateKey = loggedIn.privKey!!
    )

    Client.send(event)
    LocalCache.consume(event)

    joinChannel(event.id.toHex())
  }

  fun decryptContent(note: Note): String? {
    val event = note.event
    return if (event is PrivateDmEvent && loggedIn.privKey != null) {
      var pubkeyToUse = event.pubKey

      val recepientPK = event.recipientPubKey

      if (note.author == userProfile() && recepientPK != null)
        pubkeyToUse = recepientPK

      return try {
        val sharedSecret = Utils.getSharedSecret(loggedIn.privKey!!, pubkeyToUse)

        val retVal = Utils.decrypt(event.content, sharedSecret)

        if (retVal.startsWith(PrivateDmEvent.nip18Advertisement)) {
          retVal.substring(16)
        } else {
          retVal
        }

      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    } else {
      event?.content
    }
  }

  fun addDontTranslateFrom(languageCode: String) {
    dontTranslateFrom = dontTranslateFrom.plus(languageCode)
    invalidateData(liveLanguages)
  }

  fun updateTranslateTo(languageCode: String) {
    translateTo = languageCode
    invalidateData(liveLanguages)
  }

  fun activeRelays(): Array<Relay>? {
    return userProfile().relays?.map {
      val localFeedTypes = localRelays.firstOrNull() { localRelay -> localRelay.url == it.key }?.feedTypes ?: FeedType.values().toSet()
      Relay(it.key, it.value.read, it.value.write, localFeedTypes)
    }?.toTypedArray()
  }

  fun convertLocalRelays(): Array<Relay> {
    return localRelays.map {
      Relay(it.url, it.read, it.write, it.feedTypes)
    }.toTypedArray()
  }

  init {
    userProfile().subscribe(object: User.Listener() {
      override fun onRelayChange() {
        Client.disconnect()
        Client.connect(activeRelays() ?: convertLocalRelays())
        RelayPool.requestAndWatch()
      }
    })
  }

  // Observers line up here.
  val live: AccountLiveData = AccountLiveData(this)
  val liveLanguages: AccountLiveData = AccountLiveData(this)

  var handlerWaiting = AtomicBoolean()

  @Synchronized
  private fun invalidateData(live: AccountLiveData) {
    if (handlerWaiting.getAndSet(true)) return

    handlerWaiting.set(true)
    val scope = CoroutineScope(Job() + Dispatchers.Default)
    scope.launch {
      delay(100)
      live.refresh()
      handlerWaiting.set(false)
    }
  }

  fun isHidden(user: User) = user in hiddenUsers()

  fun isAcceptable(user: User): Boolean {
    return user !in hiddenUsers()  // if user hasn't hided this author
        && user.reportsBy( userProfile() ).isEmpty() // if user has not reported this post
        && user.reportsBy( userProfile().follows ).size < 5
  }

  fun isAcceptableDirect(note: Note): Boolean {
    return note.reportsBy( userProfile() ).isEmpty()  // if user has not reported this post
        && note.reportsBy( userProfile().follows ).size < 5 // if it has 5 reports by reliable users
  }

  fun isAcceptable(note: Note): Boolean {
    return note.author?.let { isAcceptable(it) } ?: true // if user hasn't hided this author
        && isAcceptableDirect(note)
        && (note.event !is RepostEvent
          || (note.event is RepostEvent && note.replyTo?.firstOrNull { isAcceptableDirect(it) } != null)
        ) // is not a reaction about a blocked post
  }

  fun getRelevantReports(note: Note): Set<Note> {
    val followsPlusMe = userProfile().follows + userProfile()

    val innerReports = if (note.event is RepostEvent) {
      note.replyTo?.map { getRelevantReports(it) }?.flatten() ?: emptyList()
    } else {
      emptyList()
    }

    return (note.reportsBy(followsPlusMe) +
          (note.author?.reportsBy(followsPlusMe) ?: emptyList()) +
          innerReports).toSet()
  }

  fun saveRelayList(value: List<NewRelayListViewModel.Relay>) {
    localRelays = value.toSet()
    sendNewRelayList(value.associate { it.url to ContactListEvent.ReadWrite(it.read, it.write) } )
  }
}

class AccountLiveData(private val account: Account): LiveData<AccountState>(AccountState(account)) {
  fun refresh() {
    postValue(AccountState(account))
  }
}

class AccountState(val account: Account)