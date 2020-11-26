import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.apache.spark.sql.{DataFrame, SparkSession}

class DataAnalysisController {
  val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

  def loadData(file: String) = {
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
    dataSet.show(5,false)
    dataSet
  }

  def process(df: DataFrame): DataFrame = {
    // Transfer the data type
    val df1 = df
      .withColumn("longitude", col("longitude").cast(DoubleType))
      .withColumn("latitude", col("latitude").cast(DoubleType))
      .drop("name")
      .drop("id")
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
      .drop("neighbourhood") //optional
      .drop("reviews_per_month") //optional
    df1.createOrReplaceTempView("df1")

    spark.sql("SELECT * FROM df1 WHERE latitude <= 41")
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
