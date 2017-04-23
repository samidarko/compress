package algorithms

/**
  * Created by Vincent Dupont on 18/4/17.
  */

import java.io.{File, FileInputStream, FileOutputStream, IOException}
import java.util.zip.{ZipEntry, ZipInputStream, ZipOutputStream, ZipException}
import helpers.FileSystem._

/**
  * A concrete implementation of Compression trait for Zip compression
  */
class Zip extends Compression {


  /**
    * Compress the content of an input directory to an output directory in chunks
    * @param inputDir an input directory
    * @param outputDir an output directory
    * @param chunkSize size of a chunk in MB
    * @throws java.io.IOException an IO exception
    * @throws java.util.zip.ZipException an ZipException exception
    */
  @throws(classOf[IOException])
  @throws(classOf[ZipException])
  def compress(inputDir: File, outputDir: File, chunkSize: Int): Unit = {

    assert(inputDir.isDirectory, "inputDir should be a directory")
    assert(outputDir.isDirectory, "outputDir should be a directory")

    // List files from input directory
    val files = getListOfFiles(inputDir)

    val tempFile = getTempFile
    tempFile.deleteOnExit()

    val zos = new ZipOutputStream(new FileOutputStream(tempFile))

    files.foreach { name =>

      val entryName = name.toString.replaceFirst(s"${inputDir.toString}/", "")
      zos.putNextEntry(new ZipEntry(entryName))

      val bis = getBufferedInputStream(name)

      makeStream[Int](bis.read, x => x > -1, zos.write)

      bis.close()
      zos.closeEntry()
    }
    zos.close()

    // Split file
    splitFile(tempFile, outputDir, chunkSize)
  }

  /**
    * Extract the chunks of an input directory to an output directory
    * @param inputDir an input directory
    * @param outputDir an output directory
    * @throws java.io.IOException an IO exception
    * @throws java.util.zip.ZipException an ZipException exception
    */
  @throws(classOf[IOException])
  @throws(classOf[ZipException])
  override def extract(inputDir: File, outputDir: File): Unit = {

    assert(inputDir.isDirectory, "inputDir should be a directory")
    assert(outputDir.isDirectory, "outputDir should be a directory")

    val inputFile = mergeFiles(inputDir)
    inputFile.deleteOnExit()

    val zis = new ZipInputStream(new FileInputStream(inputFile))

    def action(entry : ZipEntry) = {

      val newFile = new File(outputDir, entry.getName)
      new File(newFile.getParent).mkdirs()
      val bos = getBufferedOutputStream(newFile)

      makeStream[Int](zis.read, x => x > 0, bos.write)

      bos.close()

    }

    makeStream[ZipEntry](zis.getNextEntry, x => x != null, action)

    zis.closeEntry()
    zis.close()

  }
}
