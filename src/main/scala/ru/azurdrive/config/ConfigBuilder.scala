package ru.azurdrive.config

import java.util.Calendar

import scopt.{OParser, OParserBuilder}

object ConfigBuilder {
  private val builder: OParserBuilder[Config] = OParser.builder[Config]
  val parser1: OParser[Unit, Config] = {
    import builder._
    OParser.sequence(
      programName("java -jar mongocon.jar"),
      head("mangocon", "0.1"),
      opt[String]('k', "vpbx_api_key")
        .required()
        .action((x, c) => c.copy(vpbx_api_key = x))
        .text("mandatory vpbx api key"),
      opt[String]('s', "vpbx_api_salt")
        .required()
        .action((x, c) => c.copy(vpbx_api_salt = x))
        .text("mandatory vpbx api salt"),
      opt[String]('h', "crpt_hash")
        .optional()
        .action((x, c) => c.copy(crpt_hash = x))
        .text("optional SHA-256 by default"),
      opt[String]('c', "char_set")
        .optional()
        .action((x, c) => c.copy(char_set = x))
        .text("optional UTF-8 by default"),
      opt[String]('r', "uri_request_report")
        .optional()
        .action((x, c) => c.copy(uri_request_report = x))
        .text("optional https://app.mango-office.ru/vpbx/stats/request by default"),
      opt[String]('n', "uri_result_stats")
        .optional()
        .action((x, c) => c.copy(uri_result_stats = x))
        .text("optional https://app.mango-office.ru/vpbx/result/stats by default"),
      opt[String]('r', "uri_stats_result")
        .optional()
        .action((x, c) => c.copy(uri_stats_result = x))
        .text("optional https://app.mango-office.ru/vpbx/result/stats by default"),
      opt[Calendar]('f', "from")
        .required()
        .action((x, c) => c.copy(from = x))
        .text("mandatory accepts a value like --from 2000-12-01"),
      opt[Calendar]('t', "to")
        .required()
        .action((x, c) => c.copy(to = x))
        .text("mandatory accepts a value like --to 2000-12-01"),
      help("help").text("print help message")
    )
  }
}
