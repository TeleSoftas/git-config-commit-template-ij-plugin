package com.telesoftas.ijplugin.gitconfigcommittemplate

import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MessageTemplateProviderWithBranchRegexTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

//  "getMessageTemplate" should "return filled regex part" in {
//    val context = MockContext()
//
//    val branchName = "17-some-github-branch"
//    val template = s"""$${{^}}"""
//
//    val sut = new MessageTemplateProvider(context.git, (_, _, _) => context.gitLineHandler, _ => ())
//    sut.getMessageTemplate(mock[Project]) shouldBe defined
//    sut.getMessageTemplate(mock[Project]) shouldBe Some()
//  }
//
//  case class MockContext(
//    git: Git = mock[Git],
//    gitCommandResult: GitCommandResult = mock[GitCommandResult],
//    gitLineHandler: GitLineHandler = mock[GitLineHandler],
//    localChangeList: LocalChangeList = mock[LocalChangeList],
//    project: Project = mock[Project]) {
//
//    when(git.runCommand(gitLineHandler)).thenReturn(gitCommandResult)
//    when(project.getProjectFilePath).thenReturn(".")
//    when(localChangeList.getComment).thenReturn(null)
//  }

}
