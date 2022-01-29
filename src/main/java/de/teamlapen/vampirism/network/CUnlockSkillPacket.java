package de.teamlapen.vampirism.network;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;


public class CUnlockSkillPacket implements IMessage {
    private static final Logger LOGGER = LogManager.getLogger();

    static void encode(CUnlockSkillPacket msg, PacketBuffer buf) {
        buf.writeResourceLocation(msg.skillId);
    }

    static CUnlockSkillPacket decode(PacketBuffer buf) {
        return new CUnlockSkillPacket(buf.readResourceLocation());
    }

    static void handle(CUnlockSkillPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayerEntity player = ctx.getSender();
        Validate.notNull(player);
        ctx.enqueueWork(() -> {
            Optional<? extends IFactionPlayer<?>> factionPlayerOpt = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty);
            factionPlayerOpt.ifPresent(factionPlayer -> {
                ISkill skill = ModRegistries.SKILLS.getValue(msg.skillId);
                if (skill != null) {
                    ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                    ISkillHandler.Result result = skillHandler.canSkillBeEnabled(skill);
                    if (result == ISkillHandler.Result.OK) {
                        skillHandler.enableSkill(skill);
                        if (factionPlayer instanceof ISyncable.ISyncableEntityCapabilityInst && skillHandler instanceof SkillHandler) {
                            //does this cause problems with addons?
                            CompoundNBT sync = new CompoundNBT();
                            ((SkillHandler<?>) skillHandler).writeUpdateForClient(sync);
                            HelperLib.sync((ISyncable.ISyncableEntityCapabilityInst) factionPlayer, sync, factionPlayer.getRepresentingPlayer(), false);
                        }

                    } else {
                        LOGGER.warn("Skill {} cannot be activated for {} ({})", skill, player, result);
                    }
                } else {
                    LOGGER.warn("Skill {} was not found so {} cannot activate it", msg.skillId, player);
                }
            });
        });
        ctx.setPacketHandled(true);

    }

    private final ResourceLocation skillId;

    public CUnlockSkillPacket(ResourceLocation skillId) {
        this.skillId = skillId;
    }
}
