package com.telesoftas.ijplugin

import com.intellij.openapi.project._

import java.io._
import scala.io._
import scala.util._

package object gitconfigcommittemplate {

  implicit class ImplicitsFileApi(file: File) {
    def contents: List[File] = Option(file.listFiles()).map(_.toList).toList.flatten

    def lookUp(condition: File => Boolean): Option[File] = {
      @scala.annotation.tailrec
      def loop(in: File = file.getAbsoluteFile, out: Option[File] = None): Option[File] =
        if (in.parent.isEmpty || out.isDefined) out
        else if (condition(in)) loop(in, Some(in))
        else loop(in.getParentFile, None)

      loop()
    }

    def parent: Option[File] = Option(file.getParentFile)

    def read: Option[String] = Try {
      val source   = Source.fromFile(file)
      val contents = source.getLines().toList.mkString("\n")
      source.close()
      contents
    }.toOption
  }

  implicit class ImplicitProjectApi(project: Project) {
    def repoRoot: Option[File] =
      new File(project.getProjectFilePath)
        .getAbsoluteFile
        .lookUp(_.contents.exists(f => f.isDirectory && f.getName == ".git"))
  }

  implicit class ImplicitCastingApi(a: Any) {
    def doIf[A, B](clazz: Class[A])(f: A => Option[B]): Option[B] =
      if (clazz.isInstance(a)) Some(f(clazz.cast(a))).flatten
      else None
  }

}
