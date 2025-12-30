package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.faction.common.util.SpawnUtil;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.blocks.mother.MotherTreeStructure;
import de.teamlapen.vampirism.common.world.entity.VulnerableRemainsDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class VulnerableRemainsBlockEntity extends BlockEntity {

    public final static int MAX_HEALTH = 100;

    public static void serverTick(ServerLevel level, BlockPos blockPos, BlockState blockState, VulnerableRemainsBlockEntity e) {
        if (e.firstTick) {
            e.firstTick = false;
            e.getMother().ifPresent(mother -> {
                mother.updateFightStatus();
                e.checkDummyEntity(level, blockPos);
            });
        } else if (level.getGameTime() % 128 == 3) {
            e.checkDummyEntity(level, blockPos);
            if (e.lastDamage - level.getGameTime() > 3 * 60 * 20L) {
                e.health = Math.min(e.health + 10, MAX_HEALTH);
                if (e.health == MAX_HEALTH) {
                    e.spawnedBackup = false;
                }
            }
        }
    }

    private BlockPos motherPos;
    private int health = MAX_HEALTH;
    @Nullable
    private UUID dummy_entity_id;
    private boolean spawnedBackup;

    public VulnerableRemainsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VULNERABLE_CURSED_ROOTED_DIRT.get(), pos, state);
    }

    private long lastDamage = 0;

    private void destroyVulnerability() {
        this.level.setBlockAndUpdate(this.worldPosition, ModBlocks.INCAPACITATED_VULNERABLE_REMAINS.get().defaultBlockState());
    }

    public void onDamageDealt(DamageSource src, double damage) {
        this.health -= (int) damage;
        if (this.level != null) {
            this.lastDamage = this.level.getGameTime();
        }
        if (this.health <= 0) {
            destroyVulnerability();
        }
        if (src.getEntity() instanceof LivingEntity entity) {
            this.getMother().ifPresent(mother -> mother.onVulnerabilityHit(entity, this.health <= 0));
        }
        if (!this.spawnedBackup && this.health <= MAX_HEALTH / 2 && this.level instanceof ServerLevel serverLevel) {
            checkDummyEntity(serverLevel, this.worldPosition).spawnDefenders();
            this.spawnedBackup = true;
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.health = input.getIntOr("health", 0);
        this.lastDamage = input.getLongOr("lastDamage", 0);
        this.motherPos = input.read("motherPos", BlockPos.CODEC).orElse(null);
        this.dummy_entity_id = input.read("dummy_entity_id", UUIDUtil.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("health", this.health);
        output.putLong("lastDamage", this.lastDamage);
        if (this.motherPos != null) {
            output.store("motherPos", BlockPos.CODEC, this.motherPos);
        }
        if (this.dummy_entity_id != null) {
            output.store("dummy_entity_id", UUIDUtil.CODEC, this.dummy_entity_id);
        }
    }

    private VulnerableRemainsDummyEntity checkDummyEntity(ServerLevel level, BlockPos blockPos) {
        if (dummy_entity_id != null) {
            Entity e = level.getEntity(dummy_entity_id);
            if (e instanceof VulnerableRemainsDummyEntity dummy) {
                return dummy;
            }
            dummy_entity_id = null;
        }
        return SpawnUtil.spawn(ModEntities.VULNERABLE_REMAINS_DUMMY, level, EntitySpawnReason.STRUCTURE, entity -> {
            entity.setPos(Vec3.atCenterOf(blockPos).add(0, -0.51f, 0));
            entity.setOwnerLocation(blockPos);
            this.dummy_entity_id = entity.getUUID();
        });
    }

    private boolean firstTick = true;

    public int getHealth() {
        return health;
    }

    public Optional<MotherBlockEntity> getMother() {
        if (this.level != null) {
            return MotherTreeStructure.findMother(this.level, this.getBlockPos()).map(p -> this.level.getBlockEntity(p.getKey())).filter(MotherBlockEntity.class::isInstance).map(MotherBlockEntity.class::cast);
        }
        return Optional.empty();
    }

    public void checkNeighbor(BlockPos pNeighborPos) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getBlockState(pNeighborPos).isAir()) {
            Direction direction = UtilLib.getDirection(getBlockPos(), pNeighborPos);
            if (direction != null) {
                checkDummyEntity(serverLevel, getBlockPos()).spawnDefender(direction.getOpposite());
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.dummy_entity_id != null && this.level instanceof ServerLevel serverLevel) {
            Optional.ofNullable(serverLevel.getEntity(dummy_entity_id)).ifPresent(entity -> {
                entity.playSound(((VulnerableRemainsDummyEntity) entity).getDeathSound());
                entity.remove(Entity.RemovalReason.DISCARDED);
            });
        }
    }

    public void onPlaced() {
        if (this.level instanceof ServerLevel serverLevel) {
            checkDummyEntity(serverLevel, getBlockPos()).spawnDefenders();
        }
    }
}
