package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SkillArgumentType implements ArgumentType<ISkill> {
    private static final Collection<String> EXAMPLES = Arrays.asList("name", "modid:name");
    public static final DynamicCommandExceptionType PARTICLE_NOT_FOUND = new DynamicCommandExceptionType((p_208673_0_) -> {
        return new TranslationTextComponent("skill.notFound " + p_208673_0_, p_208673_0_);
    });

    public static SkillArgumentType skills() {
        return new SkillArgumentType();
    }

    public static ISkill getSkill(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, ISkill.class);
    }

    @Override
    public ISkill parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        if (id.getNamespace().equals("minecraft"))
            id = new ResourceLocation(REFERENCE.MODID, id.getPath());
        ISkill skill = ModRegistries.SKILLS.getValue(id);
        if (skill == null)
            throw PARTICLE_NOT_FOUND.create(id);
        return skill;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestIterable(ModRegistries.SKILLS.getKeys(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
