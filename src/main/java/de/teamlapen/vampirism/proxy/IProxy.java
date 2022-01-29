package de.teamlapen.vampirism.proxy;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.blockentity.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.network.*;
import de.teamlapen.vampirism.player.skills.SkillTree;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Proxy interface
 */
public interface IProxy extends IInitListener {

    default void displayGarlicBeaconScreen(GarlicDiffuserBlockEntity tile, Component title) {
    }

    default void displayNameSwordScreen(ItemStack stack) {
    }

    default void displayRevertBackScreen() {
    }

    @Nullable
    Player getClientPlayer();

    @Nullable
    Entity getMouseOverEntity();

    default float getRenderPartialTick() {
        return 1F;
    }

    /**
     * Only call after client_load/server started
     *
     * @param client Request the client version. Server ignores this
     * @return The appropriate skill tree
     */
    SkillTree getSkillTree(boolean client);

    default void handleBloodValuePacket(SBloodValuePacket msg) {
    }

    default void handlePlayEventPacket(SPlayEventPacket msg) {
    }

    default void handleRequestMinionSelect(SRequestMinionSelectPacket.Action action, List<Pair<Integer, Component>> minions) {
    }

    default void handleSkillTreePacket(SSkillTreePacket msg) {
    }

    /**
     * Handle client side only sleep things
     */
    void handleSleepClient(Player player);

    default void handleTaskPacket(STaskPacket msg) {
    }

    default void handleTaskStatusPacket(STaskStatusPacket msg) {
    }

    default void handleUpdateMultiBossInfoPacket(SUpdateMultiBossEventPacket msg) {
    }

    default void handleVampireBookPacket(SOpenVampireBookPacket msg) {
    }

    void renderScreenFullColor(int ticksOn, int ticksOff, int color);

    /**
     * @deprecated unused
     */
    @Deprecated
    default void resetSkillScreenCache() { //TODO BREAKING remove
    }

    /**
     * Shows a DBNO state with the given death message if the passed player is the client player
     */
    default void showDBNOScreen(Player player, @Nullable Component deathMessage) {
    }

    default void setupAPIClient() {
    }

}
