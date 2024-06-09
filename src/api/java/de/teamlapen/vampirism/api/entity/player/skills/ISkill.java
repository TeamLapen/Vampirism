package de.teamlapen.vampirism.api.entity.player.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Optional;

/**
 * Skill that tha unlocks abilities for a player.
 */
public interface ISkill<T extends IFactionPlayer<T>> extends ISkillLike<T> {
    /**
     * The description for this skill or null if there is no description.
     */
    @Nullable
    Component getDescription();

    /**
     * A skill can be either
     * @return The faction this skill belongs to
     */
    @Deprecated
    @NotNull
    Optional<IPlayableFaction<?>> getFaction();

    TagKey<? extends IFaction<?>> factions();

    default MutableComponent getName() {
        return Component.translatable(getTranslationKey());
    }

    String getTranslationKey();

    /**
     * Called when the skill is disabled (Server: on load from nbt/on disabling all skills e.g. via the gui. Client: on update from server)
     *
     * @param player Must be of the type that {@link ISkill#getFaction()} belongs to
     */
    void onDisable(T player);

    /**
     * Called when the skill is enabled (Server: on load from nbt/on enabling it via the gui. Client: on update from server)
     *
     * @param player Must be of the type that {@link ISkill#getFaction()} belongs to
     */
    void onEnable(T player);

    /**
     * Unlocking skills costs a certain amount of skill points.
     *
     * @return The cost of the skill
     */
    @Range(from = 0, to = 9)
    default int getSkillPointCost() {
        return 1;
    }

    /**
     * Skill can only be added to skill trees defined by the return value.
     * @return A key of the allowed skilltree or a tag of skilltrees
     */
    Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> allowedSkillTrees();

    @Override
    default ISkill<T> asSkill() {
        return this;
    }
}
