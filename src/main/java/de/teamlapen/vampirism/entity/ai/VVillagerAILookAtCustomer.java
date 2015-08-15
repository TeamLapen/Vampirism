package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.convertible.EntityConvertedVillager;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;

public class VVillagerAILookAtCustomer extends EntityAIWatchClosest {
    protected final EntityConvertedVillager villager;

    public VVillagerAILookAtCustomer(EntityConvertedVillager villager) {
        super(villager, EntityPlayer.class, 0.8F);
        this.villager = villager;
    }

    @Override
    public boolean shouldExecute() {
        if (villager.isTrading()) {
            this.closestEntity = this.villager.getCustomer();
            return true;
        }
        return false;
    }
}
