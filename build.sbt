ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "FillMask Scala"
  )

val akkaVersion = "2.7.0"

scalacOptions += "-target:jvm-1.8"
resolvers += Resolver.jcenterRepo

libraryDependencies += "ai.djl" % "api" % "0.19.0"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion

libraryDependencies += "ai.djl.tensorflow" % "tensorflow-model-zoo" % "0.19.0"
libraryDependencies += "ai.djl.tensorflow" % "tensorflow-native-auto" % "2.4.1"

// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.6"

val logbackVersion = "1.4.5"
// logback - backend for slf4j
libraryDependencies += "ch.qos.logback" % "logback-classic" % logbackVersion % Runtime
