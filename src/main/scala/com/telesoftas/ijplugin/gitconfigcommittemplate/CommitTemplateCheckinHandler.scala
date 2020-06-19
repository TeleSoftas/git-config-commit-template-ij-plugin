package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.notification.NotificationType._
import com.intellij.notification._
import com.intellij.openapi._
import com.intellij.openapi.project._
import com.intellij.openapi.vcs.checkin.CheckinHandler._
import com.intellij.openapi.vcs.checkin._
import com.intellij.openapi.vcs.ui._
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessage

class CustomCheckinHandler extends CheckinHandler {

  private var commitMessage: Option[wrapper.CommitMessage] = None

  override def getAfterCheckinConfigurationPanel(disposable: Disposable): RefreshableOnComponent = {
    commitMessage = wrapper.CommitMessage(disposable)
    super.getAfterCheckinConfigurationPanel(disposable)
  }

  override def beforeCheckin(): CheckinHandler.ReturnResult = {
    val result = for {
      message      <- commitMessage
      finalMessage <- filteredMessage(message)
      _             = message.set(finalMessage)
    } yield ReturnResult.COMMIT

    result.toRight(CommitNotification.show(_.MessageCanNotStartWithComment)).getOrElse(ReturnResult.CANCEL)
  }

  private def filteredMessage(commitMessage: CommitMessage): Option[String] = {
    val trimmed = commitMessage.get.trim
    if (trimmed.startsWith("#")) None
    else Some(trimmed.split("\n").filterNot(_.trim.startsWith("#")).mkString("\n"))
  }

}

object CommitNotification {

  private val LocalGroup = new NotificationGroup(
    getClass.getCanonicalName,
    NotificationDisplayType.BALLOON,
    false,
  )

  def show(f: NotificationInGroup.type => Notification): Unit =
    Notifications.Bus.notify(f(NotificationInGroup), project)

  private def project = ProjectManager.getInstance.getOpenProjects()(0)

  object NotificationInGroup {
    def MessageCanNotStartWithComment: Notification =
      LocalGroup.createNotification("Commit message can't start with a comment", WARNING)
  }

}
