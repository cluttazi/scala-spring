package com.chrisluttazi.sescalamovie.repository

import com.chrisluttazi.sescalamovie.model.CreditCard
import org.springframework.data.repository.{CrudRepository, PagingAndSortingRepository}
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize("hasAuthority('ADMIN')")
@RepositoryRestResource(collectionResourceRel = "creditcard", path = "creditcards")
trait CreditCardRepository extends CrudRepository[CreditCard, java.lang.Long] with PagingAndSortingRepository[CreditCard, java.lang.Long] {
  @Override
  @PreAuthorize("authenticated")
  def save(creditCard: CreditCard): Unit
}