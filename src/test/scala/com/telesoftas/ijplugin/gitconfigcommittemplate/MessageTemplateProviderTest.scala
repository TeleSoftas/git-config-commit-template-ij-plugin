package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.LocalChangeList
import git4idea.commands.{Git, GitCommandResult, GitLineHandler}
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.File
import scala.jdk.CollectionConverters.SeqHasAsJava

class MessageTemplateProviderTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  "runGitConfigCommitTemplateCommand" should "run with errorless command" in {
    val context = MockContext()

    val sut = new MessageTemplateProvider(context.git, (_, _, _) => context.gitLineHandler, _ => ())
    sut.runGitConfigCommitTemplateCommand(mock[Project]).toOption shouldBe defined
  }

  it should "not return contents on error" in {
    val context = MockContext()
    when(context.git.runCommand(context.gitLineHandler)).thenThrow(new Exception)

    val sut = new MessageTemplateProvider(context.git, (_, _, _) => context.gitLineHandler, _ => ())
    sut.runGitConfigCommitTemplateCommand(mock[Project]).toOption shouldBe empty
  }

  "getMessageTemplate" should "read and return template" in {
    val context  = MockContext()
    val filePath = new File("src/test/resources/nonEmptyFile")
    when(context.gitCommandResult.getOutput).thenReturn(List(filePath.getAbsolutePath).asJava)

    val sut = new MessageTemplateProvider(context.git, (_, _, _) => context.gitLineHandler, _ => ())
    sut.getMessageTemplate(context.project) should be(Some("contents"))
  }

  it should "not fail when path is incorrect" in {
    val context  = MockContext()
    val filePath = new File("not-existing-file")
    when(context.gitCommandResult.getOutput).thenReturn(List(filePath.getAbsolutePath).asJava)

    val sut = new MessageTemplateProvider(context.git, (_, _, _) => context.gitLineHandler, _ => ())
    sut.getMessageTemplate(context.project) shouldBe empty
  }

  "getCommitMessage" should "return template when message is null" in {
    val context  = MockContext()
    val filePath = new File("src/test/resources/nonEmptyFile")
    when(context.gitCommandResult.getOutput).thenReturn(List(filePath.getAbsolutePath).asJava)

    val sut = new MessageTemplateProvider(context.git, (_, _, _) => context.gitLineHandler, _ => ())

    sut.getCommitMessage(context.localChangeList, context.project) shouldBe "contents"
  }

  it should "return template when message is empty" in {
    val context  = MockContext()
    val filePath = new File("src/test/resources/nonEmptyFile")
    when(context.gitCommandResult.getOutput).thenReturn(List(filePath.getAbsolutePath).asJava)

    val sut = new MessageTemplateProvider(context.git, (_, _, _) => context.gitLineHandler, _ => ())

    when(context.localChangeList.getComment).thenReturn("")
    sut.getCommitMessage(context.localChangeList, context.project) shouldBe "contents"
  }

  it should "return message when it is not empty (template is ignored)" in {
    val context  = MockContext()
    val filePath = new File("src/test/resources/nonEmptyFile")
    when(context.gitCommandResult.getOutput).thenReturn(List(filePath.getAbsolutePath).asJava)

    val sut = new MessageTemplateProvider(context.git, (_, _, _) => context.gitLineHandler, _ => ())

    when(context.localChangeList.getComment).thenReturn("message")
    sut.getCommitMessage(context.localChangeList, context.project) shouldBe "message"
  }

  case class MockContext(
    git: Git = mock[Git],
    gitCommandResult: GitCommandResult = mock[GitCommandResult],
    gitLineHandler: GitLineHandler = mock[GitLineHandler],
    localChangeList: LocalChangeList = mock[LocalChangeList],
    project: Project = mock[Project]) {

    when(git.runCommand(gitLineHandler)).thenReturn(gitCommandResult)
    when(project.getProjectFilePath).thenReturn(".")
    when(localChangeList.getComment).thenReturn(null)
  }

}
