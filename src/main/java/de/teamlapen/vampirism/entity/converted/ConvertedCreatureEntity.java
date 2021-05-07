package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.convertible.ICurableConvertedCreature;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.goals.AttackMeleeNoSunGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

/**
 * Converted creature class.
 * Contains (stores and syncs) a normal Entity for rendering purpose
 */
public class ConvertedCreatureEntity<T extends CreatureEntity> extends VampireBaseEntity implements ICurableConvertedCreature<T>, ISyncable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DataParameter<Boolean> CONVERTING = EntityDataManager.createKey(ConvertedCreatureEntity.class, DataSerializers.BOOLEAN);


    public static boolean spawnPredicate(EntityType<? extends ConvertedCreatureEntity> entityType, IWorld iWorld, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return (iWorld.getBlockState(blockPos.down()).getBlock() == Blocks.GRASS_BLOCK || iWorld.getBlockState(blockPos.down()).getBlock() == ModBlocks.cursed_earth) && iWorld.getLightSubtracted(blockPos, 0) > 8;
    }

    private T entityCreature;
    private boolean entityChanged = false;
    private boolean canDespawn = false;
    @Nullable
    private ITextComponent name;
    private int conversionTime;
    private UUID conversationStarter;

    public ConvertedCreatureEntity(EntityType<? extends ConvertedCreatureEntity> type, World world) {
        super(type, world, false);
        this.enableImobConversion();
    }

    @Override
    public DataParameter<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.registerConvertingData(this);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!nil()) {
            entityCreature.copyLocationAndAnglesFrom(this);
            entityCreature.prevPosZ = this.prevPosZ;
            entityCreature.prevPosY = this.prevPosY;
            entityCreature.prevPosX = this.prevPosX;
            entityCreature.rotationYawHead = this.rotationYawHead;
            entityCreature.prevRotationPitch = this.prevRotationPitch;
            entityCreature.prevRotationYaw = this.prevRotationYaw;
            entityCreature.prevRotationYawHead = this.prevRotationYawHead;
            entityCreature.setMotion(this.getMotion());
            entityCreature.lastTickPosX = this.lastTickPosX;
            entityCreature.lastTickPosY = this.lastTickPosY;
            entityCreature.lastTickPosZ = this.lastTickPosZ;
            entityCreature.hurtTime = this.hurtTime;
            entityCreature.maxHurtTime = this.maxHurtTime;
            entityCreature.attackedAtYaw = this.attackedAtYaw;
            entityCreature.swingProgress = this.swingProgress;
            entityCreature.prevSwingProgress = this.prevSwingProgress;
            entityCreature.prevLimbSwingAmount = this.prevLimbSwingAmount;
            entityCreature.limbSwingAmount = this.limbSwingAmount;
            entityCreature.limbSwing = this.limbSwing;
            entityCreature.renderYawOffset = this.renderYawOffset;
            entityCreature.prevRenderYawOffset = this.prevRenderYawOffset;
            entityCreature.deathTime = this.deathTime;

//            if (world.isRemote) {
//                entityCreature.func_242277_a(this.func_242274_V()); //Careful not available on server, so if needed we have to use a proxy here
//            }
        }
        if (entityChanged) {
            this.updateEntityAttributes();
            entityChanged = false;
        }
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return super.canDespawn(distanceToClosestPlayer) && canDespawn;
    }

    @Nullable
    @Override
    public ItemEntity entityDropItem(ItemStack stack, float offsetY) {
        ItemStack actualDrop = stack;
        Item item = stack.getItem();
        if (item.isFood()) {
            if (item.getFood().isMeat()) {
                actualDrop = new ItemStack(Items.ROTTEN_FLESH, stack.getCount()); //Replace all meat with rotten flesh
            }
        }
        return super.entityDropItem(actualDrop, offsetY);
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        if (hasCustomName()) {
            return super.getName();
        }
        if (name == null) {
            this.name = new TranslationTextComponent("entity.vampirism.vampire").appendString(" ").append((nil() ? super.getName() : entityCreature.getName()));
        }
        return name;
    }
    public T getOldCreature() {
        return entityCreature;
    }

    @Override
    public void loadUpdateFromNBT(CompoundNBT nbt) {
        if (nbt.contains("entity_old")) {
            setEntityCreature((T) EntityType.loadEntityUnchecked(nbt.getCompound("entity_old"), getEntityWorld()).orElse(null));
        }
    }

    @Override
    public void playAmbientSound() {
        if (!nil()) {
            entityCreature.playAmbientSound();
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("entity_old")) {
            setEntityCreature((T) EntityType.loadEntityUnchecked(nbt.getCompound("entity_old"), world).orElse(null));
            if (nil()) {
                LOGGER.warn("Failed to create old entity {}. Maybe the entity does not exist anymore", nbt.getCompound("entity_old"));
            }
        } else {
            LOGGER.warn("Saved entity did not have a old entity");
        }
        if (nbt.contains("converted_canDespawn")) {
            canDespawn = nbt.getBoolean("converted_canDespawn");
        }
        if (nbt.contains("ConversionTime", 99) && nbt.getInt("ConversionTime") > -1) {
            this.startConverting(nbt.hasUniqueId("ConversionPlayer") ? nbt.getUniqueId("ConversionPlayer") : null, nbt.getInt("ConversionTime"),this);
        }
    }

    @Override
    public void recalculateSize() {
        super.recalculateSize();
        this.eyeHeight = entityCreature == null ? 0.5f : entityCreature.getEyeHeight();
    }

    /**
     * Allows the entity to despawn
     */
    public void setCanDespawn() {
        canDespawn = true;
    }

    /**
     * Set the old creature (the one before conversion)
     *
     * @param creature
     */
    public void setEntityCreature(T creature) {
        if ((creature == null && entityCreature != null)) {
            entityChanged = true;
            entityCreature = null;
        } else if (creature != null) {
            if (!creature.equals(entityCreature)) {
                entityCreature = creature;
                entityChanged = true;
                this.size = creature.size;
            }
        }
        if (entityCreature != null && getConvertedHelper() == null) {
            LOGGER.warn("Cannot find converting handler for converted creature {} ({})", this, entityCreature);
            entityCreature = null;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote && entityCreature == null) {
            LOGGER.debug("Setting dead, since creature is null");
            this.remove();
        }
    }

    @Nonnull
    @Override
    public String toString() {
        return "[" + super.toString() + " representing " + entityCreature + "]";
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        writeOldEntityToNBT(nbt);
        nbt.putBoolean("converter_canDespawn", canDespawn);
        nbt.putInt("ConversionTime", this.isConverting(this) ? this.conversionTime : -1);
        if (this.conversationStarter != null) {
            nbt.putUniqueId("ConversionPlayer", this.conversationStarter);
        }
    }

    @Override
    public void writeFullUpdateToNBT(CompoundNBT nbt) {
        writeOldEntityToNBT(nbt);

    }

    /**
     * @return The {@link de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler.IDefaultHelper} for this creature
     */
    @Nullable
    protected IConvertingHandler.IDefaultHelper getConvertedHelper() {
        if (nil()) return null;
        BiteableEntry biteableEntry = VampirismAPI.entityRegistry().getEntry(entityCreature);
        if (biteableEntry == null) {
            LOGGER.warn("Cannot find biteable entry for {}", entityCreature);
            return null;
        }
        IConvertingHandler handler = biteableEntry.convertingHandler;
        if (handler instanceof DefaultConvertingHandler) {
            return ((DefaultConvertingHandler) handler).getHelper();
        }
        return null;
    }

    @Override
    protected EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.converted_creature_imob : ModEntities.converted_creature;
    }

    @Nonnull
    @Override
    protected ResourceLocation getLootTable() {
        if (entityCreature != null) {
            return entityCreature.getLootTableResourceLocation();
        }
        return super.getLootTable();
    }

    protected boolean nil() {
        return entityCreature == null;
    }



    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AvoidEntityGoal<CreatureEntity>(this, CreatureEntity.class, 10, 1.0, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION)));
        //this.tasks.addTask(3, new FleeSunVampireGoal(this, 1F));
        this.goalSelector.addGoal(4, new RestrictSunGoal(this));
        this.goalSelector.addGoal(5, new AttackMeleeNoSunGoal(this, 0.9D, false));
        this.experienceValue = 2;

        this.goalSelector.addGoal(11, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(13, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(15, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<CreatureEntity>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    protected void updateEntityAttributes() {
        IConvertingHandler.IDefaultHelper helper = getConvertedHelper();
        try {
            if (helper != null) {
                this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(helper.getConvertedDMG((EntityType<? extends CreatureEntity>) entityCreature.getType()));
                this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(helper.getConvertedMaxHealth((EntityType<? extends CreatureEntity>) entityCreature.getType()));
                this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(helper.getConvertedKnockbackResistance((EntityType<? extends CreatureEntity>) entityCreature.getType()));
                this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(helper.getConvertedSpeed((EntityType<? extends CreatureEntity>) entityCreature.getType()));
            } else {
                this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
                this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0);
                this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0);
            }
        } catch (NullPointerException e) {
            LOGGER.error("Failed to update entity attributes for {} {}", this, e);
        }


    }

    /**
     * Write the old entity to nbt
     *
     * @param nbt
     */
    private void writeOldEntityToNBT(CompoundNBT nbt) {
        if (!nil()) {
            try {
                CompoundNBT entity = new CompoundNBT();
                entityCreature.removed = false;
                entityCreature.writeUnlessPassenger(entity);
                entityCreature.removed = true;
                nbt.put("entity_old", entity);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to write old entity (%s) to NBT. If this happens more often please report this to the mod author.", entityCreature), e);
                this.setEntityCreature(null);
            }
        }

    }

    @Override
    public void livingTick() {
        if (!this.world.isRemote && this.isAlive() && this.isConverting(this)) {
            --this.conversionTime;
            if (this.conversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.conversionTime = timer)) {
                this.cureEntity((ServerWorld)this.world, this, ((EntityType<T>) entityCreature.getType()));
            }
        }
        super.livingTick();
    }


    @Nonnull
    @Override
    protected ActionResultType func_230254_b_(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() != ModItems.cure_apple) return super.func_230254_b_(player, hand);
        return interactWithCureItem(player, stack, this);
    }

    @Override
    public void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, CreatureEntity entity) {
        ICurableConvertedCreature.super.startConverting(conversionStarterIn, conversionTimeIn, entity);
        this.conversationStarter = conversionStarterIn;
        this.conversionTime = conversionTimeIn;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (!handleSound(id, this)){
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public T createCuredEntity(CreatureEntity entity, EntityType<T> newType) {
        this.entityCreature.revive();
        return this.entityCreature;
    }

    public static class IMob extends ConvertedCreatureEntity implements net.minecraft.entity.monster.IMob {

        public IMob(EntityType<? extends ConvertedCreatureEntity> type, World world) {
            super(type, world);
        }
    }
}
