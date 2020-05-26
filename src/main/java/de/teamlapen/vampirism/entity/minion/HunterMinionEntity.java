package de.teamlapen.vampirism.entity.minion;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;


public class HunterMinionEntity extends MinionEntity<HunterMinionEntity.HunterMinionData> implements IHunter {

    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(HunterMinionEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HAT = EntityDataManager.createKey(HunterMinionEntity.class, DataSerializers.VARINT);


    static {
        MinionData.registerDataType(HunterMinionData.ID, HunterMinionData::new);
    }


    /**
     * Just required to execute static init
     */
    public static void init() {

    }


    public HunterMinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    public int getHatType() {
        return this.getDataManager().get(HAT);
    }

    public void setHatType(int type) {
        if (this.minionData != null) {
            this.minionData.hat = type;
        }
        this.getDataManager().set(HAT, type);
    }

    public int getHunterType() {
        return Math.max(0, getDataManager().get(TYPE));
    }

    public void setHunterType(int type) {
        if (this.minionData != null) {
            this.minionData.type = type;
        }
        this.getDataManager().set(TYPE, type);
    }

    public boolean shouldRenderLordSkin() {
        return this.getDataManager().get(TYPE) < 0;
    }

    @Override
    protected void onMinionDataReceived(@Nonnull HunterMinionData data) {
        this.getDataManager().set(TYPE, data.type);
        this.getDataManager().set(HAT, data.hat);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(TYPE, 0);
        this.getDataManager().register(HAT, -1);
    }

    public static class HunterMinionData extends MinionData {
        public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "hunter");

        private int type;
        private int hat;

        public HunterMinionData(int maxHealth, ITextComponent name, int type, int hat) {
            super(maxHealth, name);
            this.type = type;
            this.hat = hat;
        }

        private HunterMinionData() {
            super();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            type = nbt.getInt("hunter_type");
            hat = nbt.getInt("hunter_hat");
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT tag = super.serializeNBT();
            tag.putInt("hunter_type", type);
            tag.putInt("hunter_hat", hat);
            return tag;
        }

        @Override
        protected ResourceLocation getDataType() {
            return ID;
        }
    }
}
