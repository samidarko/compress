package helpers

/**
  * Created by Vincent Dupont on 18/4/17.
  */

import java.io._

/**
  * An helper object for manipulating the file system
  */
object FileSystem {

  def getTempFile: File = File.createTempFile("archiver", ".zip")

  private val filePrefix = "archive.part."

  /**
    * Get a list of files for give directory
    * @param inputDir input directory
    * @return List[File]
    */
  def getListOfFiles(inputDir: File): List[File] = {

    assert(inputDir.isDirectory, "inputDir should be a directory")

    // TODO: what happens if file are links?
    val (files, dirs) = inputDir.listFiles.toList.partition(_.isFile)

    files ++ dirs.flatMap(getListOfFiles)

  }

  /**
    * Get a BufferedOutputStream
    * @param f a file
    * @throws java.io.IOException an IO exception
    * @return BufferedOutputStream
    */
  @throws(classOf[IOException])
  def getBufferedOutputStream(f: File) : BufferedOutputStream =
    new BufferedOutputStream(new FileOutputStream(f))

  /**
    * Get a BufferedInputStream
    * @param f a file
    * @throws java.io.IOException an IO exception
    * @return BufferedInputStream
    */
  @throws(classOf[IOException])
  def getBufferedInputStream(f : File) : BufferedInputStream =
    new BufferedInputStream(new FileInputStream(f))

  /**
    * Split a file in chunks
    * @param inputFile an input file
    * @param outputDir an input directory
    * @param chunkSize size of a chunk in MB
    */
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

  /**
    * Merge a list of file into one file
    * @param inputDir an input directory
    * @return File
    */
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
