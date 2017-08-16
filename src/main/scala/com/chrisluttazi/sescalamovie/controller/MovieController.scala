package com.chrisluttazi.sescalamovie.controller

import com.chrisluttazi.sescalamovie.messages.MovieResponse
import com.chrisluttazi.sescalamovie.repository.MovieRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation._

@RestController
class MovieController(@Autowired val authenticationManager: AuthenticationManager,
                      @Autowired val movieRepository: MovieRepository) {
  @RequestMapping(value = Array("/api/v1/movies"), method = Array(RequestMethod.GET))
  @ResponseBody
  def list() = {
    try {
      new MovieResponse(movieRepository.findAll())
    } catch {
      case e: Exception =>
        ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.toString)
    }
  }
}



