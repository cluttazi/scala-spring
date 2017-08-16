package com.chrisluttazi.sescalamovie.messages

import scala.beans.BeanProperty

class LoginRequest(username_ : String, password_ : String) extends Serializable {
  @BeanProperty
  var username: String = username_
  @BeanProperty
  var password: String = password_

  def this() = this("", "")

  def this(string: String) = this(string, "")
}