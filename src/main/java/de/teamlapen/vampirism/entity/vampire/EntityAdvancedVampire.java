package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.vampire.IAdvancedVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.action.EntityActionHandler;
import de.teamlapen.vampirism.entity.ai.EntityAIAttackMeleeNoSun;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeGarlic;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeSun;
import de.teamlapen.vampirism.entity.ai.VampireAIRestrictSun;
import de.teamlapen.vampirism.entity.hunter.EntityHunterBase;
import de.teamlapen.vampirism.util.IPlayerFace;
import de.teamlapen.vampirism.util.SupporterManager;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Advanced vampire. Is strong. Represents supporters
 */
public class EntityAdvancedVampire extends EntityVampireBase implements IAdvancedVampire, IPlayerFace, IEntityActionUser {
    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityAdvancedVampire.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityAdvancedVampire.class, DataSerializers.VARINT);
    private static final DataParameter<String> NAME = EntityDataManager.createKey(EntityAdvancedVampire.class, DataSerializers.STRING);
    private static final DataParameter<String> TEXTURE = EntityDataManager.createKey(EntityAdvancedVampire.class, DataSerializers.STRING);

    private final int MAX_LEVEL = 1;
    /**
     * Store the approximate count of entities that are following this advanced vampire.
     * Not guaranteed to be exact and not saved to nbt
     */
    private int followingEntities = 0;
    /**
     * available actions for AI task & task
     */
    private final EntityActionHandler<?> entityActionHandler;
    private final EntityClassType entityclass;
    private final EntityActionTier entitytier;

    public EntityAdvancedVampire(World world) {
        super(ModEntities.advanced_vampire, world, true);
        this.setSize(0.6F, 1.95F);
        this.canSuckBloodFromPlayer = true;
        this.setSpawnRestriction(SpawnRestriction.SPECIAL);
        this.setDontDropEquipment();
        entitytier = EntityActionTier.High;
        entityclass = EntityClassType.getRandomClass(this.getRNG());
        IEntityActionUser.applyAttributes(this);
        this.entityActionHandler = new EntityActionHandler<>(this);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        boolean flag = super.attackEntityFrom(damageSource, amount);
        if (flag && damageSource.getTrueSource() instanceof EntityPlayer && this.rand.nextInt(4) == 0) {
            this.addPotionEffect(new PotionEffect(ModPotions.sunscreen, 150, 2));
        }
        return flag;
    }

    @Override
    public void decreaseFollowerCount() {
        followingEntities = Math.max(0, followingEntities - 1);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }


    @Override
    public int getEyeType() {
        return getDataManager().get(TYPE);
    }

    @Override
    public int getFollowingCount() {
        return followingEntities;
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
            if (level == 1) {
                this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1000000, 0));
            }
        }
    }

    @Override
    public int getMaxFollowerCount() {
        return Balance.mobProps.ADVANCED_VAMPIRE_MAX_FOLLOWER;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public ITextComponent getName() {
        String senderName = this.getDataManager().get(NAME);
        return "none".equals(senderName) ? super.getName() : new TextComponentString(senderName);
    }

    @Nullable
    @Override
    public String getPlayerFaceName() {
        return getTextureName();
    }

    @Nullable
    public String getTextureName() {
        String texture = this.getDataManager().get(TEXTURE);
        return "none".equals(texture) ? null : texture;
    }

    @Override
    public boolean increaseFollowerCount() {
        if (followingEntities < getMaxFollowerCount()) {
            followingEntities++;
            return true;
        }
        return false;
    }

    @Override
    public void read(NBTTagCompound tagCompund) {
        super.read(tagCompund);
        if (tagCompund.contains("level")) {
            setLevel(tagCompund.getInt("level"));
        }
        if (tagCompund.contains("type")) {
            getDataManager().set(TYPE, tagCompund.getInt("type"));
            getDataManager().set(NAME, tagCompund.getString("name"));
            getDataManager().set(TEXTURE, tagCompund.getString("texture"));
        }
        if (entityActionHandler != null) {
            entityActionHandler.read(tagCompund);
        }
    }

    @Override
    public int suggestLevel(Difficulty d) {
        if (rand.nextBoolean()) {
            return (int) (d.avgPercLevel * MAX_LEVEL / 100F);
        }
        return rand.nextInt(MAX_LEVEL + 1);

    }

    @Override
    public void writeAdditional(NBTTagCompound nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("level", getLevel());
        nbt.putInt("type", getEyeType());
        nbt.putString("texture", getDataManager().get(TEXTURE));
        nbt.putString("name", getDataManager().get(NAME));
        nbt.putInt("entityclasstype", EntityClassType.getID(entityclass));
        if (entityActionHandler != null) {
            entityActionHandler.write(nbt);
        }
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.updateEntityAttributes();

    }

    @Override
    protected float calculateFireDamage(float amount) {
        return (float) (amount * Balance.mobProps.ADVANCED_VAMPIRE_FIRE_VULNERABILITY);
    }

    @Override
    protected void registerData() {
        super.registerData();
        SupporterManager.Supporter supporter = SupporterManager.getInstance().getRandomVampire(rand);
        this.getDataManager().register(LEVEL, -1);
        this.getDataManager().register(TYPE, supporter.typeId);
        this.getDataManager().register(NAME, supporter.senderName == null ? "none" : supporter.senderName);
        this.getDataManager().register(TEXTURE, supporter.textureName == null ? "none" : supporter.textureName);

    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 10 * (1 + getLevel());
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootHandler.ADVANCED_VAMPIRE;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if (world.getDifficulty() == EnumDifficulty.HARD) {
            //Only break doors on hard difficulty
            this.tasks.addTask(1, new EntityAIBreakDoor(this));
            ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
        }
        this.tasks.addTask(2, new VampireAIRestrictSun(this));
        this.tasks.addTask(3, new VampireAIFleeSun(this, 0.9, false));
        this.tasks.addTask(3, new VampireAIFleeGarlic(this, 0.9, false));
        this.tasks.addTask(4, new EntityAIAttackMeleeNoSun(this, 1.0, false));
        this.tasks.addTask(8, new EntityAIWander(this, 0.9, 25));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 13F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityHunterBase.class, 17F));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH + Balance.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH_PL * l);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE + Balance.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE_PL * l);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.ADVANCED_VAMPIRE_SPEED);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(13);
    }

    @Override
    public EntityClassType getEntityClass() {
        return entityclass;
    }

    @Override
    public EntityActionTier getEntityTier() {
        return entitytier;
    }


}
