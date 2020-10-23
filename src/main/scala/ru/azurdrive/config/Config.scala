package ru.azurdrive.config

import java.util.Calendar

final case class Config(vpbx_api_key: String = "",
                  vpbx_api_salt: String = "",
                  crpt_hash: String = "SHA-256",
                  char_set: String = "UTF-8",
                  uri_request_report: String = "https://app.mango-office.ru/vpbx/stats/request",
                  uri_result_stats: String = "https://app.mango-office.ru/vpbx/result/stats",
                  uri_stats_result: String = "https://app.mango-office.ru/vpbx/stats/result",
                  from: java.util.Calendar = Calendar.getInstance(),
                  to: java.util.Calendar = Calendar.getInstance()
                 )


