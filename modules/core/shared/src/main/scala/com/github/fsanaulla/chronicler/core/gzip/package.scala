package com.github.fsanaulla.chronicler.core

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

package object gzip {

  /***
    * Compress the data, and return compressed byte array and it length
    *
    * @since - 0.5.5
    * */
  def compress(data: Array[Byte]): (Int, Array[Byte]) = {
    val bArrOut    = new ByteArrayOutputStream()
    val gzippedOut = new GZIPOutputStream(bArrOut)

    gzippedOut.write(data)
    gzippedOut.close()

    val gzippedData   = bArrOut.toByteArray
    val contentLength = gzippedData.length
    contentLength -> gzippedData
  }
}
