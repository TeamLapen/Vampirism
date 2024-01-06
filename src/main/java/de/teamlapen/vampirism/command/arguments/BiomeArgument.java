package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import de.teamlapen.vampirism.mixin.ResourceKeyArgumentAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class BiomeArgument extends ResourceKeyArgument<Biome> {
    public static final DynamicCommandExceptionType BIOME_UNKNOWN_TYPE = new DynamicCommandExceptionType((id) -> Component.translatable("command.vampirism.biome.not_found", id));

    public BiomeArgument() {
        super(Registries.BIOME);
    }

    public static @NotNull BiomeArgument biome() {
        return new BiomeArgument();
    }

    public static Holder.Reference<Biome> getBiome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return ResourceKeyArgumentAccessor.invokeResolveKey(context, name, Registries.BIOME, BIOME_UNKNOWN_TYPE);
    }
}
