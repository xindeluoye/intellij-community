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
package com.intellij.psi.search;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiElementFilter;

import java.util.ArrayList;
import java.util.Collection;

public interface PsiElementProcessor<T extends PsiElement> {
  boolean execute(T element);

  class CollectElements<T extends PsiElement> implements PsiElementProcessor<T> {
    private final Collection<T> myCollection;

    public CollectElements(Collection<T> collection) {
      myCollection = collection;
    }

    public CollectElements() {
      myCollection = new ArrayList<T>();
    }

    public PsiElement[] toArray() {
      return myCollection.toArray(new PsiElement[myCollection.size()]);
    }

    public Collection<T> getCollection() {
      return myCollection;
    }

    public T[] toArray(T[] array) {
      return myCollection.toArray(array);
    }

    public boolean execute(T element) {
      myCollection.add(element);
      return true;
    }
  }

  class CollectFilteredElements<T extends PsiElement> extends CollectElements<T> {
    private final PsiElementFilter myFilter;

    public CollectFilteredElements(PsiElementFilter filter, Collection<T> collection) {
      super(collection);
      myFilter = filter;
    }

    public CollectFilteredElements(PsiElementFilter filter) {
      myFilter = filter;
    }

    public boolean execute(T element) {
      if (myFilter.isAccepted(element)) return super.execute(element);
      return true;
    }
  }

  class CollectElementsWithLimit<T extends PsiElement> extends CollectElements<T>{
    private int myCount = 0;
    private boolean myOverflow = false;

    private final int myLimit;

    public CollectElementsWithLimit(int limit) {
      myLimit = limit;
    }

    public boolean execute(T element) {
      if (myCount == myLimit){
        myOverflow = true;
        return false;
      }
      myCount++;
      return super.execute(element);
    }

    public boolean isOverflow() {
      return myOverflow;
    }
  }

  class FindElement<T extends PsiElement> implements PsiElementProcessor<T> {
    private T myFoundElement = null;

    public boolean isFound() {
      return myFoundElement != null;
    }

    public T getFoundElement() {
      return myFoundElement;
    }

    public boolean execute(T element) {
      myFoundElement = element;
      return false;
    }
  }

  class FindFilteredElement<T extends PsiElement> extends FindElement<T> {

    private final PsiElementFilter myFilter;

    public FindFilteredElement(PsiElementFilter filter) {
      myFilter = filter;
    }

    public boolean execute(T element) {
      if (myFilter.isAccepted(element)) return super.execute(element);
      return true;
    }
  }
}