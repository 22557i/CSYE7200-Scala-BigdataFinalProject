package models
import DataAnalysisRegressionProcesses.DAC
import models.DataAnalysisController.spark
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.clustering.{KMeans, KMeansModel}
import org.apache.spark.sql.Row

object DataAnalysisClassificationProcess {
  val DAC = new DataAnalysisController

  def main(args: Array[String]): Unit = {
//
//    Logger.getLogger("org").setLevel(Level.OFF)
//    Logger.getLogger("akka").setLevel(Level.OFF)
//    Logger.getRootLogger().setLevel(Level.ERROR)
//
//
//    DAC.spark.sparkContext.setLogLevel("ERROR")
//
//    val hadoopConf = DAC.spark.sparkContext.hadoopConfiguration
//    hadoopConf.set("fs.hdfs.impl", classOf[org.apache.hadoop.hdfs.DistributedFileSystem].getName)
//    hadoopConf.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName)
//
//    var rdd= DAC.spark.sparkContext.parallelize(List("------------先大致看下数据-----------------"))
//    rdd.collect().foreach(println)
//    val df = DAC.loadData(DAC.PATH+".csv")
//
//    val splited = df.randomSplit(Array(0.8,0.2),666)
//    val train = splited(0)
//    val test = splited(1)
//
//    val clusterIndex :Int = 0;
//
//    val ks:Array[Int] = Array(3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
//    ks.foreach(k => {
//      val kmeansmodel = new KMeans()
//        .setK(k)
//        .setFeaturesCol("features")
//        .setPredictionCol("prediction")
//        .fit(train)
//      val results = kmeansmodel.transform(test)
//      //    results.show(false)
//
////      kmeansmodel.clusterCenters.foreach(
////        center => {
////          println("Cluster center：" + center)
////        })
//      val ssd = kmeansmodel.computeCost(train)
//      println("sum of squared distances of points to their nearest center when k=" + k + " -> "+ ssd)
//
//    })
//    DAC.spark.stop()
  }
}
