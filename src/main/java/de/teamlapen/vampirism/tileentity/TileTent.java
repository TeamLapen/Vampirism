package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.SimpleSpawnerLogic;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Tile entity which spawns hunters for tents
 */
public class TileTent extends TileEntity implements ITickable {
    public static IFixableData getTentFixer() {
        return new IFixableData() {
            @Nonnull
            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                String id = compound.getString("id");
                if ("vampirism:vampire_hunter".equals(id) || "vampirism:vampireHunter".equals(id)) { //Fix spawner id overwriting tile entity id

                    compound.setString("id", "vampirism:tent");

                    NBTTagCompound logic = new NBTTagCompound();
                    logic.setString("id", "vampirism:vampire_hunter");
                    logic.setInteger("min_delay", compound.getInteger("min_delay"));
                    logic.setInteger("max_delay", compound.getInteger("max_delay"));
                    logic.setInteger("max_nearby", compound.getInteger("max_nearby"));
                    logic.setInteger("delay", compound.getInteger("delay"));
                    logic.setInteger("activate_range", compound.getInteger("activate_range"));
                    logic.setInteger("spawn_range", compound.getInteger("spawn_range"));
                    logic.setInteger("spawn_count", compound.getInteger("spawn_count"));
                    compound.setTag("spawner_logic", logic);
                    compound.removeTag("min_delay");
                    compound.removeTag("max_delay");
                    compound.removeTag("max_nearby");
                    compound.removeTag("delay");
                    compound.removeTag("activate_range");
                    compound.removeTag("spawn_range");
                    compound.removeTag("spawn_count");

                }


                return compound;
            }

            @Override
            public int getFixVersion() {
                return 4;
            }
        };
    }

    private SimpleSpawnerLogic spawnerLogic = new SimpleSpawnerLogic() {
        @Override
        public BlockPos getSpawnerPosition() {
            return TileTent.this.getPos();
        }

        @Override
        public World getSpawnerWorld() {
            return TileTent.this.world;
        }

        @Override
        protected void onReset() {
            //TileTent.this.worldObj.addBlockEvent(getSpawnerPosition(), ModBlocks.tentMain, 1, 0);
        }

        @Override
        protected void onSpawned(Entity e) {
            super.onSpawned(e);
            if (e instanceof EntityBasicHunter) {
                ((EntityBasicHunter) e).makeCampHunter(getSpawningBox());
            }
        }
    };
    private boolean spawn = false;

    public TileTent() {
        super(ModTiles.tent);
        spawnerLogic.setEntityName(new ResourceLocation(REFERENCE.MODID, ModEntities.BASIC_HUNTER_NAME));
        spawnerLogic.setActivateRange(64);
        spawnerLogic.setSpawnRange(6);
        spawnerLogic.setMinSpawnDelay(600);
        spawnerLogic.setMaxSpawnDelay(1000);
        spawnerLogic.setMaxNearbyEntities(2);
        spawnerLogic.setLimitTotalEntities(VReference.HUNTER_CREATURE_TYPE);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().grow(1, 0, 1);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return null;//new SPacketUpdateTileEntity(this.getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return this.write(new NBTTagCompound());
    }

    @Override
    public boolean hasFastRenderer() {
        return super.hasFastRenderer();
    }

    public boolean isSpawner() {
        return spawn;
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);
        if (nbt.contains("spawner_logic")) {
            spawnerLogic.readFromNbt(nbt.getCompound("spawner_logic"));
        }
        spawn = nbt.getBoolean("spawn");
    }

    public boolean receiveClientEvent(int id, int type) {
        return this.spawnerLogic.setDelayToMin(id) || super.receiveClientEvent(id, type);
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    @Override
    public void update() {
        if (spawnerLogic.getSpawnedToday() >= Balance.general.HUNTER_CAMP_MAX_SPAWN) {
            spawnerLogic.setSpawn(false);
        }
        if (spawn) {
            spawnerLogic.updateSpawner();
            if (!this.world.isRemote && this.world.getGameTime() % 64 == 0) {
                if (this.world.villageCollection.getNearestVillage(this.pos, 5) != null) {
                    this.spawn = false; //Disable spawning inside villages
                }
            }
        }
    }


    @Nonnull
    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        NBTTagCompound nbt = super.write(compound);
        NBTTagCompound logic = new NBTTagCompound();
        spawnerLogic.writeToNbt(logic);
        nbt.put("spawner_logic", logic);
        nbt.putBoolean("spawn", spawn);
        return nbt;
    }
}
