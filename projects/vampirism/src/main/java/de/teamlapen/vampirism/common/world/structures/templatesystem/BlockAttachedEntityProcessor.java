package de.teamlapen.vampirism.common.world.structures.templatesystem;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fixes block attached entities (paintings, item frames, leash knots) placed from a structure template.
 * <ul>
 *     <li>The saved {@code block_pos} is the absolute position from when the template was saved. {@link net.minecraft.world.entity.decoration.BlockAttachedEntity#readAdditionalSaveData}
 *     rejects it with an error log, so we replace it with the transformed position.</li>
 *     <li>Vanilla places entities at their saved center position, and {@link net.minecraft.world.entity.decoration.BlockAttachedEntity#setPos(double, double, double)}
 *     derives the attachment block from that via {@link BlockPos#containing}. For paintings with an even width or height the center lies exactly on a block
 *     boundary, so the attachment block can end up one block off, which makes the painting pop off the wall. The template stores the real attachment block for
 *     paintings in {@link StructureTemplate.StructureEntityInfo#blockPos}, so we use its center instead.</li>
 * </ul>
 */
public class BlockAttachedEntityProcessor extends StructureProcessor {
    public static final BlockAttachedEntityProcessor INSTANCE = new BlockAttachedEntityProcessor();
    public static final MapCodec<BlockAttachedEntityProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public StructureTemplate.@Nullable StructureEntityInfo processEntity(@NotNull LevelReader world, @NotNull BlockPos seedPos, StructureTemplate.@NotNull StructureEntityInfo rawEntityInfo, StructureTemplate.@NotNull StructureEntityInfo entityInfo, @NotNull StructurePlaceSettings placementSettings, @Nullable StructureTemplate template) {
        if (!entityInfo.nbt.contains("block_pos")) {
            return entityInfo;
        }
        CompoundTag nbt = entityInfo.nbt.copy();
        nbt.store("block_pos", BlockPos.CODEC, entityInfo.blockPos);
        Vec3 pos = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PAINTING).toString().equals(nbt.getStringOr("id", "")) ? Vec3.atCenterOf(entityInfo.blockPos) : entityInfo.pos;
        return new StructureTemplate.StructureEntityInfo(pos, entityInfo.blockPos, nbt);
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return ModStructures.BLOCK_ATTACHED_ENTITY.get();
    }
}
