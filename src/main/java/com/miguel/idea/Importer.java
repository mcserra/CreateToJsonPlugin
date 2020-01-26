package com.miguel.idea;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Arrays;
import java.util.Optional;

public class Importer {

    private static final String JSON = "javax.json.Json";
    private static final String JSON_OBJECT = "javax.json.JsonObject";

    public static void importClass(Project project, PsiJavaFile javaFile) {
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        addImports(project, factory, javaFile, JSON, JSON_OBJECT);
    }

    private static void addImports(Project project, PsiElementFactory factory, PsiJavaFile javaFile, String... clazzNames) {
        Arrays.asList(clazzNames).forEach(className -> {
            Optional<PsiClass> psiClass = Optional.ofNullable(JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project)));
            psiClass.ifPresent(x -> addImport(x, factory, javaFile, className));
        });
    }

    private static void addImport(PsiClass psiClass, PsiElementFactory factory, PsiJavaFile javaFile, String clazzName) {
        if (!hasImportClazz(javaFile, clazzName)) {
            PsiImportStatement importStatement = factory.createImportStatement(psiClass);
            javaFile.getImportList().add(importStatement);
        }
    }

    public static boolean hasImportClazz(PsiJavaFile file, String clazzName) {
        PsiImportList importList = file.getImportList();
        if (null == importList) {
            return false;
        }
        PsiImportStatement[] statements = importList.getImportStatements();
        for (PsiImportStatement tmp : statements) {
            if (null != tmp && clazzName.equals(tmp.getQualifiedName())) {
                return true;
            }
        }
        return false;
    }
}
