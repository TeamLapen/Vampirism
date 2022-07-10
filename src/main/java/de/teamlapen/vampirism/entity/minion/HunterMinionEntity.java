package de.teamlapen.vampirism.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.lib.HelperLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.client.gui.HunterMinionAppearanceScreen;
import de.teamlapen.vampirism.client.gui.HunterMinionStatsScreen;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.AttackRangedCrossbowGoal;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.items.MinionUpgradeItem;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;
import java.util.List;


public class HunterMinionEntity extends MinionEntity<HunterMinionEntity.HunterMinionData> implements IHunter, AttackRangedCrossbowGoal.IAttackWithCrossbow {

    /**
     * Used for holding a crossbow
     */
    private static final DataParameter<Boolean> RAISED_ARM = EntityDataManager.defineId(HunterMinionEntity.class, DataSerializers.BOOLEAN);

    static {
        MinionData.registerDataType(HunterMinionData.ID, HunterMinionData::new);
    }

    /**
     * Just required to execute static init
     */
    public static void init() {

    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return BasicHunterEntity.getAttributeBuilder();
    }

    private boolean crossbowTask = false;
    private AttackRangedCrossbowGoal<HunterMinionEntity> crossbowGoal;
    private MeleeAttackGoal meleeGoal;

    public HunterMinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world, VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true, true, false, false, null).or(e -> !(e instanceof IFactionEntity) && (e instanceof IMob) && !(e instanceof CreeperEntity)));
    }

    @Nonnull
    @Override
    public ItemStack getArrowStackForAttack(LivingEntity target) {
        return new ItemStack(ModItems.CROSSBOW_ARROW_NORMAL.get());
    }

    @Override
    public List<IMinionTask<?, ?>> getAvailableTasks() {
        return Lists.newArrayList(MinionTasks.FOLLOW_LORD.get(), MinionTasks.DEFEND_AREA.get(), MinionTasks.STAY.get(), MinionTasks.COLLECT_HUNTER_ITEMS.get(), MinionTasks.PROTECT_LORD.get());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.tickCount % 100 == 0) {
            this.updateAttackGoal();
        }
    }

    public void setHatType(int type) {
        assert type >= -2;
        this.getMinionData().ifPresent(d -> d.hat = type);
    }

    public int getHunterType() {
        return this.getMinionData().map(d -> d.type).map(t -> Math.max(0, t)).orElse(0);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    /**
     * @return Whether the selected skin is from the minion specific pool or a generic vampire skin
     */
    public boolean hasMinionSpecificSkin() {
        return this.getMinionData().map(d -> d.minionSkin).orElse(false);
    }

    public int getHatType() {
        return this.getItemBySlot(EquipmentSlotType.HEAD).isEmpty() ? this.getMinionData().map(d -> d.hat).orElse(0) : -2;
    }

    @Override
    public boolean isCrossbowInMainhand() {
        return this.getMainHandItem().getItem() instanceof VampirismItemCrossbow;
    }

    public boolean isSwingingArms() {
        return this.getEntityData().get(RAISED_ARM);
    }

    protected void setSwingingArms(boolean b) {
        this.getEntityData().set(RAISED_ARM, b);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openAppearanceScreen() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().setScreen(new HunterMinionAppearanceScreen(this, Minecraft.getInstance().screen)));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openStatsScreen() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().setScreen(new HunterMinionStatsScreen(this, Minecraft.getInstance().screen)));
    }

    public void setHunterType(int type, boolean minionSkin) {
        getMinionData().ifPresent(d -> {
            d.type = type;
            d.minionSkin = minionSkin;
        });
    }

    public void setUseLordSkin(boolean useLordSkin) {
        this.getMinionData().ifPresent(d -> d.useLordSkin = useLordSkin);
    }

    public boolean shouldRenderLordSkin() {
        return this.getMinionData().map(d -> d.useLordSkin).orElse(false);
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
    protected boolean canConsume(ItemStack stack) {
        if (!super.canConsume(stack)) return false;
        boolean fullHealth = this.getHealth() == this.getMaxHealth();
        return !stack.isEdible() || !fullHealth || stack.getItem().getFoodProperties().canAlwaysEat();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(RAISED_ARM, false);

    }

    @Override
    protected void onMinionDataReceived(@Nonnull HunterMinionData data) {
        super.onMinionDataReceived(data);
        this.updateAttackGoal();
        this.updateAttributes();
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if (!this.level.isClientSide() && isLord(player) && minionData != null) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (heldItem.getItem() instanceof MinionUpgradeItem && ((MinionUpgradeItem) heldItem.getItem()).getFaction() == this.getFaction()) {
                if (this.minionData.level + 1 >= ((MinionUpgradeItem) heldItem.getItem()).getMinLevel() && this.minionData.level + 1 <= ((MinionUpgradeItem) heldItem.getItem()).getMaxLevel()) {
                    this.minionData.level++;
                    if (!player.abilities.instabuild) heldItem.shrink(1);
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.hunter_minion.equipment_upgrade"), false);
                    HelperLib.sync(this);
                } else {
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.hunter_minion.equipment_wrong"), false);

                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        meleeGoal = new MeleeAttackGoal(this, 1.0D, false);
        crossbowGoal = new AttackRangedCrossbowGoal<>(this, 0.8, 60, 25);
        this.goalSelector.addGoal(1, meleeGoal);


    }

    private void updateAttackGoal() {
        if (this.level.isClientSide()) return;
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

    private void updateAttributes() {
        float statsMultiplier = getLordOpt().flatMap(lord -> ((IFactionPlayerHandler) lord).getCurrentFactionPlayer()).map(player -> player.getSkillHandler().isSkillEnabled(HunterSkills.HUNTER_MINION_STATS_INCREASE.get())).orElse(false)?1.2f:1f;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.MINION_MAX_HEALTH + BalanceMobProps.mobProps.MINION_MAX_HEALTH_PL * getMinionData().map(HunterMinionData::getHealthLevel).orElse(0) * statsMultiplier);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE + BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE_PL * getMinionData().map(HunterMinionData::getStrengthLevel).orElse(0) * statsMultiplier);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED * statsMultiplier);
    }

    public static class HunterMinionData extends MinionData {
        public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "hunter");

        public static final int MAX_LEVEL = 6;
        public static final int MAX_LEVEL_INVENTORY = 2;
        public static final int MAX_LEVEL_HEALTH = 3;
        public static final int MAX_LEVEL_STRENGTH = 3;
        public static final int MAX_LEVEL_RESOURCES = 2;

        private int type;
        private int hat;
        private boolean useLordSkin;
        private boolean minionSkin;

        /**
         * Should be between 0 and {@link HunterMinionData#MAX_LEVEL}
         */
        private int level;
        private int inventoryLevel;
        private int healthLevel;
        private int strengthLevel;
        private int resourceEfficiencyLevel;

        public HunterMinionData(String name, int type, int hat, boolean useLordSkin) {
            super(name, 9);
            this.type = type;
            this.hat = hat;
            this.useLordSkin = useLordSkin;
            this.level = 0;
            this.minionSkin = false;
        }

        private HunterMinionData() {
            super();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            type = nbt.getInt("hunter_type");
            hat = nbt.getInt("hunter_hat");
            level = nbt.getInt("level");
            useLordSkin = nbt.getBoolean("use_lord_skin");
            inventoryLevel = nbt.getInt("l_inv");
            healthLevel = nbt.getInt("l_he");
            strengthLevel = nbt.getInt("l_str");
            resourceEfficiencyLevel = nbt.getInt("l_res");
            minionSkin = nbt.getBoolean("ms");

        }

        @Override
        public IFormattableTextComponent getFormattedName() {
            return super.getFormattedName().withStyle(VReference.HUNTER_FACTION.getChatColor());
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
            return Math.max(0, this.level - inventoryLevel - healthLevel - resourceEfficiencyLevel - strengthLevel);
        }

        public int getResourceEfficiencyLevel() {
            return resourceEfficiencyLevel;
        }

        public int getStrengthLevel() {
            return strengthLevel;
        }

        @Override
        public void handleMinionAppearanceConfig(String newName, int... data) {
            this.setName(newName);
            if (data.length >= 3) {
                type = data[0];
                hat = data[1];
                this.useLordSkin = (data[2] & 0b1) == 1;
                this.minionSkin = (data[2] & 0b10) == 0b10;
            }
        }

        @Override
        public boolean hasUsedSkillPoints() {
            return this.inventoryLevel + this.healthLevel + this.strengthLevel + this.resourceEfficiencyLevel > 0;
        }

        @Override
        public void resetStats(MinionEntity<?> entity) {
            assert entity instanceof HunterMinionEntity;
            this.inventoryLevel = 0;
            this.healthLevel = 0;
            this.strengthLevel = 0;
            this.resourceEfficiencyLevel = 0;
            this.shrinkInventory(entity);
            ((HunterMinionEntity) entity).updateAttributes();
            super.resetStats(entity);
        }

        @Override
        public void serializeNBT(CompoundNBT tag) {
            super.serializeNBT(tag);
            tag.putInt("hunter_type", type);
            tag.putInt("hunter_hat", hat);
            tag.putInt("level", level);
            tag.putInt("l_inv", inventoryLevel);
            tag.putInt("l_he", healthLevel);
            tag.putInt("l_str", strengthLevel);
            tag.putInt("l_res", resourceEfficiencyLevel);
            tag.putBoolean("use_lord_skin", useLordSkin);
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
            assert entity instanceof HunterMinionEntity;
            switch (statId) {
                case 0:
                    if (inventoryLevel >= MAX_LEVEL_INVENTORY) return false;
                    inventoryLevel++;
                    this.getInventory().setAvailableSize(getInventorySize());
                    return true;
                case 1:
                    if (healthLevel >= MAX_LEVEL_HEALTH) return false;
                    healthLevel++;
                    ((HunterMinionEntity) entity).updateAttributes();
                    entity.setHealth(entity.getMaxHealth());
                    return true;
                case 2:
                    if (strengthLevel >= MAX_LEVEL_STRENGTH) return false;
                    strengthLevel++;
                    ((HunterMinionEntity) entity).updateAttributes();
                    return true;
                case 3:
                    if (resourceEfficiencyLevel >= MAX_LEVEL_RESOURCES) return false;
                    resourceEfficiencyLevel++;
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
