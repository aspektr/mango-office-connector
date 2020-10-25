package ru.azurdrive.util

import cats.effect.IO
import ru.azurdrive.config.Config.FILENAME
import ru.azurdrive.config.Data.Call.seqStrToCall
import purecsv.unsafe._

object Csv {

  def  writeToCsv(res: List[String]): IO[Unit] = IO{
    seqStrToCall(res).writeCSVToFileName(FILENAME)
  }

}
