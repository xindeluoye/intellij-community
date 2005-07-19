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
package com.intellij.openapi.editor.markup;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.UserDataHolder;

public interface MarkupModel extends UserDataHolder {
  Document getDocument();

  RangeHighlighter addRangeHighlighter(int startOffset, int endOffset, int layer, TextAttributes textAttributes, HighlighterTargetArea targetArea);
  RangeHighlighter addLineHighlighter(int line, int layer, TextAttributes textAttributes);

  void removeHighlighter(RangeHighlighter rangeHighlighter);
  void removeAllHighlighters();

  RangeHighlighter[] getAllHighlighters();
}
