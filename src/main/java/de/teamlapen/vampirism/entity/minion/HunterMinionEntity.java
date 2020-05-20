package de.teamlapen.vampirism.entity.minion;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;


public class HunterMinionEntity extends MinionEntity<MinionData> implements IHunter {
    public HunterMinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }
}
