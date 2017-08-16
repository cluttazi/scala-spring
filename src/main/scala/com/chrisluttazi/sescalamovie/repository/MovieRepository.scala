package com.chrisluttazi.sescalamovie.repository

import java.util

import com.chrisluttazi.sescalamovie.model.Movie
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize("hasAuthority('ADMIN')")
@RepositoryRestResource(collectionResourceRel = "movie", path = "movies")
trait MovieRepository extends PagingAndSortingRepository[Movie, java.lang.Long] {
  @Override
  @PreAuthorize("authenticated")
  def findByMovieId(id: java.lang.Long): Movie

  @Override
  @PreAuthorize("permitAll") def findAll(): util.ArrayList[Movie]
}