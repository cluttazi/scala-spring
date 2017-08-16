package com.chrisluttazi.sescalamovie.model

import javax.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "authorities")
class Authority {
  @Id
  @GeneratedValue
  @BeanProperty
  @Column(name = "authority_id", unique = true, nullable = false)
  var authorityId: Long = _
  @ManyToOne
  @JoinColumn(name = "username", nullable = false)
  @BeanProperty
  var user: User = _
  @Column(name = "authority", nullable = false, length = 45)
  @BeanProperty
  var authority: String = "USER"
}
