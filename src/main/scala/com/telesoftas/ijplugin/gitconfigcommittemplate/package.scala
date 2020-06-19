package com.telesoftas.ijplugin

import java.io._

import com.intellij.openapi.project._
import com.intellij.openapi.vfs._
import git4idea.commands._

import scala.io._

package object gitconfigcommittemplate {

  implicit class ImplicitsFileApi(file: File) {
    def contents: List[File] = Option(file.listFiles()).map(_.toList).toList.flatten

    def lookUp(condition: File => Boolean): Option[File] = {
      @scala.annotation.tailrec
      def loop(in: File = file, out: Option[File] = None): Option[File] =
        if (in.parent.isEmpty || out.isDefined) out
        else if (condition(in)) loop(in, Some(in))
        else loop(in.getParentFile, None)

      loop()
    }

    def parent: Option[File] = Option(file.getParentFile)

    def read: String = {
      val source   = Source.fromFile(file)
      val contents = source.getLines().toList.mkString("\n")
      source.close()
      contents
    }
  }

  implicit class ImplicitProjectApi(project: Project) {
    def repoRoot: Option[VirtualFile] = {
      val projectRoot = new File(project.getProjectFilePath)
      val repoRoot    = projectRoot.lookUp(_.contents.exists(f => f.isDirectory && f.getName == ".git"))
      repoRoot.map(LocalFileSystem.getInstance().findFileByIoFile)
    }
  }

  implicit class ImplicitGitLineHandlerApi(handler: GitLineHandler) {
    def withParameters(parameters: String*): GitLineHandler = {
      handler.addParameters(parameters: _*)
      handler
    }
  }

}
