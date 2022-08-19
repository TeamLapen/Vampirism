package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import org.jetbrains.annotations.NotNull;

/**
 * This class has the sole purpose of providing a way to check for the minion recovery skill without the need of registering all recovery skills to check for them in {@link de.teamlapen.vampirism.entity.minion.management.PlayerMinionController#markDeadAndReleaseMinionSlot(int, int)}
 *
 * @param <T>
 */
public abstract class MinionRecoverySkill<T extends IFactionPlayer<T>> extends VampirismSkill<T> {

    public MinionRecoverySkill() {
        this.setHasDefaultDescription();
    }

    @Override
    public @NotNull ISkillType getType() {
        return SkillType.LORD;
    }
}
