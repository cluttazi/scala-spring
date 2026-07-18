package com.chrisluttazi.sescalamovie.config

import javax.sql.DataSource

import com.chrisluttazi.sescalamovie.security.TokenAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

/**
  * Web Security Config for the application.
  *
  * Migrated from the Spring Boot 1.5 / Spring Security 4 style
  * (`WebSecurityConfigurerAdapter`, `@EnableGlobalMethodSecurity`,
  * `antMatchers`) to the Spring Security 6 component style
  * (`SecurityFilterChain` bean, `@EnableMethodSecurity`, lambda DSL).
  *
  * Authentication is JDBC-backed against the JPA-managed `users` /
  * `authorities` tables, exactly like the original `jdbcAuthentication`
  * setup; the bootstrap admin user is created on startup once the schema
  * exists. Passwords are now stored encoded (Spring Security 5+ no longer
  * supports plaintext password storage).
  */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(@Value("${security.admin.username:admin@admin.com}") adminUser: String,
                        @Value("${security.admin.password:password}") adminPassword: String) {

  @Bean
  def securityFilterChain(http: HttpSecurity,
                          tokenAuthenticationFilter: TokenAuthenticationFilter): SecurityFilterChain = {
    http
      .authorizeHttpRequests(auth => auth
        // allowed using get
        .requestMatchers(HttpMethod.GET, "/api/v1/movies", "/error").permitAll()
        // allowed urls using post
        .requestMatchers(HttpMethod.POST, "/api/v1/login", "/api/v1/register", "/api/v1/tickets").permitAll()
        // restrict everything else
        .anyRequest().hasAuthority("ADMIN"))
      .csrf(csrf => csrf.disable())
      .sessionManagement(session => session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .addFilterBefore(tokenAuthenticationFilter, classOf[BasicAuthenticationFilter])
      .httpBasic(Customizer.withDefaults())
      .build()
  }

  @Bean
  def passwordEncoder(): PasswordEncoder =
    PasswordEncoderFactories.createDelegatingPasswordEncoder()

  /** JDBC user lookup against the JPA-managed users/authorities tables. */
  @Bean
  def userDetailsManager(dataSource: DataSource): JdbcUserDetailsManager =
    new JdbcUserDetailsManager(dataSource)

  @Bean
  def authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager =
    configuration.getAuthenticationManager

  /**
    * Recreates the bootstrap admin user the old `jdbcAuthentication`
    * configuration inserted at startup. Runs after the context is ready so
    * the Hibernate-generated schema already exists.
    */
  @Bean
  def adminUserBootstrap(users: JdbcUserDetailsManager,
                         passwordEncoder: PasswordEncoder): ApplicationRunner = { _ =>
    if (!users.userExists(adminUser)) {
      users.createUser(
        User.withUsername(adminUser)
          .password(passwordEncoder.encode(adminPassword))
          .authorities("ADMIN")
          .build())
    }
  }
}
