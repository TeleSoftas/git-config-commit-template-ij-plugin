package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vcs.ui.Refreshable
import com.intellij.openapi.vcs.{CommitMessageI, VcsDataKeys}

class RefreshCommitMessageAction(messageTemplateProvider: MessageTemplateProvider) extends AnAction with DumbAware {

  def this() = this(new MessageTemplateProvider())

  override def actionPerformed(e: AnActionEvent): Unit =
    for {
      event           <- Option(e)
      panel           <- resolveCommitPanel(event)
      messageTemplate <- messageTemplateProvider.getMessageTemplate(event.getProject)
      _                = panel.setCommitMessage(messageTemplate)
    } yield ()

  private def resolveCommitPanel(e: AnActionEvent): Option[CommitMessageI] =
    Option(e).flatMap(event =>
      Refreshable.PANEL_KEY.getData(event.getDataContext) match {
        case i: CommitMessageI => Some(i)
        case _                 => Option(VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.getDataContext))
      },
    )
}
