enablePlugins(ScalaJSPlugin, WorkbenchPlugin)

name := "Mazerace"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi" %%% "scalatags" % "0.6.1",
  "io.monix" %%% "minitest" % "2.0.0" % "test"
)

testFrameworks += new TestFramework("minitest.runner.Framework")



