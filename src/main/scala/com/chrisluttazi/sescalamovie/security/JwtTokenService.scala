package com.chrisluttazi.sescalamovie.security

import java.nio.charset.StandardCharsets
import java.util.Date

import io.jsonwebtoken.{Claims, Jwts}
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

import scala.jdk.CollectionConverters._

/**
  * JWT implementation of [[TokenService]] on jjwt (HMAC-SHA-256).
  *
  * Keeps the behavior of the old spring-security-token-filter starter:
  * the signing secret comes from the `security.token.filter.secret`
  * property and tokens are valid for one hour.
  */
@Service
class JwtTokenService(@Value("${security.token.filter.secret}") secret: String,
                      @Value("${security.token.filter.validity-millis:3600000}") validityMillis: Long)
  extends TokenService {

  private val key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))

  override def generateToken(authentication: Authentication): String = {
    val authorities = authentication.getAuthorities.asScala.map(_.getAuthority).mkString(",")
    val now = System.currentTimeMillis()
    Jwts.builder()
      .subject(authentication.getName)
      .claim(JwtTokenService.AuthoritiesClaim, authorities)
      .issuedAt(new Date(now))
      .expiration(new Date(now + validityMillis))
      .signWith(key)
      .compact()
  }

  /** Verifies the signature and expiry and returns the claims. */
  def parseToken(token: String): Claims =
    Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload
}

object JwtTokenService {
  val AuthoritiesClaim = "authorities"
}
