name := "stratum"

version := "2017.11"

scalaVersion := "2.12.6"
val akkaVersion = "2.5.14"
val akkaHttpVersion = "10.1.4"
val alpakkaVersion = "0.16"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  ws, guice,
  "org.slf4j" % "slf4j-jdk14" % "1.7.25",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.mockito" % "mockito-all" % "2.0.2-beta" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "net.jadler" % "jadler-all" % "1.3.0" % Test
)

herokuAppName in Compile := "stratum"

