package com.chrisluttazi.sescalamovie.config

import java.util.Optional
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.visola.spring.security.tokenfilter.TokenAuthenticationFilter
import org.visola.spring.security.tokenfilter.jwt.{AuthenticationJwtClaimsSetTransformer, JwtTokenService, UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer}

/**
  * Web Security Config for the application
  *
  * @param dataSource: Autowired
  * @param tokenAuthenticationFilter: Autowired
  */

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(@Autowired val dataSource: DataSource,
                        @Autowired val tokenAuthenticationFilter: TokenAuthenticationFilter) extends WebSecurityConfigurerAdapter {

  val TOKEN_SECRET = "wJ7ro7h76!9UoR4XcW^ubv3O$2_IIx-l"
  val ADMIN_USER = "admin@admin.com"
  val ADMIN_PASSWORD = "password"

  override def configure(http: HttpSecurity) = {
    // allowed using get
    http.authorizeRequests.antMatchers(HttpMethod.GET, "/api/v1/movies", "/error").permitAll
    // allowed urls using post
    http.authorizeRequests.antMatchers(HttpMethod.POST, "/api/v1/login", "/api/v1/register", "/api/v1/tickets").permitAll
    // restrict everything else
    http.authorizeRequests.anyRequest.hasAuthority("ADMIN")
    http.csrf.disable
    http.sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    http.addFilterBefore(tokenAuthenticationFilter, classOf[BasicAuthenticationFilter])
    http.httpBasic
  }

  @Autowired
  def configureGlobal(auth: AuthenticationManagerBuilder): Unit = {
    auth
      .jdbcAuthentication
      .dataSource(dataSource)
      .withUser(ADMIN_USER) //admin user
      .password(ADMIN_PASSWORD)
      .authorities("ADMIN")
  }

  // using jwt token
  @Autowired
  def tokenService = new JwtTokenService(claimsSetTransformer, TOKEN_SECRET)

  // token is valid for an hour
  @Autowired
  def claimsSetTransformer: AuthenticationJwtClaimsSetTransformer = { // How long will your token last and the prefix for roles
    new UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(TimeUnit.HOURS.toMillis(1), Optional.of("ROLE_"))
  }
}
