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
package com.intellij.usages;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.usageView.UsageInfo;

/**
 * @author Eugene Zhuravlev
 *         Date: Jan 17, 2005
 */
public class UsageInfoToUsageConverter {

  public static class TargetElementsDescriptor {
    private final PsiElement[] myPrimarySearchedElements;
    private final PsiElement[] myAdditionalSearchedElements;

    public TargetElementsDescriptor(PsiElement element) {
      this(new PsiElement[]{element});
    }

    public TargetElementsDescriptor(PsiElement[] primarySearchedElements) {
      this(primarySearchedElements, PsiElement.EMPTY_ARRAY);
    }

    public TargetElementsDescriptor(PsiElement[] primarySearchedElements, PsiElement[] additionalSearchedElements) {
      myPrimarySearchedElements = primarySearchedElements != null? primarySearchedElements : PsiElement.EMPTY_ARRAY;
      myAdditionalSearchedElements = additionalSearchedElements != null? additionalSearchedElements : PsiElement.EMPTY_ARRAY;
    }

    /**
     * A read-only attribute describing the target as a "primary" target.
     * A primary target is a target that was the main purpose of the search.
     * All usages of a non-primary target should be considered as a special case of usages of the corresponding primary target.
     * Example: searching field and its getter and setter methods -
     *          the field searched is a primary target, and its accessor methods are non-primary targets, because
     *          for this particular search usages of getter/setter methods are to be considered as a usages of the corresponding field.
     */
    public PsiElement[] getPrimaryElements() {
      return myPrimarySearchedElements;
    }

    public PsiElement[] getAdditionalElements() {
      return myAdditionalSearchedElements;
    }
  }

  public static Usage convert(TargetElementsDescriptor descriptor, UsageInfo usageInfo) {
    Usage usage = _convert(descriptor, usageInfo);
    final UsageConvertor[] convertors = ApplicationManager.getApplication().getComponents(UsageConvertor.class);
    for (int i = 0; i < convertors.length; i++) {
      usage = convertors[i].convert(usage);
    }
    return usage;
  }

  private static Usage _convert(final TargetElementsDescriptor descriptor, final UsageInfo usageInfo) {
    final PsiElement[] primaryElements = descriptor.getPrimaryElements();
    if (isReadWriteAccessibleElements(primaryElements)) {
      final PsiElement usageElement = usageInfo.getElement();
      if (usageElement instanceof PsiReferenceExpression) {
        final Access access = isAccessedForReading((PsiReferenceExpression)usageElement);
        return new ReadWriteAccessUsageInfo2UsageAdapter(usageInfo, access.read, access.write);
      }
    }
    return new UsageInfo2UsageAdapter(usageInfo);
  }

  public static Usage[] convert(TargetElementsDescriptor descriptor, UsageInfo[] usageInfos) {
    Usage[] usages = new Usage[usageInfos.length];
    for (int i = 0; i < usages.length; i++) {
      usages[i] = convert(descriptor, usageInfos[i]);
    }
    return usages;
  }

  private static boolean isReadWriteAccessibleElements(final PsiElement[] elements) {
    if (elements.length == 0) {
      return false;
    }
    for (int idx = 0; idx < elements.length; idx++) {
      if (!(elements[idx] instanceof PsiVariable)) {
        return false;
      }
    }
    return true;
  }

  private static final class Access {
    public final boolean read;
    public final boolean write;

    public Access(final boolean read, final boolean write) {
      this.read = read;
      this.write = write;
    }
  }
  private static Access isAccessedForReading(final PsiReferenceExpression referent) {
    boolean accessedForReading = PsiUtil.isAccessedForReading(referent);
    boolean accessedForWriting = PsiUtil.isAccessedForWriting(referent);
    if (!accessedForWriting) {
      //when searching usages of fields, should show all found setters as a "only write usage"
      PsiElement actualReferee = referent.resolve();
      if ((actualReferee instanceof PsiMethod) && PropertyUtil.isSimplePropertySetter((PsiMethod)actualReferee)) {
        accessedForWriting = true;
        accessedForReading = false;
      }
    }
    return new Access(accessedForReading, accessedForWriting);
  }

}
