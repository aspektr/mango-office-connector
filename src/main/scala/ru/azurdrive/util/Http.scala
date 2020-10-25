package ru.azurdrive.util

import ru.azurdrive.config.Config

object Http {

  def getSign(config: Config, json: String): String =
    String.format("%064x",
      new java.math.BigInteger(1, java.security.MessageDigest.getInstance(config.crpt_hash)
        .digest((config.vpbx_api_key + json + config.vpbx_api_salt).getBytes(config.char_set))))

  def makePayload(config: Config): String = {
    val fromPosix = config.from.getTime.getTime/1000 + 10800
    val toPosix = config.to.getTime.getTime/1000 + 10800
    "{\"date_from\":" + "\"" + fromPosix + "\"," + "\"date_to\":" + "\"" + toPosix + "\"}"
  }




}
