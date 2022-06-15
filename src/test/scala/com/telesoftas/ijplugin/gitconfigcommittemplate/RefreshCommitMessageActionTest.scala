package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi.actionSystem.{AnActionEvent, DataContext}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.{CommitMessageI, VcsDataKeys}
import com.intellij.openapi.vcs.ui.Refreshable
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RefreshCommitMessageActionTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  it should "not throw exception on null inputs" in {
    val mock = MockContext()

    new RefreshCommitMessageAction(mock.messageTemplateProvider).actionPerformed(null)

    verify(mock.messageTemplateProvider, times(0)).getMessageTemplate(_)
  }

  it should "not throw exception on random inputs" in {
    val mock = MockContext()

    new RefreshCommitMessageAction(mock.messageTemplateProvider).actionPerformed(mock.anActionEvent)

    verify(mock.anActionEvent, times(2)).getDataContext
    verify(mock.messageTemplateProvider, times(0)).getMessageTemplate(_)
  }

  it should "not throw exception on random inputss" in {
    val mock = MockContext()
    when(mock.messageTemplateProvider.getMessageTemplate(mock.project)).thenReturn(Some("template"))
    when(mock.dataContext.getData(Refreshable.PANEL_KEY.getName)).thenReturn(mock.commitMessageControl)

    new RefreshCommitMessageAction(mock.messageTemplateProvider).actionPerformed(mock.anActionEvent)

    verify(mock.anActionEvent, times(1)).getDataContext
    verify(mock.commitMessageControl).setCommitMessage("template")
  }

  it should "not throw exception on random inputssss" in {
    val mock = MockContext()
    when(mock.messageTemplateProvider.getMessageTemplate(mock.project)).thenReturn(Some("template"))
    when(mock.dataContext.getData(Refreshable.PANEL_KEY.getName)).thenReturn(mock.justRefreshable)
    when(mock.dataContext.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL.getName)).thenReturn(mock.commitMessageControl)

    new RefreshCommitMessageAction(mock.messageTemplateProvider).actionPerformed(mock.anActionEvent)

    verify(mock.anActionEvent, times(2)).getDataContext
    verify(mock.commitMessageControl).setCommitMessage("template")
  }

  case class MockContext(
    anActionEvent: AnActionEvent = mock[AnActionEvent],
    project: Project = mock[Project],
    dataContext: DataContext = mock[DataContext],
    messageTemplateProvider: MessageTemplateProvider = mock[MessageTemplateProvider],
    justRefreshable: Refreshable = mock[Refreshable],
    commitMessageControl: CommitMessageI with Refreshable = mock[CommitMessageI with Refreshable]) {

    when(anActionEvent.getProject).thenReturn(project)
    when(anActionEvent.getDataContext).thenReturn(dataContext)

  }

}
