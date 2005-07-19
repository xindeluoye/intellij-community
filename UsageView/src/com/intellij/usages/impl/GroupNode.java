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
package com.intellij.usages.impl;

import com.intellij.pom.Navigatable;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageGroup;
import com.intellij.usages.UsageViewSettings;
import com.intellij.usages.rules.MergeableUsage;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: max
 * Date: Dec 16, 2004
 * Time: 5:41:22 PM
 * To change this template use File | Settings | File Templates.
 */
class GroupNode extends Node implements Navigatable {
  private final static NodeComparator COMPARATOR = new NodeComparator();
  private final UsageGroup myGroup;
  private final int myRuleIndex;
  private final Map<UsageGroup, GroupNode> mySubgroupNodes = new HashMap<UsageGroup, GroupNode>();
  private final List<UsageNode> myUsageNodes = new ArrayList<UsageNode>();
  private int myRecursiveUsageCount = 0;

  public GroupNode(UsageGroup group, int ruleIndex, DefaultTreeModel treeModel) {
    super(treeModel);
    setUserObject(group);
    myGroup = group;
    myRuleIndex = ruleIndex;
  }

  protected void updateNotify() {
    myGroup.update();
  }

  public String toString() {
    String result = "";
    if (myGroup != null) result += myGroup.getText(null);
    return children != null ? result + children.toString() : result;
  }

  public GroupNode addGroup(UsageGroup group, int ruleIndex) {
    GroupNode node = mySubgroupNodes.get(group);
    if (node == null) {
      node = new GroupNode(group, ruleIndex, myTreeModel);
      mySubgroupNodes.put(group, node);
      myTreeModel.insertNodeInto(node, this, getNodeInsertionIndex(node));
    }

    return node;
  }

  public void removeAllChildren() {
    super.removeAllChildren();
    mySubgroupNodes.clear();
    myRecursiveUsageCount = 0;
    myUsageNodes.clear();
    myTreeModel.reload(this);
  }

  private UsageNode tryMerge(Usage usage) {
    if (!(usage instanceof MergeableUsage)) return null;
    if (!UsageViewSettings.getInstance().IS_FILTER_DUPLICATED_LINE) return null;
    for (int i = 0; i < myUsageNodes.size(); i++) {
      UsageNode node = myUsageNodes.get(i);
      Usage original = node.getUsage();
      if (original instanceof MergeableUsage) {
        if (((MergeableUsage)original).merge((MergeableUsage)usage)) return node;
      }
    }

    return null;
  }

  public boolean removeUsage(UsageNode usage) {
    final Collection<GroupNode> groupNodes = mySubgroupNodes.values();

    for(Iterator<GroupNode> i = groupNodes.iterator();i.hasNext();) {
      final GroupNode groupNode = i.next();

      if(groupNode.removeUsage(usage)) {
        doUpdate();

        if (groupNode.getRecursiveUsageCount() == 0) {
          myTreeModel.removeNodeFromParent(groupNode);
          mySubgroupNodes.remove(groupNode);
        }
        return true;
      }
    }

    if (myUsageNodes.remove(usage)) {
      doUpdate();
      return true;
    }

    return false;
  }

  private void doUpdate() {
    --myRecursiveUsageCount;
    myTreeModel.nodeChanged(this);
  }

  public UsageNode addUsage(Usage usage) {
    try {
      UsageNode mergedWith = tryMerge(usage);
      if (mergedWith != null) {
        return mergedWith;
      }
      else {
        UsageNode node = new UsageNode(usage, myTreeModel);
        myUsageNodes.add(node);
        myTreeModel.insertNodeInto(node, this, getChildCount());
        return node;
      }
    }
    finally {
      incrementUsageCount();
    }
  }

  private void incrementUsageCount() {
    GroupNode groupNode = this;
    while (true) {
      groupNode.myRecursiveUsageCount++;
      myTreeModel.nodeChanged(groupNode);
      TreeNode parent = groupNode.getParent();
      if (!(parent instanceof GroupNode)) return;
      groupNode = (GroupNode)parent;
    }
  }

  public String tree2string(int indent, String lineSeparator) {
    StringBuffer result = new StringBuffer();
    appendSpaces(result, indent);

    if (myGroup != null) result.append(myGroup.toString());
    result.append("[");
    result.append(lineSeparator);

    Enumeration enumeration = children();
    while (enumeration.hasMoreElements()) {
      Node node = (Node)enumeration.nextElement();
      result.append(node.tree2string(indent + 4, lineSeparator));
      result.append(lineSeparator);
    }

    appendSpaces(result, indent);
    result.append("]");
    result.append(lineSeparator);

    return result.toString();
  }

  protected boolean isDataValid() {
    return myGroup == null || myGroup.isValid();
  }

  protected boolean isDataReadOnly() {
    Enumeration enumeration = children();
    while (enumeration.hasMoreElements()) {
      Node node = (Node)enumeration.nextElement();
      if (node.isReadOnly()) return true;
    }
    return false;
  }

  int getNodeInsertionIndex(DefaultMutableTreeNode node) {
    Enumeration children = children();
    int idx = 0;
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
      if (COMPARATOR.compare(child, node) >= 0) break;
      idx++;
    }
    return idx;
  }

  private static class NodeComparator implements Comparator<DefaultMutableTreeNode> {
    private static int getClassIndex(DefaultMutableTreeNode node) {
      if (node instanceof UsageNode) return 3;
      if (node instanceof GroupNode) return 2;
      if (node instanceof UsageTargetNode) return 1;
      return 0;
    }

    public int compare(DefaultMutableTreeNode n1, DefaultMutableTreeNode n2) {
      int classIdx1 = getClassIndex(n1);
      int classIdx2 = getClassIndex(n2);
      if (classIdx1 != classIdx2) return classIdx1 - classIdx2;
      if (classIdx1 == 2) return ((GroupNode)n1).compareTo((GroupNode)n2);

      return 0;
    }
  }

  public int compareTo(GroupNode groupNode) {
    if (myRuleIndex == groupNode.myRuleIndex) {
      return myGroup.compareTo(groupNode.myGroup);
    }

    return myRuleIndex - groupNode.myRuleIndex;
  }

  public UsageGroup getGroup() {
    return myGroup;
  }

  public int getRecursiveUsageCount() {
    return myRecursiveUsageCount;
  }

  public void navigate(boolean requestFocus) {
    if (myGroup != null) {
      myGroup.navigate(requestFocus);
    }
  }

  public boolean canNavigate() {
    return myGroup != null && myGroup.canNavigate();
  }

  public boolean canNavigateToSource() {
    return myGroup != null && myGroup.canNavigateToSource();
  }


  protected boolean isDataExcluded() {
    Enumeration enumeration = children();
    while (enumeration.hasMoreElements()) {
      Node node = (Node)enumeration.nextElement();
      if (!node.isExcluded()) return false;
    }
    return true;
  }

}
