organization := "net.astail"

name := "ika3"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.8"

resolvers ++= Seq(
  "spring.io" at "https://repo.spring.io/plugins-release/",
  "jcenter.bintray.com" at "https://jcenter.bintray.com"
)

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.32.0",
  "com.typesafe" % "config" % "1.4.2",
  "net.dv8tion" % "JDA" % "4.2.0_247",
  "com.danielasfregola" %% "twitter4s" % "8.0",
  "ch.qos.logback" % "logback-classic" % "1.4.0",
  "org.slf4j" % "slf4j-api" % "2.0.0",
  "com.twitter" %% "util-core" % "22.7.0",
  "org.json4s" %% "json4s-native" % "4.0.5",
  "org.json4s" %% "json4s-jackson" % "4.0.5",
  "org.json4s" %% "json4s-ext" % "4.0.5",
  "org.jsoup" % "jsoup" % "1.15.3"
)

enablePlugins(JavaAppPackaging)

Compile / sourceGenerators += Def.task {
  import scala.sys.process.Process

  val file = (Compile / sourceManaged).value / "net" / "astail" / "Git.scala"
  val longHash = Process("""git log -1 --format="%H"""").!!
  val shortHash = Process("""git log -1 --format="%h"""").!!
  val log = Process("git show -s").!!
  val tag = Process("git rev-parse --verify HEAD").!!
  IO.write(file, s"""package net.astail
                    |
                    |object Git {
                    |  val longHash = $longHash
                    |  val shortHash = $shortHash
                    |  val tag = \"\"\"$tag\"\"\"
                    |  val log = \"\"\"$log\"\"\"
                    |}""".stripMargin)
  Seq(file)
}
