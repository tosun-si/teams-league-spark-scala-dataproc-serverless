val scala2Version = "2.13.11"
val sparkVersion = "3.3.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "teams-league-spark-scala",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala2Version,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.16",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % "test",
    libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
    libraryDependencies += "com.google.cloud.spark" %% "spark-bigquery-with-dependencies" % "0.32.0"
  )