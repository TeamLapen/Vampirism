package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * 1.12
 *
 * @author maxanier
 */
@Deprecated(forRemoval = true)
public interface ISkillManager {


    /**
     * A mutable copied list of all skills registered for this faction
     */
    @Deprecated(forRemoval = true)
    <T extends IFactionPlayer<T>> List<ISkill<T>> getSkillsForFaction(IPlayableFaction<T> faction);

    /**
     * Get a skill type registered to the given id
     *
     * @param id id of the registered {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType}
     * @return the registered {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType} or null
     */
    @Deprecated(forRemoval = true)
    @Nullable
    ISkillType getSkillType(@NotNull ResourceLocation id);

    /**
     * registers the {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType}
     *
     * @param type the skill type to register
     * @return the input skill type
     */
    @Deprecated(forRemoval = true)
    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    ISkillType registerSkillType(@NotNull ISkillType type);
}
