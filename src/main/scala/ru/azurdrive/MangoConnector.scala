package ru.azurdrive


import cats.effect.IO.fromEither
import cats.effect._
import org.http4s.{EntityDecoder, Request, Uri, UrlForm}
import org.http4s.dsl.io.POST
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.global
import com.typesafe.scalalogging.LazyLogging
import org.http4s.client.dsl.Http4sClientDsl
import io.circe.generic.auto._
import org.http4s.circe.jsonOf
import ru.azurdrive.config.Config
import scopt.OParser
import ru.azurdrive.config.ConfigBuilder.parser1
import purecsv.unsafe._
import ru.azurdrive.config.Data.Call.{seqStrToCall, strToCall}





object MangoConnector extends  IOApp with Http4sClientDsl[IO] with LazyLogging{


//  val testJson = """{"date_from":"1602547200","date_to":"1602633600"}"""


  final case class Key(key: String) {
    override def toString: String = "{\"key\":" + "\"" +  key + "\"" + "}"
  }
  implicit val keyDecoder: EntityDecoder[IO, Key] = jsonOf[IO, Key]

  private def getSign(config: Config, json: String): String =
    String.format("%064x",
      new java.math.BigInteger(1, java.security.MessageDigest.getInstance(config.crpt_hash)
        .digest((config.vpbx_api_key + json + config.vpbx_api_salt).getBytes(config.char_set))))


  private def postRequest(config: Config, url: Uri, json: String): IO[Request[IO]] =
    POST(
      UrlForm(
        "vpbx_api_key" -> config.vpbx_api_key,
        "sign" -> getSign(config, json),
        "json" -> json
    ),
    url
    )


  private def getData(client: Client[IO], config: Config): IO[List[String]] = {
    logger.info(s"json = ${makePayload(config)}")
    logger.info(s"sign = ${getSign(config, makePayload(config))}")

    for {
      request_uri <- fromEither(Uri.fromString(config.uri_request_report))
      _ = logger.info(s"request url = ${request_uri.renderString}")
      stats_result_uri <- fromEither(Uri.fromString(config.uri_stats_result))
      _ = logger.info(s"stats url = ${stats_result_uri.renderString}")
      key <- client.expect[Key](postRequest(config, request_uri, makePayload(config)))
      result <- client.expect[String](postRequest(config, stats_result_uri, key.toString))
    } yield result.split("\n").toList

  }

  //def fetchStats(vpbx_api_key: String): List[String] = BlazeClientBuilder[IO](global).resource
  // .use(postRequest(_, vpbx_api_key)).unsafeRunSync()

  private def makePayload(config: Config): String = {
    val fromPosix = config.from.getTime.getTime/1000
    val toPosix = config.to.getTime.getTime/1000
    "{\"date_from\":" + "\"" + fromPosix + "\"," + "\"date_to\":" + "\"" + toPosix + "\"}"
  }

  def runHttpClient(config: Config): IO[List[String]] = {
    BlazeClientBuilder[IO](global).resource.use(getData(_, config))
   //BlazeClientBuilder[IO](global).resource.use(makeRequests(_, config: Config, uri"https://app.mango-office.ru/vpbx/stats/request", uri"https://app.mango-office.ru/vpbx/stats/result")).as(ExitCode.Success)


  }

  def  writeToCsv(res: List[String]): IO[Unit] = IO{
    //logger.info(strToCall(res.head.trim).toCSV())
    //logger.info(res.head.trim)
    seqStrToCall(res).writeCSVToFileName("out3.csv")
  }


  def run(args: List[String]): IO[ExitCode] =  {

    OParser.parse(parser1, args, Config()) match {
      case Some(config) =>
        for {
          res <- runHttpClient(config)
          _ <- writeToCsv(res)
        } yield ExitCode.Success
      case _ => IO.pure(ExitCode.Error)
    }


  }
}
