package com.chrisluttazi.sescalamovie.model

import java.util
import jakarta.persistence._

import jakarta.validation.constraints.Email

import scala.beans.BeanProperty

@Entity
@Table(name = "users")
class User {
  @Id
  @BeanProperty
  @Email
  @Column(unique = true, nullable = false)
  var username: String = _
  @BeanProperty
  @Column(nullable = false)
  var password: String = _
  @BeanProperty
  var enabled: Boolean = true
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  @BeanProperty
  var authority: util.Set[Authority] = new util.HashSet[Authority](0)

}
