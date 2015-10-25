package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.SupporterManager;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Second type of vampire hunter. Respresents a supporter and is strong
 */
public class EntityVampireHunter2 extends EntityHunterBase implements ISyncable, IRangedAttackMob {

    private String textureName;

    private int type;
    private final int MAX_TYPES = 3;
    private final static String TAG = "Hunter2";

    public String getSenderName() {
        return senderName;
    }

    public String getTextureName() {
        return textureName;
    }

    private String senderName;

    /**
     * Determines the outfit of the hunter.
     * The lowest three bits are used for the outfit.
     * The next three bits for the hat.
     */
    private int outfit;

    public boolean shouldRenderDefaultWeapons() {
        return renderDefaultWeapons;
    }

    private boolean renderDefaultWeapons = true;

    private EntityAIBase arrowAttackTask = new EntityAIArrowAttack(this, 1.0D, 60, 13F);
    private EntityAIBase collideAttackTask = new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.1D, false);
    private EntityAIAvoidEntity fleeTask = new EntityAIAvoidEntity(this, EntityPlayer.class, 16, 0.8, 1.5);

    public EntityVampireHunter2(World world) {
        super(world);
        this.getNavigator().setBreakDoors(true);
        this.setSize(0.6F, 1.8F);

        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(3, new EntityAIMoveTowardsRestriction(this, 1.0F));

        this.tasks.addTask(6, new EntityAIWander(this, 0.7));
        this.tasks.addTask(9, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));

        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new IEntitySelector() {

            @Override
            public boolean isEntityApplicable(Entity entity) {
                if (entity instanceof EntityPlayer) {
                    return VampirePlayer.get((EntityPlayer) entity).getLevel() > BALANCE.VAMPIRE_HUNTER_ATTACK_LEVEL;
                }
                return false;
            }

        }));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVampireBase.class, 0, true));

        outfit = getRNG().nextInt(64);

        SupporterManager.Supporter s = SupporterManager.getInstance().getRandom(getRNG());
        textureName = s.textureName;
        senderName = s.senderName;
        updateType(getRNG().nextInt(MAX_TYPES));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes();

    }

    protected void updateEntityAttributes() {
        int l = 4;
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_HUNTER_MAX_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_HUNTER_ATTACK_DAMAGE * l);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_HUNTER_MOVEMENT_SPEED);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setString("textureName", textureName);
        nbt.setString("senderName", senderName == null ? "null" : senderName);
        nbt.setInteger("outfit", outfit);
        nbt.setInteger("type", type);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.loadUpdateFromNBT(nbt);

    }

    protected void updateType(int type) {
        tasks.removeTask(arrowAttackTask);
        tasks.removeTask(collideAttackTask);
        tasks.removeTask(fleeTask);
        this.setCurrentItemOrArmor(0, null);
        renderDefaultWeapons = false;

        if (type == 0) {
            renderDefaultWeapons = true;


            tasks.addTask(2, collideAttackTask);
        } else if (type == 1) {

            this.setCurrentItemOrArmor(0, new ItemStack(ModItems.garlicBomb));

            tasks.addTask(2, arrowAttackTask);
        } else if (type == 2) {
            tasks.addTask(2, fleeTask);
        }
        this.type = type;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    protected Item getDropItem() {
        return null;
    }


    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("textureName")) {
            textureName = nbt.getString("textureName");
        }
        if (nbt.hasKey("senderName")) {
            senderName = nbt.getString("senderName");
            if (senderName.equals("null")) senderName = null;
        }
        if (nbt.hasKey("type")) {
            updateType(nbt.getInteger("type"));
        }
        if (nbt.hasKey("outfit")) {
            outfit = nbt.getInteger("outfit");
        }
    }

    public int getOutfit(int part) {
        return outfit >> (part * 3);
    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {
        nbt.setString("senderName", senderName == null ? "null" : senderName);
        nbt.setString("textureName", textureName);
        nbt.setInteger("type", type);
        nbt.setInteger("outfit", outfit);
    }

    @Override
    public String getCommandSenderName() {
        return senderName == null ? super.getCommandSenderName() : senderName;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_) {
        EntityGarlicBomb garlicBomb = new EntityGarlicBomb(this.worldObj, this);
        garlicBomb.rotationPitch -= -20.0F;
        double d0 = p_82196_1_.posX + p_82196_1_.motionX - this.posX;
        double d1 = p_82196_1_.posY + (double) p_82196_1_.getEyeHeight() - 1.100000023841858D - this.posY;
        double d2 = p_82196_1_.posZ + p_82196_1_.motionZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);


        garlicBomb.setThrowableHeading(d0, d1 + (double) (f1 * 0.2F), d2, 0.75F, 8.0F);
        this.worldObj.spawnEntityInWorld(garlicBomb);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
