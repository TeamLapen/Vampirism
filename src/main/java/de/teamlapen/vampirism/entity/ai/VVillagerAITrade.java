package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.convertible.EntityConvertedVillager;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class VVillagerAITrade extends EntityAIBase {

    private EntityConvertedVillager villager;
    private static final String __OBFID = "CL_00001617";

    public VVillagerAITrade(EntityConvertedVillager p_i1658_1_) {
        this.villager = p_i1658_1_;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if (!this.villager.isEntityAlive()) {
            return false;
        } else if (this.villager.isInWater()) {
            return false;
        } else if (!this.villager.onGround) {
            return false;
        } else if (this.villager.velocityChanged) {
            return false;
        } else {
            EntityPlayer entityplayer = this.villager.getCustomer();
            return entityplayer == null ? false : (this.villager.getDistanceSqToEntity(entityplayer) > 16.0D ? false : entityplayer.openContainer instanceof Container);
        }
    }

    public void startExecuting() {
        this.villager.getNavigator().clearPathEntity();
    }

    public void resetTask() {
        this.villager.setCustomer(null);
    }
}
