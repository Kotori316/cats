/*
rule = "scala:fix.v1_0_0.RenameEitherTLiftT"
 */
package fix
package to1_0_0

object RenameEitherTLiftTTests {
  import cats.instances.OptionI._
  import cats.data._
  import cats.data.EitherT._

  val fa: Option[Int] = Some(42)
  val et1: EitherT[Option, Nothing, Int] = EitherT.liftT(fa)
  val et2: EitherT[Option, Nothing, Int] = liftT(fa)
}