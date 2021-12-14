package infrastructure

import cats._
import cats.syntax.applicative._
import fs2.io.file.Path

object config {
  def load[F[_]: Applicative]: F[AppConfig] =
    AppConfig(
      Path("./data/source/file1"),
      Path("./data/source/file2"),
      Path("./data/destination/file3")
    ).pure[F]

  case class AppConfig(file1: Path, file2: Path, target: Path)

}
