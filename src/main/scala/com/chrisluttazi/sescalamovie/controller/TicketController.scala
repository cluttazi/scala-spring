package com.chrisluttazi.sescalamovie.controller

import com.chrisluttazi.sescalamovie.messages.{Response, TicketRequest}
import com.chrisluttazi.sescalamovie.model.Ticket
import com.chrisluttazi.sescalamovie.repository.{CreditCardRepository, MovieRepository, TicketRepository, UserRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation._

@PreAuthorize("authenticated")
@RestController
class TicketController(@Autowired val userRepository: UserRepository,
                       @Autowired val movieRepository: MovieRepository,
                       @Autowired val creditCardRepository: CreditCardRepository,
                       @Autowired val ticketRepository: TicketRepository) {
  @RequestMapping(value = Array("/api/v1/tickets"), method = Array(RequestMethod.POST))
  @ResponseBody
  def register(@RequestBody ticketRequest: TicketRequest) = {
    try {
      val ticket: Ticket = new Ticket
      ticket setUser userRepository.findByUsername(ticketRequest.getUserEmail)
      ticket setMovie movieRepository.findByMovieId(ticketRequest.movieId)
      creditCardRepository save ticketRequest.creditCard
      ticket setCreditcard ticketRequest.creditCard
      ticketRepository save ticket
      new Response("Success")
    } catch {
      case e: Exception =>
        ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.toString)
    }
  }
}



