package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * 1.12
 *
 * @author maxanier
 */
public interface ISkillManager {


    /**
     * A mutable copied list of all skills registered for this faction
     *
     * @param faction
     * @return
     */
    List<ISkill> getSkillsForFaction(IPlayableFaction faction);

    /**
     * Get all available {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType}s that are registered
     */
    Collection<ISkillType> getSkillTypes();

    /**
     * Get a skill type registered to the given id
     *
     * @param id id of the registered {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType}
     * @return the registered {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType} or null
     */
    @Nullable
    ISkillType getSkillType(@Nonnull ResourceLocation id);

    /**
     * registers the {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType}
     * @param type the skill type to register
     * @return the input skill type
     */
    @SuppressWarnings("UnusedReturnValue")
    @Nonnull
    ISkillType registerSkillType(@Nonnull ISkillType type);
}
