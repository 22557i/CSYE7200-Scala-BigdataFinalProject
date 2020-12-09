package models

import play.api.data._
import play.api.data.Forms._

case class BasicForm(Year: String,
                     NeighbourhoodGroup: String,
                     Neighbourhood: String,
                     RoomType: String,
                     MinimumNights: Int,
                     AvaDays:Int,
                     Description: String)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object BasicForm {
  val form: Form[BasicForm] = Form(
    mapping(
      "House Name" -> nonEmptyText,
      "NeighbourhoodGroup" -> nonEmptyText,
      "Neighbourhood" -> nonEmptyText,
      "RoomType" -> nonEmptyText,
      "MinimumNights"->number(1,365),
      "AvaDays"->number(1,365),
      "Description"->nonEmptyText
    )(BasicForm.apply)(BasicForm.unapply)
  )
}
