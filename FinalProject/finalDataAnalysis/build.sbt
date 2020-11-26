name := "FinalDataAnalysis"

version := "0.1"

scalaVersion := "2.12.10"

//resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.0"
//libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.5"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
//libraryDependencies += "com.cloudera.sparkts" % "sparkts" % "0.4.0"
//libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.0"