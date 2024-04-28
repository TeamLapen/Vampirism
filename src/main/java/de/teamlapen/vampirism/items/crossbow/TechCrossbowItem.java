package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IArrowContainer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.mixin.accessor.CrossbowItemMixin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static net.minecraft.world.item.CrossbowItem.isCharged;

public class TechCrossbowItem extends VampirismCrossbowItem {

    public TechCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, Tier itemTier) {
        super(properties, arrowVelocity, chargeTime, itemTier);
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.TECH_WEAPONS.get();
    }

    @Nonnull
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() instanceof IArrowContainer && !((IArrowContainer) stack.getItem()).getArrows(stack).isEmpty();
    }

    /**
     * same as {@link net.minecraft.world.item.CrossbowItem#use(net.minecraft.world.level.Level, net.minecraft.world.entity.player.Player, net.minecraft.world.InteractionHand)}
     * <br>
     * check comments
     * TODO 1.19 recheck
     */
    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level p_77659_1_, Player p_77659_2_, @Nonnull InteractionHand p_77659_3_) {
        ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
        if (isCharged(itemstack)) {
            if (!performShootingMod(p_77659_1_, p_77659_2_, p_77659_3_, itemstack, getShootingPowerMod(itemstack), 1.0F)) {
                p_77659_2_.getCooldowns().addCooldown(this, 10); // add cooldown if projectiles left
            }
            return InteractionResultHolder.consume(itemstack);
        } else if (!p_77659_2_.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                ((CrossbowItemMixin) this).setStartSoundPlayed(false);
                ((CrossbowItemMixin) this).setMidLoadSoundPlayer(false);
                p_77659_2_.startUsingItem(p_77659_3_);
            }

            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    /**
     * same as {@link net.minecraft.world.item.CrossbowItem#performShooting(net.minecraft.world.level.Level, net.minecraft.world.entity.LivingEntity, net.minecraft.world.InteractionHand, net.minecraft.world.item.ItemStack, float, float)}
     * <br>
     * TODO 1.19 recheck
     */
    public boolean performShootingMod(Level p_220014_0_, LivingEntity p_220014_1_, InteractionHand p_220014_2_, ItemStack p_220014_3_, float p_220014_4_, float p_220014_5_) {
        List<ItemStack> list = p_220014_3_.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).getItems();

        ItemStack itemstack = getProjectile(p_220014_1_, p_220014_3_, list); //delegate for easy usage and frugality
        boolean flag = !(p_220014_1_ instanceof Player player) || player.getAbilities().instabuild;
        if (!itemstack.isEmpty()) {
            shootProjectileMod(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, itemstack, CrossbowItemMixin.getShotPitches(p_220014_1_.getRandom(), 0), flag, p_220014_4_, p_220014_5_); // do not shoot more than one projectile
        }

        onShot(p_220014_1_, p_220014_3_);
        setChargedProjectiles(p_220014_3_, list); // set the loaded projectiles
        return list.isEmpty();
    }

    private static void setChargedProjectiles(ItemStack p_220014_0_, List<ItemStack> p_220014_1_) {
        p_220014_0_.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(p_220014_1_));
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack crossbow, ItemStack repairItem) {
        return repairItem.is(Tags.Items.INGOTS_IRON);
    }

    private ItemStack getProjectile(LivingEntity entity, ItemStack crossbow, List<ItemStack> projectiles) {
        int frugal = isFrugal(crossbow);
        if (frugal > 0 && entity.getRandom().nextInt(Math.max(2, 4 - frugal)) == 0) {
            return projectiles.get(0).copy();
        }
        return projectiles.remove(0);
    }

    @Override
    protected boolean canBeInfinit(ItemStack crossbow) {
        return false;
    }

    @Override
    public int getChargeDurationMod(ItemStack crossbow) {
        return this.chargeTime;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.QUICK_CHARGE && enchantment != Enchantments.INFINITY && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean canSelectAmmunition(ItemStack crossbow) {
        return false;
    }

    @Override
    public Optional<Item> getAmmunition(ItemStack crossbow) {
        return Optional.empty();
    }

}
