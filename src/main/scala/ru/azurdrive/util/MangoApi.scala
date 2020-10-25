package ru.azurdrive.util

import cats.effect.IO
import cats.effect.IO.fromEither
import com.typesafe.scalalogging.LazyLogging
import org.http4s.{EntityDecoder, Status, Uri}
import org.http4s.client.Client
import ru.azurdrive.MangoConnector.postRequest
import ru.azurdrive.config.Config
import ru.azurdrive.config.Data.Key
import ru.azurdrive.util.Http.getSign
import ru.azurdrive.util.Http.makePayload
import org.http4s.circe.jsonOf
import io.circe.generic.auto._

object MangoApi extends LazyLogging{

  implicit val keyDecoder: EntityDecoder[IO, Key] = jsonOf[IO, Key]

   def getKey(client: Client[IO], config: Config, key: Key): IO[Key] = {
    logger.info(s"json = ${makePayload(config)}")
    logger.info(s"sign = ${getSign(config, makePayload(config))}")

    for {
      request_uri <- fromEither(Uri.fromString(config.uri_request_report))
      _ = logger.info(s"request url = ${request_uri.renderString}")
      responseWithKey <- postRequest(config, request_uri, makePayload(config))
      key <- client.expect[Key](responseWithKey)
    } yield key
  }

  def getResultStatus(client: Client[IO], config: Config, key: Key): IO[Status] = {
    for {
      stats_result_uri <- fromEither(Uri.fromString(config.uri_stats_result))
      _ = logger.info(s"stats url = ${stats_result_uri.renderString}")
      responseResult <- postRequest(config, stats_result_uri, key.toString)
      status <- client.status(responseResult)
      _ = logger.info(s"result is ready? = $status, status.isSuccseed = ${status.isSuccess}")
    } yield status
  }

  def fetchResults(client: Client[IO], config: Config, key: Key): IO[List[String]] = {
    logger.info(s"fetching results...")
    for {
      stats_result_uri <- fromEither(Uri.fromString(config.uri_stats_result))
      responseResult <- postRequest(config, stats_result_uri, key.toString)
      result <- client.expect[String](responseResult)
      res = result.split("\n").toList
      _ = logger.info(s"${res.length} rows have been downloaded")
    }  yield result.split("\n").toList

  }


}
