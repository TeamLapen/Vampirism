package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.blocks.HolyWaterEffectConsumer;
import de.teamlapen.vampirism.core.tags.ModBiomeTags;
import de.teamlapen.vampirism.entity.ThrowableItemEntity;
import de.teamlapen.vampirism.util.DamageHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Splash version of the holy water bottle
 *
 * @author maxanier
 */
public class HolyWaterSplashBottleItem extends HolyWaterBottleItem implements ThrowableItemEntity.IVampirismThrowableItem, ProjectileItem {

    public HolyWaterSplashBottleItem(TIER tier) {
        super(tier, new Properties());
    }

    @Override
    public void onImpact(@NotNull ThrowableItemEntity entity, ItemStack stack, @NotNull HitResult result, boolean remote) {
        if (!remote) {
            impactEntities(entity, stack, result, remote);
            impactBlocks(entity, stack, result, remote);
            entity.getCommandSenderWorld().levelEvent(2002, entity.blockPosition(), PotionContents.getColor(Potions.MUNDANE));
        }
    }

    protected void impactEntities(@NotNull ThrowableItemEntity bottleEntity, ItemStack stack, @NotNull HitResult result, boolean remote) {
        AABB impactArea = bottleEntity.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        List<LivingEntity> list1 = bottleEntity.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, impactArea);
        @Nullable Entity thrower = bottleEntity.getOwner();

        if (!list1.isEmpty()) {
            for (LivingEntity entity : list1) {
                if (thrower instanceof Player source && entity instanceof Player target && !source.canHarmPlayer(target)) {
                    continue;
                }
                DamageHandler.affectEntityHolyWaterSplash(entity, getStrength(getVampirismTier()), bottleEntity.distanceToSqr(entity), result.getType() == HitResult.Type.ENTITY, thrower instanceof LivingEntity ? (LivingEntity) thrower : null);
            }
        }
    }

    protected void impactBlocks(@NotNull ThrowableItemEntity bottleEntity, ItemStack stack, @NotNull HitResult result, boolean remote) {
        Level level = bottleEntity.getCommandSenderWorld();
        if (level.getBiome(bottleEntity.blockPosition()).is(ModBiomeTags.HasFaction.IS_FACTION_BIOME)) {
            return;
        }
        int size = switch (getVampirismTier()) {
            case NORMAL -> 1;
            case ENHANCED -> 2;
            case ULTIMATE -> 3;
        };
        AABB impactArea = bottleEntity.getBoundingBox().inflate(size);
        UtilLib.forEachBlockPos(impactArea, pos -> {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof HolyWaterEffectConsumer consumer) {
                consumer.onHolyWaterEffect(level, state, pos, stack, getVampirismTier());
            }
        });
    }


    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);


        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (playerIn.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isClientSide) {
            ThrowableItemEntity entityThrowable = new ThrowableItemEntity(worldIn, playerIn);
            ItemStack throwStack = stack.copy();
            throwStack.setCount(1);
            entityThrowable.setItem(throwStack);
            entityThrowable.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), -20.0F, 0.5F, 1.0F);
            worldIn.addFreshEntity(entityThrowable);
        }

        if (!playerIn.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

    }

    @Override
    public Projectile asProjectile(Level pLevel, Position pPos, ItemStack pStack, Direction pDirection) {
        ThrowableItemEntity entityThrowable = new ThrowableItemEntity(pLevel, pPos.x(), pPos.y(), pPos.z());
        ItemStack throwStack = pStack.copy();
        throwStack.setCount(1);
        entityThrowable.setItem(throwStack);
        return entityThrowable;
    }
}
