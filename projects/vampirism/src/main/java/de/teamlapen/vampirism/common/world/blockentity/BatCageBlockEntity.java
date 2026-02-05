package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.faction.common.world.blockentity.NetworkedBlockEntity;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class BatCageBlockEntity extends NetworkedBlockEntity {

    public static final String KEY_ENTITY_INSIDE = "EntityInside";

    private @Nullable CompoundTag entityTag;

    public BatCageBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BAT_CAGE.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.entityTag = input.read(KEY_ENTITY_INSIDE, CompoundTag.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.entityTag != null) {
            output.store(KEY_ENTITY_INSIDE, CompoundTag.CODEC, this.entityTag);
        }
    }

    public void setEntity(Entity entity) {
        if (this.level != null && this.level.isClientSide()) return;

        this.entityTag = serializeEntity(entity);
        if (this.entityTag != null) {
            this.entityTag = sanitizeEntityTag(this.entityTag);
        }

        entity.remove(Entity.RemovalReason.DISCARDED);
        setChanged();
    }

    public static @Nullable CompoundTag serializeEntity(Entity entity) {
        if (entity.isRemoved() || entity.level().isClientSide()) {
            return null;
        }

        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);

        if (entity.save(output)) {
            return output.buildResult();
        }

        return null;
    }

    public static CompoundTag sanitizeEntityTag(CompoundTag tag) {
        tag = tag.copy();
        Stream.of(
                "UUID",
                "Pos",
                "Motion",
                "Rotation",
                "FallDistance",
                "Fire",
                "Air",
                "OnGround",
                "Dimension",
                "PortalCooldown",
                "Leash"
        ).forEach(tag::remove);

        return tag;
    }

    public void setEntityTag(@Nullable CompoundTag entityTag) {
        if (this.level != null && this.level.isClientSide()) return;

        this.entityTag = entityTag;
        setChanged();
    }

    public boolean hasEntity() {
        return this.entityTag != null;
    }

    public @Nullable CompoundTag getEntityTag() {
        return entityTag;
    }

    public static boolean canContainEntity(Entity entity) {
        return entity instanceof Bat;
    }
}
