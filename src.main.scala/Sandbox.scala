import fs2.{Pipe, Stream, hash, text}

object Sandbox {
  def a[F[_]]: List[Unit] =
    Stream(1, 1, 2, 3, 4)
      .through(dedupeDuplicates(_ == _))
      .toList
      .map(println)


  def dedupeDuplicates[F[_], I](f: (I, I) => Boolean): Pipe[F, I, I] = si =>
    si.zipWithNext.map {
      case (curr, Some(next: I)) if f(curr, next) => (None: Option[I])
      case (curr, Some(_)) => Some(curr)
      case (curr, None) => Some(curr)
    }.unNone
}
