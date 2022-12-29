package de.teamlapen.vampirism.network;

import com.mojang.datafixers.util.Either;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class CToggleActionPacket implements IMessage {
    private static final Logger LOGGER = LogManager.getLogger();

    public static CToggleActionPacket createFromRaytrace(ResourceLocation action, RayTraceResult traceResult) {
        Either<Integer, BlockPos> target = null;
        if (traceResult != null) {
            if (traceResult.getType() == RayTraceResult.Type.ENTITY) {
                target = Either.left(((EntityRayTraceResult) traceResult).getEntity().getId());
            } else if (traceResult.getType() == RayTraceResult.Type.BLOCK) {
                target = Either.right(((BlockRayTraceResult) traceResult).getBlockPos());
            }
        }
        return new CToggleActionPacket(action, target);
    }

    static void encode(CToggleActionPacket msg, PacketBuffer buf) {
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

    static CToggleActionPacket decode(PacketBuffer buf) {
        ResourceLocation id = buf.readResourceLocation();
        Either<Integer, BlockPos> target = null;
        if (buf.readBoolean()) {
            if (buf.readBoolean()) {
                target = Either.left(buf.readVarInt());
            } else {
                target = Either.right(buf.readBlockPos());
            }
        }

        return new CToggleActionPacket(id, target);
    }

    static void handle(CToggleActionPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayerEntity player = ctx.getSender();
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
                IAction action = ModRegistries.ACTIONS.getValue(msg.actionId);
                if (action != null) {
                    IAction.PERM r = actionHandler.toggleAction(action, context);
                    switch (r) {
                        case NOT_UNLOCKED:
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.action.not_unlocked"), true);
                            break;
                        case DISABLED:
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.action.deactivated_by_serveradmin"), false);
                            break;
                        case COOLDOWN:
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.action.cooldown_not_over"), true);
                            break;
                        case DISALLOWED:
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.action.disallowed"), true);
                            break;
                        case PERMISSION_DISALLOWED:
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.action.permission_disallowed"), false);
                            break;
                        default://Everything alright
                    }
                } else {
                    LOGGER.error("Failed to find action with id {}", msg.actionId);
                }
            });
        });
        ctx.setPacketHandled(true);
    }

    private final ResourceLocation actionId;

    /**
     * The target the player was looking at when activating the action.
     */
    @Nullable
    private final Either<Integer, BlockPos> target;

    public CToggleActionPacket(ResourceLocation actionId, @Nullable Either<Integer, BlockPos> target) {
        this.actionId = actionId;
        this.target = target;
    }
}
