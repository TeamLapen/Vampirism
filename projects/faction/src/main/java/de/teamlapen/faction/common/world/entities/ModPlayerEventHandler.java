package de.teamlapen.faction.common.world.entities;

import com.google.common.collect.ImmutableList;
import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionEffects;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.util.RegUtil;
import de.teamlapen.faction.common.util.TotemHelper;
import de.teamlapen.faction.common.world.blockentity.TotemBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
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
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModPlayerEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerName(PlayerEvent.NameFormat event) {
        //noinspection ConstantValue
        if (FactionConfig.server().factionColorInChat.get() && (!(event.getEntity() instanceof ServerPlayer serverPlayer) || serverPlayer.connection != null)) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(event.getEntity());
            Holder<? extends IFaction<?>> f = handler.factionPlayer().getDisguise().getViewedFaction(Optional.ofNullable(FactionsMod.proxy.getClientPlayer()).map(FactionPlayerHandler::get).map(FactionPlayerHandler::getFaction).orElse(null));

            var title = handler.factionPlayer().getChatDisplay();

            MutableComponent name;

            if (title != null) {
                name = Component.literal("").append(Component.literal("[").withStyle(ChatFormatting.GRAY)).append(title).append(Component.literal("] ").withStyle(ChatFormatting.GRAY)).append(event.getDisplayname());
            } else {
                name = event.getDisplayname().copy();
            }

            event.setDisplayname(name.withStyle(style -> style.withColor((f.value().getChatColor()))));
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
    public void blockDestroyed(BreakBlockEvent event) {
            if (!(event.getLevel() instanceof ServerLevel level)) return;
            //don't allow player to destroy blocks with PointOfInterests that are owned by a totem with different faction as the player
            if (event.getPlayer().isCreative()) return;
            if (FactionConfig.server().villageAllowPoiDestruction.get()) return;

            List<BlockPos> positions = new ArrayList<>();
            List<PoiRecord> inSquare = TotemHelper.findRecords(level, event.getPos()).toList();
            if (!inSquare.isEmpty()) {
                Block block = event.getState().getBlock();
                Optional<PoiRecord> self = inSquare.stream().filter(x -> x.getPos() == event.getPos()).findAny();
                if (self.isPresent()) {
                    positions.add(self.get().getPos());
                } else {
                    positions.addAll(inSquare.stream().map(PoiRecord::getPos).filter(pos -> level.getBlockState(pos).getBlock() == block).toList());
                }
            }

            for (BlockPos position : positions) {
                BlockPos totemPosition = TotemHelper.getTotemPosition(level.dimension(), position);
                if (totemPosition != null && level.isLoaded(totemPosition)) {
                    BlockEntity totem = (event.getLevel().getBlockEntity(totemPosition));
                    if (totem instanceof TotemBlockEntity blockEntity && !IFaction.isNeutral(blockEntity.getControllingFaction()) && !FactionPlayerHandler.get(event.getPlayer()).isInFaction(blockEntity.getControllingFaction())) {
                        event.setCanceled(true);
                        event.getPlayer().sendOverlayMessage(Component.translatable("text.vampirism.village.totem_destroy.fail_totem_faction"));
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
}
