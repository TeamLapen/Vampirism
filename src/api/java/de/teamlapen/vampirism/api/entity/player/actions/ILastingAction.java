package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Action with a duration which is updated every tick
 */
public interface ILastingAction<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends IAction<T> {

    /**
     * @return Skill duration in ticks
     */
    int getDuration(T player);

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
     * For client side check {@link ILastingAction#onActivatedClient(IFactionPlayer)}}
     */
    void onReActivated(T player);

    /**
     * Called every LivingUpdate for each entity which has this action activated Calls on client side might be wrong due to sync
     *
     * @return if true the lasting action is cancelled
     */
    default boolean onUpdate(T player) {
        return false;
    }

    default boolean onUpdate(T player, int duration, int expectedDuration) {
        return onUpdate(player);
    }

    /**
     * @return if the action's duration should be rendered in the HUD
     */
    default boolean showHudDuration(Player player) {
        return false;
    }
}
