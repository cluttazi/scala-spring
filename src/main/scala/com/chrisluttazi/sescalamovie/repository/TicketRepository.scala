package com.chrisluttazi.sescalamovie.repository

import com.chrisluttazi.sescalamovie.model.Ticket
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize("hasAuthority('ADMIN')")
@RepositoryRestResource(collectionResourceRel = "ticket", path = "tickets")
trait TicketRepository extends PagingAndSortingRepository[Ticket, java.lang.Long] {
  @Override
  @PreAuthorize("authenticated")
  def save(ticket: Ticket): Unit
}