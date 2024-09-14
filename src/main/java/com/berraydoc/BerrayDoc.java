package com.berraydoc;

import com.berray.components.core.Component;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;

public class BerrayDoc {

  public static void main(String[] args) throws IOException {
    SourceRoot sourceRoot = new SourceRoot(FileSystems.getDefault().getPath("./src/main/java"));
    List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse();

    for (ParseResult<CompilationUnit> parseResult : parseResults) {
      CompilationUnit result = parseResult.getResult().get();
      NodeList<TypeDeclaration<?>> types = result.getTypes();
      for (TypeDeclaration<?> type : types) {
        if (type instanceof ClassOrInterfaceDeclaration) {
          ClassOrInterfaceDeclaration classOrInterface = (ClassOrInterfaceDeclaration) type;
          if (isExtending(classOrInterface, Component.class)) {
            System.out.println("type: " + type.getNameAsString());
            System.out.println(classOrInterface.getComment());
            List<com.github.javaparser.ast.comments.Comment> comments = type.getAllContainedComments();
            System.out.println(comments);
          }
        }
      }
    }

    System.out.println("----------------");
    for (ParseResult<CompilationUnit> parseResult : parseResults) {
      new FindJavaDocVisitor().visit(parseResult.getResult().get(), "");
    }

  }

  private static boolean isExtending(ClassOrInterfaceDeclaration classOrInterface, Class<Component> componentClass) {
      for (ClassOrInterfaceType extendedType : classOrInterface.getExtendedTypes()) {
        if (extendedType.getNameAsString().equals(Component.class.getSimpleName())) {
          return true;
        }

    }
    return false;
  }



  private static class FindJavaDocVisitor extends GenericVisitorAdapter<Boolean, String> {
    @Override
    public Boolean visit(ClassOrInterfaceDeclaration n, String arg) {
      return super.visit(n, arg);
    }
  }
}
