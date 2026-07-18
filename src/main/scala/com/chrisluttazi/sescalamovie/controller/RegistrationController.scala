package com.chrisluttazi.sescalamovie.controller

import com.chrisluttazi.sescalamovie.messages.{LoginRequest, Response}
import com.chrisluttazi.sescalamovie.model.{Authority, User}
import com.chrisluttazi.sescalamovie.repository.{AuthorityRepository, UserRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation._
import com.chrisluttazi.sescalamovie.security.TokenService

/**
  * Creates authority and then the user. Returns a token if the operation was succeful.
  *
  * @param authenticationManager: Autowired
  * @param tokenService: Autowired
  * @param userRepository: Autowired
  * @param authorityRepository: Autowired
  */

@RestController
class RegistrationController(@Autowired val authenticationManager: AuthenticationManager,
                             @Autowired val tokenService: TokenService,
                             @Autowired val userRepository: UserRepository,
                             @Autowired val authorityRepository: AuthorityRepository,
                             @Autowired val passwordEncoder: PasswordEncoder) {
  @RequestMapping(value = Array("/api/v1/register"), method = Array(RequestMethod.POST))
  @ResponseBody
  def register(@RequestBody loginRequest: LoginRequest) = {
    try {
      if (userRepository.findByUsername(loginRequest.getUsername()) != null)
        throw new Exception("Email already taken")
      val newUser: User = new User
      newUser setUsername loginRequest.getUsername()
      // passwords are stored encoded (plaintext storage is unsupported since Spring Security 5)
      newUser setPassword passwordEncoder.encode(loginRequest.getPassword())
      userRepository save newUser

      val authority: Authority = new Authority
      authority setUser newUser
      authority setAuthority "ROLE_USER"
      authorityRepository save authority

      val authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(newUser.getUsername(), loginRequest.getPassword()))
      new Response(tokenService generateToken authentication)
    } catch {
      case e: Exception =>
        ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.toString)
    }
  }
}



