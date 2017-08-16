package com.chrisluttazi.sescalamovie.messages

import java.util

import com.chrisluttazi.sescalamovie.model.Movie

import scala.beans.BeanProperty

class MovieResponse(movieList: util.ArrayList[Movie]) extends Serializable {
  @BeanProperty
  var list: util.ArrayList[Movie] = movieList
}
