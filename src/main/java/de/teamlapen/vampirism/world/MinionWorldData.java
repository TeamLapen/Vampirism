package de.teamlapen.vampirism.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MinionWorldData extends WorldSavedData {
    private final static String ID = "vampirism-minion-data";

    @Nullable
    public static MinionWorldData getData(World world) {
        if (world instanceof ServerWorld) {
            return getData(((ServerWorld) world).getWorldServer());
        }
        return null;
    }

    @Nonnull
    public static MinionWorldData getData(MinecraftServer server) {
        return server.getWorld(DimensionType.OVERWORLD).getSavedData().getOrCreate(MinionWorldData::new, ID);
    }

    public MinionWorldData() {
        super(ID);
    }

    @Override
    public void read(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return compound;
    }
}
