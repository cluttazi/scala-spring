package com.chrisluttazi.sescalamovie

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class WebApplication

object WebApplication extends App {
  SpringApplication.run(classOf[WebApplication], args: _*)
}
