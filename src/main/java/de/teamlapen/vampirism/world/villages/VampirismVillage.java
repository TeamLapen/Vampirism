package de.teamlapen.vampirism.world.villages;

import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.IAggressiveVillager;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.event.VampirismVillageEvent;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import de.teamlapen.vampirism.entity.hunter.EntityHunterVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Vampirism's instance of a village
 */
public class VampirismVillage implements IVampirismVillage {


    @CapabilityInject(IVampirismVillage.class)
    public final static Capability<IVampirismVillage> CAP = null;

    public static VampirismVillage get(Village v) {
        return (VampirismVillage) v.getCapability(CAP, null);
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IVampirismVillage.class, new VampirismVillage.Storage(), VampirismVillageDefaultImpl.class);
    }

    @SuppressWarnings("ConstantConditions")
    public static ICapabilityProvider createNewCapability(final Village village) {
        return new ICapabilitySerializable<NBTTagCompound>() {

            IVampirismVillage inst = new VampirismVillage(village);

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {

                return CAP.equals(capability) ? CAP.<T>cast(inst) : null;
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
                return CAP.equals(capability);
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private final String TAG = "VampirismVillage";
    private final Village village;
    private BlockPos center = new BlockPos(0, 0, 0);
    private int recentlyBitten;
    private int recentlyConverted;
    private boolean agressive;
    private List<VillageAggressorVampire> villageAggressorVampires = Lists.newArrayList();
    /**
     * Currently unused
     */
    private boolean dirty;
    private int recentlyBittenToDeath;
    private int tickCounter;

    public VampirismVillage(Village village) {
        this.village = village;
    }

    @Override
    public
    @Nullable
    IVampire findNearestVillageAggressor(@Nonnull EntityLivingBase entityCenter) {
        double d0 = Double.MAX_VALUE;
        VillageAggressorVampire aggressorVampire = null;

        for (VillageAggressorVampire vampire : this.villageAggressorVampires) {
            double d1 = vampire.aggressorEntity.getDistanceSqToEntity(entityCenter);

            if (d1 <= d0) {
                aggressorVampire = vampire;
                d0 = d1;
            }
        }

        return aggressorVampire != null ? aggressorVampire.aggressorVampire : null;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        int r = village.getVillageRadius();
        BlockPos cc = village.getCenter();
        return new AxisAlignedBB(cc.getX() - r, cc.getY() - 10, cc.getZ() - r, cc.getX() + r, cc.getY() + 10, cc.getZ() + r);
    }

    @Override
    public BlockPos getCenter() {
        return center;
    }

    void setCenter(BlockPos cc) {
        center = cc;
    }

    @Override
    public Village getVillage() {
        return village;
    }

    public String makeDebugString(BlockPos pos) {
        Village v = getVillage();
        if (v == null) {
            return "MC Village does not exist" + getCenter();
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Center: ").append(getCenter().toString());
            builder.append("\nIs inside: ").append(v.isBlockPosWithinSqVillageRadius(pos)).append(" (").append(getBoundingBox().contains(new Vec3d(pos))).append(')');
            builder.append("\n").append(String.format("RBitten: %s, RConv: %s, RBDeath: %s, Agrr: %s", recentlyBitten, recentlyConverted, recentlyBittenToDeath, agressive));
            List<EntityVillager> allVillagers = getAllVillager();
            List<EntityBasicHunter> hunters = getHunter();
            List<IAggressiveVillager> hunterVillagers = filterHunterVillagers(allVillagers);
            List<EntityVillager> normalVillager = filterNormalVillagers(allVillagers);
            builder.append("\n").append(String.format("Stats: Doors: %s, Aggro: %s, v: %s, vh: %s, h: %s", v.getNumVillageDoors(), calculateAggressiveCounter(), normalVillager.size(), hunterVillagers.size(), hunters.size()));
            int hunterCount = hunters.size() + hunterVillagers.size() / 2;
            boolean spawn = hunterCount < (Balance.village.MIN_HUNTER_COUNT_VILLAGE_PER_DOOR * v.getNumVillageDoors() + 1);
            builder.append("\nShould Spawn: ").append(hunterCount).append('(').append(spawn).append(')');
            builder.append("\nAgressors: ").append(villageAggressorVampires.toString());
            return builder.toString();
        }
    }

    @Override
    public void onVillagerBitten(IVampire vampire) {
        recentlyBitten++;
        dirty = true;
        addOrRenewAggressor(vampire);
    }

    @Override
    public void onVillagerBittenToDeath(IVampire vampire) {
        recentlyBittenToDeath++;
        dirty = true;
        addOrRenewAggressor(vampire);
    }

    @Override
    public void onVillagerConverted(@Nullable IVampire vampire) {
        recentlyConverted++;
        dirty = true;
        if (vampire != null) {
            addOrRenewAggressor(vampire);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        center = UtilLib.readPos(nbt, "center");
        agressive = nbt.getBoolean("AGR");
        recentlyBitten = nbt.getInteger("BITTEN");
        recentlyConverted = nbt.getInteger("CONVERTED");
        recentlyBittenToDeath = nbt.getInteger("KILLED");
    }

    /**
     * Updates the VillageVampire
     *
     * @param worldTime
     * @return dirty
     */
    public boolean tick(long worldTime) {
        this.tickCounter = (int) worldTime;
        if (worldTime % 20 == 13) {
            int tick = (int) (worldTime / 20);
            this.removeDeadAndOldAggressors();
            Village v = getVillage();
            if (tick % (Balance.village.REDUCE_RATE) == 0) {
                if (recentlyBitten > 0) {
                    recentlyBitten--;
                    dirty = true;
                }
                boolean respawn = false;
                if (recentlyConverted > 0) {
                    recentlyConverted--;
                    dirty = true;
                    respawn = true;

                } else if (recentlyBittenToDeath > 0) {
                    recentlyBittenToDeath--;
                    dirty = true;
                    respawn = true;
                }
                if (respawn && v.world.rand.nextInt(Balance.village.VILLAGER_RESPAWN_RATE) == 0) {
                    spawnVillager();

                }
            }

            v.world.profiler.startSection("checkVillagersHunters");
            List<EntityVillager> allVillagers = getAllVillager();
            List<EntityBasicHunter> hunters = getHunter();
            List<IAggressiveVillager> hunterVillagers = filterHunterVillagers(allVillagers);
            List<EntityVillager> normalVillager = filterNormalVillagers(allVillagers);
            if (v.world.rand.nextInt(30) == 0) {
                int hunterCount = hunters.size() + hunterVillagers.size() / 2;
                boolean spawn = hunterCount < (Balance.village.MIN_HUNTER_COUNT_VILLAGE_PER_DOOR * v.getNumVillageDoors() + 1);
                if (spawn || v.world.rand.nextInt(30) == 0) {
                    VampirismMod.log.d(TAG, "Stats: Doors: %s, Aggro: %s, v: %s, vh: %s, h: %s", v.getNumVillageDoors(), calculateAggressiveCounter(), normalVillager.size(), hunterVillagers.size(), hunters.size());
                }
                if (spawn && hunterCount > 20) {
                    //TODO maybe remove or downgrade these logs
                    VampirismMod.log.w(TAG, "Too many hunters spawning. Canceling");
                    VampirismMod.log.w(TAG, "Stats: Doors: %s, Aggro: %s, v: %s, vh: %s, h: %s", v.getNumVillageDoors(), calculateAggressiveCounter(), normalVillager.size(), hunterVillagers.size(), hunters.size());
                    VampirismMod.log.w(TAG, "Hunter Count: %s, Spawn Config: %s", hunterCount, Balance.village.MIN_HUNTER_COUNT_VILLAGE_PER_DOOR);
                    spawn = false;
                }
                if (spawn) {
                    spawnHunter();
                }
            }
            v.world.profiler.endSection();
            int aggressiveCounter = calculateAggressiveCounter();
            if (aggressiveCounter >= Balance.village.AGGRESSIVE_COUNTER_THRESHOLD) {
                if (!agressive) {
                    spawnVillager();
                    Collections.shuffle(normalVillager);
                    makeAgressive(selectVillagersToBecomeHunter(normalVillager));
                }
            } else if (agressive && aggressiveCounter < (Balance.village.AGGRESSIVE_COUNTER_THRESHOLD / 2 + 1)) {
                makeCalm(hunterVillagers);
            }

        }

        if (dirty) {
            dirty = false;
            return true;
        }
        return false;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        UtilLib.write(nbt, "center", center);
        nbt.setBoolean("AGR", agressive);
        nbt.setInteger("BITTEN", recentlyBitten);
        nbt.setInteger("CONVERTED", recentlyConverted);
        nbt.setInteger("KILLED", recentlyBittenToDeath);
    }

    /**
     * Adds or updates the aggressor entry for the given vampire
     *
     * @param vampire
     */
    private void addOrRenewAggressor(@Nonnull IVampire vampire) {
        for (VillageAggressorVampire aggressor : this.villageAggressorVampires) {
            if (aggressor.aggressorVampire.equals(vampire)) {
                aggressor.agressionTime = this.tickCounter;
                return;
            }
        }
        this.villageAggressorVampires.add(new VillageAggressorVampire(vampire.getRepresentingEntity(), vampire, this.tickCounter));
    }

    /**
     * Calculates the aggressive counter values from recently bitten/converted/killed villagers
     */
    private int calculateAggressiveCounter() {
        return recentlyBitten * Balance.village.BITTEN_AGGRESSIVE_FACTOR + recentlyBittenToDeath * Balance.village.BITTEN_TO_DEATH_AGGRESSIVE_FACTOR + recentlyConverted * Balance.village.CONVERTED_AGGRESSIVE_FACTOR;
    }

    /**
     * @param all List to filter
     * @return A new list containing only {@link IAggressiveVillager}
     */
    private List<IAggressiveVillager> filterHunterVillagers(List<EntityVillager> all) {
        List<IAggressiveVillager> filtered = new ArrayList<>();
        for (EntityVillager villager : all) {
            if (villager instanceof IAggressiveVillager) {
                filtered.add((IAggressiveVillager) villager);
            }
        }
        return filtered;
    }

    /**
     * @param all List to filter
     * @return A new list containing only normal villagers
     */
    private List<EntityVillager> filterNormalVillagers(List<EntityVillager> all) {
        List<EntityVillager> filtered = new ArrayList<>();
        for (EntityVillager villager : all) {
            if (!(villager instanceof IVampire || villager instanceof IHunter)) {
                filtered.add(villager);
            }
        }
        return filtered;
    }

    /**
     * @param all List to filter
     * @return A new list containing only {@link IVampire} villagers
     */
    private List<EntityVillager> filterVampireVillagers(List<EntityVillager> all) {
        List<EntityVillager> filtered = new ArrayList<>();
        for (EntityVillager villager : all) {
            if (villager instanceof IVampire) {
                filtered.add(villager);
            }
        }
        return filtered;
    }

    /**
     * @return A list of all villagers in the given village
     */
    private List<EntityVillager> getAllVillager() {
        return village.world.getEntitiesWithinAABB(EntityVillager.class, getBoundingBox());
    }

    private List<EntityBasicHunter> getHunter() {
        return village.world.getEntitiesWithinAABB(EntityBasicHunter.class, getBoundingBox());
    }

    private void makeAgressive(List<EntityVillager> villagers) {
        VampirismMod.log.d(TAG, "Making villagers aggressive");
        agressive = true;
        dirty = true;
        for (EntityVillager v : villagers) {
            VampirismVillageEvent.MakeAggressive event = new VampirismVillageEvent.MakeAggressive(this, v);
            if (MinecraftForge.EVENT_BUS.post(event) && event.getAggressiveVillager() != null) {
                v.getEntityWorld().spawnEntity((Entity) event.getAggressiveVillager());
            } else {
                EntityHunterVillager hunter = EntityHunterVillager.makeHunter(v);
                v.getEntityWorld().spawnEntity(hunter);
            }

            v.setDead();

        }
    }

    private void makeCalm(List<IAggressiveVillager> hunters) {
        VampirismMod.log.d(TAG, "Making villagers calm");
        for (IAggressiveVillager h : hunters) {
            Entity calm = h.makeCalm();
            h.getRepresentingEntity().getEntityWorld().spawnEntity(calm);
            h.getRepresentingEntity().setDead();
        }
        agressive = false;
        dirty = true;
    }

    private void removeDeadAndOldAggressors() {
        villageAggressorVampires.removeIf(aggressorVampire -> !aggressorVampire.aggressorEntity.isEntityAlive() || Math.abs(this.tickCounter - aggressorVampire.agressionTime) > 600);

    }

    /**
     * Creates a list of villagers that should become hunters. This considers things like childhood or trading. Also uses random.
     */
    private List<EntityVillager> selectVillagersToBecomeHunter(List<EntityVillager> villagers) {
        List<EntityVillager> selected = new LinkedList<>();
        for (EntityVillager v : villagers) {
            if (v.isChild() || !v.isEntityAlive()) {
                continue;
            }
            if (v.isPotionActive(ModPotions.sanguinare)) {
                continue;
            }
            if (v.isTrading() || v.isMating()) {
                continue;
            }
            if (v.getRNG().nextInt(Balance.village.VILLAGER_HUNTER_CHANCE) == 0) {
                selected.add(v);
            }
        }
        return selected;
    }

    private void spawnHunter() {
        EntityBasicHunter hunter = new EntityBasicHunter(village.world);
        boolean flag = UtilLib.spawnEntityInWorld(village.world, getBoundingBox(), hunter, 5);
        if (flag) {
            hunter.makeVillageHunter(this);
        } else {
            hunter.setDead();
        }
        //VampirismMod.log.t("Spawning Vampire Hunter %s", flag);
    }

    private void spawnVillager() {
        //VampirismMod.log.t("Spawning villager at village %s", village.getCenter());
        @SuppressWarnings("rawtypes")
        List l = village.world.getEntitiesWithinAABB(EntityVillager.class, getBoundingBox());
        if (l.size() > 0) {
            EntityVillager ev = (EntityVillager) l.get(village.world.rand.nextInt(l.size()));
            EntityVillager entityvillager;
            if (agressive && ev.getRNG().nextInt(Balance.village.VILLAGER_HUNTER_CHANCE) == 0) {
                EntityVillager temp = new EntityVillager(ev.getEntityWorld());
                VampirismVillageEvent.MakeAggressive event = new VampirismVillageEvent.MakeAggressive(this, temp);
                if (MinecraftForge.EVENT_BUS.post(event) && event.getAggressiveVillager() != null) {
                    entityvillager = (EntityVillager) event.getAggressiveVillager();
                } else {
                    entityvillager = EntityHunterVillager.makeHunter(temp);
                }
                temp.setDead();
            } else {
                entityvillager = ev.createChild(ev);
                entityvillager.setGrowingAge(-24000);
                ev.setGrowingAge(6000);
            }
            entityvillager.setLocationAndAngles(ev.posX, ev.posY, ev.posZ, 0.0F, 0.0F);
            village.world.spawnEntity(entityvillager);
            village.world.setEntityState(entityvillager, (byte) 12);
        }
    }

    private static class Storage implements Capability.IStorage<IVampirismVillage> {

        @Override
        public void readNBT(Capability<IVampirismVillage> capability, IVampirismVillage instance, EnumFacing side, NBTBase nbt) {
            ((VampirismVillage) instance).readFromNBT((NBTTagCompound) nbt);
        }

        @Override
        public NBTBase writeNBT(Capability<IVampirismVillage> capability, IVampirismVillage instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            ((VampirismVillage) instance).writeToNBT(nbt);
            return nbt;
        }
    }

    /**
     * Keeps track of a vampire that bit a villager
     */
    private class VillageAggressorVampire {
        final EntityLivingBase aggressorEntity;
        final IVampire aggressorVampire;
        int agressionTime;

        private VillageAggressorVampire(EntityLivingBase aggressorEntity, IVampire aggressorVampire, int agressionTime) {
            this.aggressorEntity = aggressorEntity;
            this.aggressorVampire = aggressorVampire;
            this.agressionTime = agressionTime;
        }

        @Override
        public String toString() {
            return "VillageAggressorVampire{" +
                    "aggressorEntity=" + aggressorEntity +
                    ", agressionTime=" + agressionTime +
                    '}';
        }
    }
}
