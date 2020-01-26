package com.miguel.idea;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;

public class ToJsonAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e)
    {
        PsiFile currentFile = e.getData(LangDataKeys.PSI_FILE);
        if (currentFile == null || !JavaLanguage.INSTANCE.is(currentFile.getLanguage())) {
            return;
        }
        WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
            Project project = e.getData(PlatformDataKeys.PROJECT);
            PsiField[] classFields = getClassFields(currentFile, e);
            PsiElementFactory factory = psiElementFactory(project);
            PsiClass psiClass = psiClass(currentFile, e);

            //Create method
            PsiMethod toJson = factory.createMethodFromText(
                new CodeGenerator().toJson(classFields), psiClass);

            if (psiClass.findMethodsByName(toJson.getName(), false).length == 0) {
                //Add method to class
                psiClass.add(toJson);

                //Add class imports
                Importer.importClass(project, (PsiJavaFile) currentFile);
            }
        });
    }

    private PsiClass psiClass(PsiFile currentFile, AnActionEvent e) {
        PsiElement element = currentFile
            .findElementAt(e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset());
        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }

    private PsiElementFactory psiElementFactory(Project project) {
        return JavaPsiFacade.getElementFactory(project);
    }

    private PsiField[] getClassFields(final PsiFile currentFile, AnActionEvent e) {
        PsiElement element = currentFile
            .findElementAt(e.getData(PlatformDataKeys.EDITOR).getCaretModel().getOffset());
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return psiClass.getFields();
    }
}
