
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.sql.{DataFrame, SparkSession}

object DataAnalysisRegressionProcesses {
  val DAC = new DataAnalysisController

  def main(args: Array[String]): Unit = {

    val dataSet = DAC.processForRegression(DAC.loadData("AB_NYC_2019.csv")).cache()
    val categoricalFeatures = Seq("neighbourhood_group", "room_type" )
    val numericFeatures = Seq("latitude",
      "longitude",
      "price",
      "minimum_nights",
      "number_of_reviews",
      "calculated_host_listings_count",
      "availability_365")

  }

  def logisticRegressionProcess(logisticRegressionModel: LogisticRegressionModel) :Unit = {

  }
  def logisticRegModelGenerator(df :DataFrame,sc: SparkSession) : LogisticRegressionModel ={
    import sc.implicits._


    val featureColumns = Array("latitude","longitude","reviews_per_month","price")

    val indexLabelColunm = ""

    val splitSeed = 666
    val Array(trainData,testData) = df.randomSplit(Array(0.8,0.2),splitSeed)
    val lr = new LogisticRegression().setMaxIter(370).setRegParam(0.01).setElasticNetParam(0.92)
    val model = lr.fit(trainData)
    val predictions = model.transform(testData)
    predictions.show(30)
    val evaluator = new BinaryClassificationEvaluator()
      .setLabelCol("label")
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

    model
  }
}
