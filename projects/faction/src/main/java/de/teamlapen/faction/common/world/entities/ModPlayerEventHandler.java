package de.teamlapen.faction.common.world.entities;

import com.google.common.collect.ImmutableList;
import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionEffects;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.tags.FactionTags;
import de.teamlapen.faction.common.util.RegUtil;
import de.teamlapen.faction.common.util.TotemHelper;
import de.teamlapen.faction.common.world.blockentity.TotemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ModPlayerEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerName(PlayerEvent.NameFormat event) {
        //noinspection ConstantValue
        if (FactionConfig.server().factionColorInChat.get() && (!(event.getEntity() instanceof ServerPlayer serverPlayer) || serverPlayer.connection != null)) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(event.getEntity());
            Holder<? extends IFaction<?>> f = handler.factionPlayer().getDisguise().getViewedFaction(Optional.ofNullable(FactionsMod.proxy.getClientPlayer()).map(FactionPlayerHandler::get).map(FactionPlayerHandler::getFaction).orElse(null));
            if (!IFaction.is(f, FactionTags.IS_NEUTRAL)) {
                MutableComponent displayName;
                displayName = handler.getLordPlayer().filter(h -> h.getLordLevel() > 0).filter(x -> FactionConfig.server().factionLordPrefixInChat.get()).map(ILordPlayer::getLordTitle)
                        .map(x -> Component.literal("[").append(x).append("] ").append(event.getDisplayname()))
                        .orElseGet(() -> event.getDisplayname().copy());
                event.setDisplayname(displayName.withStyle(style -> style.withColor((f.value().getChatColor()))));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.@NotNull Start event) {
        if (!FactionRestriction.canUse(event.getEntity(), event.getItem(), true)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemRightClick(PlayerInteractEvent.@NotNull RightClickItem event) {
        if (!FactionRestriction.canUse(event.getEntity(), event.getItemStack(), true)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect().is(FactionEffects.RESURRECTION_FATIGUE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void blockDestroyed(BlockEvent.@NotNull BreakEvent event) {
        if (!(event.getLevel() instanceof Level)) return;
        //don't allow player to destroy blocks with PointOfInterests that are owned by a totem with different faction as the player
        if (event.getPlayer().isCreative()) return;
        if (FactionConfig.server().villageAllowPoiDestruction.get()) return;
        Set<BlockPos> positions = new HashSet<>();
        BlockPos totemPos = TotemHelper.getTotemPosition(((Level) event.getLevel()).dimension(), event.getPos());
        Block block = event.getState().getBlock();
        //if the blockstate does not have a POI, but another blockstate of the specific block e.g. the bed, search for the blockstate in a 3x3x3 radius
        //or the other way around
        ImmutableList<BlockState> validStates = block.getStateDefinition().getPossibleStates();
        if (validStates.size() > 1 && RegUtil.values(BuiltInRegistries.POINT_OF_INTEREST_TYPE).stream().flatMap(poiType -> poiType.matchingStates().stream()).anyMatch(validStates::contains)) {
            for (int x = event.getPos().getX() - 1; x <= event.getPos().getX() + 1; ++x) {
                for (int z = event.getPos().getZ() - 1; z <= event.getPos().getZ() + 1; ++z) {
                    for (double y = event.getPos().getY() - 1; y <= event.getPos().getY() + 1; ++y) {
                        BlockPos pos1 = new BlockPos(x, (int) y, z);
                        if (((Level) event.getLevel()).isLoaded(pos1) && event.getLevel().getBlockState(pos1).getBlock() == block) {
                            BlockPos totemPos1 = TotemHelper.getTotemPosition(((Level) event.getLevel()).dimension(), pos1);
                            if (totemPos1 != null && totemPos == null) {
                                totemPos = totemPos1;
                            }
                            positions.add(pos1);
                        }
                    }
                }
            }
        }
        //cancel the event and notify client about the failed block destroy.
        //also notify client about wrong destroyed neighbor blocks (bed)
        if (totemPos != null && event.getLevel().hasChunkAt(totemPos)) {
            BlockEntity totem = (event.getLevel().getBlockEntity(totemPos));
            if (totem instanceof TotemBlockEntity blockEntity && !IFaction.isNeutral(blockEntity.getControllingFaction()) && IFaction.is(FactionPlayerHandler.get(event.getPlayer()).getFaction(), blockEntity.getControllingFaction())) {
                event.setCanceled(true);
                event.getPlayer().displayClientMessage(Component.translatable("text.factionapi.village.totem_destroy.fail_totem_faction"), true);
                if (!positions.isEmpty() && event.getPlayer() instanceof ServerPlayer player) {
                    positions.forEach(pos -> {
                        player.connection.send(new ClientboundBlockUpdatePacket(event.getLevel(), pos));
                        BlockEntity tileentity = event.getLevel().getBlockEntity(pos);
                        if (tileentity != null) {
                            Packet<?> pkt = tileentity.getUpdatePacket();
                            if (pkt != null) {
                                player.connection.send(pkt);
                            }
                        }
                    });
                }
            }
        }
    }
}
