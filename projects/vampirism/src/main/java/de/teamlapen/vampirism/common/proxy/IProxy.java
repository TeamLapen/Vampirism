package de.teamlapen.vampirism.common.proxy;

import de.teamlapen.vampirism.common.world.blockentity.AltarInfusionBlockEntity;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Proxy interface
 */
public interface IProxy {

    default void displayNameSwordScreen(ItemStack stack) {
    }

    default void displayRevertBackScreen() {
    }

    default void displayHeritageRunAwayScreen() {
    }

    @Nullable
    Entity getMouseOverEntity();

    /**
     * Handle client side only sleep things
     */
    void handleSleepClient(Player player);

    /**
     * Shows a DBNO state with the given death message if the passed player is the client player
     */
    default void showDBNOScreen(Player player, @Nullable Component deathMessage) {
    }

    default Collection<Player> getServerPlayers() {
        return Collections.emptyList();
    }

    default void addBossEventSound(UUID bossEventUuid, ResourceKey<SoundEvent> sound) {

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

    default void addAltarOfInfusionSound(AltarInfusionBlockEntity blockEntity) {

    }

    /**
     * @return The string describing the currently active language. "English" on server side
     */
    default String getActiveLanguageCode(){
        return "en_us";
    }

    default String getActiveLanguageName(){
        return "English";
    }
}
