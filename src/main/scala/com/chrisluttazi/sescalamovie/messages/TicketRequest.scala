package com.chrisluttazi.sescalamovie.messages

import javax.validation.Valid

import com.chrisluttazi.sescalamovie.model.CreditCard

import scala.beans.BeanProperty

class TicketRequest(movieId_ : Long, userEmail_ : String, @Valid creditCard_ : CreditCard) extends Serializable {
  @BeanProperty
  var movieId: Long = movieId_
  @BeanProperty
  var userEmail: String = userEmail_
  @BeanProperty
  var creditCard: CreditCard = creditCard_

  def this() = this(0L, "", null)

}