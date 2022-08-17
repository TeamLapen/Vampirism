package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class BiomeArgument implements ArgumentType<ResourceLocation> {
    public static final DynamicCommandExceptionType BIOME_UNKNOWN_TYPE = new DynamicCommandExceptionType((id) -> Component.translatable("command.vampirism.biome.not_found", id));

    public static @NotNull BiomeArgument biome() {
        return new BiomeArgument();
    }

    public static ResourceLocation getBiomeId(@NotNull CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return checkIfEntityExists(context.getArgument(name, ResourceLocation.class));
    }

    private static ResourceLocation checkIfEntityExists(ResourceLocation id) throws CommandSyntaxException {
        if (ForgeRegistries.BIOMES.getValue(id) == null) {
            throw BIOME_UNKNOWN_TYPE.create(id);
        }
        return id;
    }

    @Override
    public @NotNull ResourceLocation parse(@NotNull StringReader reader) throws CommandSyntaxException {
        return checkIfEntityExists(ResourceLocation.read(reader));
    }
}
