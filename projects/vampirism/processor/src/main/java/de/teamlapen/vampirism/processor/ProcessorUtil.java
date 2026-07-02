package de.teamlapen.vampirism.processor;

import org.jspecify.annotations.Nullable;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.function.Supplier;

public final class ProcessorUtil {

    private ProcessorUtil() {}

    public static void imports(StringBuilder sb, String... fqns) {
        for (String fqn : fqns) {
            sb.append("import ").append(fqn).append(";\n");
        }
    }

    /**
     * Extracts the {@link TypeMirror} from a class-valued annotation element.
     * At compile time, accessing {@code annotation.someClass()} throws {@link MirroredTypeException}
     * instead of returning an actual {@link Class} — this method catches that and returns the mirror.
     */
    @Nullable
    static TypeMirror mirror(Supplier<Class<?>> accessor) {
        try {
            accessor.get();
            return null;
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
    }

    static String packageOf(ProcessingEnvironment env, Element element) {
        PackageElement pe = env.getElementUtils().getPackageOf(element);
        return pe.isUnnamed() ? "" : pe.getQualifiedName().toString();
    }

    static String upper(String s) {
        return s.replaceAll("(?<=[a-z0-9])(?=[A-Z])", "_")
                .toUpperCase(Locale.ROOT);
    }

    static String lower(String s) {
        return s.replaceAll("(?<=[a-z0-9])(?=[A-Z])", "_")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Converts a mod ID to a CamelCase class-name segment.
     * {@code "vampirism"} → {@code "Vampirism"}, {@code "my_mod"} → {@code "MyMod"}.
     */
    public static String modIdToClassPart(String modId) {
        StringBuilder sb = new StringBuilder();
        for (String segment : modId.split("[_\\-]")) {
            if (!segment.isEmpty()) {
                sb.append(Character.toUpperCase(segment.charAt(0)));
                if (segment.length() > 1) sb.append(segment.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return sb.toString();
    }

    /** Renders a float literal without a trailing {@code .0} so {@code 0.9F} stays {@code 0.9F}. */
    public static String str(float f) {
        if (f == Math.rint(f)) {
            return Integer.toString((int) f);
        }
        return Float.toString(f);
    }

    static void write(ProcessingEnvironment env, Messager messager, Element element, String pkg, String name, String source) {
        String qualified = pkg.isEmpty() ? name : pkg + "." + name;
        try {
            JavaFileObject file = env.getFiler().createSourceFile(qualified, element);
            try (PrintWriter writer = new PrintWriter(file.openWriter())) {
                writer.print(source);
            }
        } catch (IOException e) {
            error(messager, element, "Failed to generate " + qualified + ": " + e.getMessage());
        }
    }

    static void error(Messager messager, Element element, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
