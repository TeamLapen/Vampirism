package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.world.attachments.ModDamageSources;
import de.teamlapen.vampirism.common.world.blocks.VelmorraAltarBlock;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraDimension;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaFightData;
import de.teamlapen.vampirism.common.world.portal.VelmorraPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VelmorraAltarBlockEntity extends BlockEntity {

    public VelmorraAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.VELMORRA_ALTAR.get(), pos, blockState);
    }

    public boolean offerBlood(Player player) {
        if (!(this.level instanceof ServerLevel serverLevel)){
            return false;
        }

        DamageHandler.hurtModded(serverLevel, player, ModDamageSources::ritualKnife, 7.5f);
        updateBlocks();

        FactionPlayerHandler factionPlayerHandler = FactionPlayerHandler.get(player);
        IPlayableFaction<?> faction = factionPlayerHandler.getFaction().value();
        if (factionPlayerHandler.getPlayerLord().map(ILordPlayer::getLordLevel).orElse(0) != faction.getHighestLordLevel()) {
            player.sendOverlayMessage(Component.translatable("message.vampirism.velmorra_altar.weak"));
            return false;
        }

        activatePortal(serverLevel);
        return true;
    }

    private void updateBlocks() {
        level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(VelmorraAltarBlock.HAS_BLOOD, true), 3);
    }

    private void activatePortal(ServerLevel level) {
        VelmorraPortalShape.findEmptyPortalShape(level, worldPosition)
                .ifPresent(x -> x.activate(level));
    }
}
