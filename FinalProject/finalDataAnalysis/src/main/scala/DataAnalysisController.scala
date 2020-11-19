import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SparkSession}

class DataAnalysisController {
  val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

  // Load data
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

    dataSet
  }

  // Save spark dataframe
  def saveFile(df: DataFrame, path: String): Unit = {
    try {
      df.coalesce(1).write.option("header", "true").format("csv").save(path)

    } catch {
      case e1: IllegalArgumentException => print("Fail to save the file:" + e1)
      case e2: RuntimeException => print("Fail to save the file:" + e2)
      case e3: Exception => print("Fail to save the file:" + e3)
    }
  }

  def saveOutputResult(result: DataFrame, path: String): Unit = {
    try{
      //      repartition (preferred if upstream data is large, but requires a shuffle)
      //      coalesce(1)(can use when fit all the data into RAM on one worker thus)
      result.repartition(1).write.format("com.databricks.spark.csv").option("header", "true").save(path)
    }catch{
      case e1:IllegalArgumentException=> print("fail to save the data into csv:"+e1)
      case e2:RuntimeException => print("fail to save the data into csv:"+e2)
      case e3: Exception =>print("fail to save the data into csv:"+e3)
    }
  }

  // Change the saved csv folder to single csv file
  def changeFilePath(oldFile: String, newFile: String): Unit = {
    val sc: SparkContext = spark.sparkContext
    val fs = FileSystem.get(sc.hadoopConfiguration)
    val file = fs.globStatus(new Path(oldFile + "/part*"))(0).getPath().getName()
    fs.rename(new Path(oldFile + "/" + file), new Path(newFile))
    fs.delete(new Path(oldFile), true)
  }
}
