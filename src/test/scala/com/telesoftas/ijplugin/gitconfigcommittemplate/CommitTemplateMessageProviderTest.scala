package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.LocalChangeList
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.MessageTemplate.CommandExecutor
import git4idea.commands.Git
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommitTemplateMessageProviderTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  it should "provide message when happy path" in {
    val sut = new CommitTemplateMessageProvider {
      override val git: Option[Git]                  = Some(mock[Git])
      override val executor: Option[CommandExecutor] = Some((_, _, _, _) => Some(s"error: #{msg} // jeeee"))
    }

    val localChangeList = mock[LocalChangeList]
    when(localChangeList.getComment).thenReturn("Message")

    sut.getCommitMessage(localChangeList, mock[Project]) shouldBe "error: Message // jeeee"
  }

  it should "provide default message when no changes" in {
    val sut = new CommitTemplateMessageProvider {
      override val git: Option[Git]                  = Some(mock[Git])
      override val executor: Option[CommandExecutor] = Some((_, _, _, _) => Some(s"error: #{msg} // jeeee"))
    }

    val localChangeList = mock[LocalChangeList]
    sut.getCommitMessage(localChangeList, mock[Project]) shouldBe "error: # message // jeeee"
  }

  it should "provide changes comment when no commit message" in {
    val sut = new CommitTemplateMessageProvider {
      override val git: Option[Git]                  = Some(mock[Git])
      override val executor: Option[CommandExecutor] = Some((_, _, _, _) => None)
    }

    val localChangeList = mock[LocalChangeList]
    when(localChangeList.getComment).thenReturn("Message")
    sut.getCommitMessage(localChangeList, mock[Project]) shouldBe "Message"
  }

}
