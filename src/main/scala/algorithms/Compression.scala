package algorithms

/**
  * Created by Vincent Dupont on 18/4/17.
  */

import java.io.File

/**
  * A trait representing the methods of compression algorithm
  */
trait Compression {

  def compress(inputDir: File, outputDir: File, chunkSize: Int) : Unit

  def extract(inputDir: File, outputDir: File) : Unit
}
