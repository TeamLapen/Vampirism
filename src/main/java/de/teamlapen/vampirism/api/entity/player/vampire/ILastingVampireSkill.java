package de.teamlapen.vampirism.api.entity.player.vampire;

/**
 * Vampire skill with a duration which is updated every tick
 */
public interface ILastingVampireSkill extends IVampireSkill {
    /**
     * @param level Vampire player level
     * @return Skill duration in ticks
     */
    int getDuration(int level);

    /**
     * Called when the skill is deactivated SERVER SIDE ONLY
     *
     * @param vampire
     */
    void onDeactivated(IVampirePlayer vampire);

    /**
     * Called when the skill is activated after a world reload. SERVER SIDE ONLY
     *
     * @param vampire
     */
    void onReActivated(IVampirePlayer vampire);

    /**
     * Called every LivingUpdate for each entity which has this skill activated Calls on client side might be wrong due to sync
     *
     * @param vampire
     * @return if true the skill is cancelled
     */
    boolean onUpdate(IVampirePlayer vampire);
}
