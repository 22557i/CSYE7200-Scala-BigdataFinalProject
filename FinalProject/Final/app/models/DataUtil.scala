package models

import models.DataAnalysisController.spark
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.functions.approx_count_distinct

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

  def getRow(df: DataFrame, n:Int): Row = {
    df.orderBy(-df("price")).distinct().limit(n).collect.toList.last
  }
}
