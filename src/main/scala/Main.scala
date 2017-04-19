/**
  * Created by samidarko on 4/18/17.
  */

import algorithm.Zip

object Main {

  def main(args: Array[String]): Unit = {

    val parser = CommandLine.getParser

    val compressor : Compressor = new Compressor(new Zip)

    parser.parse(args, CommandLine.getConfig) match {
      case Some(config) =>
        if (config.mode == "compress" && CommandLine.checkArgs(config))
          compressor.compress(config.in, config.out, config.fileSize)
        if (config.mode == "extract" && CommandLine.checkArgs(config))
          compressor.extract(config.in, config.out)
        if (config.version) println("Version is: " + CommandLine.getVersion)
      case None => println("Please use --help argument for usage")
    }

  }
}
