package ru.azurdrive


import cats.effect.{IO, _}
import com.typesafe.scalalogging.LazyLogging
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io.POST
import org.http4s.{Request, Uri, UrlForm}
import ru.azurdrive.config.Config
import ru.azurdrive.config.ConfigBuilder.parser1
import scopt.OParser
import scala.concurrent.ExecutionContext.global
import ru.azurdrive.config.Config.SLEEPTIME
import ru.azurdrive.config.Data.Key
import ru.azurdrive.util.Http.getSign
import ru.azurdrive.util.MangoApi.{getResultStatus, fetchResults, getKey}
import ru.azurdrive.config.Config.OK
import ru.azurdrive.util.Csv.writeToCsv





object MangoConnector extends  IOApp with Http4sClientDsl[IO] with LazyLogging{

  def postRequest(config: Config, url: Uri, json: String): IO[Request[IO]] =
    POST(
      UrlForm(
        "vpbx_api_key" -> config.vpbx_api_key,
        "sign" -> getSign(config, json),
        "json" -> json
      ),
      url
    )

  /**
   * Return call stats from mango-office
   * @param vpbx_api_key - key from your account
   * @param vpbx_api_salt salt from your account
   * @param from date from
   * @param to date do
   * @return stats in csv formats
   */
  def fetchStats(vpbx_api_key: String,
                 vpbx_api_salt: String,
                 from: java.util.Calendar,
                 to: java.util.Calendar): List[String] = {
    val config = Config(
      vpbx_api_key = vpbx_api_key,
      vpbx_api_salt = vpbx_api_salt,
      from = from,
      to = to
    )
    getCsvResponse(config).unsafeRunSync()
  }

  private def runHttp[A](f: (Client[IO], Config, Key) => IO[A], config: Config, key: Key = Key("")): IO[A] =
    BlazeClientBuilder[IO](global).resource.use(f(_, config, key))

  private def getCsvResponse(config: Config): IO[List[String]] =
    for {
      key <- runHttp(getKey, config)
      reportReady <- runHttp(getResultStatus, config, key)
      res <- if (reportReady.code == OK) runHttp(fetchResults, config, key) else {Thread.sleep(SLEEPTIME); getCsvResponse(config)}
    }  yield res



   def run(args: List[String]): IO[ExitCode] =  {

    OParser.parse(parser1, args, Config()) match {
      case Some(config) =>
        for {
          response <- getCsvResponse(config)
          _ <- writeToCsv(response)
        } yield ExitCode.Success
      case _ => IO.pure(ExitCode.Error)
    }


  }
}
