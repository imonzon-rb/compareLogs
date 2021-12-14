import cats.effect._
import domain.SiteCreationMessage
import fs2.Chunk.Queue
import fs2.io.file.{Files, Path}
import io.circe.Json
import io.circe.fs2._
import fs2.{Pipe, Stream, text}
import io.circe.syntax.EncoderOps

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    program[IO]
      .compile
      .drain
      .as(ExitCode.Success)


  /** I want program to return creation of output file */
  def program[F[_] : Files : Sync]: Stream[F, String] = {
    loadConfiguration[F].flatMap(config =>
      (for {
        partAB <- consumeFile(config.file1)
        partB <- consumeFile(config.file2)
        if partAB != partB
      } yield partAB)
        .through(writeFile(config.target))
        .map(_ => "ok")
    )
  }


  def writeFile[F[_]: Files](target: Path): Pipe[F, SiteCreationMessage, fs2.INothing]  = {
    in => in
      .through(_.map(_.asJson))
      .through(jsonToString)
      .through(text.utf8.encode)
      .through(Files[F].writeAll(target))
  }

  def jsonToString[F[_]]: Pipe[F, Json, String] = ???

  def loadConfiguration[F[_]]: Stream[F, Configuration] = ???

  case class Configuration(file1: Path, file2: Path, target: Path)

  def consumeFile[F[_] : Files : Sync](path: Path): Stream[F, SiteCreationMessage] =
    Files[F]
      .readAll(path)
      .through(text.utf8.decode)
      .through(text.lines)
      .through(stringArrayParser)
      .through(buildRequest)

  def buildRequest[F[_]]: Pipe[F, Json, SiteCreationMessage] = ???


  //        partA     <- partAB.(partB)
  //        x         <- Stream.emit(partAB).withFilter()
  //        partA     <- leftJoin(partAB, partB).where(partAB.id is null)
}