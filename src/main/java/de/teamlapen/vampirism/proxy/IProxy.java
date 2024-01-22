package de.teamlapen.vampirism.proxy;

import com.mojang.authlib.GameProfile;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.blockentity.diffuser.FogDiffuserBlockEntity;
import de.teamlapen.vampirism.blockentity.diffuser.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.network.ClientboundUpdateMultiBossEventPacket;
import de.teamlapen.vampirism.util.PlayerModelType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Proxy interface
 */
public interface IProxy extends IInitListener {

    default void displayNameSwordScreen(ItemStack stack) {
    }

    default void displayRevertBackScreen() {
    }

    @Nullable
    Player getClientPlayer();

    @Nullable
    Entity getMouseOverEntity();

    /**
     * Handle client side only sleep things
     */
    void handleSleepClient(Player player);

    default void handleUpdateMultiBossInfoPacket(ClientboundUpdateMultiBossEventPacket msg) {
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

    default Collection<Player> getServerPlayers() {
        return Collections.emptyList();
    }

    default void addBossEventSound(UUID bossEventUuid, ResourceKey<SoundEvent> sound){

    }

    default void sendToServer(CustomPacketPayload packetPayload) {

    }

    default void spawnParticles(Level world, @NotNull BlockPos pos, @NotNull BlockState state) {

    }

    default void displayVampireMinionAppearanceScreen(VampireMinionEntity entity) {
    }

    default void displayVampireMinionStatsaScreen(VampireMinionEntity entity) {
    }

    default void displayHunterMinionAppearanceScreen(HunterMinionEntity entity) {
    }

    default void displayHunterMinionStatsScreen(HunterMinionEntity entity) {

    }

    default void obtainPlayerSkins(GameProfile profile, @NotNull Consumer<Pair<ResourceLocation, PlayerModelType>> callback) {

    }
}
