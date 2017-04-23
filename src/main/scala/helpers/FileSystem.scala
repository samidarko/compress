package helpers

/**
  * Created by vincentdupont on 18/4/17.
  */

import java.io._

object FileSystem {

  def getTempFile: File = File.createTempFile("archiver", ".zip")

  private val filePrefix = "archive.part."

  def getListOfFiles(inputDir: File): List[File] = {

    assert(inputDir.isDirectory, "inputDir should be a directory")

    // TODO: what happens if file are links?
    val (files, dirs) = inputDir.listFiles.toList.partition(_.isFile)

    files ++ dirs.flatMap(getListOfFiles)

  }

  @throws(classOf[IOException])
  def getBufferedOutputStream(f: File) : BufferedOutputStream =
    new BufferedOutputStream(new FileOutputStream(f))

  @throws(classOf[IOException])
  def getBufferedInputStream(f : File) : BufferedInputStream =
    new BufferedInputStream(new FileInputStream(f))

  def splitFile(inputFile: File, outputDir: File, chunkSize: Int): Unit = {

    assert(inputFile.isFile, "inputFile should be a file")
    assert(outputDir.isDirectory, "outputDir should be a directory")
    assert(chunkSize > 0, "chunkSize should be greater than 0")

    var chunkIndex = 0

    val bis = getBufferedInputStream(inputFile)

    def getChunkZipOutputStream(outputDir: File): BufferedOutputStream = {
      val chunkName = s"$filePrefix$chunkIndex"
      chunkIndex += 1
      getBufferedOutputStream(new File(outputDir, chunkName))
    }

    val bufferSize = 1024 * 1024 // 1MB
    val buffer = new Array[Byte](bufferSize)

    var currentChunkSize = chunkSize

    var bos = getChunkZipOutputStream(outputDir)

    Stream.continually(bis.read(buffer, 0, bufferSize))
      .takeWhile(_ > -1)
      .foreach(count => {
        if (currentChunkSize == 0) {
          bos = getChunkZipOutputStream(outputDir)
          currentChunkSize = chunkSize
        }
        bos.write(buffer, 0, count)
        currentChunkSize -= 1
      })

    bos.close()

  }

  def mergeFiles(inputDir: File): File = {
    assert(inputDir.isDirectory, "inputDir should be a directory")
    val files = getListOfFiles(inputDir).foldLeft(List[(File, Int)]()) {
      (acc, file) => {
        if (file.toString.contains(filePrefix)) {
          (file, file.toString.split(filePrefix).last.toInt) :: acc
        } else acc
      }
    } sortBy (_._2) map (_._1)

    val outputFile = getTempFile

    val bos = getBufferedOutputStream(outputFile)

    files.foreach(file => {
      val bis = getBufferedInputStream(file)
      Stream.continually(bis.read).takeWhile(_ > -1).foreach(bos.write)
      bis.close()
    })

    bos.close()

    outputFile

  }

}
