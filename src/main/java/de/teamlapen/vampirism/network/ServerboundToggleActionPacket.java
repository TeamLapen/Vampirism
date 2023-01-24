package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @param actionId the id of the action that was toggled
 * @param target   The target the player was looking at when activating the action.
 */
public record ServerboundToggleActionPacket(ResourceLocation actionId, @Nullable Either<Integer, BlockPos> target) implements IMessage.IServerBoundMessage {
    private static final Logger LOGGER = LogManager.getLogger();

    public static @NotNull ServerboundToggleActionPacket createFromRaytrace(ResourceLocation action, @Nullable HitResult traceResult) {
        if (traceResult != null) {
            if (traceResult.getType() == HitResult.Type.ENTITY) {
                return new ServerboundToggleActionPacket(action, ((EntityHitResult) traceResult).getEntity().getId());
            } else if (traceResult.getType() == HitResult.Type.BLOCK) {
                return new ServerboundToggleActionPacket(action, ((BlockHitResult) traceResult).getBlockPos());
            }
        }
        return new ServerboundToggleActionPacket(action);
    }

    static void encode(@NotNull ServerboundToggleActionPacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.actionId);
        if (msg.target != null) {
            buf.writeBoolean(true);
            msg.target.ifLeft(i -> {
                buf.writeBoolean(true);
                buf.writeVarInt(i);
            });
            msg.target.ifRight(p -> {
                buf.writeBoolean(false);
                buf.writeBlockPos(p);
            });
        } else {
            buf.writeBoolean(false);
        }
    }

    static @NotNull ServerboundToggleActionPacket decode(@NotNull FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        Either<Integer, BlockPos> target = null;
        if (buf.readBoolean()) {
            if (buf.readBoolean()) {
                target = Either.left(buf.readVarInt());
            } else {
                target = Either.right(buf.readBlockPos());
            }
        }

        return new ServerboundToggleActionPacket(id, target);
    }

    static void handle(@NotNull ServerboundToggleActionPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayer player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty);
            factionPlayerOpt.ifPresent(factionPlayer -> {
                IAction.ActivationContext context = msg.target != null ? msg.target.map(entityId -> {
                    Entity e = player.getCommandSenderWorld().getEntity(entityId);
                    if (e == null) {
                        LOGGER.warn("Could not find entity {} the player was looking at when toggling action", entityId);
                    }
                    return new ActionHandler.ActivationContext(e);
                }, ActionHandler.ActivationContext::new) : new ActionHandler.ActivationContext();

                IActionHandler<?> actionHandler = factionPlayer.getActionHandler();
                IAction action = RegUtil.getAction(msg.actionId);
                if (action != null) {
                    IAction.PERM r = actionHandler.toggleAction(action, context);
                    switch (r) {
                        case NOT_UNLOCKED -> player.displayClientMessage(Component.translatable("text.vampirism.action.not_unlocked"), true);
                        case DISABLED -> player.displayClientMessage(Component.translatable("text.vampirism.action.deactivated_by_serveradmin"), false);
                        case COOLDOWN -> player.displayClientMessage(Component.translatable("text.vampirism.action.cooldown_not_over"), true);
                        case DISALLOWED -> player.displayClientMessage(Component.translatable("text.vampirism.action.disallowed"), true);
                        case PERMISSION_DISALLOWED -> player.displayClientMessage(Component.translatable("text.vampirism.action.permission_disallowed"), false);
                        default -> {
                            //Everything alright
                        }
                    }
                } else {
                    LOGGER.error("Failed to find action with id {}", msg.actionId);
                }
            });
        });
        ctx.setPacketHandled(true);
    }

    public ServerboundToggleActionPacket(ResourceLocation actionId, @Nullable Integer target) {
        this(actionId, Either.left(target));
    }

    public ServerboundToggleActionPacket(ResourceLocation actionId, @Nullable BlockPos target) {
        this(actionId, Either.right(target));
    }

    public ServerboundToggleActionPacket(ResourceLocation actionId) {
        this(actionId, (Either<Integer, BlockPos>) null);
    }
}
