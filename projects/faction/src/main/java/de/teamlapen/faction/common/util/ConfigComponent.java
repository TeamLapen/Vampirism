package de.teamlapen.faction.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.*;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Optional;

public abstract class ConfigComponent<T> implements ComponentContents {

    public static final MapCodec<ConfigComponent<?>> MAP_CODEC = Type.CODEC.dispatchMap("mode", ConfigComponent::type, ConfigComponent::codecFor);

    private static MapCodec<? extends ConfigComponent<?>> codecFor(Type type) {
        return switch (type) {
            case DEFAULT -> DefaultConfigComponent.MAP_CODEC;
            case BOOLEAN -> BooleanConfigComponent.MAP_CODEC;
            case CALCULATE -> CalculatedConfigComponent.MAP_CODEC;
        };
    }

    protected final ModConfigSpec.ConfigValue<T> value;

    public ConfigComponent(ModConfigSpec.ConfigValue<T> value) {
        this.value = value;
    }

    protected abstract Type type();

    protected abstract String getValue();

    private static class DefaultConfigComponent extends ConfigComponent<Object> {

        public static final MapCodec<DefaultConfigComponent> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ConfigValueCodec.<Object>codec().fieldOf("configValue").forGetter(x -> x.value)
        ).apply(i, DefaultConfigComponent::new));

        @SuppressWarnings("unchecked")
        public DefaultConfigComponent(ModConfigSpec.ConfigValue<?> value) {
            super((ModConfigSpec.ConfigValue<Object>) value);
        }

        @Override
        protected String getValue() {
            return value.get().toString();
        }

        @Override
        protected Type type() {
            return Type.DEFAULT;
        }
    }

    private static class BooleanConfigComponent extends ConfigComponent<Boolean> {

        public static final MapCodec<BooleanConfigComponent> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ConfigValueCodec.<Boolean>codec().fieldOf("configValue").forGetter(x -> x.value),
                ComponentSerialization.CODEC.fieldOf("falseComponent").forGetter(x -> x.falseComponent),
                ComponentSerialization.CODEC.fieldOf("trueComponent").forGetter(x -> x.trueComponent)
        ).apply(i, BooleanConfigComponent::new));

        private final Component falseComponent;
        private final Component trueComponent;

        public BooleanConfigComponent(ModConfigSpec.ConfigValue<Boolean> value, Component falseComponent, Component trueComponent) {
            super(value);
            this.falseComponent = falseComponent;
            this.trueComponent = trueComponent;
        }

        @Override
        protected String getValue() {
            return this.value.get() ? trueComponent.getString() : falseComponent.getString();
        }

        @Override
        protected Type type() {
            return Type.BOOLEAN;
        }
    }

    private static class CalculatedConfigComponent extends ConfigComponent<Number> {

        public static final MapCodec<CalculatedConfigComponent> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ConfigValueCodec.<Number>codec().fieldOf("configValue").forGetter(x -> x.value),
                Codec.DOUBLE.fieldOf("number").forGetter(x -> x.number),
                Operator.CODEC.fieldOf("operator").forGetter(x -> x.operator),
                Codec.BOOL.fieldOf("integer").forGetter(x -> x.integer)
        ).apply(i, CalculatedConfigComponent::new));

        private final double number;
        private final Operator operator;
        private final boolean integer;

        public CalculatedConfigComponent(ModConfigSpec.ConfigValue<Number> value, double number, Operator operator, boolean integer) {
            super(value);
            this.number = number;
            this.operator = operator;
            this.integer = integer;
        }

        @Override
        protected String getValue() {
            double result = switch (this.operator) {
                case PLUS -> this.value.get().doubleValue() + number;
                case MINUS -> this.value.get().doubleValue() - number;
                case MULTIPLY -> this.value.get().doubleValue() * number;
                case DIVIDE -> this.value.get().doubleValue() / number;
            };
            return this.integer ? Integer.toString((int) result) : Double.toString(result);
        }

        @Override
        protected Type type() {
            return Type.CALCULATE;
        }
    }

    public static <T> MutableComponent config(ModConfigSpec.ConfigValue<T> value) {
        return MutableComponent.create(new DefaultConfigComponent(value));
    }

    public static MutableComponent config(ModConfigSpec.ConfigValue<Boolean> value, Component falseComponent, Component trueComponent) {
        return MutableComponent.create(new BooleanConfigComponent(value, falseComponent, trueComponent));
    }

    public static MutableComponent calculateInt(ModConfigSpec.ConfigValue<Integer> value, double number, Operator operator) {
        return MutableComponent.create(new CalculatedConfigComponent((ModConfigSpec.ConfigValue<Number>) (Object) value, number, operator, true));
    }

    public static MutableComponent calculateDouble(ModConfigSpec.ConfigValue<Double> value, double number, Operator operator) {
        return MutableComponent.create(new CalculatedConfigComponent((ModConfigSpec.ConfigValue<Number>) (Object) value, number, operator, false));
    }

    @Override
    public MapCodec<? extends ComponentContents> codec() {
        return MAP_CODEC;
    }

    @Override
    public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> output, Style currentStyle) {
        return output.accept(currentStyle, getValue());
    }

    @Override
    public <T> Optional<T> visit(FormattedText.ContentConsumer<T> output) {
        return output.accept(getValue());
    }

    protected enum Type implements StringRepresentable {
        DEFAULT("default"),
        BOOLEAN("boolean"),
        CALCULATE("calculate");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String id;

        Type(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }
    }

    public enum Operator implements StringRepresentable {
        PLUS("plus"),
        MINUS("minus"),
        MULTIPLY("multiply"),
        DIVIDE("divide");

        public static final Codec<Operator> CODEC = StringRepresentable.fromEnum(Operator::values);

        private final String id;

        Operator(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }
    }
}
