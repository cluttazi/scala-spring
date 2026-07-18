package com.chrisluttazi.sescalamovie.security

import org.springframework.security.core.Authentication

/**
  * Issues and parses bearer tokens for authenticated users.
  *
  * Replaces `org.visola.spring.security.tokenfilter.TokenService` from the
  * abandoned spring-security-token-filter library (Spring Boot 1.x only).
  */
trait TokenService {
  def generateToken(authentication: Authentication): String
}
