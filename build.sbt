organization in ThisBuild := "io.methvin.fastforward"
licenses in ThisBuild := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html"))
homepage in ThisBuild := Some(url("https://github.com/gmethvin/fastforward"))
scmInfo in ThisBuild := Some(
  ScmInfo(url("https://github.com/gmethvin/fastforward"), "scm:git@github.com:gmethvin/fastforward.git")
)
developers in ThisBuild := List(
  Developer("gmethvin", "Greg Methvin", "greg@methvin.net", new URL("https://github.com/gmethvin"))
)

scalaVersion in ThisBuild := "2.12.4"

lazy val macros = (project in file("macros"))
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    )
  )

lazy val root = (project in file("."))
  .settings(
    PgpKeys.publishSigned := {},
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    skip in publish := true
  )
  .aggregate(macros)

publishMavenStyle in ThisBuild := true
publishTo in ThisBuild := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

import ReleaseTransformations._
releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)

scalafmtOnCompile in ThisBuild := true
