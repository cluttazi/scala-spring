package com.chrisluttazi.sescalamovie.messages

import java.util

import com.chrisluttazi.sescalamovie.model.Movie

import scala.beans.BeanProperty

class MovieResponse(movieList: util.List[Movie]) extends Serializable {
  @BeanProperty
  var list: util.List[Movie] = movieList
}
