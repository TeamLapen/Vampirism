package de.teamlapen.vampirism.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.lib.HelperLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.client.gui.VampireMinionAppearanceScreen;
import de.teamlapen.vampirism.client.gui.VampireMinionStatsScreen;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.RestrictSunVampireGoal;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.items.MinionUpgradeItem;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;


public class VampireMinionEntity extends MinionEntity<VampireMinionEntity.VampireMinionData> implements IVampire {

    static {
        MinionData.registerDataType(VampireMinionEntity.VampireMinionData.ID, VampireMinionEntity.VampireMinionData::new);
    }

    /**
     * Just required to execute static init
     */
    public static void init() {

    }


    private boolean sundamageCache;
    private EnumStrength garlicCache = EnumStrength.NONE;

    public VampireMinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world, VampirismAPI.factionRegistry().getPredicate(VReference.VAMPIRE_FACTION, true, true, true, false, null).or(e -> !(e instanceof IFactionEntity) && e instanceof IMob && !(e instanceof ZombieEntity)));
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {

    }

    @Override
    public List<IMinionTask<?, ?>> getAvailableTasks() {
        return Lists.newArrayList(MinionTasks.follow_lord, MinionTasks.stay, MinionTasks.defend_area, MinionTasks.collect_blood, MinionTasks.protect_lord);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    public int getVampireType() {
        return this.getMinionData().map(d -> d.type).map(t -> Math.max(0, t)).orElse(0);
    }

    public void setVampireType(int type) {
        getMinionData().ifPresent(d -> d.type = type);
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(IWorld iWorld, boolean forcerefresh) {
        if (forcerefresh) {
            garlicCache = Helper.getGarlicStrength(this, iWorld);
        }
        return garlicCache;
    }

    @Override
    public boolean isGettingSundamage(IWorld iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.world.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.isPotionActive(ModEffects.sunscreen);
    }

    @Override
    public void livingTick() {
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(world, true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(world, true);
        }
        if (!world.isRemote) {
            if (isGettingSundamage(world) && ticksExisted % 40 == 11) {
                double dmg = getAttribute(VReference.sunDamage).getValue();
                if (dmg > 0) this.attackEntityFrom(VReference.SUNDAMAGE, (float) dmg);
            }
            if (isGettingGarlicDamage(world) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(world), this.ticksExisted);
            }
        }
        if (!this.world.isRemote) {
            if (isAlive() && isInWater()) {
                setAir(300);
                if (ticksExisted % 16 == 4) {
                    addPotionEffect(new EffectInstance(Effects.WEAKNESS, 80, 0));
                }
            }
        }
        super.livingTick();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openAppearanceScreen() {
        Minecraft.getInstance().displayGuiScreen(new VampireMinionAppearanceScreen(this, Minecraft.getInstance().currentScreen));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openStatsScreen() {
        Minecraft.getInstance().displayGuiScreen(new VampireMinionStatsScreen(this, Minecraft.getInstance().currentScreen));
    }

    public void setUseLordSkin(boolean useLordSkin) {
        this.getMinionData().ifPresent(d -> d.useLordSkin = useLordSkin);
    }

    public boolean shouldRenderLordSkin() {
        return this.getMinionData().map(d -> d.useLordSkin).orElse(false);
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        return false;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    protected void onMinionDataReceived(@Nonnull VampireMinionData data) {
        super.onMinionDataReceived(data);
        updateAttributes();
    }


    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        if (!this.world.isRemote() && isLord(player) && minionData != null) {
            ItemStack heldItem = player.getHeldItem(hand);
            if (heldItem.getItem() instanceof MinionUpgradeItem && ((MinionUpgradeItem) heldItem.getItem()).getFaction() == this.getFaction()) {
                if (this.minionData.level + 1 >= ((MinionUpgradeItem) heldItem.getItem()).getMinLevel() && this.minionData.level + 1 <= ((MinionUpgradeItem) heldItem.getItem()).getMaxLevel()) {
                    this.minionData.level++;
                    if (!player.abilities.isCreativeMode) heldItem.shrink(1);
                    player.sendStatusMessage(new TranslationTextComponent("text.vampirism.vampire_minion.equipment_upgrade"), false);
                    HelperLib.sync(this);
                } else {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.vampire_minion.binding_wrong"));

                }
                return true;
            }
        }
        return super.processInteract(player, hand);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttributes().registerAttribute(VReference.sunDamage).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
        updateAttributes();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new RestrictSunVampireGoal<>(this));
    }

    private void updateAttributes() {
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.MINION_MAX_HEALTH + BalanceMobProps.mobProps.MINION_MAX_HEALTH_PL * getMinionData().map(VampireMinionData::getHealthLevel).orElse(0));
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE + BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE_PL * getMinionData().map(VampireMinionData::getStrengthLevel).orElse(0));
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_SPEED + 0.05 * getMinionData().map(VampireMinionData::getSpeedLevel).orElse(0));
    }

    public static class VampireMinionData extends MinionData {
        public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "vampire");

        public static final int MAX_LEVEL = 6;
        public static final int MAX_LEVEL_INVENTORY = 2;
        public static final int MAX_LEVEL_HEALTH = 3;
        public static final int MAX_LEVEL_STRENGTH = 3;
        public static final int MAX_LEVEL_SPEED = 3;
        private int type;
        private boolean useLordSkin;
        /**
         * Should be between 0 and {@link VampireMinionData#MAX_LEVEL}
         */
        private int level;
        private int inventoryLevel;
        private int healthLevel;
        private int strengthLevel;
        private int speedLevel;

        public VampireMinionData(String name, int type, boolean useLordSkin) {
            super(name, 9);
            this.type = type;
            this.useLordSkin = useLordSkin;
            this.level = 0;
        }

        private VampireMinionData() {
            super();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            type = nbt.getInt("vampire_type");
            level = nbt.getInt("level");
            useLordSkin = nbt.getBoolean("use_lord_skin");
            inventoryLevel = nbt.getInt("l_inv");
            healthLevel = nbt.getInt("l_he");
            strengthLevel = nbt.getInt("l_str");
            speedLevel = nbt.getInt("l_spe");
        }

        @Override
        public ITextComponent getFormattedName() {
            return super.getFormattedName().applyTextStyle(VReference.VAMPIRE_FACTION.getChatColor());
        }

        public int getHealthLevel() {
            return healthLevel;
        }

        public int getInventoryLevel() {
            return this.inventoryLevel;
        }

        public int getInventorySize() {
            return inventoryLevel == 1 ? 12 : (inventoryLevel == 2 ? 15 : 9);
        }

        public int getLevel() {
            return this.level;
        }

        public int getRemainingStatPoints() {
            return Math.max(0, this.level - inventoryLevel - healthLevel - speedLevel - strengthLevel);
        }

        public int getSpeedLevel() {
            return this.speedLevel;
        }

        public int getStrengthLevel() {
            return strengthLevel;
        }

        @Override
        public void handleMinionAppearanceConfig(String newName, int... data) {
            this.setName(newName);
            if (data.length >= 2) {
                this.type = data[0];
                this.useLordSkin = data[1] == 1;
            }
        }

        @Override
        public void serializeNBT(CompoundNBT tag) {
            super.serializeNBT(tag);
            tag.putInt("vampire_type", type);
            tag.putInt("level", level);
            tag.putBoolean("use_lord_skin", useLordSkin);
            tag.putInt("l_inv", inventoryLevel);
            tag.putInt("l_he", healthLevel);
            tag.putInt("l_str", strengthLevel);
            tag.putInt("l_spe", speedLevel);
        }

        /**
         * @param level 0, 1 or 2
         * @return If the new level is higher than the old
         */
        public boolean setLevel(int level) {
            if (level < 0 || level > MAX_LEVEL) return false;
            boolean levelup = level > this.level;
            this.level = level;
            return levelup;
        }

        @Override
        public boolean upgradeStat(int statId, MinionEntity<?> entity) {
            if (getRemainingStatPoints() == 0) {
                LOGGER.warn("Cannot upgrade minion stat as no stat points are left");
                return false;
            }
            assert entity instanceof VampireMinionEntity;
            switch (statId) {
                case 0:
                    if (inventoryLevel >= MAX_LEVEL_INVENTORY) return false;
                    inventoryLevel++;
                    this.getInventory().setAvailableSize(getInventorySize());
                    return true;
                case 1:
                    if (healthLevel >= MAX_LEVEL_HEALTH) return false;
                    healthLevel++;
                    ((VampireMinionEntity) entity).updateAttributes();
                    return true;
                case 2:
                    if (strengthLevel >= MAX_LEVEL_STRENGTH) return false;
                    strengthLevel++;
                    ((VampireMinionEntity) entity).updateAttributes();
                    return true;
                case 3:
                    if (speedLevel >= MAX_LEVEL_SPEED) return false;
                    speedLevel++;
                    return true;

                default:
                    LOGGER.warn("Cannot upgrade minion stat {} as it does not exist", statId);
                    return false;
            }
        }

        @Override
        protected ResourceLocation getDataType() {
            return ID;
        }
    }
}
