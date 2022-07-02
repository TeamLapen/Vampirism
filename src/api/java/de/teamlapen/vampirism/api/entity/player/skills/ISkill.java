package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Skill that can be unlocked
 */
public interface ISkill<T extends IFactionPlayer<T>> {
    /**
     * The description for this skill. Can be null
     */
    @OnlyIn(Dist.CLIENT)
    Component getDescription();

    /**
     * @return The faction this skill belongs to
     */
    @Nonnull
    Optional<IPlayableFaction<?>> getFaction();

    default Component getName() {
        return Component.translatable(getTranslationKey());
    }

    @OnlyIn(Dist.CLIENT)
    int getRenderColumn();

    @OnlyIn(Dist.CLIENT)
    int getRenderRow();

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

    /**
     * Save this. It's required for rendering
     */
    void setRenderPos(int row, int column);

    /**
     * @return The {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType} of this skill
     */
    default ISkillType getType() {
        return SkillType.LEVEL;
    }
}
