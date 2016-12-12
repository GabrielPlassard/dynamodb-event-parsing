organization := "fr.gplassard"

name := "dynamodb-event-parsing"

version := "1.0"

scalaVersion := "2.11.8"

val validationVersion = "2.0.1"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-events"  % "1.3.0",
  "io.github.jto" %% "validation-core"        % validationVersion,
  "org.specs2"    %% "specs2-core"            % "3.8.5" % "test"
)
