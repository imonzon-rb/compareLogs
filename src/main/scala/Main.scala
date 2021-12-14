import cats.effect._
import domain.SiteCreationMessage
import fs2.io.file.{Files, Path}
import fs2.{Pipe, Stream, text}
import io.circe.Json
import io.circe.fs2._
import io.circe.syntax.EncoderOps
import infrastructure._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    program[IO]
      .compile
      .drain
      .as(ExitCode.Success)

  /** I want program to return creation of output file */
  def program[F[_] : Files : Sync]: Stream[F, String] = {
    Stream.eval(config.load[F]).flatMap(config => {
      val partA: Stream[F, SiteCreationMessage] = for {
        partAB <- consumeFile(config.file1)
        partB <- consumeFile(config.file2)
        if partAB != partB
      } yield partAB

      partA
        .through(writeFile(config.target))
    })
  }

  def consumeFile[F[_] : Files : Sync](path: Path): Stream[F, SiteCreationMessage] = {
    def buildRequest[F[_]]: Pipe[F, Json, SiteCreationMessage] = ???

    Files[F]
      .readAll(path)
      .through(text.utf8.decode)
      .through(text.lines)
      .through(stringArrayParser)
      .through(buildRequest)
  }


  def writeFile[F[_]: Files](target: Path): Pipe[F, SiteCreationMessage, fs2.INothing]  = {
    def jsonToString[F[_]]: Pipe[F, Json, String] = ???

    in => in
      .through(_.map(_.asJson))
      .through(jsonToString)
      .through(text.utf8.encode)
      .through(Files[F].writeAll(target))
  }

}