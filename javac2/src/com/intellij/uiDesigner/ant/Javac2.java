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
package com.intellij.uiDesigner.ant;

import com.intellij.uiDesigner.compiler.AlienFormFileException;
import com.intellij.uiDesigner.compiler.CodeGenerator;
import com.intellij.uiDesigner.compiler.Utils;
import com.intellij.uiDesigner.lw.CompiledClassPropertiesProvider;
import com.intellij.uiDesigner.lw.LwRootContainer;
import org.apache.bcel.Repository;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public final class Javac2 extends Javac{
  private ArrayList myFormFiles;

  public Javac2(){
  }

  protected void compile(){
    // compile java
    super.compile();

    // we instrument every file, because we cannot find which files should not be instrumented without dependency storage
    final ArrayList formsToInstrument = myFormFiles;

    if (formsToInstrument.size() == 0){
      log("No forms to instrument found", Project.MSG_VERBOSE);
      return;
    }

    final StringBuffer classPathBuffer = new StringBuffer();

    classPathBuffer.append(getDestdir().getAbsolutePath());
    
    final Path classpath = getClasspath();
    final String[] pathElements = classpath.list();
    for (int i = 0; i < pathElements.length; i++) {
      final String pathElement = pathElements[i];
      classPathBuffer.append(File.pathSeparator); 
      classPathBuffer.append(pathElement);
    }

    classPathBuffer.append(File.pathSeparator);
    classPathBuffer.append(ClassPath.getClassPath());

    final String classPath = classPathBuffer.toString();
    log("classpath=" + classPath, Project.MSG_VERBOSE);

    try {
      initBcel(new ClassPath(classPath));
      final ClassLoader loader;
      try {
        loader = createClassLoader(classPath);
      }
      catch (MalformedURLException e) {
        fireError(e.getMessage());
        return;
      }

      final HashMap class2form = new HashMap();

      formLoop: for (int i = 0; i < formsToInstrument.size(); i++) {
        final File formFile = (File)formsToInstrument.get(i);

        log("compiling form " + formFile.getAbsolutePath(), Project.MSG_VERBOSE);

        final byte[] bytes = new byte[(int)formFile.length()];
        try {
          final FileInputStream fileReader = new FileInputStream(formFile);
          fileReader.read(bytes);
          fileReader.close();
        }
        catch (IOException e) {
          fireError(e.getMessage());
          continue;
        }

        final String formFileContent;
        try {
          formFileContent = new String(bytes, "utf8");
        }
        catch (UnsupportedEncodingException e) {
          fireError(e.getMessage());
          continue;
        }

        final LwRootContainer rootContainer;
        try {
          rootContainer = Utils.getRootContainer(formFileContent, new CompiledClassPropertiesProvider(loader));
        }
        catch (AlienFormFileException e) {
          // ignore non-IDEA forms
          continue;
        }
        catch (Exception e) {
          fireError("Cannot process form file " + formFile.getAbsolutePath() + ". Reason: " + e);
          continue;
        }

        final String classToBind = rootContainer.getClassToBind();
        if (classToBind == null) {
          continue;
        }

        String name = classToBind.replace('.','/');
        File classFile = getClassFile(name);
        if (classFile == null) {
          log(formFile.getAbsolutePath() + ": Class to bind does not exist: " + classToBind, Project.MSG_WARN);
          continue formLoop;
        }

        final File alreadyProcessedForm = (File)class2form.get(classToBind);
        if (alreadyProcessedForm != null) {
          fireError(
            formFile.getAbsolutePath() + ": " +
            "The form is bound to the class " + classToBind + ".\n" +
            "Another form " + alreadyProcessedForm.getAbsolutePath() + " is also bound to this class."
          );
          continue;
        }
        class2form.put(classToBind, formFile);

        final CodeGenerator codeGenerator = new CodeGenerator(rootContainer, classFile, loader);
        codeGenerator.patch();
        final String[] warnings = codeGenerator.getWarnings();

        for (int j = 0; j < warnings.length; j++) {
          log(formFile.getAbsolutePath() + ": " + warnings[j], Project.MSG_WARN);
        }
        final String[] errors = codeGenerator.getErrors();
        if (errors.length > 0) {
          StringBuffer message = new StringBuffer();
          for (int j = 0; j < errors.length; j++) {
            if (message.length() > 0) {
              message.append("\n");
            }
            message.append(formFile.getAbsolutePath()).append(": ").append(errors[j]);
          }
          fireError(message.toString());
        }
      }
    }
    finally {
      disposeBcel();
    }
  }

  private void fireError(final String message) {
    if (failOnError) {
      throw new BuildException(message, getLocation());
    }
    else {
      log(message, Project.MSG_ERR);
    }
  }

  private File getClassFile(String className) {
    File classFile = new File(getDestdir().getAbsolutePath(), className + ".class");
    if (classFile.exists()) return classFile;
    int position = className.lastIndexOf('/');
    if (position == -1) return null;
    return getClassFile(className.substring(0, position) + '$' + className.substring(position + 1));
  }

  private static URLClassLoader createClassLoader(final String classPath) throws MalformedURLException{
    final ArrayList urls = new ArrayList();
    for (StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator); tokenizer.hasMoreTokens();) {
      final String s = tokenizer.nextToken();
      urls.add(new File(s).toURL());
    }
    final URL[] urlsArr = (URL[])urls.toArray(new URL[urls.size()]);
    return new URLClassLoader(urlsArr, null);
  }
  
  protected void resetFileLists(){
    super.resetFileLists();
    myFormFiles = new ArrayList();
  }

  protected void scanDir(final File srcDir, final File destDir, final String[] files) {
    super.scanDir(srcDir, destDir, files);
    for (int i = 0; i < files.length; i++) {
      final String file = files[i];
      if (file.endsWith(".form")) {
        log("Found form file " + file, Project.MSG_VERBOSE);
        myFormFiles.add(new File(srcDir, file));
      }
    }
  }

  private static void initBcel(ClassPath cp) {
    final Class aClass = Repository.class;
    try {
      final java.lang.reflect.Field field = aClass.getDeclaredField("_repository");
      field.setAccessible(true);
      final org.apache.bcel.util.Repository currentRepository = (org.apache.bcel.util.Repository)field.get(null);
      if (currentRepository instanceof org.apache.bcel.util.SyntheticRepository) {
        ((SyntheticRepository)currentRepository).clear();
      }
      field.set(null, SyntheticRepository.getInstance(cp));
    }
    catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static void disposeBcel() {
    Repository.clearCache();
    // clear all ever-created synthetic repositories, there should be no references to SyntheticRepository objects at all
    // otherwise jar-files in the repository's classpath will be locked
    try {
      final java.lang.reflect.Field field = SyntheticRepository.class.getDeclaredField("_instances");
      field.setAccessible(true);
      Map cpToRepositoryMap = (Map)field.get(null);
      for (Iterator it = cpToRepositoryMap.values().iterator(); it.hasNext();) {
        SyntheticRepository repository = (SyntheticRepository)it.next();
        if (repository != null) {
          repository.clear();
        }
      }
      cpToRepositoryMap.clear();
    }
    catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }

    try {
      final java.lang.reflect.Field field = Repository.class.getDeclaredField("_repository");
      field.setAccessible(true);
      field.set(null, SyntheticRepository.getInstance()); // initialize with the default value
    }
    catch (Exception e) {
      throw new RuntimeException("Error while disposing BCEL: " +  e.getClass() + " : " + e.getMessage());
    }
  }
}
