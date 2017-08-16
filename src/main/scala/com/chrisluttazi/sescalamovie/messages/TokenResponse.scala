package com.chrisluttazi.sescalamovie.messages

import scala.beans.BeanProperty

class TokenResponse(token_ : String) extends Serializable {
  @BeanProperty
  var token: String = token_

  def this() = this("")
}
