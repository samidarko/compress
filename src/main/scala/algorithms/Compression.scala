package algorithms

import java.io.File

/**
  * Created by vincentdupont on 18/4/17.
  */
trait Compression {

  def compress(in: File, out: File, size: Int) : Unit

  def extract(in: File, out: File) : Unit
}
