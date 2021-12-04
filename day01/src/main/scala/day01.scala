import zio._
import zio.blocking.Blocking
import zio.console._
import zio.nio.channels._
import zio.nio.core.file
import zio.nio.file._
import zio.stream._

import java.io.{File, FileInputStream, IOException}
import java.nio.file.StandardOpenOption

object day01 extends App {

  override def run(args: List[String]): URIO[Console with Blocking, ExitCode] = {

    val filePath = file.Path("input.txt")

    def open(filename: String): ZStream[Blocking, IOException, Byte] = {
      val file = new File(getClass.getClassLoader.getResource(filename).getPath)
      val fis = new FileInputStream(file)

      ZStream.fromInputStream(fis)
    }

    val stream: ZStream[Blocking, IOException, Byte] =
      open(filePath.toString())

    val program: ZIO[Console with Blocking, IOException, Int] =
      for {
        data <- stream
          .aggregate(ZTransducer.utf8Decode)
          .aggregate(ZTransducer.splitLines)
          .zipWithPrevious
          .map {
            case (None, _) => 0
            case (Some(t1), t2) =>
              if (t2.toInt > t1.toInt) {
                1
              } else {
                0
              }
          }
          .fold(0) {
            (it1: Int, it2: Int) => {
              it1 + it2
            }
          }

      } yield data


    program
      .tap(r => putStrLn(s"star 1: ${r.toString}"))
      .catchAll(err => ZIO.debug(err))
      .exitCode

  }

}
