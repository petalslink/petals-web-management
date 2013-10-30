import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

   def fromEnv(name: String) = System.getenv(name) match {
      case null => None
      case value => Some(value)
   }

   val appName = fromEnv("project.artifactId").getOrElse("petals-web-management")
   val appVersion = fromEnv("project.version").getOrElse("1.0-SNAPSHOT")

   val appDependencies = Seq(
      javaCore,
      javaJdbc,
      javaEbean
   )
  
   val main = play.Project(appName, appVersion, appDependencies).settings(
      unmanagedBase <<= baseDirectory { base => base / "target/lib" }
   )
}
