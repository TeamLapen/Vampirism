package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.util.SpawnHelper;
import de.teamlapen.vampirism.blocks.mother.MotherTreeStructure;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.VulnerableRemainsDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class VulnerableRemainsBlockEntity extends BlockEntity {

    private final static int MAX_HEALTH = 100;

    public static void serverTick(ServerLevel level, BlockPos blockPos, BlockState blockState, VulnerableRemainsBlockEntity e) {
        if (e.firstTick) {
            e.firstTick = false;
            e.getMother().ifPresent(mother -> {
                mother.updateFightStatus();
                e.checkDummyEntity(level, blockPos);
            });
        } else if (level.getRandom().nextInt(100) == 3) {
            e.checkDummyEntity(level, blockPos);
            if (e.lastDamage - level.getGameTime() > 3 * 60 * 20L) {
                e.health = Math.min(e.health + 10, MAX_HEALTH);
            }
        }
    }
    private BlockPos motherPos;
    private int health = MAX_HEALTH;
    @Nullable
    private UUID dummy_entity_id;

    public VulnerableRemainsBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.VULNERABLE_CURSED_ROOTED_DIRT.get(), pos, state);
    }
    private long lastDamage = 0;

    private void destroyVulnerability() {
        this.level.setBlockAndUpdate(this.worldPosition, ModBlocks.INCAPACITATED_VULNERABLE_REMAINS.get().defaultBlockState());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.health = tag.getInt("health");
        this.lastDamage = tag.getLong("lastDamage");
        if (tag.contains("motherPos")) {
            int[] pos = tag.getIntArray("motherPos");
            this.motherPos = new BlockPos(pos[0], pos[1], pos[2]);
        }
        if (tag.contains("dummy_entity_id", Tag.TAG_INT_ARRAY)) {
            this.dummy_entity_id = tag.getUUID("dummy_entity_id");
        }
    }

    public void onDamageDealt(DamageSource src, double damage) {
        this.health -= (int) damage;
        if (this.level != null) {
            this.lastDamage = this.level.getGameTime();
            this.level.playSound(null, worldPosition, health > 0 ? ModSounds.REMAINS_HIT.get() : ModSounds.REMAINS_DESTROYED.get(), SoundSource.BLOCKS, 1f, 1f);
        }
        if (this.health <= 0) {
            destroyVulnerability();
        }
        if (src.getEntity() instanceof LivingEntity entity) {
            this.getMother().ifPresent(mother -> mother.onVulnerabilityHit(entity, this.health <= 0));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("health", this.health);
        tag.putLong("lastDamage", this.lastDamage);
        if (this.motherPos != null) {
            tag.putIntArray("motherPos", new int[]{this.motherPos.getX(), this.motherPos.getY(), this.motherPos.getZ()});
        }
        if (this.dummy_entity_id != null) {
            tag.putUUID("dummy_entity_id", this.dummy_entity_id);
        }
    }

    private void checkDummyEntity(ServerLevel level, BlockPos blockPos) {
        if (dummy_entity_id != null) {
            Entity e = level.getEntity(dummy_entity_id);
            if (e instanceof VulnerableRemainsDummyEntity) {
                return;
            }
            dummy_entity_id = null;
        }
        SpawnHelper.createEntity(level, ModEntities.VULNERABLE_REMAINS_DUMMY, blockPos.getCenter().add(0, -0.51f, 0)).ifPresent(entity -> {
            entity.setOwnerLocation(blockPos);
            level.addFreshEntity(entity);
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

}
