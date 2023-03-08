package com.vitorpamplona.amethyst.test

import android.util.Log
import com.vitorpamplona.amethyst.model.toByteArray
import fr.acinq.secp256k1.Secp256k1
import fr.acinq.secp256k1.Secp256k1Exception
import nostr.postr.Utils
import nostr.postr.events.Event
import nostr.postr.toHex
import okio.ByteString.Companion.decodeBase64
import org.elastos.did.DID
import org.elastos.did.DIDDocument
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class MyEvent(
    id: ByteArray,
    pubKey: ByteArray,
    createdAt: Long,
    tags: List<List<String>>,
    content: String,
    sig: ByteArray
): Event(id, pubKey, createdAt, kind, tags, content, sig) {
    @Transient val replyTos: List<String>
    @Transient val mentions: List<String>

    init {
        replyTos = tags.filter { it.firstOrNull() == "e" }.mapNotNull { it.getOrNull(1) }
        mentions = tags.filter { it.firstOrNull() == "p" }.mapNotNull { it.getOrNull(1) }
    }

    companion object {
        const val kind = 1

        fun create(msg: String, replyTos: List<String>?, mentions: List<String>?,didTags: List<String>?, privateKey: ByteArray, createdAt: Long = Date().time / 1000): MyEvent {
            val pubKey = Utils.pubkeyCreate(privateKey)
//            val pubKeyStr = "ioUyXVxTkZmJYGa5sWUzAfb8khDQc5zKT3"
            Log.d("wangran", "create: privateKey"+privateKey);
            Log.d("wangran", "create: privateKey toHex"+privateKey.toHex());

            val pubkey0 = Secp256k1.get().pubKeyCompress(Secp256k1.get().pubkeyCreate(privateKey));
            Log.d("wangran", "create: pubkey0"+pubkey0);
            Log.d("wangran", "create: pubkey0 toHex "+pubkey0.toHex());

            val pubKey00 = pubKeyCompress(pubkey0)
            Log.d("wangran", "create: pubKey00"+pubKey00);
            Log.d("wangran", "create: pubKey00 toHex "+pubKey00.toHex());

            val pubKey000 = pubKeyCompress(pubkey0).copyOfRange(1, 33)
            Log.d("wangran", "create: pubKey000"+pubKey000);
            Log.d("wangran", "create: pubKey000 toHex "+pubKey000.toHex());

//            val did = "did:elastos:inn8mJh5kaduEdiqa9E9UP7FK2Cv72AbJd"
//            thread {



//            Log.d("wangran", "create: 11111111111")
//            DIDHelper.initRootIdentity()
//            Log.d("wangran", "create: 222222222")
//            val diddocument = DIDHelper.initDid()
//
//            Log.d("wangran", "create: 33333333333")
//            Log.d("wangran", "create:diddocument "+diddocument)
//            Log.d("wangran", "create:defaultPublicKey "+diddocument.defaultPublicKey.publicKeyBase58)
//            val pubKeyBA = diddocument.defaultPublicKey.publicKeyBytes
//            Log.d("wangran", "create: pubKeyBA"+pubKeyBA.toHex())
//            val pubKey = pubKeyCompress(pubKeyBA).copyOfRange(1, 33)
//            Log.d("wangran", "create: pubKey"+pubKey.toHex())
//
//            val pubKey2 = pubKeyCompress(pubKeyBA)
//            Log.d("wangran", "create: pubKey2"+pubKey2.toHex())


//                val document = DIDHelper.getDidDocument(did)
//
//                Log.d("wangran", "document: ====> "+document);
//
//
//                val defaultPubKey = DIDHelper.getDefaultPublicKey(did)
//
//                Log.d("wangran", "defaultPubKey: ====> "+defaultPubKey);

//            }

//            val pubKeyStr = "dPS7BwbNLNAEcoHVdGx4BYcosdN9JxTeH3KAyTrJ8RnA"
//            val pubKey = org.elastos.did.crypto.Base58.decode(pubKeyStr)


//            val pubKeyStr = DIDHelper.getDefaultPublicKey(did);
//            Log.d("wangran", "pubKey: ====> "+pubKeyStr);
//            val pubkeyBA = pubKeyStr.toByteArray();

//            val pubKey = pubKeyCompress(pubkeyBA).copyOfRange(1, 33)

//            val pubKey = pubKeyStr.toByteArray()

//            Thread.sleep(10000);



//            val pubKeyBA = DIDHelper.getDefaultPublicKey(did);





            Log.d("wangran", "pubKey: ====> "+pubKey);
            val tags = mutableListOf<List<String>>()
            replyTos?.forEach {
                tags.add(listOf("e", it))
            }
            mentions?.forEach {
                tags.add(listOf("p", it))
            }
            didTags?.forEach {
                tags.add(listOf("did", it))
            }

            val rawEvent = listOf(
                0,
                pubKey.toHex(),
                createdAt,
                kind,
                tags,
                msg
            )
            val rawEventJson = gson.toJson(rawEvent)
            Log.d("wangran", "create rawEventJson : "+rawEventJson);
//            val id = "test".toByteArray();
//            val id = sha256.digest(rawEventJson.toByteArray())


            val id = generateId(pubKey, createdAt, kind, tags, msg)
            Log.d("wangran", "create id: "+id);
            Log.d("wangran", "create id str: "+id.toString());

            val sig = Utils.sign(id, privateKey)

//            val sigStr = DIDHelper.signData(diddocument, id);

//            Log.d("wangran", "create: sigStr = "+sigStr);
//            val sig = sigStr.toByteArray();

            Log.d("wangran", "create: signResult = "+sig);
            Log.d("wangran", "create: signResult = "+sig.toString());


            Log.d("wangran", "create: "+"sig")
            return MyEvent(id, pubKey, createdAt, tags, msg, sig)
        }

        public fun pubKeyCompress(pubkey: ByteArray): ByteArray {
            return when {
                pubkey.size == 33 && (pubkey[0] == 2.toByte() || pubkey[0] == 3.toByte()) -> pubkey
                pubkey.size == 65 && pubkey[0] == 4.toByte() -> {
                    val compressed = pubkey.copyOf(33)
                    compressed[0] = if (pubkey.last() % 2 == 0) 2.toByte() else 3.toByte()
                    compressed
                }
                else -> throw Secp256k1Exception("invalid public key")
            }
        }
    }


}