package com.chrisluttazi.sescalamovie.model

import javax.persistence._

import org.hibernate.validator.constraints.Length

import scala.beans.BeanProperty

@Entity
class Movie {
  @Id
  @GeneratedValue
  @BeanProperty
  var movieId: Long = _
  @BeanProperty
  @Length(min = 3, max = 40)
  var title: String = _
  @BeanProperty
  @Length(min = 10, max = 140)
  var description: String = _

}
