ThisBuild / version := "0.1.1-SNAPSHOT"

// ThisBuild / scalaVersion := "3.5.0"
// ThisBuild / scalaVersion := "2.13.12"
ThisBuild / scalaVersion := "2.12.19"

val awsSDK = "2.29.15"
// val akkaVersion = "2.10.0"
val akkaVersion = "2.9.0-M1"
val akkaOtherVersion = "10.5.3"

lazy val root = (project in file("."))
  .settings(
    name := "Exercises441"
  )

// javaHome := Some(file("/usr/lib/jvm/java-1.8.0-openjdk-amd64"))
javacOptions ++= Seq("-source", "11", "-target", "11")
// scalacOptions += "-target:jvm-1.8"
javaOptions ++= Seq(
  "--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED"
)
javaOptions ++= Seq("-Xms4g", "-Xmx16g", "-XX:+UseG1GC")
javaOptions in Global ++= Seq("-Xms4g", "-Xmx16g")

resolvers ++= Seq(
  Resolver.mavenCentral,                               // Maven Central repository
  "Akka library repository" at "https://repo.akka.io/maven",
  "Apache Snapshots" at "https://repository.apache.org/content/repositories/snapshots",
  "Apache repo" at "https://repository.apache.org/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Deeplearning4j" at "https://dl.bintray.com/deeplearning4j/deeplearning4j/",
  "DL4J Releases" at "https://oss.sonatype.org/content/repositories/public/"
)

resolvers += Resolver.sbtPluginRepo("releases")

// AWS
libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "bedrock" % awsSDK
)

// Akka
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.typesafe.akka" %% "akka-pki" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaOtherVersion, 
  "com.typesafe.akka" %% "akka-stream" % akkaVersion
)


// misc. dependencies
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.5.8",
  "org.slf4j" % "slf4j-api" % "2.0.16",
  "org.slf4j" % "slf4j-simple" % "2.0.16",
  "com.typesafe" % "config" % "1.4.3",
  // Add scala-xml for Scala 3 explicitly with version 2.0.1, 
  // which is compatible with Scala 3
  // "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
  "org.scala-lang.modules" %% "scala-xml" % "1.2.0"
)

// test dependencies
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.apache.hadoop" % "hadoop-minicluster" % "3.3.0" % Test,
  "org.mockito" % "mockito-core" % "5.14.2" % Test,
  "org.mockito" %% "mockito-scala" % "1.17.37" % Test, // Mockito-Scala integration
  "org.mockito" %% "mockito-scala-scalatest" % "1.17.37" % Test,
  "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test
)

unmanagedSources in Compile := (unmanagedSources in Compile).value.filterNot(_.name.matches(".*MapReduce.scala"))

// Test / excludeFilter := "*MapReduceSpec.scala"

Test / fork := true
Test / javaOptions ++= Seq(
  "-XX:MaxDirectMemorySize=8g",  // Adjust as needed
  "-Xmx4g"  // Adjust as needed
)

// Test options (optional, for running specific tests or logging)
// Test / testOptions += Tests.Argument("-oD") // Show test duration

// assembly / mainClass := Some("SplitterMapReduce")

import sbtassembly.AssemblyPlugin.autoImport._

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", serviceFile) if serviceFile.contains("org.nd4j") =>
    MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => xs match {
    case "MANIFEST.MF" :: Nil => MergeStrategy.discard  // Discard duplicate MANIFEST files
    case "INDEX.LIST" :: Nil => MergeStrategy.discard   // Discard INDEX.LIST files
    case "DEPENDENCIES" :: Nil => MergeStrategy.discard // Discard DEPENDENCIES files
    case "LICENSE" :: Nil => MergeStrategy.rename       // Rename LICENSE files to avoid conflicts
    case "NOTICE" :: Nil => MergeStrategy.rename        // Rename NOTICE files to avoid conflicts
    case _ => MergeStrategy.discard                    // Discard other META-INF files
  }
  case "reference.conf" => MergeStrategy.concat         // Concatenate reference.conf
  case "application.conf" => MergeStrategy.concat       // Concatenate application.conf
  case "rootdoc.txt" => MergeStrategy.concat            // Concatenate rootdoc.txt (if needed)
  case "module-info.class" => MergeStrategy.discard     // Discard module-info.class (Java 9+)
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.discard // Discard HTML docs
  case PathList(ps @ _*) if ps.last endsWith ".txt" => MergeStrategy.discard  // Discard TXT docs
  // Ignore the old mapreduce code which is in Scala 3
  // Should not be necessary if files are excluded by unmanagedSources already
  // case PathList(_, _, xs @ _*) if xs.last.matches(".*(MapReduce)\\.class") => MergeStrategy.discard
  case _ => MergeStrategy.first                        // Use the first file for other conflicts
}

