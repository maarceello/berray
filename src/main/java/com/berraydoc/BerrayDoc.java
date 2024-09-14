package com.berraydoc;

import com.berray.components.core.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.stream.Collectors;

public class BerrayDoc {

  public static void main(String[] args) throws IOException {
    SourceRoot sourceRoot = new SourceRoot(FileSystems.getDefault().getPath("./src/main/java"));
    List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse();

    List<ClassDocumentation> classes = new ArrayList<>();

    System.out.println("----------------");
    for (ParseResult<CompilationUnit> parseResult : parseResults) {
      FindJavaDocVisitor visitor = new FindJavaDocVisitor();
      visitor.visit(parseResult.getResult().get(), "");
      visitor.finishedClasses
          .stream().filter(c -> c.extendedClasses.contains(Component.class.getName()))
          .forEach(c -> {
            System.out.println(visitor.packageName + "." + c.getName());
            System.out.println("  extends " + c.extendedClasses);
            System.out.println("  " + c.classJavaDoc);
            System.out.println(c.methods);
            classes.add(c);
          });
    }
    System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(classes));
  }

  private static class FindJavaDocVisitor extends BerrayVisitorAdapter<Boolean, String> {
    private Map<String, String> imports = new HashMap<>();
    private Deque<ClassDocumentation> classStack = new ArrayDeque<>();
    private String packageName;
    private List<ClassDocumentation> finishedClasses = new ArrayList<>();

    @Override
    public Boolean visit(PackageDeclaration packageDeclaration, String arg) {
      this.packageName = packageDeclaration.getName().asString();
      return super.visit(packageDeclaration, arg);
    }

    @Override
    public Boolean visit(ImportDeclaration importDeclaration, String arg) {
      Name name = importDeclaration.getName();
      imports.put(name.getIdentifier(), name.getQualifier().get().asString());
      return super.visit(importDeclaration, arg);
    }

    @Override
    public Boolean visit(ClassOrInterfaceDeclaration n, String arg) {
      // create new class documentation holder and place them on the stack
      String className = getClassName(n.getName().getIdentifier());
      ClassDocumentation documentation = new ClassDocumentation(className);
      classStack.push(documentation);
      n.getComment().ifPresent(
          comment -> documentation.setClassJavaDoc(comment.getContent())
      );
      try {
        return super.visit(n, arg);
      } finally {
        finishedClasses.add(classStack.pop());
      }
    }

    private String getClassName(String thisClassName) {
      if (classStack.isEmpty()) {
        return thisClassName;
      }

      // The stack contains outer classes. append the outer classnames to this class name
      return classStack.stream()
          .map(ClassDocumentation::getName)
          .collect(Collectors.joining("."))
          + "." + thisClassName;
    }

    @Override
    public Boolean visitExtendsType(ClassOrInterfaceType n, String arg) {
      classStack.peek().addExtends(getFullQualifiedName(n.getName()));
      return super.visitExtendsType(n, arg);
    }

    @Override
    public Boolean visit(MethodDeclaration n, String arg) {
      if (n.getComment().isPresent()) {
        Comment comment = n.getComment().get();
        classStack.peek().addMethod(new MethodDocumentation(n.getName().asString(), comment.asString()));
      }
      return super.visit(n, arg);
    }

    private String getFullQualifiedName(SimpleName name) {
      String lastPart = name.getIdentifier();
      if (imports.containsKey(lastPart)) {
        return imports.get(lastPart) + "." + lastPart;
      }
      return packageName + "." + lastPart;
    }

  }


  private static class ClassDocumentation {
    private String name;
    private String classJavaDoc;
    private List<String> extendedClasses = new ArrayList<>();
    private List<MethodDocumentation> methods = new ArrayList<>();

    public ClassDocumentation(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public String getClassJavaDoc() {
      return classJavaDoc;
    }

    public List<MethodDocumentation> getMethods() {
      return methods;
    }

    public void addExtends(String name) {
      extendedClasses.add(name);
    }

    public void addMethod(MethodDocumentation method) {
      this.methods.add(method);
    }


    public void setClassJavaDoc(String classJavaDoc) {
      this.classJavaDoc = classJavaDoc;
    }
  }

  private static class MethodDocumentation {
    private String name;
    private String documentation;

    public MethodDocumentation(String name, String documentation) {
      this.name = name;
      this.documentation = documentation;
    }

    public String getName() {
      return name;
    }

    public String getDocumentation() {
      return documentation;
    }

    @Override
    public String toString() {
      return "MethodDocumentation{" +
          "name='" + name + '\'' +
          ", documentation='" + documentation + '\'' +
          '}';
    }
  }
}
