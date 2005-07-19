/*
 * Copyright 2000-2005 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.ide.util.treeView.smartTree;

import javax.swing.*;

public class ActionPresentationData implements ActionPresentation{
  private final String myText;
  private final String myDescription;
  private final Icon myIcon;

  public ActionPresentationData(String text, String description, Icon icon) {
    myText = text;
    myDescription = description;
    myIcon = icon;
  }

  public String getText() {
    return myText;
  }

  public String getDescription() {
    return myDescription;
  }

  public Icon getIcon() {
    return myIcon;
  }
}
