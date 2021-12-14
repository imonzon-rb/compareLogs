import cats.effect._
import fs2.io.file.{Files, Path}
import cats.syntax.all._
import domain.SiteCreationMessage
import io.circe.Json
import io.circe.fs2._
import fs2.{Stream, text}

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    program[IO]
      .compile
      .drain
      .as(ExitCode.Success)

  /** I want program to return creation of output file */
  //        partA     <- partAB.(partB)
  //        x         <- Stream.emit(partAB).withFilter()
  //        partA     <- leftJoin(partAB, partB).where(partAB.id is null)
  def program[F[_] : Files : Sync]: Stream[F, String] = {
    loadConfiguration[F].flatMap(config =>
      (for {
        partAB <- consumeFile(config.file1)
        partB <- consumeFile(config.file2)
        if partAB != partB
      } yield partAB))
      .through(Files[F].writeAll(config.target))
    )
  }

  def loadConfiguration[F[_]]: Stream[F, Configuration] = ???

  case class Configuration(file1: Path, file2: Path, target: Path)


  def consumeFile[F[_] : Files : Sync](path: Path): Stream[F, SiteCreationMessage] =
    Files[F]
      .readAll(path)
      .through(text.utf8.decode)
      .through(text.lines)
      .through(stringArrayParser)
      .evalMap(buildRequest[F])

  def buildRequest[F[_]](json: Json): F[SiteCreationMessage] = ???

}