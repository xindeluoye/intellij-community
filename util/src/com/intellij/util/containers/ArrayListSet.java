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
package com.intellij.util.containers;

import java.util.AbstractSet;
import java.util.ArrayList;

public class ArrayListSet<E> extends AbstractSet<E> {
  private final ArrayList<E> myList = new ArrayList<E>();

  public java.util.Iterator<E> iterator() {
    return myList.iterator();
  }

  public int size() {
    return myList.size();
  }

  public boolean contains(Object object) {
    return myList.contains(object);
  }

  public boolean add(E e) {
    if (!myList.contains(e)){
      myList.add(e);
      return true;
    }
    else{
      return false;
    }
  }

  public boolean remove(Object object) {
    return myList.remove(object);
  }

  public void clear() {
    myList.clear();
  }
}
