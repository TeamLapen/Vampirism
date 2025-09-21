package de.teamlapen.vampirism.common.entity.player.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.common.serialization.ModCodecs;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;
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
                    TagKey.codec(VampirismRegistries.Keys.SKILL_TREE).optionalFieldOf("name_suffix", ModSkillTreeTags.DEFAULT).forGetter(ISkillTree::skillPointTag)
            ).apply(inst, SkillTree::new)
    ));

    public SkillTree(@NotNull Holder<? extends IPlayableFaction<?>> faction, @NotNull EntityPredicate unlockPredicate, @NotNull ItemStack display, @NotNull Component name) {
        this(faction, unlockPredicate, display, name, Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public SkillTree(@NotNull Holder<? extends IPlayableFaction<?>> faction, @NotNull EntityPredicate unlockPredicate, @NotNull ItemStack display, @NotNull Component name, @NotNull Optional<ResourceLocation> background) {
        this(faction, unlockPredicate, display, name, background, ModSkillTreeTags.DEFAULT);
    }
}
