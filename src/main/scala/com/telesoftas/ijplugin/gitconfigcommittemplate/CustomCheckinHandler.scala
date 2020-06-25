package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.notification.NotificationType._
import com.intellij.notification._
import com.intellij.openapi._
import com.intellij.openapi.project._
import com.intellij.openapi.vcs.checkin.CheckinHandler._
import com.intellij.openapi.vcs.checkin._
import com.intellij.openapi.vcs.ui._
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessage
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessage.CommitMessageHandler

class CustomCheckinHandler extends CheckinHandler {

  private var project: Option[Project]                     = None
  private var commitMessage: Option[wrapper.CommitMessage] = None

  override def getAfterCheckinConfigurationPanel(disposable: Disposable): RefreshableOnComponent = {
    project = disposable.doIf(classOf[CommitMessageHandler])(c => Option(c.getWorkflow.getProject))
    commitMessage = wrapper.CommitMessage(disposable)
    super.getAfterCheckinConfigurationPanel(disposable)
  }

  override def beforeCheckin(): CheckinHandler.ReturnResult =
    if (commitMessage.isDefined) {
      val result = for {
        message      <- commitMessage
        finalMessage <- filteredMessage(message)
        _             = message.set(finalMessage)
      } yield ReturnResult.COMMIT

      result.toRight(CommitNotification.show(project, _.MessageCanNotStartWithComment)).getOrElse(ReturnResult.CANCEL)
    } else super.beforeCheckin()

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

  def show(project: Option[Project], f: NotificationInGroup.type => Notification): Unit =
    project.foreach(p => Notifications.Bus.notify(f(NotificationInGroup), p))

  object NotificationInGroup {
    def MessageCanNotStartWithComment: Notification =
      LocalGroup.createNotification("Commit message can't start with a comment", WARNING)
  }

}
