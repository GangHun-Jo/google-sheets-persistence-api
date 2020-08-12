package com.ntscorp.gpa.processor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import com.ntscorp.gpa.annotation.GPAQuery;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

@SupportedAnnotationTypes({"com.ntscorp.gpa.annotation.GPAQuery"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AutoGenerateProcessor extends AbstractProcessor {

	private final String prefix = "G";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (annotations.size() == 0) {
			return false;
		}

		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GPAQuery.class);

		for (Element element : elements) {
			if (element.getKind() != ElementKind.CLASS) {
				error("The annotation @GPAQuery can only be applied on classes: ", element);
			} else {
				try {
					generateClass(element);
				} catch (Exception e) {
					error(e.getMessage(), null);
				}
			}
		}
		return false;
	}

	private void generateClass(Element element) throws Exception {
		ParameterizedTypeName predicateType = ParameterizedTypeName.get(
			ClassName.get(Predicate.class), ClassName.get(getPackage(element), element.getSimpleName().toString()));
		FieldSpec predicateField = FieldSpec.builder(predicateType, "condition", Modifier.PRIVATE).build();

		List<MethodSpec> methodList = new ArrayList<>();
		for (VariableElement fieldElement : ElementFilter.fieldsIn(element.getEnclosedElements())) {
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(fieldElement.getSimpleName().toString())
				.addModifiers(Modifier.PUBLIC)
				.returns(ClassName.get(getPackage(element), prefix + element.getSimpleName().toString()));

			switch (fieldElement.asType().toString()) {
				case "int":
					methodBuilder.addParameter(int.class, fieldElement.getSimpleName().toString());
					methodBuilder.addStatement("this.condition = condition.and(object -> object." + getGetterName(fieldElement) + "() == " + fieldElement.getSimpleName() + ")");
					break;
				case "java.lang.String":
					methodBuilder.addParameter(String.class, fieldElement.getSimpleName().toString());
					methodBuilder.addStatement("this.condition = condition.and(object -> object." + getGetterName(fieldElement) + "().equals(" + fieldElement.getSimpleName() + "))");
					break;
				case "java.time.LocalDateTime":
					methodBuilder.addParameter(LocalDateTime.class, fieldElement.getSimpleName().toString());
					methodBuilder.addStatement("this.condition = condition.and(object -> object." + getGetterName(fieldElement) + "().equals(" + fieldElement.getSimpleName() + "))");
					break;
				default:
					continue;
			}

			methodBuilder.addStatement("return this");
			methodList.add(methodBuilder.build());
		}

		MethodSpec buildMethod = MethodSpec.methodBuilder("build")
			.addModifiers(Modifier.PUBLIC)
			.returns(predicateType)
			.addStatement("return this.condition")
			.build();

		TypeSpec gClass = TypeSpec.classBuilder(prefix + element.getSimpleName().toString())
			.addModifiers(Modifier.PUBLIC)
			.addMethods(methodList)
			.addMethod(buildMethod)
			.addField(predicateField)
			.build();

		JavaFile javaFile = JavaFile.builder(getPackage(element), gClass)
			.build();

		javaFile.writeTo(processingEnv.getFiler());
	}

	private String getPackage(Element element) {
		String fullName = element.asType().toString();
		String[] list = fullName.split("\\.");
		int idx = fullName.lastIndexOf(list[list.length - 1]);

		return fullName.substring(0, idx - 1);
	}

	private String getGetterName(VariableElement element) {
		return "get" + element.getSimpleName().toString().substring(0, 1).toUpperCase()
			+ element.getSimpleName().toString().substring(1);
	}

	private void error(String msg, Element e) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
	}
}