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

    val stream1: ZStream[Blocking, IOException, Byte] = open(filePath.toString())

    val star1: ZIO[Blocking, IOException, Int] =
      for {
        data <- stream1
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

    val stream2: ZStream[Blocking, IOException, Byte] = open(filePath.toString())

    val star2: ZIO[Console with Blocking, IOException, Int] =
      for {
        data <- stream2
          .aggregate(ZTransducer.utf8Decode)
          .aggregate(ZTransducer.splitLines)
          .zipWithPreviousAndNext
          .zipWithPrevious
          .map {
            case (None, _) => 0
            case (Some( (None, _, _) ), _) => 0
            case (Some( (_, _, _) ), (_, _, None)) => 0
            case (Some( (t1, t2, t3) ), (t4, t5, t6)) =>
              if (
                (t4.get.toInt + t5.toInt + t6.get.toInt) >
                  (t1.get.toInt + t2.toInt + t3.get.toInt)
              ) {
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


    val app = for {
      one <- star1
      two <- star2
      _ <- putStrLn(s"star 1: ${one.toString}")
      _ <- putStrLn(s"star 2: ${two.toString}")
    } yield ()

    app
      .catchAll(err => ZIO.debug(err))
      .exitCode
  }

}
