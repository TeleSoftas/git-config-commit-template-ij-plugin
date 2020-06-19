package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi.vcs._
import com.intellij.openapi.vcs.changes._
import com.intellij.openapi.vcs.checkin._

class CommitTemplateCheckinHandlerFactory extends CheckinHandlerFactory {
  override def createHandler(panel: CheckinProjectPanel, context: CommitContext): CheckinHandler =
    new CustomCheckinHandler
}
