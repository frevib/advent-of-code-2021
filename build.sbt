import sbt.Keys.libraryDependencies

name := "aoc2021"

version := "0.1"

scalaVersion := "2.13.7"
val ZIOVersion        = "1.0.12"
val ZIONIOVersion        = "1.0.0-RC10"

lazy val commonDependencies = Seq(
  // ZIO
  "dev.zio"          %% "zio"              % ZIOVersion,
  "com.github.wi101" %% "embroidery"       % "0.1.1",
  //  "dev.zio"          %% "zio-interop-cats" % ZIOInterop,
  "dev.zio"          %% "zio-test"         % ZIOVersion % "test",
  "dev.zio"          %% "zio-test-sbt"     % ZIOVersion % "test",
)


lazy val day01 = project.settings(
  name := "day01",
  libraryDependencies ++= commonDependencies ++
    Seq("dev.zio" %% "zio-nio" % ZIONIOVersion)
)

