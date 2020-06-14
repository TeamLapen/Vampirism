package de.teamlapen.vampirism.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.entity.minion.goals.DefendAreaGoal;
import de.teamlapen.vampirism.entity.minion.goals.FollowLordGoal;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.MinionTask;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;


public class HunterMinionEntity extends MinionEntity<HunterMinionEntity.HunterMinionData> implements IHunter {



    static {
        MinionData.registerDataType(HunterMinionData.ID, HunterMinionData::new);
        MinionTask.init();
    }


    /**
     * Just required to execute static init
     */
    public static void init() {

    }


    public HunterMinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world, VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true, true, true, false, null));
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    public int getHatType() {
        return this.getMinionData().map(d -> d.hat).orElse(0);
    }

    public void setHatType(int type) {
        this.getMinionData().ifPresent(d -> d.hat = type);
    }

    public int getHunterType() {
        return this.getMinionData().map(d -> d.type).map(t -> Math.max(0, t)).orElse(0);
    }

    public void setHunterType(int type) {
        assert type > 0;
        this.getMinionData().ifPresent(d -> d.type = type);
    }

    @Override
    public void activateTask(MinionTask.Type type) {

    }

    public boolean shouldRenderLordSkin() {
        return getMinionData().map(d -> d.type).orElse(0) < 0;
    }

    @Override
    public List<MinionTask.Type> getAvailableTasks() {
        return Lists.newArrayList(MinionTask.Type.DEFEND_AREA, MinionTask.Type.FOLLOW, MinionTask.Type.STAY);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));

        this.goalSelector.addGoal(4, new FollowLordGoal(this, 1.1, 5, 10));

        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new DefendAreaGoal(this));

    }

    @Override
    protected void onMinionDataReceived(@Nonnull HunterMinionData data) {
    }


    public static class HunterMinionData extends MinionData {
        public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "hunter");

        private int type;
        private int hat;

        public HunterMinionData(int maxHealth, ITextComponent name, int type, int hat) {
            super(maxHealth, name, 9);
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
        public void serializeNBT(CompoundNBT tag) {
            super.serializeNBT(tag);
            tag.putInt("hunter_type", type);
            tag.putInt("hunter_hat", hat);
        }

        @Override
        protected ResourceLocation getDataType() {
            return ID;
        }
    }
}
