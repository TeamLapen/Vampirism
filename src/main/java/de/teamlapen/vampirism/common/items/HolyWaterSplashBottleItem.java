package de.teamlapen.vampirism.common.items;

import de.teamlapen.lib.util.UtilLib;
import de.teamlapen.vampirism.api.blocks.HolyWaterEffectConsumer;
import de.teamlapen.vampirism.common.tags.ModBiomeTags;
import de.teamlapen.vampirism.common.entity.ThrowableItemEntity;
import de.teamlapen.vampirism.common.util.DamageHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Splash version of the holy water bottle
 *
 * @author maxanier
 */
public class HolyWaterSplashBottleItem extends HolyWaterBottleItem implements ThrowableItemEntity.IVampirismThrowableItem, ProjectileItem {

    public HolyWaterSplashBottleItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public void onImpact(ThrowableItemEntity entity, ItemStack stack, HitResult impact, boolean remote) {
        if (!remote) {
            impactEntities(entity, stack, impact);
            impactBlocks(entity, stack, impact);
            entity.getCommandSenderWorld().levelEvent(2002, entity.blockPosition(), new PotionContents(Potions.MUNDANE).getColor());
        }
    }

    protected void impactEntities(ThrowableItemEntity bottleEntity, ItemStack stack, HitResult impact) {
        AABB impactArea = bottleEntity.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        List<LivingEntity> list1 = bottleEntity.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, impactArea);
        @Nullable Entity thrower = bottleEntity.getOwner();

        if (!list1.isEmpty()) {
            for (LivingEntity entity : list1) {
                if (thrower instanceof Player source && entity instanceof Player target && !source.canHarmPlayer(target)) {
                    continue;
                }
                DamageHandler.affectEntityHolyWaterSplash(entity, getStrength(getVampirismTier()), bottleEntity.distanceToSqr(entity), impact.getType() == HitResult.Type.ENTITY, thrower instanceof LivingEntity ? (LivingEntity) thrower : null);
            }
        }
    }

    protected void impactBlocks(ThrowableItemEntity bottleEntity, ItemStack stack, HitResult impact) {
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


    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

        if (level instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileFromRotation(ThrowableItemEntity::new, serverLevel, stack, player, -20, ThrowablePotionItem.PROJECTILE_SHOOT_POWER, 1);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);

        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return new ThrowableItemEntity(level, pos.x(), pos.y(), pos.z(), stack.copy());
    }
}
