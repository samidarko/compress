package algorithm
import java.io._
import java.nio.file.{Path, Paths}
import java.util.zip.{ZipEntry, ZipInputStream, ZipOutputStream}

/**
  * Created by vincentdupont on 18/4/17.
  */
class Zip extends CompressionAlgo{

  override def compress(in: File, out: File, size: Int): Unit = {

    val files = FileSystem.getListOfFiles(in)

    // TODO test if `out` is directory
    val zip = new ZipOutputStream(new FileOutputStream(out))

    files.foreach { name =>
      zip.putNextEntry(new ZipEntry(name.toString))
      val in = new BufferedInputStream(new FileInputStream(name))
      var b = in.read()
      while (b > -1) {
        zip.write(b)
        b = in.read()
      }
      in.close()
      zip.closeEntry()
    }
    zip.close()
  }

  override def extract(in: File, out: File): Unit = {

    val buffer = new Array[Byte](1024)

    val zis = new ZipInputStream(new FileInputStream(in))
    Stream
      .continually(zis.getNextEntry)
      .takeWhile(_ != null)
      .foreach { entry =>
        val fileName = entry.getName
        val newFile = new File(out, fileName)
        new File(newFile.getParent).mkdirs()
        if (! fileName.endsWith("/")) {
          val fos = new FileOutputStream(newFile)

          var len: Int = zis.read(buffer)

          while (len > 0) {

            fos.write(buffer, 0, len)
            len = zis.read(buffer)
          }

          fos.close()
        }
      }
    zis.closeEntry()
    zis.close()

  }
}
