/**
  * Created by samidarko on 4/18/17.
  */

import algorithms.Zip

object Main {

  def main(args: Array[String]): Unit = {

    val parser = CommandLine.getParser

    val compressor : Compressor = new Compressor(new Zip)

    parser.parse(args, CommandLine.getConfig) match {
      case Some(config) =>
        if (config.version) println("Version is: " + CommandLine.getVersion)
        else {
          if (CommandLine.checkArgs(config)) {
            if (config.mode == "compress") compressor.compress(config.in, config.out, config.fileSize)
            if (config.mode == "extract") compressor.extract(config.in, config.out)
          }
        }
      case None => println("Please use --help argument for usage")
    }

  }
}
