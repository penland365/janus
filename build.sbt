import com.typesafe.sbt.SbtNativePackager._

lazy val buildSettings = Seq(
  organization := "io.penland365",
  scalaVersion := "2.11.7"
)

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-unused-import",
  "-Ywarn-numeric-widen",
  "-Xfuture"
)

lazy val baseSettings = Seq(
  scalacOptions ++= compilerOptions, 
  scalacOptions in (Compile, console) := compilerOptions
)

lazy val allSettings = buildSettings ++ baseSettings

lazy val coreVersion = "0.0.1-SNAPSHOT"

lazy val testVersion  = "0.0.1-SNAPSHOT"

lazy val root = project.in(file("."))
  .settings(allSettings)
  .aggregate(core, test)
  .dependsOn(core, test)

lazy val core = project
  .settings(moduleName := "janus-core")
  .settings(version := coreVersion)
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.spire-math"  %%  "cats"      %   "0.2.0",
      "joda-time"       %   "joda-time" %   "2.8.2"
    )
  )

lazy val test = project
  .settings(moduleName := "janus-test")
  .settings(allSettings)
  .settings(version := testVersion)
  .settings(packageArchetype.java_application)
  .dependsOn(core)
