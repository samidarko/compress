package algorithm

/**
  * Created by vincentdupont on 18/4/17.
  */

import java.io.File

object FileSystem {

  def getListOfFiles(d: File): List[File] = {
    if (d.exists && d.isDirectory) {

      // TODO: what happens if file are links?
      val (files, dirs) = d.listFiles.toList.partition(_.isFile)

      files ++ dirs.flatMap(getListOfFiles)

    } else {
      List[File]()
    }
  }

}
