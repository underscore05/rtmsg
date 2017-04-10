name := """rtmsg"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "it, test",
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2",
  "com.typesafe.play" %% "play-mailer" % "5.0.0",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.github.tminglei" %% "slick-pg" % "0.13.0",
  "com.github.tminglei" %% "slick-pg_date2" % "0.13.0",

  "com.rabbitmq" % "amqp-client" % "4.1.0",
  "org.mindrot" % "jbcrypt" % "0.4",

  "com.pauldijou" %% "jwt-play-json" % "0.7.1",
  "se.radley" % "play-plugins-enumeration_2.10" % "1.1.0"
)

parallelExecution in IntegrationTest := false

sourceDirectory in IntegrationTest := baseDirectory.value / "it"

fork in run := true