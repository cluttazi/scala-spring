package com.chrisluttazi.sescalamovie.messages

import scala.beans.BeanProperty

class Response(message_ : String) extends Serializable {
  @BeanProperty
  var message: String = message_

  def this() = this("")
}
