package de.teamlapen.faction.api.factions.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import net.minecraft.core.Holder;

/**
 * Action with a duration which is updated every tick
 */
public interface ILastingAction<TFactionPlayer extends ISkillPlayer<TFactionPlayer>> extends IAction<TFactionPlayer> {

    @SuppressWarnings("unchecked")
    Codec<Holder<ILastingAction<?>>> CODEC = IAction.CODEC.flatXmap(x -> {
        if (x.value() instanceof ILastingAction) return DataResult.success((Holder<ILastingAction<?>>) (Object) x);
        return DataResult.error(() -> "Action is not a lasting action");
    }, x -> DataResult.success((Holder<IAction<?>>) (Object) x));

    /**
     * @return Skill duration in ticks
     */
    int getDuration(TFactionPlayer player);

    /**
     * Called on the client after the action was activated on the server side.
     */
    void onActivatedClient(TFactionPlayer player);

    /**
     * Called when the action is deactivated
     * Client and server side
     */
    void onDeactivated(TFactionPlayer player);

    /**
     * Called when the action is activated after a world reload.
     */
    void onReActivatedServer(TFactionPlayer player);

    /**
     * Called every LivingUpdate for each entity that has this action activated Calls on the client side might be wrong due to sync
     *
     * @return if true, the lasting action is canceled
     */
    default boolean onUpdate(TFactionPlayer player) {
        return false;
    }

    default boolean onUpdate(TFactionPlayer player, int duration, int expectedDuration) {
        return onUpdate(player);
    }

}
