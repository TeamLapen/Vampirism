package de.teamlapen.vampirism.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.lib.HelperLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.client.gui.VampireMinionAppearanceScreen;
import de.teamlapen.vampirism.client.gui.VampireMinionStatsScreen;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.FleeSunVampireGoal;
import de.teamlapen.vampirism.entity.goals.RestrictSunVampireGoal;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.items.BloodBottleItem;
import de.teamlapen.vampirism.items.MinionUpgradeItem;
import de.teamlapen.vampirism.items.VampirismItemBloodFood;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

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

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return BasicVampireEntity.getAttributeBuilder();
    }
    private boolean sundamageCache;
    private EnumStrength garlicCache = EnumStrength.NONE;

    public VampireMinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world, VampirismAPI.factionRegistry().getPredicate(VReference.VAMPIRE_FACTION, true, true, true, false, null).or(e -> !(e instanceof IFactionEntity) && e instanceof IMob && !(e instanceof ZombieEntity) && !(e instanceof SkeletonEntity) && !(e instanceof CreeperEntity)));
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.heal(amt / 3f); //blood bottle = 900 amt = 9 amt = 2.5 health
    }

    @Override
    public List<IMinionTask<?, ?>> getAvailableTasks() {
        return Lists.newArrayList(MinionTasks.FOLLOW_LORD.get(), MinionTasks.STAY.get(), MinionTasks.DEFEND_AREA.get(), MinionTasks.COLLECT_BLOOD.get(), MinionTasks.PROTECT_LORD.get());
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    public int getVampireType() {
        return this.getMinionData().map(d -> d.type).map(t -> Math.max(0, t)).orElse(0);
    }

    /**
     * @return Whether the selected skin is from the minion specific pool or a generic vampire skin
     */
    public boolean hasMinionSpecificSkin() {
        return this.getMinionData().map(d -> d.minionSkin).orElse(false);
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(IWorld iWorld, boolean forceRefresh) {
        if (forceRefresh) {
            garlicCache = Helper.getGarlicStrength(this, iWorld);
        }
        return garlicCache;
    }

    @Override
    public void aiStep() {
        if (this.tickCount % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(level, true);
        }
        if (this.tickCount % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(level, true);
        }
        if (!level.isClientSide) {
            if (isGettingSundamage(level) && tickCount % 40 == 11) {
                double dmg = getAttribute(ModAttributes.SUNDAMAGE.get()).getValue();
                if (dmg > 0) this.hurt(VReference.SUNDAMAGE, (float) dmg);
            }
            if (isGettingGarlicDamage(level) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(level), this.tickCount);
            }
        }
        if (!this.level.isClientSide) {
            if (isAlive() && isInWater()) {
                setAirSupply(300);
                if (tickCount % 16 == 4) {
                    addEffect(new EffectInstance(Effects.WEAKNESS, 80, 0));
                }
            }
        }
        super.aiStep();
    }

    @Nonnull
    @Override
    public ItemStack eat(@Nonnull World world, @Nonnull ItemStack stack) {
        return stack;
    }

    @Override
    public boolean isGettingSundamage(IWorld iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.level.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.hasEffect(ModEffects.SUNSCREEN.get());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openAppearanceScreen() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().setScreen(new VampireMinionAppearanceScreen(this, Minecraft.getInstance().screen)));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openStatsScreen() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().setScreen(new VampireMinionStatsScreen(this, Minecraft.getInstance().screen)));
    }

    public void setUseLordSkin(boolean useLordSkin) {
        this.getMinionData().ifPresent(d -> d.useLordSkin = useLordSkin);
    }

    public void setVampireType(int type, boolean minionSkin) {
        getMinionData().ifPresent(d -> {
            d.type = type;
            d.minionSkin = minionSkin;
        });
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
    protected boolean canConsume(ItemStack stack) {
        if (!super.canConsume(stack)) return false;
        if ((stack.isEdible() && !(stack.getItem() instanceof VampirismItemBloodFood))) return false;
        boolean fullHealth = this.getHealth() == this.getMaxHealth();
        if (fullHealth && (stack.isEdible() && stack.getItem() instanceof VampirismItemBloodFood)) return false;
        if (stack.getItem() instanceof BloodBottleItem && stack.getDamageValue() == 0) return false;
        return !fullHealth || !(stack.getItem() instanceof BloodBottleItem);
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if (!this.level.isClientSide() && isLord(player) && minionData != null) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (heldItem.getItem() instanceof MinionUpgradeItem && ((MinionUpgradeItem) heldItem.getItem()).getFaction() == this.getFaction()) {
                if (this.minionData.level + 1 >= ((MinionUpgradeItem) heldItem.getItem()).getMinLevel() && this.minionData.level + 1 <= ((MinionUpgradeItem) heldItem.getItem()).getMaxLevel()) {
                    this.minionData.level++;
                    if (!player.abilities.instabuild) heldItem.shrink(1);
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.vampire_minion.binding_upgrade"), false);
                    HelperLib.sync(this);
                } else {
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.vampire_minion.binding_wrong"), false);

                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void onMinionDataReceived(@Nonnull VampireMinionData data) {
        super.onMinionDataReceived(data);
        updateAttributes();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(8, new FleeSunVampireGoal<>(this, 1, true));
    }

    private void updateAttributes() {
        boolean stats = getLordOpt().flatMap(lord -> ((IFactionPlayerHandler) lord).getCurrentFactionPlayer()).map(player -> player.getSkillHandler().isSkillEnabled(VampireSkills.vampire_minion_stats_increase)).orElse(false);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.MINION_MAX_HEALTH + BalanceMobProps.mobProps.MINION_MAX_HEALTH_PL * getMinionData().map(VampireMinionData::getHealthLevel).orElse(0) * (stats ? 1.2 : 1));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE + BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE_PL * getMinionData().map(VampireMinionData::getStrengthLevel).orElse(0) * (stats ? 1.2 : 1));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_SPEED + 0.05 * getMinionData().map(VampireMinionData::getSpeedLevel).orElse(0) * (stats ? 1.2 : 1));
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
        private boolean minionSkin;
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
            this.minionSkin = false;
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
            minionSkin = nbt.getBoolean("ms");
        }

        @Override
        public IFormattableTextComponent getFormattedName() {
            return super.getFormattedName().withStyle(VReference.VAMPIRE_FACTION.getChatColor());
        }

        public int getHealthLevel() {
            return healthLevel;
        }

        public int getInventoryLevel() {
            return this.inventoryLevel;
        }

        @Override
        public int getInventorySize() {
            int size = getDefaultInventorySize();
            return inventoryLevel == 1 ? size + 3 : (inventoryLevel == 2 ? size + 6 : size);
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
                this.useLordSkin = (data[1] & 0b1) == 1;
                this.minionSkin = (data[1] & 0b10) == 0b10;
            }
        }

        @Override
        public boolean hasUsedSkillPoints() {
            return this.inventoryLevel + this.healthLevel + this.strengthLevel + this.speedLevel > 0;
        }

        @Override
        public void resetStats(MinionEntity<?> entity) {
            assert entity instanceof VampireMinionEntity;
            this.inventoryLevel = 0;
            this.healthLevel = 0;
            this.strengthLevel = 0;
            this.speedLevel = 0;
            this.getInventory().setAvailableSize(getInventorySize());
            ((VampireMinionEntity) entity).updateAttributes();
            super.resetStats(entity);
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
            tag.putBoolean("ms", minionSkin);
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
            if (super.upgradeStat(statId, entity)) return true;
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
