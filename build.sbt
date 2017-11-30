import com.typesafe.sbt.SbtNativePackager.packageArchetype

name := "trailer-maker"
organization := "io.trailermaker"
version := "0.1"
scalaVersion := "2.12.4"
isSnapshot := true

libraryDependencies += "ch.qos.logback"             % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"  % "3.7.2"
libraryDependencies += "org.scalactic"              %% "scalactic"      % "3.0.4"
libraryDependencies += "org.scalatest"              %% "scalatest"      % "3.0.4" % "test"
libraryDependencies += "com.github.pathikrit"       %% "better-files"   % "3.2.0"

wartremoverErrors ++= Warts.allBut(
  Wart.DefaultArguments,
  Wart.Nothing,
  Wart.Equals,
  Wart.NonUnitStatements
)

mainClass in assembly := Some("io.trailermaker.core.TrailerMaker")
// we specify the name for our fat jar

assemblyJarName in assembly := "trailer-maker.jar"
mainClass in Compile := Some("io.trailermaker.core.TrailerMaker")

// using the java server for this application. java_application would be fine, too
//packageArchetype.java_application
//
//// removes all jar mappings in universal and appends the fat jar
//mappings in Universal := {
//  val universalMappings = (mappings in Universal).value
//  val fatJar = (assembly in Compile).value
//  val filtered = universalMappings filter {
//    case (file, name) =>  ! name.endsWith(".jar")
//  }
//  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
//}
//
//// the bash scripts classpath only needs the fat jar
//scriptClasspath := Seq( (assemblyJarName in assembly).value )

enablePlugins(JavaAppPackaging, DebianPlugin, RpmPlugin)

// Linux packaging
packageSummary := "generate trailers easily from a video file"

packageDescription :=
"""Trailer maker is a small program that generates trailers from a video file.
 It cuts parts from within the video of a specified cut length at various timestamps.
 Then, the cut parts are glued together.""".stripMargin

// Debian

maintainer := "Alexandre Langlais (alanglaispro@gmail.com)"
debianPackageDependencies in Debian ++= Seq("java2-runtime", "ffmpeg (>= 3.3.3)")
name in Debian += "trailer-maker"

// Rpm
rpmVendor := "trailermaker"
rpmLicense := Some("MIT")
rpmRequirements := Seq("java", "ffmpeg >= 3.3.3")
rpmAutoreq := "yes"
rpmAutoprov := "yes"