/**
  * Created by Vincent Dupont on 18/4/17.
  */
import java.io.File

/**
  * An helper object for parsing the command line
  */
object CommandLine {

  private val currentVersion = "0.1.0"

  case class Config(
                     mode: String = "",
                     in: File = new File("."),
                     out: File = new File("."),
                     version: Boolean = false,
                     versionNumber: String = "0.1.0",
                     chunkSize: Int = 1)

  /**
    * Create a command line parser
    * @return OptionParser
    */
  def getParser = new scopt.OptionParser[Config]("scalarchiver") {
    head("Scalarchiver", currentVersion)

    opt[File]('i', "in") required() valueName "<file>" action((x, c) => c.copy(in = x)) text "Path to Input directory"

    opt[File]('o', "out") required() valueName "<file>" action((x, c) => c.copy(out = x)) text "Path to Output directory"

    cmd("compress") action ((_, c) => c.copy(mode = "compress")) children (
      opt[Int]('s', "chunkSize") required() valueName "<MB>" action((x, c) => c.copy(chunkSize = x)) text "Maximum compressed size per file expressed in MB"
      ) text "Create a new archive containing the specified items."

    cmd("extract") action ((_, c) => c.copy(mode = "extract")) text "Extract to disk from the archive."

    version("version") text "Print version information and quit"

    help("help") text "prints this usage text and quit"
  }

  def getVersion: String = currentVersion

  def getConfig : Config = Config()

  /**
    * Check the arguments given to the command line
    * @return Boolean
    */
  def checkArgs(config: Config) : Boolean = {
    if (!config.in.isDirectory) {
      println("--in argument should be a directory")
      false
    } else if (!config.out.isDirectory) {
      println("--out argument should be a directory")
      false
    } else if (config.chunkSize < 1) {
      println("--chunkSize should be greater than 0")
      false
    } else true
  }

}
