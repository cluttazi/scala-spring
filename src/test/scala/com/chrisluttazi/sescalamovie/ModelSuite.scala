package com.chrisluttazi.sescalamovie

import com.chrisluttazi.sescalamovie.model.User
import com.chrisluttazi.sescalamovie.repository.{AuthorityRepository, UserRepository}
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.test.context.TestContextManager

/**
  * Boots the full Spring context (against in-memory H2, see
  * src/test/resources/application.properties) and exercises the
  * security-annotated repositories.
  */
@RunWith(classOf[JUnitRunner])
@SpringBootTest(classes = Array(classOf[WebApplication]))
class ModelSuite extends AnyFlatSpec with Matchers {

  @Autowired val userRepository: UserRepository = null
  @Autowired val authorityRepository: AuthorityRepository = null

  new TestContextManager(getClass).prepareTestInstance(this)

  // Note: on Spring Security 4 this surfaced as
  // AuthenticationCredentialsNotFoundException because method security was
  // evaluated before the repository call; with Spring Security 6 the
  // method-level @PreAuthorize("permitAll") on save applies as declared, so
  // the invalid entity now reaches Hibernate and is rejected there
  // (null @Id -> IdentifierGenerationException wrapped in JpaSystemException).
  "A UserRepository" should "not accept null values" in {
    val user = new User
    assertThrows[JpaSystemException](userRepository save user)
  }

}
