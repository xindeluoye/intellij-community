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
package com.intellij.lang.findUsages;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The default empty implementation of the {@link FindUsagesProvider} interface.
 * @author max
 */
public class EmptyFindUsagesProvider implements FindUsagesProvider {
  public boolean mayHaveReferences(IElementType token, final short searchContext) {
    return false;
  }

  @Nullable
  public WordsScanner getWordsScanner() {
    return null;
  }

  public boolean canFindUsagesFor(PsiElement psiElement) {
    return false;
  }

  @Nullable
  public String getHelpId(PsiElement psiElement) {
    return null;
  }

  @NotNull
  public String getType(PsiElement element) {
    return "";
  }

  @NotNull
  public String getDescriptiveName(PsiElement element) {
    return element instanceof PsiNamedElement ? ((PsiNamedElement)element).getName() : "";
  }

  @NotNull
  public String getNodeText(PsiElement element, boolean useFullName) {
    return element instanceof PsiNamedElement ? ((PsiNamedElement)element).getName() : "";
  }
}
