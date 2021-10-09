/*
 * Copyright (c) 2015 Typelevel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cats

package object instances {
  object all extends AllInstancesBinCompat
  object ArraySeqI extends ArraySeqInstances
  object BigIntI extends BigIntInstances
  object BigDecimalI extends BigDecimalInstances
  object BitSetI extends BitSetInstances
  object BooleanI extends BooleanInstances
  object ByteI extends ByteInstances
  object CharI extends CharInstances
  object DoubleI extends DoubleInstances
  object DurationI extends CoreDurationInstances with DurationInstances
  object EitherI extends EitherInstances
  object EqI extends EqInstances
  object EquivI extends EquivInstances
  object FloatI extends FloatInstances
  object FiniteDurationI extends CoreFiniteDurationInstances with FiniteDurationInstances
  object DeadlineI extends DeadlineInstances
  object FunctionI extends FunctionInstances with FunctionInstancesBinCompat0
  object PartialFunctionI extends PartialFunctionInstances

  /**
   * @deprecated
   *   Any non-pure use of [[scala.concurrent.Future Future]] with Cats is error prone
   *   (particularly the semantics of [[cats.Traverse#traverse traverse]] with regard to execution order are unspecified).
   *   We recommend using [[https://typelevel.org/cats-effect/ Cats Effect `IO`]] as a replacement for ''every'' use case of [[scala.concurrent.Future Future]].
   *   However, at this time there are no plans to remove these instances from Cats.
   *
   * @see [[https://github.com/typelevel/cats/issues/4176 Changes in Future traverse behavior between 2.6 and 2.7]]
   */
  object FutureI extends FutureInstances

  object IntI extends IntInstances
  object InvariantI extends InvariantMonoidalInstances with InvariantInstances with InvariantInstancesBinCompat0
  object ListI extends ListInstances with ListInstancesBinCompat0
  object LongI extends LongInstances
  object OptionI extends OptionInstances with OptionInstancesBinCompat0
  object MapI extends MapInstances with MapInstancesBinCompat0 with MapInstancesBinCompat1
  object OrderI extends OrderInstances
  object OrderingI extends OrderingInstances
  object ParallelI extends ParallelInstances
  object PartialOrderI extends PartialOrderInstances
  object PartialOrderingI extends PartialOrderingInstances
  object QueueI extends QueueInstances
  object SetI extends SetInstances
  object SeqI extends SeqInstances
  object ShortI extends ShortInstances
  object SortedMapI
      extends SortedMapInstances
      with SortedMapInstancesBinCompat0
      with SortedMapInstancesBinCompat1
      with SortedMapInstancesBinCompat2
  object SortedSetI extends SortedSetInstances with SortedSetInstancesBinCompat0 with SortedSetInstancesBinCompat1

  @deprecated("Use cats.instances.lazyList", "2.0.0-RC2")
  object StreamI extends StreamInstances with StreamInstancesBinCompat0
  object LazyListI extends LazyListInstances
  object StringI extends StringInstances
  object TailRecI extends TailRecInstances
  object TryI extends TryInstances
  object TupleI extends TupleInstances with Tuple2InstancesBinCompat0
  object UnitI extends UnitInstances
  object UuidI extends UUIDInstances
  object VectorI extends VectorInstances with VectorInstancesBinCompat0
}
