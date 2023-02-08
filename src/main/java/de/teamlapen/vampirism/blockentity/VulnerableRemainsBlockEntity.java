package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.blocks.mother.ActiveVulnerableRemainsBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.VulnerableRemainsDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VulnerableRemainsBlockEntity extends BlockEntity {

    private int health = 50;
    private BlockPos motherPos;

    private int dummy_entity_id;

    public VulnerableRemainsBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.VULNERABLE_CURSED_ROOTED_DIRT.get(), pos, state);
    }


    private Optional<MotherBlockEntity> getMother() {
        if (motherPos == null) {
            ((ActiveVulnerableRemainsBlock) getBlockState().getBlock()).getConnector().getMother(this.level, this.worldPosition).ifPresentOrElse(pos -> {
                motherPos = pos.getKey();
            }, () -> {
                this.level.setBlockAndUpdate(this.worldPosition, ModBlocks.REMAINS.get().defaultBlockState());
            });
        }
        return Optional.ofNullable(motherPos).map(pos -> {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MotherBlockEntity mother) {
                return mother;
            }
            return null;
        });
    }

    private void destroyVulnerability() {
        this.level.setBlockAndUpdate(this.worldPosition, ModBlocks.INCAPACITATED_VULNERABLE_REMAINS.get().defaultBlockState());
    }

    public void attacked(@NotNull BlockState state, @NotNull ServerPlayer player) {

    }

    public void onDamageDealt(DamageSource src, double damage) {
        this.health -= damage;
        if (this.health <= 0) {
            destroyVulnerability();
        }
        if (src.getEntity() instanceof ServerPlayer p) {
            this.getMother().ifPresent(mother -> mother.onVulnerabilityHit(p, this.health <= 0));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("health", this.health);
        if (this.motherPos != null) {
            tag.putIntArray("motherPos", new int[]{this.motherPos.getX(), this.motherPos.getY(), this.motherPos.getZ()});
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.health = tag.getInt("health");
        if (tag.contains("motherPos")) {
            int[] pos = tag.getIntArray("motherPos");
            this.motherPos = new BlockPos(pos[0], pos[1], pos[2]);
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

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, VulnerableRemainsBlockEntity e) {
        if (e.firstTick) {
            e.firstTick = false;
            Optional<MotherBlockEntity> motherOpt = e.getMother();
            motherOpt.ifPresent(MotherBlockEntity::updateFightStatus);
            motherOpt.ifPresent(mother -> {
                mother.updateFightStatus();
                Entity e2 = e.checkDummyEntity();
                //level.getNearbyPlayers(TargetingConditions.DEFAULT, e2, AABB.ofSize(blockPos.getCenter(), 5, 5, 5)).forEach(mother::addPlayer);
            });
        }
    }

}
