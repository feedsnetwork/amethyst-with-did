package com.vitorpamplona.amethyst.test

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.SerializedName
import com.vitorpamplona.amethyst.model.toByteArray
import com.vitorpamplona.amethyst.service.DIDHelper
import fr.acinq.secp256k1.Secp256k1
import fr.acinq.secp256k1.Secp256k1Exception
import nostr.postr.Utils
import nostr.postr.events.ContactListEvent
import nostr.postr.events.DeletionEvent
import nostr.postr.events.Event
import nostr.postr.events.MetadataEvent
import nostr.postr.events.PrivateDmEvent
import nostr.postr.events.RecommendRelayEvent
import nostr.postr.events.TextNoteEvent
import nostr.postr.events.generateId
import nostr.postr.toHex
import okio.ByteString.Companion.decodeBase64
import org.elastos.did.DID
import org.elastos.did.DIDDocument
import java.lang.Exception
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import kotlin.concurrent.thread

class MyEvent(
    val id: ByteArray,
    @SerializedName("pubkey") val pubKey: ByteArray,
    @SerializedName("created_at") val createdAt: Long,
    val kind: Int,
    val tags: List<List<String>>,
    val content: String,
    val sig: String
) {
    @Transient val replyTos: List<String>
    @Transient val mentions: List<String>

    init {
        replyTos = tags.filter { it.firstOrNull() == "e" }.mapNotNull { it.getOrNull(1) }
        mentions = tags.filter { it.firstOrNull() == "p" }.mapNotNull { it.getOrNull(1) }
    }
    fun toJson(): String = gson.toJson(this)

//    /**
//     * Checks if the ID is correct and then if the pubKey's secret key signed the event.
//     */
//    fun checkSignature() {
//        if (!id.contentEquals(generateId())) {
//            throw Exception(
//                """|Unexpected ID.
//                   |  Event: ${toJson()}
//                   |  Actual ID: ${id.toHex()}
//                   |  Generated: ${generateId().toHex()}""".trimIndent()
//            )
//        }
//        if (!secp256k1.verifySchnorr(sig, id, pubKey)) {
//            throw Exception("""Bad signature!""")
//        }
//    }

    class EventDeserializer : JsonDeserializer<MyEvent> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): MyEvent {
            val jsonObject = json.asJsonObject
            return MyEvent(
                id = Hex.decode(jsonObject.get("id").asString),
                pubKey = Hex.decode(jsonObject.get("pubkey").asString),
                createdAt = jsonObject.get("created_at").asLong,
                kind = jsonObject.get("kind").asInt,
                tags = jsonObject.get("tags").asJsonArray.map {
                    it.asJsonArray.map { s -> s.asString }
                },
                content = jsonObject.get("content").asString,
                sig = jsonObject.get("sig").asString
            )
        }
    }

    class EventSerializer : JsonSerializer<MyEvent> {
        override fun serialize(
            src: MyEvent,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonObject().apply {
                addProperty("id", src.id.toHex())
                addProperty("pubkey", src.pubKey.toHex())
                addProperty("created_at", src.createdAt)
                addProperty("kind", src.kind)
                add("tags", JsonArray().also { jsonTags ->
                    src.tags.forEach { tag ->
                        jsonTags.add(JsonArray().also { jsonTagElement ->
                            tag.forEach { tagElement ->
                                jsonTagElement.add(tagElement)
                            }
                        })
                    }
                })
                addProperty("content", src.content)
                addProperty("sig", src.sig)
            }
        }
    }

    class ByteArrayDeserializer : JsonDeserializer<ByteArray> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): ByteArray = Hex.decode(json.asString)
    }

    class ByteArraySerializer : JsonSerializer<ByteArray> {
        override fun serialize(
            src: ByteArray,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ) = JsonPrimitive(src.toHex())
    }

    companion object {
        private val secp256k1 = Secp256k1.get()

        val sha256: MessageDigest = MessageDigest.getInstance("SHA-256")
        val gson: Gson = GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(MyEvent::class.java, EventSerializer())
            .registerTypeAdapter(MyEvent::class.java, EventDeserializer())
            .registerTypeAdapter(ByteArray::class.java, ByteArraySerializer())
            .registerTypeAdapter(ByteArray::class.java, ByteArrayDeserializer())
            .create()

//        fun fromJson(json: String, lenient: Boolean = false): MyEvent = gson.fromJson(json, MyEvent::class.java).getRefinedEvent(lenient)
//
//        fun fromJson(json: JsonElement, lenient: Boolean = false): MyEvent = gson.fromJson(json, MyEvent::class.java).getRefinedEvent(lenient)
//
//        fun MyEvent.getRefinedEvent(lenient: Boolean = false): MyEvent = when (kind) {
//            MetadataEvent.kind -> MetadataEvent(id, pubKey, createdAt, tags, content, sig)
//            TextNoteEvent.kind -> TextNoteEvent(id, pubKey, createdAt, tags, content, sig)
//            RecommendRelayEvent.kind -> RecommendRelayEvent(id, pubKey, createdAt, tags, content, sig, lenient)
//            ContactListEvent.kind -> ContactListEvent(id, pubKey, createdAt, tags, content, sig)
//            PrivateDmEvent.kind -> PrivateDmEvent(id, pubKey, createdAt, tags, content, sig)
//            DeletionEvent.kind -> DeletionEvent(id, pubKey, createdAt, tags, content, sig)
//            6 -> this // content has full event. Resend/Retweet
//            7 -> this // no content but e and p tags. Boosts
//            17 -> this // nwiki. tag w->subject https://github.com/fiatjaf/nwiki
//            30 -> this // jester https://jesterui.github.io/
//            40 -> this // some market place?
//            7357 -> this // events that contain only an e tag?
//            else -> this
//        }

        fun generateId(pubKey: ByteArray, createdAt: Long, kind: Int, tags: List<List<String>>, content: String): ByteArray {
            val rawEvent = listOf(
                0,
                pubKey.toHex(),
                createdAt,
                kind,
                tags,
                content
            )
            val rawEventJson = gson.toJson(rawEvent)
            return sha256.digest(rawEventJson.toByteArray(Charsets.UTF_8))
        }

        fun create(privateKey: ByteArray, kind: Int, tags: List<List<String>> = emptyList(), content: String = "", createdAt: Long = Date().time / 1000): MyEvent {
            val pubKey = privateKey
            Log.d("wangran", "pubKey: ====> "+pubKey);

            val rawEvent = listOf(
                0,
                pubKey.toHex(),
                createdAt,
                kind,
                tags,
                content
            )
            val rawEventJson = gson.toJson(rawEvent)
            Log.d("wangran", "create rawEventJson : "+rawEventJson);
//            val id = "test".toByteArray();
//            val id = sha256.digest(rawEventJson.toByteArray())


            val id = generateId(pubKey, createdAt, kind, tags, content)
            Log.d("wangran", "create id: "+id);
            Log.d("wangran", "create id str: "+String(id))

//            val sig = Utils.sign(id, privateKey)

            val sigStr = DIDHelper.signData(id);

            Log.d("wangran", "create: sigStr = "+sigStr)
//            val sig = sigStr.toByteArray(Charsets.UTF_8)

//            Log.d("wangran", "create: signResult = "+sig)
//            Log.d("wangran", "create: signResult = "+String(sig))


            Log.d("wangran", "create: "+"sig")
            return MyEvent(id, pubKey, createdAt, kind, tags, content, sigStr)
        }
    }
}

fun MyEvent.generateId(): ByteArray {
    val rawEvent = listOf(
        0,
        pubKey.toHex(),
        createdAt,
        kind,
        tags,
        content
    )
    val rawEventJson = Event.gson.toJson(rawEvent)
    return Event.sha256.digest(rawEventJson.toByteArray())
}