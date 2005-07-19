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
package com.intellij.openapi.vfs;

/**
 *
 */
public class VirtualFileMoveEvent extends VirtualFileEvent {
  private final VirtualFile myOldParent;
  private final VirtualFile myNewParent;

  public VirtualFileMoveEvent(Object requestor, VirtualFile file, VirtualFile oldParent, VirtualFile newParent){
    super(requestor, file, file.getName(), file.isDirectory(), file.getParent());
    myOldParent = oldParent;
    myNewParent = newParent;
  }

  public VirtualFile getOldParent(){
    return myOldParent;
  }

  public VirtualFile getNewParent(){
    return myNewParent;
  }
}
