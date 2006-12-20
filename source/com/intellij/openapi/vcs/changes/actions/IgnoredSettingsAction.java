/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

/*
 * Created by IntelliJ IDEA.
 * User: yole
 * Date: 20.12.2006
 * Time: 19:42:50
 */
package com.intellij.openapi.vcs.changes.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vcs.changes.ui.IgnoredSettingsDialog;
import com.intellij.openapi.vcs.changes.ChangeListManager;

public class IgnoredSettingsAction extends AnAction {
  public IgnoredSettingsAction() {
    super("Configure Ignored Files...", "Specify file paths and masks which are ignored",
          IconLoader.getIcon("/actions/properties.png"));
  }

  public void actionPerformed(AnActionEvent e) {
    Project project = e.getData(DataKeys.PROJECT);
    IgnoredSettingsDialog dlg = new IgnoredSettingsDialog(project);
    dlg.setItems(ChangeListManager.getInstance(project).getFilesToIgnore());
    dlg.show();
    if (!dlg.isOK()) {
      return;
    }
    ChangeListManager.getInstance(project).setFilesToIgnore(dlg.getItems());
  }
}