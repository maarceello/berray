package com.berraydoc;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

/** Extends the Java Parser visitor with some detailed visiting methods. */
public class BerrayVisitorAdapter<R, A> extends GenericVisitorAdapter<R, A> {

  public R visit(ClassOrInterfaceDeclaration n, A arg) {
    R result = super.visit(n, arg);
    if (result != null) {
      return result;
    }

    for (ClassOrInterfaceType extendedType : n.getExtendedTypes()) {
      result = visitExtendsType(extendedType, arg);
      if (result != null) {
        return result;
      }
    }

    return null;
  }

  public R visitExtendsType(ClassOrInterfaceType n, A arg) {
    return null;
  }

}
