package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vcs.ui.Refreshable
import com.intellij.openapi.vcs.{CommitMessageI, VcsDataKeys}
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.MessageTemplate

class RefreshCommitMessageAction extends AnAction with DumbAware {
  override def actionPerformed(e: AnActionEvent): Unit =
    for {
      event           <- Option(e)
      panel           <- resolveCommitPanel(event)
      messageTemplate <- MessageTemplate(event.getProject)
      _                = panel.setCommitMessage(messageTemplate.apply(""))
    } yield ()

  private def resolveCommitPanel(e: AnActionEvent): Option[CommitMessageI] =
    Option(e).flatMap(event =>
      Refreshable.PANEL_KEY.getData(event.getDataContext) match {
        case i: CommitMessageI => Some(i)
        case _                 => Option(VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.getDataContext))
      },
    )
}
