package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.SupporterManager;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Second type of vampire hunter. Respresents a supporter and is strong
 */
public class EntityVampireHunter2 extends EntityHunterBase implements ISyncable {

    private String textureName;

    public String getSenderName() {
        return senderName;
    }

    public String getTextureName() {
        return textureName;
    }

    private String senderName;

    public EntityVampireHunter2(World world) {
        super(world);
        this.getNavigator().setBreakDoors(true);
        this.setSize(0.6F, 1.8F);

        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityVampire.class, 1.1, false));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.1, false));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityCreature.class, 0.9, false));
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

        SupporterManager.Supporter s = SupporterManager.getInstance().getRandom(getRNG());
        textureName = s.textureName;
        senderName = s.senderName;
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
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.loadUpdateFromNBT(nbt);

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
    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {
        nbt.setString("senderName", senderName == null ? "null" : senderName);
        nbt.setString("textureName", textureName);
    }

    @Override
    public String getCommandSenderName() {
        return senderName == null ? super.getCommandSenderName() : senderName;
    }
}
