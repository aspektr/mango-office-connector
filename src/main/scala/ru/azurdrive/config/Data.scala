package ru.azurdrive.config

import scala.util.{Failure, Success, Try}

object Data {

  val SEPARATOR = ";"

  /**
   *
   * @param records          идентификаторы записей разговоров в виде [rec1,rec2,rec3];
   * @param start            время начала разговора. Формат данных идентичен date_from;
   * @param finish           время окончания разговора. Формат данных идентичен date_from
   * @param answer           answer — время ответа на вызов. Если снятия трубки не было, передается значение 0. Формат данных идентичен date_from;
   * @param fromExtension    from_extension — идентификатор сотрудника ВАТС для
   *                         вызывающего абонента. Не передается в случае, если ВАТС не
   *                         удалось идентифицировать вызывающего абонента как сотрудника ВАТС,
   *                         либо у сотрудника ВАТС нет идентификатора (внутреннего номера).
   * @param fromNumber       номер вызывающего абонента (строка), в случае, если ВАТС удалось
   *                         определить номер. Опциональный параметр.
   * @param toExtension      идентификатор сотрудника ВАТС для вызываемого абонента.
   *                         Опциональный параметр. Не передается в случае, если ВАТС не
   *                         удалось идентифицировать вызываемого абонента как сотрудника ВАТС,
   *                         либо у сотрудника ВАТС нет идентификатора (внутреннего номера).
   * @param toNumber         номер вызываемого абонента (строка)
   * @param disconnectReason причина завершения вызова
   * @param entryId          идентификатор группы вызовов
   * @param lineNumber       линия (номер) ВАТС, использованная для обработки/размещения вызова
   * @param location         расположение вызова в ВАТС в момент завершения вызова, список возможных значений
   *                         указан в разделе "Уведомление о вызове"
   */
  final case class Call(
                         records: String,
                         start: Long,
                         finish: Long,
                         answer: Int,
                         fromExtension: String,
                         fromNumber: String,
                         toExtension: String,
                         toNumber: String,
                         disconnectReason: String,
                         entryId: String,
                         lineNumber: String,
                         location: String  = ""
                       )

  object Call {
    def seqStrToCall(in: List[String]): Seq[Call] = in.map(strToCall)

    def strToCall(in: String): Call = {
      print(in)
      print(in.trim)
      val attr = in.trim.split(SEPARATOR)
      Call(tryGetVal(0, attr),
        tryGetVal(1, attr).toLong,
        tryGetVal(2, attr).toLong,
        tryGetVal(3, attr).toInt,
        tryGetVal(4, attr),
        tryGetVal(5, attr),
        tryGetVal(6, attr),
        tryGetVal(7, attr),
        tryGetVal(8, attr),
        tryGetVal(9, attr),
        tryGetVal(10, attr),
        tryGetVal(11, attr))
    }

    def tryGetVal(ind: Int, a: Array[String]):String =
      Try(a(ind)) match {
        case Success(value) => value
        case Failure(_) => ""
      }

  }

}
