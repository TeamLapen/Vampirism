package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

/**
 * Summon bat skill
 */
public class SummonBatVampireAction extends DefaultVampireAction {

    public SummonBatVampireAction() {
        super(null);
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer player) {
        return player.getActionHandler().isActionActive(VampireActions.batAction);
    }

    @Override
    public int getCooldown() {
        return Balance.vpa.SUMMON_BAT_COOLDOWN * 20;
    }

    @Override
    public int getMinU() {
        return 96;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getUnlocalizedName() {
        return "action.vampirism.vampire.summon_bats";
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.SUMMON_BAT_ENABLED;
    }

    @Override
    public boolean onActivated(IVampirePlayer player) {
        for (int i = 0; i < Balance.vpa.SUMMON_BAT_COUNT; i++) {
            Entity e = EntityList.createEntityByName(ModEntities.BLINDING_BAT_NAME, player.getRepresentingPlayer().worldObj);
            ((EntityBlindingBat) e).restrictLiveSpan();
            e.copyLocationAndAnglesFrom(player.getRepresentingPlayer());
            player.getRepresentingPlayer().worldObj.spawnEntityInWorld(e);
        }
        return true;
    }
}
