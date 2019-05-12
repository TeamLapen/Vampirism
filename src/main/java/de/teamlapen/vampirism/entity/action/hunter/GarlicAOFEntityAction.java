package de.teamlapen.vampirism.entity.action.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class GarlicAOFEntityAction<T extends EntityCreature & IEntityActionUser> extends HunterEntityAction<T> implements ILastingAction<T> {

    public GarlicAOFEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.GARLIC_COOLDOWN * 20;
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.GARLIC_DURATION * 20;
    }

    @Override
    public void deactivate(T entity) {
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

}
