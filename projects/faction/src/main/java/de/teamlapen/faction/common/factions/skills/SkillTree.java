package de.teamlapen.faction.common.factions.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.tags.FactionSkillTreeTags;
import de.teamlapen.faction.common.util.ModCodecs;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.Optional;

public record SkillTree(Holder<? extends IPlayableFaction<?>> faction, EntityPredicate unlockPredicate, ItemStackTemplate display, Component name, Optional<Identifier> background, TagKey<ISkillTree> skillPointTag, List<ResourceKey<ISkillTree>> orderAfter) implements ISkillTree {

    public static final Codec<ISkillTree> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(inst ->
            inst.group(
                    ModCodecs.playableFaction().fieldOf("faction").forGetter(ISkillTree::faction),
                    EntityPredicate.CODEC.fieldOf("unlock_predicate").forGetter(ISkillTree::unlockPredicate),
                    ItemStackTemplate.CODEC.fieldOf("display").forGetter(ISkillTree::display),
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(ISkillTree::name),
                    Identifier.CODEC.optionalFieldOf("background").forGetter(ISkillTree::background),
                    TagKey.codec(FactionRegistries.Keys.SKILL_TREE).optionalFieldOf("skill_point_tag", FactionSkillTreeTags.DEFAULT).forGetter(ISkillTree::skillPointTag),
                    ResourceKey.codec(FactionRegistries.Keys.SKILL_TREE).listOf().optionalFieldOf("order_after", List.of()).forGetter(ISkillTree::orderAfter)
            ).apply(inst, SkillTree::new)
    ));

    public SkillTree(Holder<? extends IPlayableFaction<?>> faction, EntityPredicate unlockPredicate, ItemStackTemplate display, Component name) {
        this(faction, unlockPredicate, display, name, Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public SkillTree(Holder<? extends IPlayableFaction<?>> faction, EntityPredicate unlockPredicate, ItemStackTemplate display, Component name, Optional<Identifier> background) {
        this(faction, unlockPredicate, display, name, background, FactionSkillTreeTags.DEFAULT);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public SkillTree(Holder<? extends IPlayableFaction<?>> faction, EntityPredicate unlockPredicate, ItemStackTemplate display, Component name, Optional<Identifier> background, TagKey<ISkillTree> skillPointTag) {
        this(faction, unlockPredicate, display, name, background, skillPointTag, List.of());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public SkillTree(Holder<? extends IPlayableFaction<?>> faction, EntityPredicate unlockPredicate, ItemStackTemplate display, Component name, Optional<Identifier> background, List<ResourceKey<ISkillTree>> orderAfter) {
        this(faction, unlockPredicate, display, name, background, FactionSkillTreeTags.DEFAULT, orderAfter);
    }
}
