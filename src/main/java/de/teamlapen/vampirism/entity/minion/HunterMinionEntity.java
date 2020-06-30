package de.teamlapen.vampirism.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.client.gui.HunterMinionAppearanceScreen;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.AttackRangedCrossbowGoal;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;


public class HunterMinionEntity extends MinionEntity<HunterMinionEntity.HunterMinionData> implements IHunter, AttackRangedCrossbowGoal.IAttackWithCrossbow {

    static {
        MinionData.registerDataType(HunterMinionData.ID, HunterMinionData::new);
    }


    /**
     * Just required to execute static init
     */
    public static void init() {

    }

    /**
     * Used for holding a crossbow
     */
    private static final DataParameter<Boolean> RAISED_ARM = EntityDataManager.createKey(MinionEntity.class, DataSerializers.BOOLEAN);

    private boolean crossbowTask = false;
    private AttackRangedCrossbowGoal<HunterMinionEntity> crossbowGoal;
    private MeleeAttackGoal meleeGoal;

    public HunterMinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world, VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true, true, true, false, null).or(e -> !(e instanceof IFactionEntity) && (e instanceof IMob)));
    }

    @Nonnull
    @Override
    public ItemStack getArrowStackForAttack(LivingEntity target) {
        return new ItemStack(ModItems.crossbow_arrow_normal);
    }

    @Override
    public boolean isCrossbowInMainhand() {
        return this.getHeldItemMainhand().getItem() instanceof VampirismItemCrossbow;
    }

    public boolean isSwingingArms() {
        return this.getDataManager().get(RAISED_ARM);
    }

    protected void setSwingingArms(boolean b) {
        this.getDataManager().set(RAISED_ARM, b);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.ticksExisted % 100 == 0) {
            this.updateAttackGoal();
        }
    }

    @Override
    public void startTargeting() {
        this.setSwingingArms(true);
    }

    @Override
    public void stopTargeting() {
        this.setSwingingArms(false);
    }

    @Override
    protected void onMinionDataReceived(@Nonnull HunterMinionData data) {
        super.onMinionDataReceived(data);
        this.updateAttackGoal();
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    public int getHatType() {
        return this.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty() ? this.getMinionData().map(d -> d.hat).orElse(0) : -2;
    }

    public void setHatType(int type) {
        assert type >= -2;
        this.getMinionData().ifPresent(d -> d.hat = type);
    }

    public int getHunterType() {
        return this.getMinionData().map(d -> d.type).map(t -> Math.max(0, t)).orElse(0);
    }

    public void setHunterType(int type) {
        assert type >= 0;
        this.getMinionData().ifPresent(d -> d.type = type);
    }


    public void setUseLordSkin(boolean useLordSkin) {
        this.getMinionData().ifPresent(d -> d.useLordSkin = useLordSkin);
    }

    @Override
    public List<IMinionTask<?>> getAvailableTasks() {
        return Lists.newArrayList(MinionTasks.follow_lord, MinionTasks.defend_area, MinionTasks.stay, MinionTasks.collect_hunter_items);
    }

    public boolean shouldRenderLordSkin() {
        return this.getMinionData().map(d -> d.useLordSkin).orElse(false);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH + BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH_PL * 3);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE + BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE_PL * 3);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(RAISED_ARM, false);

    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        meleeGoal = new MeleeAttackGoal(this, 1.0D, false);
        crossbowGoal = new AttackRangedCrossbowGoal<>(this, 0.8, 60, 25);
        this.goalSelector.addGoal(1, meleeGoal);


    }

    private void updateAttackGoal() {
        if (this.world.isRemote()) return;
        boolean usingCrossbow = isCrossbowInMainhand();
        if (crossbowTask && !usingCrossbow) {
            this.goalSelector.removeGoal(crossbowGoal);
            this.goalSelector.addGoal(1, meleeGoal);
            crossbowTask = false;
        } else if (!crossbowTask && usingCrossbow) {
            this.goalSelector.removeGoal(meleeGoal);
            this.goalSelector.addGoal(1, crossbowGoal);
            crossbowTask = true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openAppearanceScreen() {
        Minecraft.getInstance().displayGuiScreen(new HunterMinionAppearanceScreen(this));
    }

    public static class HunterMinionData extends MinionData {
        public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "hunter");

        private int type;
        private int hat;
        private boolean useLordSkin;

        public HunterMinionData(int maxHealth, String name, int type, int hat, boolean useLordSkin) {
            super(maxHealth, name, 9);
            this.type = type;
            this.hat = hat;
            this.useLordSkin = useLordSkin;
        }

        private HunterMinionData() {
            super();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            type = nbt.getInt("hunter_type");
            hat = nbt.getInt("hunter_hat");
            useLordSkin = nbt.getBoolean("use_lord_skin");
        }

        @Override
        public void serializeNBT(CompoundNBT tag) {
            super.serializeNBT(tag);
            tag.putInt("hunter_type", type);
            tag.putInt("hunter_hat", hat);
            tag.putBoolean("use_lord_skin", useLordSkin);
        }

        @Override
        protected ResourceLocation getDataType() {
            return ID;
        }

        @Override
        public ITextComponent getFormattedName() {
            return super.getFormattedName().applyTextStyle(VReference.HUNTER_FACTION.getChatColor());
        }

        @Override
        public void handleMinionAppearanceConfig(String newName, int... data) {
            this.setName(newName);
            if (data.length >= 3) {
                type = data[0];
                hat = data[1];
                useLordSkin = data[2] == 1;
            }
        }
    }
}
