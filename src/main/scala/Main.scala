import cats.Show
import cats.effect._
import cats.effect.std.Console
import domain.SiteCreationMessage
import fs2.io.file.{Files, Path}
import fs2.{Pipe, Stream, text}
import io.circe.fs2._
import io.circe.syntax.EncoderOps
import infrastructure._
import io.circe.{Decoder, Encoder}


object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    program[IO]
      .compile
      .drain
      .as(ExitCode.Success)

  /** I want program to return creation of output file */
  def program[F[_] : Files : Sync]: Stream[F, SiteCreationMessage] = {
    Stream.eval(config.load[F]).flatMap(config => {
      val partA: Stream[F, SiteCreationMessage] =
        for {
          partAB <- consumeFile[F, SiteCreationMessage](config.file1)
          partB <- consumeFile[F, SiteCreationMessage](config.file1)
          if partAB != partB
        } yield partAB

      partA
        .through(writeFile(config.target))
    })
  }

  def consumeFile[F[_] : Files : Sync, A: Decoder](path: Path): Stream[F, A] =
    Files[F]
      .readAll(path)
      .through(text.utf8.decode)
      .through(text.lines)
      .through(stringStreamParser)
      .through(decoder[F, A])


  def writeFile[F[_]: Files, A: Encoder](target: Path): Pipe[F, A, fs2.INothing]  =
    in => in
      .through(_.map(_.asJson.noSpaces))
      .through(text.utf8.encode)
      .through(Files[F].writeAll(target))

}