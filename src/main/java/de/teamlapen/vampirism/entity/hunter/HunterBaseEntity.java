package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Base class for all vampire hunter
 */
@SuppressWarnings("EntityConstructor")
public abstract class HunterBaseEntity extends VampirismEntity implements IHunterMob {

    public static boolean spawnPredicateHunter(EntityType<? extends HunterBaseEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && spawnPredicateCanSpawn(entityType, world, spawnReason, blockPos, random);
    }
    private final boolean countAsMonster;

    public HunterBaseEntity(EntityType<? extends HunterBaseEntity> type, World world, boolean countAsMonster) {
        super(type, world);
        this.countAsMonster = countAsMonster;
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
    }

    @Override
    public EntityClassification getClassification(boolean forSpawnCount) {
        if (forSpawnCount && countAsMonster) {
            return EntityClassification.MONSTER;
        }
        return super.getClassification(forSpawnCount);
    }

    /**
     * Tries to cure sanguinare and if successful sends a message.
     *
     * @return If player was cured
     */
    protected boolean tryCureSanguinare(PlayerEntity entity) {
        if (!this.world.isRemote && entity.isPotionActive(ModEffects.sanguinare)) {
            entity.removePotionEffect(ModEffects.sanguinare);
            entity.sendMessage(new TranslationTextComponent("text.vampirism.hunter.cured_sanguinare"));
            return true;
        }
        return false;
    }
}
