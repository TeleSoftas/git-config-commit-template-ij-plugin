package com.telesoftas.ijplugin.gitconfigcommittemplate.taskid

import com.telesoftas.ijplugin.gitconfigcommittemplate.taskid.TaskIdPattern.Empty

object TaskIdInjector {

  implicit class TaskIdSetter(template: String) {
    def setTaskId(branchName: String): String = TaskIdPattern.values
      .foldLeft(template)((tmpl, pattern) =>
        tmpl.replace(pattern.key, (pattern.value orElse Empty.value).apply(branchName))
      )
  }

}
