package de.teamlapen.factions.common.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.skills.ISkillTree;
import de.teamlapen.factions.common.tags.FactionSkillTreeTags;
import de.teamlapen.factions.common.util.ModCodecs;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SkillTree(@NotNull Holder<? extends IPlayableFaction<?>> faction, @NotNull EntityPredicate unlockPredicate, @NotNull ItemStack display, @NotNull Component name, @NotNull Optional<ResourceLocation> background, @NotNull TagKey<ISkillTree> skillPointTag) implements ISkillTree {

    public static final Codec<ISkillTree> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(inst ->
            inst.group(
                    ModCodecs.playableFaction().fieldOf("faction").forGetter(ISkillTree::faction),
                    EntityPredicate.CODEC.fieldOf("unlock_predicate").forGetter(ISkillTree::unlockPredicate),
                    ItemStack.CODEC.fieldOf("display").forGetter(ISkillTree::display),
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(ISkillTree::name),
                    ResourceLocation.CODEC.optionalFieldOf("background").forGetter(ISkillTree::background),
                    TagKey.codec(FactionRegistries.Keys.SKILL_TREE).optionalFieldOf("name_suffix", FactionSkillTreeTags.DEFAULT).forGetter(ISkillTree::skillPointTag)
            ).apply(inst, SkillTree::new)
    ));

    public SkillTree(@NotNull Holder<? extends IPlayableFaction<?>> faction, @NotNull EntityPredicate unlockPredicate, @NotNull ItemStack display, @NotNull Component name) {
        this(faction, unlockPredicate, display, name, Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public SkillTree(@NotNull Holder<? extends IPlayableFaction<?>> faction, @NotNull EntityPredicate unlockPredicate, @NotNull ItemStack display, @NotNull Component name, @NotNull Optional<ResourceLocation> background) {
        this(faction, unlockPredicate, display, name, background, FactionSkillTreeTags.DEFAULT);
    }
}
