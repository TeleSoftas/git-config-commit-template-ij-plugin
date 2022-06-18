package com.telesoftas.ijplugin

import java.io._
import scala.io._
import scala.util._

package object gitconfigcommittemplate {

  implicit class ImplicitsFileApi(file: File) {
    def contents: List[File] = Option(file.listFiles()).map(_.toList).toList.flatten

    def parent: Option[File] = Option(file.getParentFile)

    def read: Either[Throwable, String] = Try {
      val source   = Source.fromFile(file)
      val contents = source.getLines().toList.mkString("\n")
      source.close()
      contents
    }.toEither
  }

  implicit class ImplicitCastingApi(a: Any) {
    def doIf[A, B](clazz: Class[A])(f: A => Option[B]): Option[B] =
      if (clazz.isInstance(a)) Some(f(clazz.cast(a))).flatten
      else None
  }

  implicit class ImplicitModifier[A](a: A) {
    def wrapMutable(f: A => Unit): A = {
      f(a)
      a
    }
  }

  implicit class OptionImplicits[A](o: Option[A]) {
    def toEither(message: => String): Either[Throwable, A] = o match {
      case Some(a) => Right(a)
      case None => Left(new IllegalArgumentException(message))
    }
  }

}
