name := "mango"

version := "0.1"

scalaVersion := "2.12.3"

val http4sVersion = "0.21.7"

// Only necessary for SNAPSHOT releases
//esolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)


libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-circe" % http4sVersion,
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.13.0",
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.13.0"
)

libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.0-RC2"

libraryDependencies += "com.github.melrief" %% "purecsv" % "0.1.1"

assemblyJarName in assembly := "mangocon.jar"
test in assembly := {}
mainClass in assembly := Some("ru.azurdrive.MangoConnector")