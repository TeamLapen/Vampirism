package de.teamlapen.vampirism.api.entity.player.actions;

/**
 * Action with a duration which is updated every tick
 */
public interface ILastingAction<T extends IActionPlayer> extends IAction<T> {
    /**
     * @param level Player's faction level
     * @return Skill duration in ticks
     */
    int getDuration(int level);

    /**
     * Called on the server after the action was activated on server side.
     * This means it is also called when the server reactivated the action, e.g. on world join
     */
    void onActivatedClient(T player);

    /**
     * Called when the action is deactivated
     * Client and server side
     */
    void onDeactivated(T player);

    /**
     * Called when the action is activated after a world reload.
     * Called SERVER SIDE ONLY.
     * For client side check {@link ILastingAction#onActivatedClient(IActionPlayer)}
     */
    void onReActivated(T player);

    /**
     * Called every LivingUpdate for each entity which has this action activated Calls on client side might be wrong due to sync
     *
     * @return if true the lasting action is cancelled
     */
    boolean onUpdate(T player);
}
