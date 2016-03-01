package de.teamlapen.vampirism.api.entity.player.vampire;

/**
 * Vampire skill with a duration which is updated every tick
 */
public interface ILastingVampireAction extends IVampireAction {
    /**
     * @param level Vampire player level
     * @return Skill duration in ticks
     */
    int getDuration(int level);

    /**
     * Called on the server after the action was activated on server side.
     * This means it is also called when the server reactivated the skill, e.g. on world join
     *
     * @param vampire
     */
    void onActivatedClient(IVampirePlayer vampire);

    /**
     * Called when the action is deactivated
     * Client and server side
     *
     * @param vampire
     */
    void onDeactivated(IVampirePlayer vampire);

    /**
     * Called when the action is activated after a world reload.
     * Called SERVER SIDE ONLY.
     * For client side check {@link ILastingVampireAction#onActivatedClient(IVampirePlayer)}
     *
     * @param vampire
     */
    void onReActivated(IVampirePlayer vampire);

    /**
     * Called every LivingUpdate for each entity which has this action activated Calls on client side might be wrong due to sync
     *
     * @param vampire
     * @return if true the skill is cancelled
     */
    boolean onUpdate(IVampirePlayer vampire);
}
