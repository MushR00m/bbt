name := """bbt"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.sedis" %% "sedis" % "1.2.2",
  "com.belerweb" % "pinyin4j" % "2.5.0",
  "com.github.fernandospr" % "javapns-jdk16" % "2.2.1",
  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "mysql" % "mysql-connector-java" % "5.1.18"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
