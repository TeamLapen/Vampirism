package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.VulnerableRemainsDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VulnerableRemainsBlockEntity extends BlockEntity {

    private int invulnerableTicks;
    private int health = 5;
    private BlockPos motherPos;

    private int dummy_entity_id;

    public VulnerableRemainsBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.VULNERABLE_CURSED_ROOTED_DIRT.get(), pos, state);
    }


    private Optional<MotherBlockEntity> getMother() {
        if (motherPos == null) {
            ((ServerLevel) this.level).getPoiManager().find((poi) -> poi.is(ModVillage.MOTHER.getKey()), (pos) -> true, this.worldPosition, 10, PoiManager.Occupancy.ANY).ifPresent((pos) -> {
                motherPos = pos;
            });
            if (motherPos == null) {
                this.level.setBlockAndUpdate(this.worldPosition, ModBlocks.REMAINS.get().defaultBlockState());
            }
        }
        return Optional.ofNullable(motherPos).map(pos -> {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MotherBlockEntity mother) {
                return mother;
            }
            return null;
        });
    }

    private void finish() {
        this.level.setBlockAndUpdate(this.worldPosition, ModBlocks.INCAPACITATED_VULNERABLE_REMAINS.get().defaultBlockState());
        getMother().ifPresent(MotherBlockEntity::updateFightStatus);
    }

    public void attacked(@NotNull BlockState state, @NotNull ServerPlayer player) {

    }

    public void onDamageDealt(DamageSource src, double damage) {
        if (src.getEntity() instanceof ServerPlayer p) {
            this.getMother().ifPresent(mother -> mother.addPlayer(p));
        }
        if (health-- <= 0) {
            finish();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("health", this.health);
        tag.putInt("invulnerableTicks", this.invulnerableTicks);
        if (this.motherPos != null) {
            tag.putIntArray("motherPos", new int[]{this.motherPos.getX(), this.motherPos.getY(), this.motherPos.getZ()});
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.health = tag.getInt("health");
        this.invulnerableTicks = tag.getInt("invulnerableTicks");
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
            e.getMother().ifPresent(MotherBlockEntity::updateFightStatus);
            e.getMother().ifPresent(mother -> {
                mother.updateFightStatus();
                Entity e2 = e.checkDummyEntity();
                //level.getNearbyPlayers(TargetingConditions.DEFAULT, e2, AABB.ofSize(blockPos.getCenter(), 5, 5, 5)).forEach(mother::addPlayer);
            });
        }
    }

}
