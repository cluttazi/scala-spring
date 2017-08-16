package com.chrisluttazi.sescalamovie.model

import javax.persistence._
import javax.validation.constraints.NotNull

import scala.beans.BeanProperty

@Entity
class Ticket {
  @Id
  @GeneratedValue
  @BeanProperty
  var ticketId: Long = _

  @ManyToOne
  @JoinColumn(name = "userId")
  @NotNull
  @BeanProperty
  var user: User = _

  @ManyToOne
  @JoinColumn(name = "movieId")
  @NotNull
  @BeanProperty
  var movie: Movie = _

  @ManyToOne
  @JoinColumn(name = "creditcardId")
  @NotNull
  @BeanProperty
  var creditcard: CreditCard = _

}
