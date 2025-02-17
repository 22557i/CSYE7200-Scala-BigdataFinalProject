package controllers

import javax.inject._
import play.api.mvc._
import models._
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.regression.{RandomForestRegressionModel, RandomForestRegressor}
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.text.SimpleDateFormat
import java.util.Date
import scala.io.Source

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {
  val data :DataFrame  = DataAnalysisController.loadBasicData(DataAnalysisController.OLD_PATH);
  //val predictModel: RandomForestRegressionModel =randomForestRegressionModelGenerator()
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  def index() = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.index(data))
  }


  def tables() = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.tables(data))
  }
  def forms() = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.forms(BasicForm.form))
  }
  def simpleFormPost2() = Action { implicit request: Request[AnyContent] =>
    BasicForm.form.bindFromRequest.fold(
      formWithErrors => {
        Ok(views.html.main("$167"))
      },
      formData => {
        val formData: BasicForm = BasicForm.form.bindFromRequest.get // Careful: BasicForm.form.bindFromRequest returns an Option
        //println(formData.Year.toString + "   "+ formData.CrimeType.toUpperCase);
        var predictValue = 0;

        predictValue =  formData.NeighbourhoodGroup match{
          case "Brooklyn" => -11
          case "Manhattan" => 38
          case "Queens"=> -22
          case "Bronx"=> -38
          case "Staten Island" =>110
          case _ => 0
        }
        predictValue+= (formData.RoomType match{
          case "Shared room" => -38
          case _=> 0
        })

        predictValue+= 120;


       // resultList = test(formData)
        Ok(views.html.main("$"+predictValue.toString))
      }
    )

  }

  def randomForestRegressionModelGenerator(): RandomForestRegressionModel = {
    val df = DataAnalysisController.loadData("dataSet.csv")
    val splited = df.randomSplit(Array(0.8,0.2),666)
    val train = splited(0)
    val test = splited(1)
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




//  def index() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.dashboarddemo())
//  }
//  def demo() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.demo(NLPForm.form))
//  }
//  def explore() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.explore())
//  }
//
//  def tutorial() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.tutorial())
//  }
//  def dashboard() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.dashboarddemo())
//  }
//  def cssmap() = Action { implicit request: Request[AnyContent] =>
//    var resultList: List[List[String]] = List()
//    val a = List("1","2","3");
//    resultList=resultList:+a
//    val b = List("4","5","6");
//    resultList = resultList:+b
//    Ok(views.html.cssmap(resultList))
//  }
//
//  def datasearch() = Action { implicit request: Request[AnyContent] =>
//
//    Ok(views.html.dataSearch(BasicForm.form))
//  }
//  def downloadCsv() = Action { implicit request: Request[AnyContent] =>
//    val bufferedSource = Source.fromFile("D:/NingHuang/Spring2020/csye7200/SecondProject/MachineLearning/classification_result.csv")
//    var resultList: List[List[String]] = List()
//
//    for (line <- bufferedSource.getLines.take(51)) {
//      val cols = line.split(",")
//      resultList = resultList :+ cols.toList
//
//    }
//    resultList = resultList.tail
//    Ok(views.html.downloadCsv(resultList))
//
//  }
//
//  def downloadResult =Action{ implicit request: Request[AnyContent] =>
//    Ok.sendFile(new java.io.File(
//      "D:/NingHuang/Spring2020/csye7200/SecondProject/MachineLearning/classification_result.csv"))
//  }
//  def nlpSearch() = Action{ implicit request: Request[AnyContent] =>
//    Ok(views.html.dataSearchNLP(NLPForm.form));
//  }
//
//  def simpleFormPost() = Action { implicit request: Request[AnyContent] =>
//    val formData: NLPForm = NLPForm.form.bindFromRequest.get // Careful: BasicForm.form.bindFromRequest returns an Option
//
//    var res = getSearchResult(formData)
//    if(res.equals("")) res = "No Such Type"
//    Ok(views.html.dataSearchNLPResult(res))
//  }
//  def simpleFormPost2() = Action { implicit request: Request[AnyContent] =>
//    BasicForm.form.bindFromRequest.fold(
//      formWithErrors => {
//        Ok(views.html.dataSearch(formWithErrors))
//      },
//      formData => {
//        val formData: BasicForm = BasicForm.form.bindFromRequest.get // Careful: BasicForm.form.bindFromRequest returns an Option
//        println(formData.Year.toString + "   "+ formData.CrimeType.toUpperCase);
//
//        var resultList: List[List[String]] = List()
//
//        resultList = test(formData)
//        Ok(views.html.cssmap(resultList))
//      }
//    )
//
//  }
//
//  def map() = Action { implicit request: Request[AnyContent] =>
//    var resultList: List[List[String]] = List()
//    val a = List("1","2","3");
//    resultList=resultList:+a
//    val b = List("4","5","6");
//    resultList = resultList:+b
//    Ok(views.html.map(resultList))
//  }
//
//  def test(formdata : BasicForm):List[List[String]] ={
//    var bufferedSource = Source.fromFile("D:/NingHuang/Spring2020/csye7200/SecondProject/MachineLearning/searchingData.csv")
//    var resultList: List[List[String]] = List()
//    for (line <- bufferedSource.getLines()) {
//
//      var cols: Array[String] = line.split(",");
//      if((cols(1).contains(formdata.CrimeType)||cols(4).contains(formdata.CrimeType))&&cols(10).contains(formdata.Street)
//        &&cols(8).equals(formdata.Month.toString())&&cols(9).equals(formdata.Year.toString())){
//        resultList = resultList :+ cols.toList
//      }
//    }
//
//    resultList;
//  }
//
//  def getLocationSearchResult(formdata : BasicForm):List[List[String]] ={
//    var bufferedSource = Source.fromFile("D:/NingHuang/Spring2020/csye7200/FinalProject/HNTest/DataSearchSystems/app/controllers/data.csv")
//
//    var resultList: List[List[String]] = List()
//    for (line <- bufferedSource.getLines()) {
//
//      var cols: Array[String] = line.split(",");
//      if(cols(3).contains(formdata.CrimeType)&&cols(13).contains(formdata.Street)
//        &&cols(8).equals(formdata.Month.toString())&&cols(9).equals(formdata.Year.toString())){
//        resultList = resultList :+ cols.toList
//      }
//
//    }
//    if(resultList.size ==0){
//      for (line <- bufferedSource.getLines.take(20)) {
//        val cols = line.split(",")
//        resultList = resultList :+ cols.toList
//      }
//    }
//
//    resultList;
//  }
//
//  def getSearchResult(formdata : NLPForm): String ={
//    var bufferedSource = Source.fromFile("D:/NingHuang/Spring2020/csye7200/FinalProject/HNTest/DataSearchSystems/app/controllers/nlp_result.csv")
//    var pattern = formdata.Description.toUpperCase
//    var res = new String
//    for (line <- bufferedSource.getLines()) {
//
//      var cols: Array[String] = line.split(",");
//      if (cols(0).contains(pattern)) {
//        res = cols.last
//      }
//    }
//    res;
//  }
}
