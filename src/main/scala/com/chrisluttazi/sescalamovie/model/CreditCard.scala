package com.chrisluttazi.sescalamovie.model

import jakarta.persistence._
import jakarta.validation.constraints.{Max, Min}

import org.hibernate.validator.constraints.Length

import scala.beans.BeanProperty

@Entity
class CreditCard {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @BeanProperty
  var creditcardId: Long = _
  @BeanProperty
  @Length(min = 3, max = 40)
  var name: String = _
  @BeanProperty
  @Length(min = 16, max = 16)
  var number: String = _
  @BeanProperty
  @Length(min = 3, max = 10)
  var cvv: String = _
  @BeanProperty
  @Min(1)
  @Max(12)
  var expirationMonth: Integer = _
  @BeanProperty
  @Min(2017)
  @Max(2050)
  var expirationYear: Integer = _

}
