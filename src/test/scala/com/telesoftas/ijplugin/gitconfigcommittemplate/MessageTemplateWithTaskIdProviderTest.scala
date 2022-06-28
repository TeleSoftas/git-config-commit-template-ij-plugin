package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.LocalChangeList
import com.intellij.openapi.vcs.checkin.CheckinHandler.ReturnResult
import git4idea.commands.{Git, GitCommandResult, GitLineHandler}
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.Tables._

import java.io.{File, FileOutputStream}
import java.nio.file.Files
import scala.jdk.CollectionConverters.SeqHasAsJava

class MessageTemplateWithTaskIdProviderTest
    extends AnyFlatSpec
    with Matchers
    with MockitoSugar
    with ArgumentMatchersSugar {

  it should "should recognise gh id" in {
    Table(
      ("template", "branch", "expected"),
      ("The ${TaskId} is the change", "14-the-task", "The gh-14 is the change"),
      ("The ${TaskId} is the change", "PROD-14-the-task", "The PROD-14 is the change"),
      ("The ${TaskId} is the change", "the-task", "The  is the change"),
      ("The ${GhTaskId} is the change", "14-the-task", "The gh-14 is the change"),
      ("The ${GhTaskId} is the change", "PROD-14-the-task", "The  is the change"),
      ("The ${GhTaskId} is the change", "the-task", "The  is the change"),
      ("The ${JiraTaskId} is the change", "14-the-task", "The  is the change"),
      ("The ${JiraTaskId} is the change", "PROD-14-the-task", "The PROD-14 is the change"),
      ("The ${JiraTaskId} is the change", "the-task", "The  is the change")
    ).forEvery { (template, branch, expected) =>
      val context  = MockContext()
      val sut      = new MessageTemplateProvider(context.git, (_, _, _) => context.gitLineHandler, _ => ())
      val filePath = new File("build/template").withBody(template)
      when(context.gitConfigCommandResult.getOutput).thenReturn(List(filePath.getAbsolutePath).asJava)
      when(context.gitBranchCommandResult.getOutput).thenReturn(List(branch).asJava)

      sut.getMessageTemplate(mock[Project]) shouldBe Some(expected)
    }
  }

  implicit class FileImplicit(file: File) {
    def withBody(body: String): File = {
      val stream = new FileOutputStream(file)
      stream.write(body.getBytes)
      stream.close()
      file
    }
  }

  case class MockContext(
      git: Git = mock[Git],
      gitConfigCommandResult: GitCommandResult = mock[GitCommandResult],
      gitBranchCommandResult: GitCommandResult = mock[GitCommandResult],
      gitLineHandler: GitLineHandler = mock[GitLineHandler],
      localChangeList: LocalChangeList = mock[LocalChangeList],
      project: Project = mock[Project]
  ) {

    when(gitBranchCommandResult.getOutput).thenReturn(List("").asJava)
    when(git.runCommand(any[GitLineHandler])).thenReturn(gitConfigCommandResult, gitBranchCommandResult)
    when(project.getProjectFilePath).thenReturn(".")
    when(localChangeList.getComment).thenReturn(null)
  }

}
