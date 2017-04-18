/**
  * Created by samidarko on 4/18/17.
  */
import java.io.File

object Compress {

//  def listFiles(files : Array[File]): Array[File] = {
//    files.foldLeft(Array[File]()) {
//      (acc, file) => {
//        if (file.isDirectory) {
//
//        } else {
//
//        }
//      }
//    }
//  }

  def main(args: Array[String]): Unit = {
    val dir = new File(".")
    dir.listFiles()
  }
}
