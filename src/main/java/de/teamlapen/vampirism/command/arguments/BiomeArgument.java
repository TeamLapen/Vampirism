package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeArgument implements ArgumentType<ResourceLocation> {
    public static final DynamicCommandExceptionType BIOME_UNKNOWN_TYPE = new DynamicCommandExceptionType((id) -> {
        return new TranslationTextComponent("command.vampirism.biome.notFound", id);
    });

    public static BiomeArgument biome() {
        return new BiomeArgument();
    }

    public static ResourceLocation getBiomeId(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return checkIfEntityExists(context.getArgument(name, ResourceLocation.class));
    }

    private static ResourceLocation checkIfEntityExists(ResourceLocation id) throws CommandSyntaxException {
        if (ForgeRegistries.BIOMES.getValue(id) == null) {
            throw BIOME_UNKNOWN_TYPE.create(id);
        }
        return id;
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return checkIfEntityExists(ResourceLocation.read(reader));
    }
}
