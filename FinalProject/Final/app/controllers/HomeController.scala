package controllers

import javax.inject._
import play.api.mvc._
import models._
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {
  val data :DataFrame  = DataAnalysisController.loadBasicData(DataAnalysisController.OLD_PATH);
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
  //  def categoryMapping() = Action { implicit request: Request[AnyContent] =>
  //    val bufferedSource = Source.fromFile("D:/NingHuang/Spring2020/csye7200/FinalProject/HNTest/DataSearchSystems/app/controllers/reclassify.csv")
  //
  //    var resultList: List[List[String]] = List()
  //
  //    for (line <- bufferedSource.getLines.take(51)) {
  //      val cols = line.split(",")
  //      resultList = resultList :+ cols.toList
  //
  //    }
  //    resultList = resultList.tail
  //    Ok(views.html.categoryMapping(resultList))
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
