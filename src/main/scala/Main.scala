/**
  * Created by samidarko on 4/18/17.
  */

import algorithms.Zip

object Main {

  def main(args: Array[String]): Unit = {

    val parser = CommandLine.getParser

    val archiver : Archiver = new Archiver(new Zip)

    parser.parse(args, CommandLine.getConfig) match {
      case Some(config) =>
        if (config.version) println("Version is: " + CommandLine.getVersion)
        else {
          if (CommandLine.checkArgs(config)) {
            if (config.mode == "compress") archiver.compress(config.in, config.out, config.chunkSize)
            if (config.mode == "extract") archiver.extract(config.in, config.out)
          }
        }
      case None => println("Please use --help argument for usage")
    }

  }
}
