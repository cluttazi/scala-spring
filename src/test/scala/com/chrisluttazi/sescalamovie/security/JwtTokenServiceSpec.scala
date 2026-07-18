package com.chrisluttazi.sescalamovie.security

import io.jsonwebtoken.JwtException
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

import scala.jdk.CollectionConverters._

/**
  * Regression coverage for the in-repo JWT stack that replaced the
  * abandoned spring-security-token-filter library.
  */
@RunWith(classOf[JUnitRunner])
class JwtTokenServiceSpec extends AnyFlatSpec with Matchers {

  private val secret = "0123456789abcdef0123456789abcdef" // 32 bytes for HS256
  private val service = new JwtTokenService(secret, 3600000L)

  "JwtTokenService" should "round-trip subject and authorities" in {
    val authentication = new UsernamePasswordAuthenticationToken(
      "user@example.com", null, Seq(new SimpleGrantedAuthority("ROLE_USER")).asJava)

    val claims = service.parseToken(service.generateToken(authentication))

    claims.getSubject shouldBe "user@example.com"
    claims.get(JwtTokenService.AuthoritiesClaim, classOf[String]) shouldBe "ROLE_USER"
    claims.getExpiration.after(claims.getIssuedAt) shouldBe true
  }

  it should "reject tokens signed with a different key" in {
    val other = new JwtTokenService("fedcba9876543210fedcba9876543210", 3600000L)
    val token = other.generateToken(
      new UsernamePasswordAuthenticationToken("user@example.com", null, java.util.List.of()))
    assertThrows[JwtException](service.parseToken(token))
  }

  it should "reject expired tokens" in {
    val expiring = new JwtTokenService(secret, -1000L)
    val token = expiring.generateToken(
      new UsernamePasswordAuthenticationToken("user@example.com", null, java.util.List.of()))
    assertThrows[JwtException](service.parseToken(token))
  }
}
