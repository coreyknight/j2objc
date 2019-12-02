/*
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

package com.google.devtools.j2objc.javac;

import com.google.devtools.j2objc.util.ParserEnvironment;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

public class JavacEnvironment implements ParserEnvironment {
  private final JavacTask task;
  private final StandardJavaFileManager fileManager;
  private final DiagnosticCollector<JavaFileObject> diagnostics;
  private final Elements elements;
  private final Types types;
  private final Trees trees;
  // ARGC ++ {{
  public static TypeElement notImportedException;
  public static TypeElement javaLangObject;
  public final ExecutableElement throwNotImportedStatic;
  public final ExecutableElement throwNotImportedInstance;
  public final ExecutableElement createNotImportedMethod;
  // }}

  JavacEnvironment(JavacTask task, StandardJavaFileManager fileManager,
      DiagnosticCollector<JavaFileObject> diagnostics) {
    this.task = task;
    this.fileManager = fileManager;
    this.diagnostics = diagnostics;
    elements = task.getElements();
    types = task.getTypes();
    trees = Trees.instance(task);
    javaLangObject = elements.getTypeElement("java.lang.Object");
    notImportedException = elements.getTypeElement("org.ninefolders.NotImportedClassException");
    List<? extends Element> list = elements.getAllMembers(notImportedException);
    ExecutableElement throw_ = null, throw_instance = null;
    ExecutableElement init_ = null;
    for (Element e : list) {
    	if (e.getKind() == ElementKind.METHOD) {
    		String s = e.getSimpleName().toString();
    		if (s.equals("throwNotImported")) {
        		ExecutableElement m = (ExecutableElement)e;
	    		throw_ = (ExecutableElement)e;
	    		break;
        	}
    	}
    	else if (e.getKind() == ElementKind.CONSTRUCTOR) {
    		ExecutableElement m = (ExecutableElement)e;
    		if (m.isVarArgs()) {
    			init_ = m;
    		}
    	}
    }
    throwNotImportedStatic = throw_;
    throwNotImportedInstance = throw_instance;    
    createNotImportedMethod = init_;
  }

  public PackageElement defaultPackage() {
    return elements.getPackageElement("");
  }

  @Override
  public Elements elementUtilities() {
    return elements;
  }

  @Override
  public Types typeUtilities() {
    return types;
  }

  public Trees treeUtilities() {
    return trees;
  }

  public JavacTask task() {
    return task;
  }

  public StandardJavaFileManager fileManager() {
    return fileManager;
  }

  public DiagnosticCollector<JavaFileObject> diagnostics() {
    return diagnostics;
  }
}
