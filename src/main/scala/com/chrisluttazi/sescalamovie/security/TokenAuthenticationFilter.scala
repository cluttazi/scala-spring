package com.chrisluttazi.sescalamovie.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

import scala.jdk.CollectionConverters._

/**
  * Authenticates requests carrying an `Authorization: Bearer <jwt>` header.
  *
  * Replaces `org.visola.spring.security.tokenfilter.TokenAuthenticationFilter`
  * (abandoned, javax-servlet based) with a jakarta-servlet filter.
  */
@Component
class TokenAuthenticationFilter(tokenService: JwtTokenService) extends OncePerRequestFilter {

  private val BearerPrefix = "Bearer "

  override def doFilterInternal(request: HttpServletRequest,
                                response: HttpServletResponse,
                                filterChain: FilterChain): Unit = {
    val header = request.getHeader(HttpHeaders.AUTHORIZATION)
    if (header != null && header.startsWith(BearerPrefix)) {
      try {
        val claims = tokenService.parseToken(header.substring(BearerPrefix.length))
        val authorities = Option(claims.get(JwtTokenService.AuthoritiesClaim, classOf[String]))
          .getOrElse("")
          .split(',')
          .filter(_.nonEmpty)
          .map(new SimpleGrantedAuthority(_))
          .toSeq
        val authentication =
          new UsernamePasswordAuthenticationToken(claims.getSubject, null, authorities.asJava)
        SecurityContextHolder.getContext.setAuthentication(authentication)
      } catch {
        case _: JwtException => SecurityContextHolder.clearContext()
      }
    }
    filterChain.doFilter(request, response)
  }
}
