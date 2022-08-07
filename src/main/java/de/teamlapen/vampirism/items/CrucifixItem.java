package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CrucifixItem extends Item implements IItemWithTier, IFactionExclusiveItem, IFactionLevelItem<IHunterPlayer> {

    private final TIER tier;
    /**
     * All crucifix items are added to this set. This is used to add cooldown for all existing crucifix items at once.
     * Synchronized set, so no issues during Mod creation. Later on no synchronicity needed.
     */
    private static final Set<CrucifixItem> all_crucifix = Collections.synchronizedSet(new HashSet<>());

    public CrucifixItem(IItemWithTier.TIER tier) {
        super(new Properties().tab(VampirismMod.creativeTab).stacksTo(1));
        this.tier = tier;
        all_crucifix.add(this);
    }

    @Override
    public int getMinLevel(@Nonnull ItemStack stack) {
        return 1;
    }

    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        if (tier == TIER.ULTIMATE) return HunterSkills.ULTIMATE_CRUCIFIX.get();
        return HunterSkills.CRUCIFIX_WIELDER.get();
    }

    @Nullable
    @Override
    public IPlayableFaction<IHunterPlayer> getUsingFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public UseAction getUseAnimation(ItemStack p_77661_1_) {
        return UseAction.NONE;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return ActionResult.consume(itemstack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);
        PlayerEntity playerEntity = VampirismMod.proxy.getClientPlayer();
        this.addFactionPoisonousToolTip(stack, worldIn, tooltip, flagIn, playerEntity);

    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean held) {
        if (held && entity instanceof LivingEntity && entity.tickCount % 16 == 8) {
            if (Helper.isVampire(entity)) {
                ((LivingEntity) entity).addEffect(new EffectInstance(ModEffects.POISON.get(), 20, 1));
            }
        }
    }

    protected boolean affectsEntity(LivingEntity e) {
        return e.getMobType() == CreatureAttribute.UNDEAD || Helper.isVampire(e);
    }


    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int p_77615_4_) {
        if (entity instanceof PlayerEntity) {
            all_crucifix.forEach(item -> {
                ((PlayerEntity) entity).getCooldowns().addCooldown(item, getCooldown(stack));
            });
        }
    }

    protected int getCooldown(ItemStack stack) {
        switch (tier) {
            case ENHANCED:
                return 100;
            case ULTIMATE:
                return 60;
            default:
                return 140;
        }
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }

    protected static int determineEntityTier(LivingEntity e) {
        if (e instanceof PlayerEntity) {
            int level = VampirismPlayerAttributes.get((PlayerEntity) e).vampireLevel;
            int tier = 1;
            if (level == VReference.VAMPIRE_FACTION.getHighestReachableLevel()) {
                tier = 3;
            } else if (level >= 8) {
                tier = 2;
            }
            if (VampirePlayer.getOpt((PlayerEntity) e).map(VampirePlayer::getSkillHandler).map(h -> h.isRefinementEquipped(ModRefinements.CRUCIFIX_RESISTANT.get())).orElse(false)) {
                tier++;
            }
            return tier;
        } else if (e instanceof VampireBaronEntity) {
            return 3;
        } else if (e instanceof AdvancedVampireEntity) {
            return 2;
        }
        return 1;
    }

    protected double determineSlowdown(int entityTier) {
        switch (tier) {
            case NORMAL:
                return entityTier > 1 ? 0.1 : 0.5;
            case ENHANCED:
                return entityTier > 2 ? 0.1 : 0.5;
            case ULTIMATE:
                return entityTier > 3 ? 0.3 : 0.5;
        }
        return 0;
    }

    protected int getRange(ItemStack stack) {
        switch (tier) {
            case ENHANCED:
                return 8;
            case ULTIMATE:
                return 10;
            default:
                return 4;
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        for (LivingEntity nearbyEntity : player.level.getNearbyEntities(LivingEntity.class, new EntityPredicate().selector(this::affectsEntity), player, player.getBoundingBox().inflate(getRange(stack)))) {
            Vector3d baseVector = player.position().subtract(nearbyEntity.position()).multiply(1, 0, 1).normalize(); //Normalized horizontal (xz) vector giving the direction towards the holder of this crucifix
            Vector3d oldDelta = nearbyEntity.getDeltaMovement();
            Vector3d horizontalDelta = oldDelta.multiply(1, 0, 1);
            double parallelScale = baseVector.dot(horizontalDelta);
            if (parallelScale > 0) {
                Vector3d parallelPart = baseVector.scale(parallelScale); //Part of delta that is parallel to baseVector
                double scale = determineSlowdown(determineEntityTier(nearbyEntity));
                Vector3d newDelta = oldDelta.subtract(parallelPart.scale(scale)); //Substract parallel part from old Delta (scaled to still allow some movement)
                if (newDelta.lengthSqr() > oldDelta.lengthSqr()) { //Just to make sure we do not speed up the movement even though this should not be possible
                    newDelta = Vector3d.ZERO;
                }
                //Unfortunately, Vanilla converts y-collision with ground into forward movement later on (in #move)
                //Therefore, we check for collision here and remove any y component if entity would collide with ground
                Vector3d collisionDelta = nearbyEntity.collide(newDelta);
                if (collisionDelta.y != newDelta.y && newDelta.y < 0) {
                    newDelta = newDelta.multiply(1, 0, 1);
                }

                nearbyEntity.setDeltaMovement(newDelta);
            }
        }
    }


}
