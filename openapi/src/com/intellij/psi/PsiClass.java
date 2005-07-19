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
package com.intellij.psi;

import com.intellij.openapi.util.Pair;
import com.intellij.pom.java.PomMemberOwner;
import com.intellij.psi.meta.PsiMetaOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PsiClass extends PsiElement, PsiNamedElement, PsiModifierListOwner, PsiDocCommentOwner, PsiMetaOwner, PsiTypeParameterListOwner, PsiMember {
  PsiClass[] EMPTY_ARRAY  = new PsiClass[0];

  @Nullable(documentation = "return null for anonymous and local classes, and for type parameters")
  String getQualifiedName();

  boolean isInterface();
  boolean isAnnotationType();
  boolean isEnum();

  @Nullable(documentation = "return null for anonymous classes, enums and annotation types")
  PsiReferenceList getExtendsList();

  @Nullable(documentation = "return null for anonymous classes")
  PsiReferenceList getImplementsList();

  @NotNull
  PsiClassType[] getExtendsListTypes();

  @NotNull
  PsiClassType[] getImplementsListTypes();

  @Nullable(documentation = "May return null when jdk is not configured, so no java.lang.Object is found, or for java.lang.Object itself")
  PsiClass getSuperClass();

  PsiClass[] getInterfaces();

  /**
   * @AtLeast(elements = 0, description = "May return zero elements when jdk is not configured, so no java.lang.Object is found")
   */
  PsiClass[] getSupers();

  /**
   * @AtLeast(elements = 1, description = "At least type of java.lang.Object is returned")
   */
  PsiClassType[] getSuperTypes();

  PsiField[] getFields();
  PsiMethod[] getMethods();
  PsiMethod[] getConstructors();
  PsiClass[] getInnerClasses();
  PsiClassInitializer[] getInitializers();

  PsiField[] getAllFields();
  PsiMethod[] getAllMethods();
  PsiClass[] getAllInnerClasses();

  @Nullable PsiField    findFieldByName(String name, boolean checkBases);
  @Nullable PsiMethod   findMethodBySignature(PsiMethod patternMethod, boolean checkBases);
  @NotNull PsiMethod[] findMethodsBySignature(PsiMethod patternMethod, boolean checkBases);
  @NotNull PsiMethod[] findMethodsByName(String name, boolean checkBases);
  @NotNull List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(String name, boolean checkBases);
  @NotNull List<Pair<PsiMethod,PsiSubstitutor>> getAllMethodsAndTheirSubstitutors();

  @Nullable PsiClass findInnerClassByName(String name, boolean checkBases);

  @Nullable PsiJavaToken getLBrace();
  @Nullable PsiJavaToken getRBrace();

  @Nullable(documentation = "parser understands classes without name identifiers")
  PsiIdentifier getNameIdentifier();

  // very special method!
  PsiElement getScope();

  boolean isInheritor(PsiClass baseClass, boolean checkDeep);

  @Nullable
  PomMemberOwner getPom();

  @Nullable
  PsiClass getContainingClass();
}
