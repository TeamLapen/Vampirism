package de.teamlapen.vampirism.world;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Store all kinds of data which needs to be stored and related to a world
 */
public class VampirismWorldData extends WorldSavedData {

    private static final String IDENTIFIER = "vampirism";

    public static @Nonnull
    VampirismWorldData get(@Nonnull World world) {
        String s = fileNameForProvider(world.dimension);
        DimensionType key = world.getDimension().getType().isVanilla() ? DimensionType.OVERWORLD : world.getDimension().getType();
        VampirismWorldData data = world.getSavedData(key, VampirismWorldData::new, s);//TODO @Maxanier
        if (data == null) {
            data = new VampirismWorldData(world);
            world.setSavedData(key, s, data);
        } else {
            data.world = world;
        }
        return data;
    }

    private static String fileNameForProvider(Dimension provider) {
        return IDENTIFIER + provider.getType().getSuffix();
    }

    private final List<BlockPos> vampireDungeons = Lists.newLinkedList();
    private World world;

    public VampirismWorldData(String name) {
        super(name);
    }

    private VampirismWorldData(World world) {
        this(fileNameForProvider(world.dimension));
        this.world = world;
        this.markDirty();
    }

    /**
     * Register a new vampire dungeon position during world gen
     */
    public void addNewVampireDungeon(@Nonnull BlockPos pos) {
        vampireDungeons.add(pos);
        this.markDirty();
    }

    /**
     * Return a random vampire dungeon
     */
    public @Nullable
    BlockPos getRandomVampireDungeon(Random rng) {
        return vampireDungeons.size() == 0 ? null : vampireDungeons.get(rng.nextInt(vampireDungeons.size()));
    }

    /**
     * Called when an altar of inspiration is destroyed.
     * If ther was a vampire dungeon at this postion it is removed
     */
    public void onAltarInspirationDestroyed(BlockPos pos) {
        if (vampireDungeons.remove(pos)) {
            this.markDirty();
        }
    }

    @Override
    public void read(CompoundNBT nbt) {
        if (nbt.contains("vampire_dungeons")) {
            vampireDungeons.clear();
            ListNBT dungeons = nbt.getList("vampire_dungeons", 10);
            for (int i = 0; i < dungeons.size(); i++) {
                CompoundNBT tag = dungeons.getCompound(i);
                vampireDungeons.add(NBTUtil.readBlockPos(tag));
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT dungeons = new ListNBT();
        for (BlockPos pos : vampireDungeons) {
            dungeons.add(NBTUtil.writeBlockPos(pos));
        }
        compound.put("vampire_dungeons", dungeons);
        return compound;
    }
}
