package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.hunter.IBasicHunter;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.action.EntityActionHandler;
import de.teamlapen.vampirism.entity.ai.EntityAIAttackRangedCrossbow;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveThroughVillageCustom;
import de.teamlapen.vampirism.entity.ai.HunterAIDefendVillage;
import de.teamlapen.vampirism.entity.ai.HunterAILookAtTrainee;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import de.teamlapen.vampirism.inventory.HunterBasicContainer;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.world.loot.LootHandler;
import de.teamlapen.vampirism.world.villages.VampirismVillageHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Exists in {@link EntityBasicHunter#MAX_LEVEL}+1 different levels
 */
public class EntityBasicHunter extends EntityHunterBase implements IBasicHunter, HunterAIDefendVillage.IVillageHunterCreature, HunterAILookAtTrainee.ITrainer, EntityAIAttackRangedCrossbow.IAttackWithCrossbow, IEntityActionUser {
    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityBasicHunter.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(EntityBasicHunter.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> WATCHED_ID = EntityDataManager.createKey(EntityBasicHunter.class, DataSerializers.VARINT);
    private final int MAX_LEVEL = 3;
    private final int MOVE_TO_RESTRICT_PRIO = 3;
    private final int DEFEND_VILLAGE_PRIO = 3;
    private final int WANDER_VILLAGE_PRIO = 5;
    private final int ATTACK_ZOMBIE_PRIO = 5;
    private final EntityAIBase wanderVillage = new EntityAIMoveThroughVillageCustom(this, 0.7F, false, 300);
    private final EntityAIBase defendVillage = new HunterAIDefendVillage(this);
    private final EntityAIBase targetZombies = new EntityAINearestAttackableTarget<>(this, EntityZombie.class, true, true);
    private final EntityAIAttackMelee attackMelee;
    private final EntityAIAttackRangedCrossbow attackRange;
    private boolean villageHunter = false;
    private boolean defendVillageAdded = false;
    /**
     * Player currently being trained otherwise null
     */
    private @Nullable
    EntityPlayer trainee;
    /**
     * Caches the village of this hunter if he is a villageHunter
     */
    private
    @Nullable
    IVampirismVillage IVampirismVillage;
    /**
     * Stores the x axis angle between when targeting an enemy with the crossbow
     */
    private float targetAngle = 0;

    public EntityBasicHunter(World world) {
        super(world, true);
        saveHome = true;
        ((PathNavigateGround) this.getNavigator()).setEnterDoors(true);

        this.setSize(0.6F, 1.95F);


        this.setDontDropEquipment();

        this.attackMelee = new EntityAIAttackMelee(this, 1.0, false);
        this.attackRange = new EntityAIAttackRangedCrossbow(this, this, 0.6, 60, 20);
        this.updateCombatTask();
        this.entitytier = EntityActionTier.Medium;
        this.entityActionHandler = new EntityActionHandler<>(this);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean flag = super.attackEntityAsMob(entity);
        if (flag && this.getHeldItemMainhand() == null) {
            this.swingArm(EnumHand.MAIN_HAND);  //Swing stake if nothing else is held
        }
        return flag;
    }

    @Override
    public @Nonnull
    ItemStack getArrowStackForAttack(EntityLivingBase target) {
        return new ItemStack(ModItems.crossbow_arrow);
    }

    @Override
    public int getLevel() {
        return getDataManager().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getDataManager().set(LEVEL, level);
            this.updateEntityAttributes();
            if (level == 3) {
                this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1000000, 1));
            }
        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public EntityCreature getRepresentingCreature() {
        return this;
    }

    public float getTargetAngle() {
        return targetAngle;
    }

    @Override
    public EntityPlayer getTrainee() {
        return trainee;
    }

    @Nullable
    @Override
    public IVampirismVillage getVampirismVillage() {
        return IVampirismVillage;
    }

    @Override
    public boolean isCrossbowInMainhand() {
        return this.getHeldItemMainhand() != null && this.getHeldItemMainhand().getItem() instanceof VampirismItemCrossbow;
    }

    @Override
    public boolean isLookingForHome() {
        return !hasHome();
    }

    public boolean isSwingingArms() {
        return this.getDataManager().get(SWINGING_ARMS);
    }

    public void setSwingingArms(boolean b) {
        this.getDataManager().set(SWINGING_ARMS, b);
    }

    @Override
    public void makeCampHunter(AxisAlignedBB box) {
        if (villageHunter) {
            this.makeNormalHunter();
        }
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @Override
    public void makeNormalHunter() {
        super.setHome(null);
        this.disableMoveTowardsRestriction();
        this.villageHunter = false;
        this.setDefendVillage(false);
    }

    @Override
    public void makeVillageHunter(IVampirismVillage village) {
        super.setHome(village.getBoundingBox());
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
        this.villageHunter = true;
        this.IVampirismVillage = village;
        this.setDefendVillage(true);
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);

        if (this.getRNG().nextInt(4) == 0) {
            this.setLeftHanded(true);
            Item crossBow = getLevel() > 1 ? ModItems.enhanced_crossbow : ModItems.basic_crossbow;
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(crossBow));

        } else {
            this.setLeftHanded(false);
        }

        this.updateCombatTask();
        return livingdata;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (trainee != null && !(trainee.openContainer instanceof HunterBasicContainer)) {
            this.trainee = null;
        }
        if (!world.isRemote) {
            EntityLivingBase target = getAttackTarget();
            int id = target == null ? 0 : target.getEntityId();
            this.updateWatchedId(id);
        } else {
            targetAngle = 0;
            if (isSwingingArms()) {
                int id = getWatchedId();
                if (id != 0) {
                    Entity target = world.getEntityByID(id);
                    if (target instanceof EntityLivingBase) {

                        double dx = target.posX - (this).posX;
                        double dy = target.posY - this.posY;
                        double dz = target.posZ - this.posZ;
                        float dist = MathHelper.sqrt(dx * dx + dz * dz);
                        targetAngle = (float) Math.atan(dy / dist);
                    }
                }
            }

        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompund) {
        super.readFromNBT(tagCompund);
        if (tagCompund.hasKey("level")) {
            setLevel(tagCompund.getInteger("level"));
        }
        this.villageHunter = tagCompund.getBoolean("villageHunter");
        if (villageHunter) {
            this.setDefendVillage(true);
        }
        if (tagCompund.hasKey("crossbow") && tagCompund.getBoolean("crossbow")) {
            this.setLeftHanded(true);
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.basic_crossbow));
        } else {
            this.setLeftHanded(false);
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        this.updateCombatTask();


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
    public int suggestLevel(Difficulty d) {
        switch (this.rand.nextInt(5)) {
            case 0:
                return (int) (d.minPercLevel / 100F * MAX_LEVEL);
            case 1:
                return (int) (d.avgPercLevel / 100F * MAX_LEVEL);
            case 2:
                return (int) (d.maxPercLevel / 100F * MAX_LEVEL);
            default:
                return this.rand.nextInt(MAX_LEVEL + 1);
        }

    }

    public void updateCombatTask() {
        if (this.world != null && !this.world.isRemote) {
            this.tasks.removeTask(attackMelee);
            this.tasks.removeTask(attackRange);
            ItemStack stack = this.getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() instanceof VampirismItemCrossbow) {
                this.tasks.addTask(2, this.attackRange);
            } else {
                this.tasks.addTask(2, this.attackMelee);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("level", getLevel());
        nbt.setBoolean("villageHunter", villageHunter);
        nbt.setBoolean("crossbow", isCrossbowInMainhand());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes();

    }

    @Override
    protected boolean canDespawn() {
        return isLookingForHome() && super.canDespawn();
    }


    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(LEVEL, -1);
        this.getDataManager().register(SWINGING_ARMS, false);
        this.getDataManager().register(WATCHED_ID, 0);
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 6 + getLevel();
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootHandler.BASIC_HUNTER;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        //Attack task is added in #updateCombatTasks which is e.g. called at end of constructor
        this.tasks.addTask(3, new HunterAILookAtTrainee(this));

        this.tasks.addTask(6, new EntityAIWander(this, 0.7, 50));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 13F));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityVampireBase.class, 17F));
        this.tasks.addTask(8, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityCreature>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)) {

            @Override
            protected double getTargetDistance() {
                return super.getTargetDistance() / 2;
            }
        });
        //Also check the priority of tasks that are dynamically added. See top of class
    }

    @Override
    protected void onRandomTick() {
        super.onRandomTick();
        if (villageHunter) {
            this.IVampirismVillage = VampirismVillageHelper.getNearestVillage(world, getPosition(), 32);
            if (this.IVampirismVillage == null) {
                this.makeNormalHunter();
            }
        } else {
            IVampirismVillage = null;
        }
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        int hunterLevel = HunterPlayer.get(player).getLevel();
        if (this.isEntityAlive() && !player.isSneaking()) {
            if (!world.isRemote) {
                if (HunterLevelingConf.instance().isLevelValidForBasicHunter(hunterLevel + 1)) {
                    if (trainee == null) {
                        player.openGui(VampirismMod.instance, ModGuiHandler.ID_HUNTER_BASIC, this.world, (int) posX, (int) posY, (int) posZ);
                        trainee = player;
                    } else {
                        player.sendMessage(new TextComponentTranslation("text.vampirism.i_am_busy_right_now"));
                    }
                } else if (hunterLevel > 0) {
                    player.sendMessage(new TextComponentTranslation("text.vampirism.basic_hunter.cannot_train_you_any_further"));
                }
            }
            return true;
        }


        return super.processInteract(player, hand);
    }

    /**
     * Add the DefendVillage task.
     * * @param active If the task should be active or not
     */
    protected void setDefendVillage(boolean active) {
        if (defendVillageAdded && !active) {
            this.targetTasks.removeTask(defendVillage);
            this.tasks.removeTask(wanderVillage);
            this.targetTasks.removeTask(targetZombies);
            defendVillageAdded = false;
        } else if (active && !defendVillageAdded) {
            targetTasks.addTask(DEFEND_VILLAGE_PRIO, defendVillage);
            tasks.addTask(WANDER_VILLAGE_PRIO, wanderVillage);
            targetTasks.addTask(ATTACK_ZOMBIE_PRIO, targetZombies);
            defendVillageAdded = true;
        }

    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_MAX_HEALTH + Balance.mobProps.VAMPIRE_HUNTER_MAX_HEALTH_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE + Balance.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.VAMPIRE_HUNTER_SPEED);
    }

    private int getWatchedId() {
        return getDataManager().get(WATCHED_ID);
    }

    private void updateWatchedId(int id) {
        getDataManager().set(WATCHED_ID, id);
    }
}
