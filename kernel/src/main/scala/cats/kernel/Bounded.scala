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

package cats.kernel

import java.util.UUID
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.{specialized => sp}

/**
 * A type class used to name the lower limit of a type.
 */
trait LowerBounded[@sp A] {
  def partialOrder: PartialOrder[A]

  /**
   * Returns the lower limit of a type.
   */
  def minBound: A
}

trait LowerBoundedFunctions[L[T] <: LowerBounded[T]] {
  def minBound[@sp A](implicit ev: L[A]): A = ev.minBound
}

object LowerBounded extends LowerBoundedFunctions[LowerBounded] {
  @inline def apply[A](implicit l: LowerBounded[A]): LowerBounded[A] = l

  implicit def catsKernelLowerBoundedForUnit: LowerBounded[Unit] = cats.kernel.instances.all.catsKernelStdOrderForUnit
  implicit def catsKernelLowerBoundedForBoolean: LowerBounded[Boolean] =
    cats.kernel.instances.all.catsKernelStdOrderForBoolean
  implicit def catsKernelLowerBoundedForByte: LowerBounded[Byte] = cats.kernel.instances.all.catsKernelStdOrderForByte
  implicit def catsKernelLowerBoundedForInt: LowerBounded[Int] = cats.kernel.instances.all.catsKernelStdOrderForInt
  implicit def catsKernelLowerBoundedForShort: LowerBounded[Short] =
    cats.kernel.instances.all.catsKernelStdOrderForShort
  implicit def catsKernelLowerBoundedForLong: LowerBounded[Long] = cats.kernel.instances.all.catsKernelStdOrderForLong
  implicit def catsKernelLowerBoundedForDuration: LowerBounded[Duration] =
    cats.kernel.instances.all.catsKernelStdOrderForDuration
  implicit def catsKernelLowerBoundedForFiniteDuration: LowerBounded[FiniteDuration] =
    cats.kernel.instances.all.catsKernelStdOrderForFiniteDuration
  implicit def catsKernelLowerBoundedForChar: LowerBounded[Char] = cats.kernel.instances.all.catsKernelStdOrderForChar
  implicit def catsKernelLowerBoundedForString: LowerBounded[String] =
    cats.kernel.instances.all.catsKernelStdOrderForString
  implicit def catsKernelLowerBoundedForSymbol: LowerBounded[Symbol] =
    cats.kernel.instances.all.catsKernelStdOrderForSymbol
  implicit def catsKernelLowerBoundedForUUID: LowerBounded[UUID] = cats.kernel.instances.all.catsKernelStdOrderForUUID
}

/**
 * A type class used to name the upper limit of a type.
 */
trait UpperBounded[@sp A] {
  def partialOrder: PartialOrder[A]

  /**
   * Returns the upper limit of a type.
   */
  def maxBound: A
}

trait UpperBoundedFunctions[U[T] <: UpperBounded[T]] {
  def maxBound[@sp A](implicit ev: U[A]): A = ev.maxBound
}

object UpperBounded extends UpperBoundedFunctions[UpperBounded] {
  @inline def apply[A](implicit u: UpperBounded[A]): UpperBounded[A] = u

  implicit def catsKernelUpperBoundedForUnit: UpperBounded[Unit] = cats.kernel.instances.all.catsKernelStdOrderForUnit
  implicit def catsKernelUpperBoundedForBoolean: UpperBounded[Boolean] =
    cats.kernel.instances.all.catsKernelStdOrderForBoolean
  implicit def catsKernelUpperBoundedForByte: UpperBounded[Byte] = cats.kernel.instances.all.catsKernelStdOrderForByte
  implicit def catsKernelUpperBoundedForInt: UpperBounded[Int] = cats.kernel.instances.all.catsKernelStdOrderForInt
  implicit def catsKernelUpperBoundedForShort: UpperBounded[Short] =
    cats.kernel.instances.all.catsKernelStdOrderForShort
  implicit def catsKernelUpperBoundedForLong: UpperBounded[Long] = cats.kernel.instances.all.catsKernelStdOrderForLong
  implicit def catsKernelUpperBoundedForDuration: UpperBounded[Duration] =
    cats.kernel.instances.all.catsKernelStdOrderForDuration
  implicit def catsKernelUpperBoundedForFiniteDuration: UpperBounded[FiniteDuration] =
    cats.kernel.instances.all.catsKernelStdOrderForFiniteDuration
  implicit def catsKernelUpperBoundedForChar: UpperBounded[Char] = cats.kernel.instances.all.catsKernelStdOrderForChar
  implicit def catsKernelUpperBoundedForUUID: UpperBounded[UUID] = cats.kernel.instances.all.catsKernelStdOrderForUUID
}
