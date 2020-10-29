package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSuggestionProvider {
    public static final SuggestionProvider<CommandSource> ENTITIES = SuggestionProviders.register(new ResourceLocation(REFERENCE.MODID, "entities"), (context, builder) -> {
        return ISuggestionProvider.func_201725_a(ForgeRegistries.ENTITIES.getValues().stream().filter(type -> !VampirismAPI.entityRegistry().isEntityTypeBlacklistedByDefault(type)), builder, EntityType::getKey, (c) -> {
            return new TranslationTextComponent(Util.makeTranslationKey("entity", EntityType.getKey(c)));
        });
    });
}
