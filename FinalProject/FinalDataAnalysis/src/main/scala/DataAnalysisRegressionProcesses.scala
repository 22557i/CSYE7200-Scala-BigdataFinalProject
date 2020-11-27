
import org.apache.log4j.{Level, Logger}
import org.apache.parquet.format.IntType
import org.apache.spark
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{FeatureHasher, StringIndexer}
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, SparkSession}

object DataAnalysisRegressionProcesses {
  val DAC = new DataAnalysisController

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    Logger.getRootLogger().setLevel(Level.ERROR) //这里是用来抑制一大堆log信息的.


    DAC.spark.sparkContext.setLogLevel("ERROR")

    val hadoopConf = DAC.spark.sparkContext.hadoopConfiguration
    hadoopConf.set("fs.hdfs.impl", classOf[org.apache.hadoop.hdfs.DistributedFileSystem].getName)
    hadoopConf.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName)
    var rdd= DAC.spark.sparkContext.parallelize(List("------------先大致看下数据-----------------"))
    rdd.collect().foreach(println)

    val df = DAC.loadData(DAC.PATH+".csv")
    val numIterations = 100
    val stepSize = 0.1
    val lr = new LinearRegression()
      .setMaxIter(numIterations)
      .setRegParam(0.1)
      .setElasticNetParam(0.1)

    var model  = lr.fit(df)

    println(s"Coefficients: ${model.coefficients} Intercept: ${model.intercept}")

    val trainingSummary = model.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")
    trainingSummary.predictions.show()

    DAC.spark.stop()

    //---------------------------------------------------------
    //Start
  // var dataSet = .cache()
//    val categoricalFeatures = Seq("neighbourhood_group", "room_type" )
////    val numericFeatures = Seq("latitude",
////      "longitude",
////      "price",
////      "minimum_nights",
////      "number_of_reviews",
////      "calculated_host_listings_count",
////      "availability_365")
//
//    for(cata<- categoricalFeatures){
//      val stringModel = new StringIndexer()
//        .setInputCol(cata)
//        .setOutputCol(cata.concat("_index"))
//        .fit(dataSet)
//
//      val dataSet_enriched = stringModel.transform(dataSet)
//      dataSet = dataSet_enriched
//
//    }
//    dataSet.show()
//
//
//    val lr_model = logisticRegModelGenerator(dataSet,DAC.spark)
     DAC.spark.stop()

  }

  def logisticRegressionProcess(logisticRegressionModel: LogisticRegressionModel) :Unit = {

  }
  def logisticRegModelGenerator(df:DataFrame ,sc: SparkSession) : LogisticRegressionModel ={
    import sc.implicits._

    val splitSeed = 666
    val Array(trainData,testData) = df.randomSplit(Array(0.8,0.2),splitSeed)
    val lr = new LogisticRegression()
      .setMaxIter(370)
      .setRegParam(0.01)
      .setElasticNetParam(0.92)
      .setFeaturesCol("feature")
      .setPredictionCol("pre_result")
      .setLabelCol("price")

    val model_lr = lr.fit(trainData)
    println(s"每个特征对应系数: ${model_lr.coefficients} 截距: ${model_lr.intercept}")
    val predictions = model_lr.transform(testData)
    predictions.show(30)
    val evaluator = new BinaryClassificationEvaluator()
      .setLabelCol("price")
      .setRawPredictionCol("rawPrediction")
      .setMetricName("areaUnderROC")

    val accuracy = evaluator.evaluate(predictions)

    val lp = predictions.select(s"label", s"prediction")
    val counttotal = predictions.count()
    val correct = lp.filter($"label" === $"prediction").count()
    val wrong = lp.filter(!($"label" === $"prediction")).count()
    val truep = lp.filter($"prediction" === 0.0).filter($"label" === $"prediction").count()
    val falseN = lp.filter($"prediction" === 0.0).filter(!($"label" === $"prediction")).count()
    val falseP = lp.filter($"prediction" === 1.0).filter(!($"label" === $"prediction")).count()
    val ratioWrong = wrong.toDouble / counttotal.toDouble
    val ratioCorrect = correct.toDouble / counttotal.toDouble

    println(s"Count Total: $counttotal")
    println(s"Correct: $correct")
    println(s"Wrong: $wrong")
    println(s"True Positives: $truep")
    println(s"False Negatives: $falseN")
    println(s"False Positives: $falseP")
    println(s"Ratio of wrong results: $ratioWrong")
    println(s"Ration of correct results: $ratioCorrect")
    println(s"Accuracy is:  $accuracy")
    model_lr
  }
//  def linearRegressionProcess(df:DataFrame, sc:SparkSession): LinearRegressionModel={
//
//  }
}
