package com.chrisluttazi.sescalamovie

import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.test.context.TestContextManager

import scala.jdk.CollectionConverters._

/**
  * Verifies the startup admin bootstrap (the replacement for the old
  * `jdbcAuthentication().withUser(...)` setup): the runner creates the
  * admin user and HTTP-basic style authentication works against it.
  * ApplicationRunner beans are not executed by @SpringBootTest, so the
  * runner is invoked explicitly here.
  */
@RunWith(classOf[JUnitRunner])
@SpringBootTest(classes = Array(classOf[WebApplication]))
class AdminBootstrapSuite extends AnyFlatSpec with Matchers {

  @Autowired val adminUserBootstrap: ApplicationRunner = null
  @Autowired val users: JdbcUserDetailsManager = null
  @Autowired val authenticationManager: AuthenticationManager = null

  new TestContextManager(getClass).prepareTestInstance(this)

  "The admin bootstrap" should "create an authenticatable ADMIN user" in {
    adminUserBootstrap.run(null)
    adminUserBootstrap.run(null) // idempotent

    users.userExists("admin@admin.com") shouldBe true

    val authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken("admin@admin.com", "password"))
    authentication.isAuthenticated shouldBe true
    authentication.getAuthorities.asScala.map(_.getAuthority) should contain("ADMIN")
  }

}
