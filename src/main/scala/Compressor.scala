/**
  * Created by vincentdupont on 18/4/17.
  */

import java.io.File

import algorithm.CompressionAlgo

class Compressor(algo: CompressionAlgo) extends CompressionAlgo {

  override def compress(in: File, out: File, size: Int): Unit = algo.compress(in, out, size)

  override def extract(in: File, out: File): Unit = algo.extract(in, out)
}
