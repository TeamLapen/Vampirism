package de.teamlapen.faction.api.world.entities.minion;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.faction.api.FactionDataComponents;
import de.teamlapen.faction.api.world.entities.ICustomizationOption;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MinionAppearance<T> implements ICustomizationOption<T> {
    public static final MinionAppearance<String> NAME_TYPE = new MinionAppearance<>(FactionDataComponents.MINION_NAME, "Minion", "name", StringArgumentType.string()) {
        @Nullable
        @Override
        public String setValue(IMinionEntity entity, IMinionData data, String value) {
            var old = super.setValue(entity, data, value);
            entity.asEntity().setCustomName(Component.literal(value));
            return old;
        }
    };
    public static final MinionAppearance<Integer> SKIN_TYPE = new MinionAppearance<>(FactionDataComponents.MINION_SKIN, 0, "skin", IntegerArgumentType.integer(0));
    public static final MinionAppearance<Boolean> LORD_SKIN = new MinionAppearance<>(FactionDataComponents.MINION_USE_LORD_SKIN, false, "use_lord_skin", BoolArgumentType.bool());
    public static final MinionAppearance<Boolean> MINION_SKIN = new MinionAppearance<>(FactionDataComponents.MINION_USE_MINION_SKIN, false, "use_minion_skin", BoolArgumentType.bool());

    private final Holder<DataComponentType<T>> type;
    private final T defaultValue;
    private final String commandName;
    private final ArgumentType<T> argumentType;

    /**
     * @param commandName  Name of the argument in the minion create command
     * @param argumentType Type of the argument in the minion create command
     */
    public MinionAppearance(Holder<DataComponentType<T>> type, T defaultValue, String commandName, ArgumentType<T> argumentType) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.commandName = commandName;
        this.argumentType = argumentType;
    }

    public MinionAppearance(DeferredHolder<DataComponentType<?>, DataComponentType<T>> type, T defaultValue, String commandName, ArgumentType<T> argumentType) {
        this((Holder)type, defaultValue, commandName, argumentType);
    }

    public T currentValue(IMinionEntity entity, IMinionData data) {
        return data.getOrDefault(type::value, defaultValue);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public T currentValue(IMinionEntity entity, Optional<? extends IMinionData> data) {
        return data.map(x -> x.get(type::value)).orElse(defaultValue);
    }

    @Nullable
    public T setValue(IMinionEntity entity, IMinionData data, T value) {
        return data.set(type::value, value);
    }

    public String commandName() {
        return this.commandName;
    }

    public ArgumentType<T> argumentType() {
        return this.argumentType;
    }

    /**
     * Reads the value of this appearance from the argument {@link #commandName()} and applies it to the minion data
     */
    @SuppressWarnings("unchecked")
    public void applyCommandArgument(CommandContext<CommandSourceStack> context, IMinionData data) {
        data.set(type::value, (T) context.getArgument(this.commandName, Object.class));
    }

    @Override
    public Identifier id() {
        return type.unwrapKey().orElseThrow().identifier();
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, T> codec() {
        return type.value().streamCodec();
    }
}
