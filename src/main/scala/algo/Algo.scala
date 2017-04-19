package algo

import java.io.File

/**
  * Created by vincentdupont on 18/4/17.
  */
trait Algo {

  def compress(in: File, out: File, size: Int) : Unit

  def extract(in: File, out: File) : Unit
}
