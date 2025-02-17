import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}
class DataAnalysisSpec extends FunSuite with Matchers with BeforeAndAfter{
  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
  Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
  private val master = "local[*]"
  private val appName = "testing"
  var spark: SparkSession = _


  test("Enriched Data successfully") {
    spark = new SparkSession.Builder().appName(appName).master(master).getOrCreate()
    val sc = spark.sqlContext

    val DAC = new DataAnalysisController

    val actualDF = DAC.loadData("dataSet1.csv")
//
    val schema = List(
      StructField("price", IntegerType, nullable = true),
      StructField("latitude", DoubleType, nullable = true),
      StructField("longitude", DoubleType, nullable = true),
      StructField("minimum_nights", IntegerType, nullable = true),
      StructField("number_of_reviews", IntegerType, nullable = true),
      StructField("reviews_per_month", IntegerType, nullable = true),
      StructField("calculated_host_listings_count", IntegerType, nullable = true),
      StructField("availability_365", IntegerType, nullable = true),
      StructField("neighbourhood_group_index", IntegerType, nullable = true),
      StructField("room_type_index", IntegerType, nullable = true),
    )

    val sampleRow = Seq(Row(59,40.75406,-73.80613,1,1,1,3,86,2,2))

    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(sampleRow),
      StructType(schema)
    )
    println(df.count()+"-------dfcount")
    println(actualDF.count()+"-------actualDFdfcount")
    df.count() should equal (1)
    actualDF.count() should equal(38677)
    actualDF.except(actualDF).count() should equal(0)
  }
}
