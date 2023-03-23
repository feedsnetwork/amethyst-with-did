package com.vitorpamplona.amethyst.did

import nostr.postr.toHex

class DIDPersona(
//    privKey: ByteArray? = null,
    pubKey: ByteArray
) {
    val privKey: ByteArray
    val pubKey: ByteArray
    var petName: String? = null
    val follows: Array<ByteArray>? = null

//    init {
//        if (privKey == null) {
//            if (pubKey == null) {
//                // create new, random keys
//                this.privKey = Utils.privkeyCreate()
//                this.pubKey = Utils.pubkeyCreate(this.privKey)
//            } else {
//                // this is a read-only account
//                check(pubKey.size == 32)
//                this.privKey = null
//                this.pubKey = pubKey
//            }
//        } else {
//            // as private key is provided, ignore the public key and set keys according to private key
//            this.privKey = privKey
//            this.pubKey = Utils.pubkeyCreate(privKey)
//        }
//    }

    init {
        if (pubKey != null) {
            this.pubKey = pubKey
        }else{
            this.pubKey = "".encodeToByteArray()
        }
        this.privKey = pubKey
    }

    override fun toString(): String {
        return "Persona( privateKey=${privKey.toHex()}, publicKey=${pubKey.toHex()}, petName=$petName, follows=${follows?.contentToString()})"
    }
}
