package com.vitorpamplona.amethyst.ui.note

import nostr.postr.toHex

fun ByteArray.toShortenHex(): String {
  return toHex().toShortenHex()
}

fun String.toShortenHex(): String {
  return replaceRange(10, length-10, "...")
}

