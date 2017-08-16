package com.chrisluttazi.sescalamovie.repository

import com.chrisluttazi.sescalamovie.model.Authority
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize("hasAuthority('ADMIN')")
@RepositoryRestResource(collectionResourceRel = "authority", path = "authorities")
trait AuthorityRepository extends PagingAndSortingRepository[Authority, java.lang.Long] {
  @Override
  @PreAuthorize("permitAll") def save(authority: Authority): Unit
}