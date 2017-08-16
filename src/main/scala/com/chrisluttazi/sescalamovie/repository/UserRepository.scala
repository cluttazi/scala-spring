package com.chrisluttazi.sescalamovie.repository

import com.chrisluttazi.sescalamovie.model.User
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize("hasAuthority('ADMIN')")
@RepositoryRestResource(collectionResourceRel = "user", path = "users")
trait UserRepository extends PagingAndSortingRepository[User, java.lang.Long] {

  @Override
  @PreAuthorize("authenticated")
  def findByUsername(username: String): User

  @Override
  @PreAuthorize("permitAll") def save(user: User): Unit

}