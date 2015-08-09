package de.teamlapen.vampirism.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Avoids no such field exceptions caused by modded enums
 *
 * @param <T>
 * @author OniBait
 */
public final class ModdedEnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
    private final Map<String, T> nameToConstant = new HashMap<String, T>();
    private final Map<T, String> constantToName = new HashMap<T, String>();

    public ModdedEnumTypeAdapter(Class<T> classOfT) {
        for (T constant : classOfT.getEnumConstants()) {
            String name = constant.name();
            // Ignore when the field can't be found since modified enums won't have it
            // which is fine, since it wouldn't have an annotation anyway (how could it?)
            try {
                SerializedName annotation = classOfT.getField(name).getAnnotation(SerializedName.class);
                if (annotation != null) {
                    name = annotation.value();
                }
            } catch (NoSuchFieldException ex) {
            }
            nameToConstant.put(name, constant);
            constantToName.put(constant, name);
        }
    }

    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return nameToConstant.get(in.nextString());
    }

    public void write(JsonWriter out, T value) throws IOException {
        out.value(value == null ? null : constantToName.get(value));
    }

    public static final TypeAdapterFactory ENUM_FACTORY = newEnumTypeHierarchyFactory();

    public static <TT> TypeAdapterFactory newEnumTypeHierarchyFactory() {
        return new TypeAdapterFactory() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                Class<? super T> rawType = typeToken.getRawType();
                if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
                    return null;
                }
                if (!rawType.isEnum()) {
                    rawType = rawType.getSuperclass(); // handle anonymous subclasses
                }
                return (TypeAdapter<T>) new ModdedEnumTypeAdapter(rawType);
            }
        };
    }
}