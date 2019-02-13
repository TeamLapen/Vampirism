package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.hunter.EntityHunterBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.List;

public class GarlicAOFEntityAction<T extends EntityHunterBase & IHunter & IAdjustableLevel> extends DefaultEntityAction implements ILastingAction<T> {

    @Override
    public int getCooldown(int level) {
        // TODO Auto-generated method stub
        return 100;
    }

    @Override
    public int getDuration(int level) {
        // TODO Auto-generated method stub
        return 40;
    }

    @Override
    public void deactivate(T entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpdate(T entity, int duration) {
        List<EntityPlayer> players = entity.getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.posX - 4, entity.posY - 1, entity.posZ - 4, entity.posX + 4, entity.posY + 3, entity.posZ + 4));
        for (EntityPlayer e : players) {
            if (VampirismAPI.factionRegistry().getFaction(e) == VReference.VAMPIRE_FACTION) {
                if (e.getActivePotionEffect(ModPotions.garlic) == null || e.getActivePotionEffect(ModPotions.garlic).getDuration() <= 60) {
                    e.addPotionEffect(new PotionEffect(ModPotions.garlic, 99));
                }
            }
        }
    }

    @Override
    public void activate(T entity) {
    }

    @Override
    public void updatePreAction(T entity, int duration) {
    }

}
