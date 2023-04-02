package de.teamlapen.vampirism.proxy;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.blockentity.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.entity.player.skills.SkillTree;
import de.teamlapen.vampirism.network.*;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

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

    default void handleBloodValuePacket(ClientboundBloodValuePacket msg) {
    }

    default void handlePlayEventPacket(ClientboundPlayEventPacket msg) {
    }

    default void handleRequestMinionSelect(ClientboundRequestMinionSelectPacket.Action action, List<Pair<Integer, Component>> minions) {
    }

    default void handleSkillTreePacket(ClientboundSkillTreePacket msg) {
    }

    /**
     * Handle client side only sleep things
     */
    void handleSleepClient(Player player);

    default void handleTaskPacket(ClientboundTaskPacket msg) {
    }

    default void handleTaskStatusPacket(ClientboundTaskStatusPacket msg) {
    }

    default void handleUpdateMultiBossInfoPacket(ClientboundUpdateMultiBossEventPacket msg) {
    }

    default void handleVampireBookPacket(VampireBookManager.BookInfo msg) {
    }

    void renderScreenFullColor(int ticksOn, int ticksOff, int color);

    /**
     * Shows a DBNO state with the given death message if the passed player is the client player
     */
    default void showDBNOScreen(Player player, @Nullable Component deathMessage) {
    }

    default void setupAPIClient() {
    }

    default void endBloodVisionBatch() {

    }

    default void applyConvertibleOverlays(Map<EntityType<? extends PathfinderMob>, ResourceLocation> convertibleOverlay) {

    }
}
