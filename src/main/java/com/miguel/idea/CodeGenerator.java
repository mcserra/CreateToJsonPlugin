package com.miguel.idea;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;

import java.util.Arrays;
import java.util.List;

public class CodeGenerator {

    private static List<String> ignorableFields = Arrays.asList("tableName", "logger", "messagePrefix", "serialVersionUID");

    public String toJson(PsiField[] fields) {
        StringBuilder sb = new StringBuilder();

        sb.append("public JsonObject toJson(){ \n");
        sb.append("return Json.createObjectBuilder()\n");

        Arrays.asList(fields).forEach(psiField -> addField(psiField, sb));

        sb.append(".build();");
        sb.append("}");
        return sb.toString();
    }

    private void addField(PsiField psiField, StringBuilder sb) {
        if (hasWrongModifiers(psiField) || ignorableFields.contains(psiField.getName())) {
            return;
        }
        String parameterName = psiField.getName();
        sb.append(String.format(".add(\"%s\", %s)%n", parameterName, parameterName));
    }

    private boolean hasWrongModifiers(PsiField psiField) {
        PsiModifierList psiModifierList = psiField.getModifierList();

        if (psiModifierList == null) {
            return true;
        }

        return psiModifierList.hasModifierProperty("static");
    }
}
