package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("CodeBlock2Expr")
public class ModSuggestionProvider {
    public static final SuggestionProvider<CommandSourceStack> ENTITIES = SuggestionProviders.register(new ResourceLocation(REFERENCE.MODID, "entities"), (context, builder) -> {
        return SharedSuggestionProvider.suggestResource(ForgeRegistries.ENTITY_TYPES.getValues().stream(), builder, ForgeRegistries.ENTITY_TYPES::getKey, (c) -> {
            return Component.translatable(Util.makeDescriptionId("entity", ForgeRegistries.ENTITY_TYPES.getKey(c)));
        });
    });
    public static final SuggestionProvider<CommandSourceStack> BIOMES = SuggestionProviders.register(new ResourceLocation(REFERENCE.MODID, "biomes"), (context, builder) -> {
        return SharedSuggestionProvider.suggestResource(ForgeRegistries.BIOMES.getValues().stream(), builder, ForgeRegistries.BIOMES::getKey, (c) -> {
            return Component.translatable(Util.makeDescriptionId("biome", ForgeRegistries.BIOMES.getKey(c)));
        });
    });
}
