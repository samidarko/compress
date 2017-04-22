package algorithms
import java.io._
import java.util.zip.{ZipEntry, ZipInputStream, ZipOutputStream}

/**
  * Created by vincentdupont on 18/4/17.
  */
class Zip extends Compression{

//  private var currentChunkIndex : Int = 0
//
//  private def getChunkName : String = {
//    val chunkName = s"archive.part.$currentChunkIndex.zip"
//    currentChunkIndex += 1
//    chunkName
//  }

  def compress(in: File, out: File, size: Int): Unit = {


//    val bufferSize = 1024 * 1024 * size
//    val buffer = new Array[Byte](bufferSize)
//
    val files = FileSystem.getListOfFiles(in)

//    var zos = new ZipOutputStream(new FileOutputStream(new File(out, getChunkName)))
    val zos = new ZipOutputStream(new FileOutputStream(new File(out, "archive.zip")))

    files.foreach { name =>

      zos.putNextEntry(new ZipEntry(name.toString))

      val bis = new BufferedInputStream(new FileInputStream(name))

      Stream.continually(bis.read).takeWhile(_ > -1).foreach(zos.write)

      bis.close()
      zos.closeEntry()
    }
    zos.close()
  }

  override def extract(in: File, out: File): Unit = {

    val buffer = new Array[Byte](1024)

    val zis = new ZipInputStream(new FileInputStream(in))
    Stream
      .continually(zis.getNextEntry)
      .takeWhile(_ != null)
      .foreach { entry =>
        val newFile = new File(out, entry.getName)
        new File(newFile.getParent).mkdirs()
        val fos = new FileOutputStream(newFile)

        Stream.continually(zis.read(buffer))
          .takeWhile(_ > 0).foreach(len => fos.write(buffer, 0, len))

        fos.close()
      }
    zis.closeEntry()
    zis.close()

  }
}
