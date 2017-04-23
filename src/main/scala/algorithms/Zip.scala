package algorithms

import java.io.{File, FileInputStream, FileOutputStream, BufferedInputStream, BufferedOutputStream, IOException}
import java.util.zip.{ZipEntry, ZipInputStream, ZipOutputStream, ZipException}
import helpers.FileSystem.{splitFile, mergeFiles, getTempFile, getListOfFiles}

/**
  * Created by vincentdupont on 18/4/17.
  */
class Zip extends Compression {


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

      val bis = new BufferedInputStream(new FileInputStream(name))

      Stream.continually(bis.read).takeWhile(_ > -1).foreach(zos.write)

      bis.close()
      zos.closeEntry()
    }
    zos.close()

    // Split file
    splitFile(tempFile, outputDir, chunkSize)
  }

  @throws(classOf[IOException])
  @throws(classOf[ZipException])
  override def extract(inputDir: File, outputDir: File): Unit = {

    assert(inputDir.isDirectory, "inputDir should be a directory")
    assert(outputDir.isDirectory, "outputDir should be a directory")

    val buffer = new Array[Byte](1024)

    val inputFile = mergeFiles(inputDir)
    inputFile.deleteOnExit()

    val zis = new ZipInputStream(new FileInputStream(inputFile))

    Stream
      .continually(zis.getNextEntry)
      .takeWhile(_ != null)
      .foreach { entry =>
        val newFile = new File(outputDir, entry.getName)
        new File(newFile.getParent).mkdirs()
        val bos = new BufferedOutputStream(new FileOutputStream(newFile))

        Stream.continually(zis.read(buffer))
          .takeWhile(_ > 0).foreach(count => bos.write(buffer, 0, count))

        bos.close()
      }

    zis.closeEntry()
    zis.close()

  }
}
