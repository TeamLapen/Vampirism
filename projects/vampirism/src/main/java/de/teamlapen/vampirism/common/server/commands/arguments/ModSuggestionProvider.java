package de.teamlapen.vampirism.common.server.commands.arguments;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.level.biome.Biome;

@SuppressWarnings("CodeBlock2Expr")
public class ModSuggestionProvider {
    public static final SuggestionProvider<CommandSourceStack> ENTITIES = SuggestionProviders.register(VResourceLocation.mod("entities"), (context, builder) -> {
        return SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.stream(), builder, BuiltInRegistries.ENTITY_TYPE::getKey, entity -> {
            return Component.translatable(Util.makeDescriptionId("entity", BuiltInRegistries.ENTITY_TYPE.getKey(entity)));
        });
    });
    public static final SuggestionProvider<CommandSourceStack> BIOMES = SuggestionProviders.register(VResourceLocation.mod("biomes"), (context, builder) -> {
        Registry<Biome> biomes = context.getSource().registryAccess().lookupOrThrow(Registries.BIOME);
        return SharedSuggestionProvider.suggestResource(biomes.stream(), builder, biomes::getKey, (c) -> {
            return Component.translatable(Util.makeDescriptionId("biome", biomes.getKey(c)));
        });
    });
}
