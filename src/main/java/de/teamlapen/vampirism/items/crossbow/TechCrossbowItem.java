package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IArrowContainer;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.mixin.accessor.CrossbowItemMixin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TechCrossbowItem extends VampirismCrossbowItem {

    public TechCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, Tier itemTier, Supplier<ISkill<IHunterPlayer>> requiredSkill) {
        super(properties, arrowVelocity, chargeTime, itemTier, requiredSkill);
    }


    @Nonnull
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() instanceof IArrowContainer && !((IArrowContainer) stack.getItem()).getArrows(stack).isEmpty();
    }

    @Override
    protected void shoot(@NotNull Level level, Player player, @NotNull InteractionHand interactionHand, ItemStack itemstack) {
        if(performShooting(level, player, interactionHand, itemstack, getShootingPowerMod(itemstack), 1.0F)) { // do not set uncharged if projectiles left | get shooting power from crossbow
            setCharged(itemstack, false);
        } else {
            boolean faster = HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.CROSSBOW_TECHNIQUE);
            player.getCooldowns().addCooldown(this, faster ? 5 : 10); // add cooldown if projectiles left
        }
    }

    @Override
    public boolean performShooting(Level level, LivingEntity livingEntity, InteractionHand hand, ItemStack crossbow, float speed, float angle) {
        List<ItemStack> list = getChargedProjectiles(crossbow);
        float[] afloat = getShotPitches(livingEntity.getRandom());

        ItemStack itemstack = getProjectile(livingEntity, crossbow, list); //delegate for easy usage and frugality
        boolean flag = !(livingEntity instanceof Player player) || player.getAbilities().instabuild;
        if (!itemstack.isEmpty()) {
            shootProjectile(level, livingEntity, hand, crossbow, itemstack, afloat[0], flag, speed, angle); // do not shoot more than one projectile
        }

        onCrossbowShot(level, livingEntity, crossbow);
        setChargedProjectiles(crossbow, list); // set the loaded projectiles
        return list.isEmpty();
    }

    private void setChargedProjectiles(ItemStack crossbow, List<ItemStack> projectiles) {
        CompoundTag crossbowTag = crossbow.getOrCreateTag();
        ListTag list = new ListTag();
        projectiles.forEach(stack -> {
            CompoundTag stackNbt = new CompoundTag();
            stack.save(stackNbt);
            list.add(stackNbt);
        });
        crossbowTag.put("ChargedProjectiles", list);
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
    protected boolean canBeInfinite(ItemStack crossbow) {
        return false;
    }

    @Override
    public int getChargeDuration(ItemStack crossbow) {
        return this.chargeTime;
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack, @NotNull Enchantment enchantment) {
        return enchantment != Enchantments.QUICK_CHARGE && enchantment != Enchantments.INFINITY_ARROWS && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean canSelectAmmunition(ItemStack crossbow) {
        return false;
    }

    @Override
    public Optional<Item> getAmmunition(ItemStack crossbow) {
        return Optional.empty();
    }

    @Override
    public float[] getShotPitches(RandomSource pRandom) {
        return new float[]{ 1 };
    }
}
