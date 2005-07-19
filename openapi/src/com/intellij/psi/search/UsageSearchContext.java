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

public interface UsageSearchContext {
  /**
   * Element's usages in its language code are requested
   */
  public static final short IN_CODE        = 0x1;

  /**
   * Usages in comments are requested
   */
  public static final short IN_COMMENTS    = 0x2;

  /**
   * Usages in string literals are requested
   */
  public static final short IN_STRINGS     = 0x4;

  /**
   * Element's usages in other languages are requested,
   * e.g. usages of java class in jsp attribute value
   */
  public static final short IN_FOREIGN_LANGUAGES = 0x8;

  /**
   * Plain text occurences are requested
   */
  public static final short IN_PLAIN_TEXT  = 0x10;

  /**
   * Any of above
   */
  public static final short ANY            = 0xFF;
}
