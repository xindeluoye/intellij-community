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
package com.intellij.openapi.fileEditor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;

public abstract class FileDocumentManagerAdapter implements FileDocumentManagerListener{
  public void beforeDocumentSaving(Document document) throws VetoDocumentSavingException {
  }

  public void fileWithNoDocumentChanged(VirtualFile file) {
  }

  public void beforeFileContentReload(VirtualFile file, Document document) throws VetoDocumentReloadException {
  }

  public void fileContentReloaded(VirtualFile file, Document document) {
  }

  public void fileContentLoaded(VirtualFile file, Document document) {
  }
}
