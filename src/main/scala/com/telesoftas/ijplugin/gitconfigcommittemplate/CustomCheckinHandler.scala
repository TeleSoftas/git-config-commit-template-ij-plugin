package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi._
import com.intellij.openapi.project._
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.checkin.CheckinHandler._
import com.intellij.openapi.vcs.checkin._
import com.intellij.openapi.vcs.ui._
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessageField
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessageField._
import git4idea.i18n.GitBundle

class CustomCheckinHandler extends CheckinHandler {

  private var project: Option[Project]                       = None
  private var commitMessageField: Option[CommitMessageField] = None

  override def getAfterCheckinConfigurationPanel(disposable: Disposable): RefreshableOnComponent = {
    project = disposable.doIf(classOf[CommitMessageHandler])(c => Option(c.getWorkflow.getProject))
    commitMessageField = CommitMessageField(disposable)
    super.getAfterCheckinConfigurationPanel(disposable)
  }

  override def beforeCheckin(): CheckinHandler.ReturnResult = {
    commitMessageField.fold(super.beforeCheckin()) { messageField =>
      filteredMessage(messageField.getText) match {
        case Some(message) =>
          messageField.setText(message)
          ReturnResult.COMMIT
        case None          =>
          project.foreach(
            Messages.showMessageDialog(
              _,
              "Specify message in uncommented line",
              GitBundle.message("git.commit.message.empty.title"),
              Messages.getErrorIcon,
            ),
          )
          ReturnResult.CANCEL
      }
    }
  }

  private def filteredMessage(commitMessage: String): Option[String] = {
    val trimmedInLines = commitMessage.trim.split("\n").map(_.trim)
    val finalLines     = trimmedInLines.filterNot(line => line.startsWith("#"))
    if (finalLines.exists(_.nonEmpty)) Some(finalLines.mkString("\n"))
    else None
  }

}
