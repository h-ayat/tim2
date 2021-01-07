package tim.core

import tim.core.Tag
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.extras.semiauto._
import io.circe.generic.extras.defaults._
import zio.IO

sealed trait Serial[T] {
  def serialize(t: T): String
  def deserialize(s: String): IO[Error, T]
}

object Circe {
  private implicit val tagIdEncoder: Codec[TagId] = deriveUnwrappedCodec
  private implicit val conceptIdEncoder: Codec[ConceptId] = deriveUnwrappedCodec

  private def make[T: Decoder: Encoder]: Serial[T] = new Serial[T] {
    override def serialize(t: T): String = t.asJson.noSpaces

    override def deserialize(s: String): zio.IO[Error, T] =
      IO.fromEither(decode[T](s))
  }

  val tag = make[Tag]

}
