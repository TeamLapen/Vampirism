package de.teamlapen.vampirism.entity.player.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Extension of {@link DefaultSkill} with vampirism default unloc names/descriptions
 */
public abstract class VampirismSkill<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends DefaultSkill<T> {
    private final Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> skillTree;
    private Supplier<Component> description = () -> null;
    private Consumer<T> activate = (T player) -> {
    };
    private Consumer<T> deactivate = (T player) -> {
    };

    public VampirismSkill(Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> skillTree) {
        super(2);
        this.skillTree = skillTree;
    }

    public VampirismSkill(Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> skillTree, boolean hasDescription) {
        this(skillTree, 2, hasDescription);
    }

    public VampirismSkill(Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> skillTree, int skillPointCost, boolean hasDescription) {
        super(skillPointCost);
        this.skillTree = skillTree;
        if (hasDescription) {
            this.setHasDefaultDescription();
        }
    }

    @Override
    public Component getDescription() {
        return description.get();
    }

    public @NotNull VampirismSkill<T> setDescription(Supplier<Component> descriptionIn) {
        this.description = descriptionIn;
        return this;
    }

    @Override
    public Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> allowedSkillTrees() {
        return this.skillTree;
    }

    /**
     * Enable description using "text.vampirism.skill."+getID()+".desc" as unloc key
     */
    public @NotNull VampirismSkill<T> setHasDefaultDescription() {
        description = () -> Component.translatable(getTranslationKey() + ".desc");
        return this;
    }

    public @NotNull VampirismSkill<T> setToggleActions(Consumer<T> activateIn, Consumer<T> deactivateIn) {
        this.activate = activateIn;
        this.deactivate = deactivateIn;
        return this;
    }

    @Override
    protected void onDisabled(T player) {
        deactivate.accept(player);
        super.onDisabled(player);
    }

    @Override
    protected void onEnabled(T player) {
        activate.accept(player);
        super.onEnabled(player);
    }

    /**
     * Simple hunter skill implementation. Does nothing by itself
     */
    public static class SimpleHunterSkill extends VampirismSkill<IHunterPlayer> {

        /**
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleHunterSkill(int skillPointCost, boolean desc) {
            super(Either.left(HunterSkills.Trees.LEVEL), skillPointCost, desc);
        }

        @Override
        public TagKey<? extends IFaction<?>> factions() {
            return ModFactionTags.IS_HUNTER;
        }
    }

    public static class HunterLordSkill extends VampirismSkill<IHunterPlayer> {

        /**
         * @param desc Enable description using the default unlocalized key
         */
        public HunterLordSkill(int skillPointCost, boolean desc) {
            super(Either.left(HunterSkills.Trees.LORD), skillPointCost, desc);
        }

        @Override
        public TagKey<? extends IFaction<?>> factions() {
            return ModFactionTags.IS_HUNTER;
        }
    }

    /**
     * Simple vampire skill implementation. Does nothing by itself
     */
    public static class SimpleVampireSkill extends VampirismSkill<IVampirePlayer> {

        public SimpleVampireSkill(int skillPointCost, boolean desc) {
            super(Either.left(VampireSkills.Trees.LEVEL), skillPointCost, desc);
        }

        @Override
        public TagKey<? extends IFaction<?>> factions() {
            return ModFactionTags.IS_VAMPIRE;
        }
    }

    public static class VampireLordSkill extends VampirismSkill<IVampirePlayer> {
        public VampireLordSkill(int skillPointCost, boolean desc) {
            super(Either.left(VampireSkills.Trees.LORD), skillPointCost, desc);
        }

        @Override
        public TagKey<? extends IFaction<?>> factions() {
            return ModFactionTags.IS_VAMPIRE;
        }
    }

}
