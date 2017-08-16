package com.chrisluttazi.sescalamovie.repository

import com.chrisluttazi.sescalamovie.model.CreditCard
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize("hasAuthority('ADMIN')")
@RepositoryRestResource(collectionResourceRel = "creditcard", path = "creditcards")
trait CreditCardRepository extends PagingAndSortingRepository[CreditCard, java.lang.Long] {
  @Override
  @PreAuthorize("authenticated")
  def save(creditCard: CreditCard): Unit
}