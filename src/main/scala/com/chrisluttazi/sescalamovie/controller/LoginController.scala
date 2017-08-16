package com.chrisluttazi.sescalamovie.controller

import com.chrisluttazi.sescalamovie.messages.{LoginRequest, TokenResponse}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.web.bind.annotation._
import org.visola.spring.security.tokenfilter.TokenService

/**
  *
  * Controller used for the login. Return token if OK
  *
  * @param authenticationManager: Autowired
  * @param tokenService: Autowired
  */

@RestController
class LoginController(@Autowired val authenticationManager: AuthenticationManager,
                      @Autowired val tokenService: TokenService) {
  @RequestMapping(value = Array("/api/v1/login"), method = Array(RequestMethod.POST))
  @ResponseBody
  def login(@RequestBody loginRequest: LoginRequest) = {
    try {
      val authentication = authenticationManager authenticate
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername, loginRequest.getPassword)
      new TokenResponse(tokenService.generateToken(authentication))
    } catch {
      case e: Exception =>
        ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.toString)
    }
  }
}



