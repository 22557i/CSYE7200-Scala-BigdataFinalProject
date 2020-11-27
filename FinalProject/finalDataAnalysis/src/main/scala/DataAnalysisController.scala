import org.apache.spark.ml.feature.{LabeledPoint, StringIndexer}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.apache.spark.sql.{DataFrame, SparkSession}


class DataAnalysisController {
  val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
  val PATH:String = "dataSet"
  val OLD_PATH:String = "AB_NYC_2019.csv"
  def loadData(file: String):DataFrame = {
    import spark.implicits._
      val dataSet = spark.sparkContext.textFile(file)
      val header = dataSet.first()
      val dataSet1 = dataSet.filter(row=> row!=header)

      val parsedData = dataSet1.map{line=>
        val parts = line.split(",")
        LabeledPoint(parts(0).toDouble,
          Vectors.dense(parts(1).toDouble
            ,parts(2).toDouble
            ,parts(3).toDouble
            ,parts(4).toDouble
            ,parts(5).toDouble
            ,parts(6).toDouble
            ,parts(7).toDouble
            ,parts(8).toDouble
            ,parts(9).toDouble
          )
          )
      }.toDF("label","features")
    parsedData.show()
    parsedData
  }
  def storeCSVAfterClean(file:String) :Unit ={
    val dataSet = spark.read.
      option("header", "true")
      .option("mode", "DROPMALFORMED") // delete bad-formatted record
      .option("inferSchema", "false")
      .format("com.databricks.spark.csv")
      .load(file)
      .distinct() // avoid duplicated records
      .na.drop() // delete rows which contains null/ NaN value
      .toDF()
      .cache()
    var df1 = dataSet
      .drop("id")
      .drop("name")
      .drop("host_name")
      .drop("host_id")
      .drop("last_review")
      .drop("neighbourhood")
      .drop("last_review")
    val catalog_features = Array("neighbourhood_group","room_type")
    for(cf<-catalog_features) {
      val indexer = new StringIndexer()

        .setInputCol(cf)

        .setOutputCol(cf.concat("_index"))
      val train_index_model = indexer.fit(df1)
      val df2 = train_index_model.transform(df1)
      df1 = df2
    }

    val df2 = df1.drop("neighbourhood_group").drop("room_type")
    df2.show()
    df2.coalesce(1).write.option("header",true).csv(PATH)
  }

  def process(df: DataFrame): DataFrame = {
    // Transfer the data type
    val df1 = df
      .withColumn("longitude", col("longitude").cast(DoubleType))
      .withColumn("latitude", col("latitude").cast(DoubleType))
      .drop("name")
      .drop("host_name")
      .drop("last_review")
    df1.createOrReplaceTempView("df1")

    // Process the outliers
    //    df1.createOrReplaceTempView("df1")
    //    val outliersDF = spark.sql("SELECT * FROM df1 WHERE latitude > 41")
    //    outliersDF.createOrReplaceTempView("outliersDF")
    //    val othersDF = spark.sql("SELECT * FROM df1 WHERE latitude <= 41")
    //    othersDF.createOrReplaceTempView("othersDF")
    //    othersDF
    df1
  }

  def processForRegression(df: DataFrame): DataFrame = {
    val df1 = df.drop("name")
      .drop("id")
      .drop("host_name")
      .drop("last_review")
      .drop("host_id")
      .drop("calculated_host_listings_count")
      .drop("availability_365")
      .drop("neighbourhood") //optional
      .drop("reviews_per_month") //optional
      .withColumn("price",col("price").cast(IntegerType))
    df1.createOrReplaceTempView("df1")

    val res = spark.sql("SELECT * FROM df1 WHERE latitude <= 41")
    res.show(5,false)
    res
  }

  def processForClassification(df: DataFrame): DataFrame = {
    val df1 = df.drop("name")
      .drop("id")
      .drop("host_name")
      .drop("last_review")
      .drop("host_id")
      .drop("neighbourhood") //optional
      .drop("reviews_per_month") //optional
    df1.createOrReplaceTempView("df1")
    spark.sql("SELECT * FROM df1 WHERE latitude <= 41")
  }
}
