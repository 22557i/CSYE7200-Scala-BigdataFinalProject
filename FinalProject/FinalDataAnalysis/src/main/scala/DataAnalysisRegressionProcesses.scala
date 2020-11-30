
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, RegressionEvaluator}
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel, RandomForestRegressionModel, RandomForestRegressor}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object DataAnalysisRegressionProcesses {
  val DAC = new DataAnalysisController

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    Logger.getRootLogger().setLevel(Level.ERROR)


    DAC.spark.sparkContext.setLogLevel("ERROR")

    val hadoopConf = DAC.spark.sparkContext.hadoopConfiguration
    hadoopConf.set("fs.hdfs.impl", classOf[org.apache.hadoop.hdfs.DistributedFileSystem].getName)
    hadoopConf.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName)
//    DAC.storeCSVAfterClean(DAC.OLD_PATH)

    var rdd= DAC.spark.sparkContext.parallelize(List("------------先大致看下数据-----------------"))
    rdd.collect().foreach(println)
    val df = DAC.loadData(DAC.PATH+".csv")
    val splited = df.randomSplit(Array(0.8,0.2),666)
    val train = splited(0)
    val test = splited(1)
    val rfrModel = randomForestRegressionModelGenerator(train,test,DAC.spark)
    val linearModel = linearRegressionModelGenerator(train,test,DAC.spark)
    DAC.spark.stop()


  }

  def logisticRegModelGenerator(train:DataFrame,test:DataFrame ,sc: SparkSession) : LogisticRegressionModel ={

    val numIterations = 10
    val lr = new LogisticRegression()
      .setMaxIter(numIterations)
      .setRegParam(0.3)
      .setElasticNetParam(0)
      .setLabelCol("label")
      .setFeaturesCol("features")
      .setPredictionCol("prediction")



    var model  = lr.fit(train)

    //    println(s"Coefficients: ${model.coefficients} Intercept: ${model.intercept}")
    val predictions = model.transform(test)
    predictions.show(10)

    val predictionRdd = predictions.select("label","prediction").rdd.map{
      case Row(prediction:Double, label:Double)=>(prediction,label)
    }
    val metrics = new MulticlassMetrics(predictionRdd)
    val accuracy = metrics.accuracy

    val weightedPrecision = metrics.weightedPrecision

    val weightedRecall = metrics.weightedRecall

    val f1 = metrics.weightedFMeasure

    println(s"LR评估结果：\n分类正确率：${accuracy}\n加权正确率：${weightedPrecision}\n加权召回率：${weightedRecall}\nF1值：${f1}")
    val trainingSummary = model.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
    //trainingSummary.residuals.show()
    //    println(s"RMSE: ${trainingSummary.r}")
    //    println(s"r2: ${trainingSummary.r2}")
    trainingSummary.predictions.show()
    model
  }

  def randomForestRegressionModelGenerator(train:DataFrame,test:DataFrame ,sc: SparkSession): RandomForestRegressionModel = {
    val rf = new RandomForestRegressor()
      .setLabelCol("label")
      .setFeaturesCol("features")
      .setNumTrees(20)
    val pipeline = new Pipeline()
      .setStages(Array(rf))

    val model = pipeline.fit(train)
    val predictions = model.transform(test)
    predictions.select("prediction", "label", "features").show(5)

    // Select (prediction, true label) and compute test error.
    predictions.show()
    val evaluator = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("rmse")
    val rmse = evaluator.evaluate(predictions)
    println("Root Mean Squared Error (RMSE) on test data = " + rmse)


    val rfModel = model.stages(0).asInstanceOf[RandomForestRegressionModel]

    //println("Learned regression forest model:\n" + rfModel.featureImportances)
//
//    rf.fit(train)
      rfModel
  }

  def linearRegressionModelGenerator(train:DataFrame,test:DataFrame, sc:SparkSession) : LinearRegressionModel = {
    val lr = new LinearRegression()
      .setMaxIter(10000)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    val linearModel = lr.fit(train)
    println(s"Coefficients: ${linearModel.coefficients} Intercept: ${linearModel.intercept}")

    val trainingSummary = linearModel.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")
    trainingSummary.predictions.show()
    linearModel
  }
}
