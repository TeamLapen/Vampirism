package de.teamlapen.vampirism.api.entity.player.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Optional;

/**
 * Skill that can be unlocked
 */
public interface ISkill<T extends IFactionPlayer<T>> {
    /**
     * The description for this skill. Can be null
     */
    Component getDescription();

    /**
     * @return The faction this skill belongs to
     */
    @NotNull
    Optional<IPlayableFaction<?>> getFaction();

    default Component getName() {
        return Component.translatable(getTranslationKey());
    }

    /**
     * Use {@link ISkill#getName()}
     */
    @Deprecated
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

    @Range(from = 0, to = 9)
    default int getSkillPointCost() {
        return 1;
    }

    Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> allowedSkillTrees();

}
