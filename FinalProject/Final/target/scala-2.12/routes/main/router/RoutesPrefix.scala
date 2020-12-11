// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/harold/Desktop/CodeFolder/7200/CSYE7200-Scala-BigdataFinalProject/FinalProject/Final/conf/routes
// @DATE:Thu Dec 03 21:56:06 EST 2020


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
