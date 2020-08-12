package com.ntscorp.gpa.processor;


import com.ntscorp.gpa.annotation.Mandatory;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Converts getters to field
 */
public class FieldInfo {

    private final LinkedHashMap<String, String> fields;
    private final List<String> mandatoryFields;

    public FieldInfo(LinkedHashMap<String, String> fields, List<String> mandatoryFields) {

        this.fields = fields;
        this.mandatoryFields = mandatoryFields;
    }

    public LinkedHashMap<String, String> getFields() {
        return fields;
    }

    public List<String> getMandatoryFields() {
        return mandatoryFields;
    }

    public static FieldInfo get(Element element) {
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        List<String> mandatoryFields = new ArrayList<>();

        for (ExecutableElement executableElement :
                ElementFilter.methodsIn(element.getEnclosedElements())) {

            if (executableElement.getKind() != ElementKind.METHOD) {
                continue;
            }

            String methodName = executableElement.getSimpleName().toString();

            String fieldName = methodToFieldName(methodName);
            if (fieldName == null) {
                continue;
            }
            String returnType = executableElement.getReturnType().toString();
            if ("void".equals(returnType)) {
                continue;
            }
            fields.put(fieldName, returnType);

            if (executableElement.getAnnotation(Mandatory.class) != null) {
                mandatoryFields.add(fieldName);
            }
        }

        return new FieldInfo(fields, mandatoryFields);
    }

    private static String methodToFieldName(String methodName) {
        if (methodName.startsWith("get")) {
            String str = methodName.substring(3);
            if (str.length() == 0) {
                return null;
            } else if (str.length() == 1) {
                return str.toLowerCase();
            } else {
                return Character.toLowerCase(str.charAt(0)) + str.substring(1);
            }
        }
        return null;
    }
}