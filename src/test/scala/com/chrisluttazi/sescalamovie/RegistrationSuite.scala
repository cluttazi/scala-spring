package com.chrisluttazi.sescalamovie

import com.chrisluttazi.sescalamovie.controller.RegistrationController
import com.chrisluttazi.sescalamovie.messages.{LoginRequest, Response}
import com.chrisluttazi.sescalamovie.repository.UserRepository
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestContextManager

import scala.jdk.CollectionConverters._

/**
  * Regression test for the registration flow after the Spring Security 6
  * migration: passwords must be stored encoded (never plaintext) and the
  * freshly registered user must still be able to authenticate, yielding a
  * token.
  */
@RunWith(classOf[JUnitRunner])
@SpringBootTest(classes = Array(classOf[WebApplication]))
class RegistrationSuite extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  @Autowired val registrationController: RegistrationController = null
  @Autowired val userRepository: UserRepository = null
  @Autowired val passwordEncoder: PasswordEncoder = null

  new TestContextManager(getClass).prepareTestInstance(this)

  override def beforeEach(): Unit =
    // the repository methods are @PreAuthorize-guarded, as they would be
    // behind the (permitAll) /api/v1/register endpoint filter chain
    SecurityContextHolder.getContext.setAuthentication(
      new UsernamePasswordAuthenticationToken(
        "caller", null, Seq(new SimpleGrantedAuthority("ROLE_USER")).asJava))

  override def afterEach(): Unit = SecurityContextHolder.clearContext()

  "Registration" should "store an encoded password and return a token" in {
    val rawPassword = "s3cret-Passw0rd"
    val result = registrationController.register(new LoginRequest("new.user@example.com", rawPassword))

    result shouldBe a[Response]

    val saved = userRepository.findByUsername("new.user@example.com")
    saved should not be null
    saved.getPassword() should not be rawPassword
    passwordEncoder.matches(rawPassword, saved.getPassword()) shouldBe true
  }

}
