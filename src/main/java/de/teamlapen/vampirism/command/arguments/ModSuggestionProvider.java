package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSuggestionProvider {
    public static final SuggestionProvider<CommandSource> ENTITIES = SuggestionProviders.register(new ResourceLocation(REFERENCE.MODID, "entities"), (context, builder) -> {
        return ISuggestionProvider.func_201725_a(ForgeRegistries.ENTITIES.getValues().stream(), builder, ForgeRegistries.ENTITIES::getKey, (c) -> {
            return new TranslationTextComponent(Util.makeTranslationKey("entity", ForgeRegistries.ENTITIES.getKey(c)));
        });
    });
    public static final SuggestionProvider<CommandSource> BIOMES = SuggestionProviders.register(new ResourceLocation(REFERENCE.MODID, "biomes"), (context, builder) -> {
        return ISuggestionProvider.func_201725_a(ForgeRegistries.BIOMES.getValues().stream(), builder, ForgeRegistries.BIOMES::getKey, (c) -> {
            return new TranslationTextComponent(Util.makeTranslationKey("biome", ForgeRegistries.BIOMES.getKey(c)));
        });
    });
}
