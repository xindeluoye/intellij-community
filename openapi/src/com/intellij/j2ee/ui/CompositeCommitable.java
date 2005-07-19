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
package com.intellij.j2ee.ui;

import com.intellij.j2ee.j2eeDom.xmlData.ReadOnlyDeploymentDescriptorModificationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * author: lesya
 */
public class CompositeCommitable implements Commitable {
  private List<Commitable> myComponents = new ArrayList<Commitable>();

  public void addComponent(Commitable panel) {
    myComponents.add(panel);
  }

  public void commit() throws ReadOnlyDeploymentDescriptorModificationException {
    for (Iterator iterator = myComponents.iterator(); iterator.hasNext();) {
      ((Commitable)iterator.next()).commit();
    }
  }

  public void reset() {
    for (Iterator iterator = myComponents.iterator(); iterator.hasNext();) {
      ((Commitable)iterator.next()).reset();
    }
  }

  public void dispose() {
    for (Iterator iterator = myComponents.iterator(); iterator.hasNext();) {
      ((Commitable)iterator.next()).dispose();
    }
  }

  public List<Warning> getWarnings() {
    ArrayList<Warning> result = new ArrayList<Warning>();
    for (Iterator iterator = myComponents.iterator(); iterator.hasNext();) {
      List<Warning> warnings = ((Commitable)iterator.next()).getWarnings();
      if (warnings != null) {
        result.addAll(warnings);
      }
    }
    return result;
  }
}
