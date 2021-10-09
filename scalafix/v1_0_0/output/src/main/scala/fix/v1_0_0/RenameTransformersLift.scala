package fix
package to1_0_0

object RenameTransformersLiftTests {
  import cats.instances.OptionI._
  import cats.instances.StringI._
  import cats.data.{Kleisli, StateT, WriterT}

  val fa: Option[Int] = Some(42)
  val k: Kleisli[Option, Nothing, Int] = Kleisli.liftF(fa)
  val w: WriterT[Option, String, Int] = WriterT.liftF(fa)
  val s: StateT[Option, Nothing, Int] = StateT.liftF(fa)
}
