package models


import models.DataAnalysisController.spark
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.functions._


object DataUtil {
  def countNumber(data: DataFrame,label:String):String = {
    data.agg(approx_count_distinct(label)).collect().map(_(0)).toList(0).toString
  }

  def rangeOfPrice(data:DataFrame) : String = {
    data.createOrReplaceTempView("df_table")
    val s1 = spark.sql("SELECT MIN(price) as maxval FROM df_table").collect().map(_(0)).toList(0).toString
    val s2 = spark.sql("SELECT MAX(price) as maxval FROM df_table").collect().map(_(0)).toList(0).toString
  s1.concat(" -- ").concat(s2)
  }
  def countSum(data:DataFrame, col:String) : String = {
    data.agg(sum(col)).collect().map(_(0)).toList(0).toString
  }
  def countAverageSum(data:DataFrame, col:String) : String = {
    (data.agg(sum(col)).collect().map(_ (0)).toList(0).asInstanceOf[Double] / data.count()).formatted("%.2f")+""
  }

  def countAverageAva(data:DataFrame) : String = {
    (data.agg(sum("availability_365")).collect().map(_ (0)).toList(0).asInstanceOf[Double] / data.count()).formatted("%.2f")+""
  }

}
