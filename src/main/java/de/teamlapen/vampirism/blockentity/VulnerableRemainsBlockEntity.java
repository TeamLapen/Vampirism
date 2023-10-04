package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.blocks.mother.MotherTreeStructure;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.VulnerableRemainsDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VulnerableRemainsBlockEntity extends BlockEntity {

    private final static int MAX_HEALTH = 50;

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, VulnerableRemainsBlockEntity e) {
        if (e.firstTick) {
            e.firstTick = false;
            e.getMother().ifPresent(mother -> {
                mother.updateFightStatus();
                e.checkDummyEntity();
            });
        } else if (level.getRandom().nextInt(100) == 3) {
            e.checkDummyEntity();
            if (e.lastDamage - level.getGameTime() > 3 * 60 * 20L) {
                e.health = Math.min(e.health + 10, MAX_HEALTH);
            }
        }
    }
    private BlockPos motherPos;
    private int health = MAX_HEALTH;

    private int dummy_entity_id;

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
    }

    public void onDamageDealt(DamageSource src, double damage) {
        if (this.health <= 0) {
            if (this.level != null) {
                this.level.playSound(null, worldPosition, ModSounds.REMAINS_DESTROYED.get(), SoundSource.BLOCKS, 1f, 1f);
            }
            destroyVulnerability();
        } else {
            this.health -= (int) damage;
            if (this.level != null) {
                this.lastDamage = this.level.getGameTime();
                this.level.playSound(null, worldPosition, ModSounds.REMAINS_HIT.get(), SoundSource.BLOCKS, 1f, 1f);
            }
            if (src.getEntity() instanceof ServerPlayer p) {
                this.getMother().ifPresent(mother -> mother.onVulnerabilityHit(p, this.health <= 0));
            }
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
    }

    private VulnerableRemainsDummyEntity checkDummyEntity() {
        if (dummy_entity_id != 0) {
            Entity e = this.level.getEntity(dummy_entity_id);
            if (e instanceof VulnerableRemainsDummyEntity de) {
                return de;
            }
            dummy_entity_id = 0;
        }
        VulnerableRemainsDummyEntity e = ModEntities.VULNERABLE_REMAINS_DUMMY.get().create(this.level);
        e.setPos(this.getBlockPos().getCenter().add(0, -0.51f, 0));
        e.setOwnerLocation(this.getBlockPos());

        this.level.addFreshEntity(e);
        this.dummy_entity_id = e.getId();
        return e;

    }

    private boolean firstTick = true;

    public int getHealth() {
        return health;
    }

    private Optional<MotherBlockEntity> getMother() {
        if (this.level != null) {
            return MotherTreeStructure.findMother(this.level, this.getBlockPos()).map(p -> this.level.getBlockEntity(p.getKey())).filter(e -> e instanceof MotherBlockEntity).map(e -> (MotherBlockEntity) e);
        }
        return Optional.empty();
    }

}
