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

import scala.{specialized => sp}

/**
 * A group is a monoid where each element has an inverse.
 */
trait Group[@sp(Int, Long, Float, Double) A] extends Any with Monoid[A] {

  /**
   * Find the inverse of `a`.
   *
   * `combine(a, inverse(a))` = `combine(inverse(a), a)` = `empty`.
   *
   * Example:
   * {{{
   * scala> import cats.kernel.instances.all._
   *
   * scala> Group[Int].inverse(5)
   * res0: Int = -5
   * }}}
   */
  def inverse(a: A): A

  /**
   * Remove the element `b` from `a`.
   *
   * Equivalent to `combine(a, inverse(b))`
   *
   * Example:
   * {{{
   * scala> import cats.kernel.instances.all._
   *
   * scala> Group[Int].remove(5, 2)
   * res0: Int = 3
   * }}}
   */
  def remove(a: A, b: A): A = combine(a, inverse(b))

  /**
   * Return `a` appended to itself `n` times. If `n` is negative, then
   * this returns `inverse(a)` appended to itself `n` times.
   */
  override def combineN(a: A, n: Int): A =
    // This method is a bit tricky. Normally, to sum x a negative
    // number of times (n), we can sum (-x) a positive number of times
    // (-n). The issue here is that Int.MinValue cannot be negated; in
    // other words (-MinValue) == MinValue.
    //
    // To work around that, we rely on the fact that we can divide n
    // by 2 if we sum 2a (i.e. combine(a, a)) instead. Here is the
    // transformation we use:
    //
    //   combineN(x, -2147483648)
    //   combineN(combine(x, x), -1073741824)
    //   combineN(inverse(combine(x, x)), 1073741824)
    if (n > 0) repeatedCombineN(a, n)
    else if (n == 0) empty
    else if (n == Int.MinValue) combineN(inverse(combine(a, a)), 1073741824)
    else repeatedCombineN(inverse(a), -n)
}

abstract class GroupFunctions[G[T] <: Group[T]] extends MonoidFunctions[G] {
  def inverse[@sp(Int, Long, Float, Double) A](a: A)(implicit ev: G[A]): A =
    ev.inverse(a)

  def remove[@sp(Int, Long, Float, Double) A](x: A, y: A)(implicit ev: G[A]): A =
    ev.remove(x, y)
}

object Group extends GroupFunctions[Group] {

  /**
   * Access an implicit `Group[A]`.
   */
  @inline final def apply[A](implicit ev: Group[A]): Group[A] = ev
}
