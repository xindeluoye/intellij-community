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
package com.intellij.openapi.vcs.history;

public interface VcsRevisionNumber extends Comparable<VcsRevisionNumber>{

  VcsRevisionNumber NULL = new VcsRevisionNumber() {
    public String asString() {
      return "";
    }

    public int compareTo(VcsRevisionNumber vcsRevisionNumber) {
      return 0;
    }
  };

  class Int implements VcsRevisionNumber{
    private final int myValue;

    public Int(int value) {
      myValue = value;
    }

    public String asString() {
      return String.valueOf(myValue);
    }

    public int compareTo(VcsRevisionNumber vcsRevisionNumber) {
      if (vcsRevisionNumber instanceof VcsRevisionNumber.Int){
        return myValue - ((Int)vcsRevisionNumber).myValue;
      }
      return 0;
    }
  }

  class Long implements VcsRevisionNumber{
    private final long myValue;

    public Long(long value) {
      myValue = value;
    }

    public String asString() {
      return String.valueOf(myValue);
    }

    public int compareTo(VcsRevisionNumber vcsRevisionNumber) {
      if (vcsRevisionNumber instanceof VcsRevisionNumber.Long){
        return (int)(myValue - ((Long)vcsRevisionNumber).myValue);
      }
      return 0;
    }

    public long getLongValue() {
      return myValue;
    }
  }

  String asString();
}
