package infrastructure

import cats._
import cats.syntax.applicative._
import fs2.io.file.Path

object config {
  def load[F[_]: Applicative]: F[AppConfig] =
    AppConfig(
      Path("./source/file1"),
      Path("./source/file2"),
      Path("./destination/file3")
    ).pure[F]

  case class AppConfig(file1: Path, file2: Path, target: Path)

}
