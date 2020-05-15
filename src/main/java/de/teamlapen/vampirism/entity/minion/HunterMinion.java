package de.teamlapen.vampirism.entity.minion;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;


public class HunterMinion extends MinionEntity implements IHunter {
    public HunterMinion(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }
}
