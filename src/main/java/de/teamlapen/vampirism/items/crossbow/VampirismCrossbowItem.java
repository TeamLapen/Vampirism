package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public abstract class VampirismCrossbowItem extends CrossbowItem implements IFactionLevelItem<IHunterPlayer>, IVampirismCrossbow {

    private final ItemTier itemTier;
    private final float arrowVelocity;
    private final int chargeTime;

    public VampirismCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ItemTier itemTier) {
        super(properties);
        this.arrowVelocity = arrowVelocity;
        this.chargeTime = chargeTime;
        this.itemTier = itemTier;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flag) {
        super.appendHoverText(stack, level, tooltips, flag);
        this.addFactionLevelToolTip(stack, level, tooltips, flag,  VampirismMod.proxy.getClientPlayer());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.INFINITY_ARROWS;
    }

    @Override
    public boolean isValidRepairItem(ItemStack crossbow, ItemStack repairItem) {
        return Tags.Items.STRING.contains(repairItem.getItem()) || super.isValidRepairItem(crossbow, repairItem);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return this.itemTier.getEnchantmentValue();
    }

    @Override
    public int getMinLevel(@Nonnull ItemStack stack) {
        return 0;
    }

    @Nullable
    @Override
    public IPlayableFaction<IHunterPlayer> getUsingFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return (stack) -> false;
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return getChargeDurationMod(p_77626_1_) + 3;
    }

    /**
     * same as {@link net.minecraft.item.CrossbowItem#use(net.minecraft.world.World, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand)}<br>
     * check comments for changes
     * TODO 1.19 recheck
     */
    public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
        ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
        if (isCharged(itemstack)) {
            performShootingMod(p_77659_1_, p_77659_2_, p_77659_3_, itemstack, getShootingPowerMod(itemstack), 1.0F); //call modded shoot function with shooting power
            setCharged(itemstack, false);
            return ActionResult.consume(itemstack);
        } else if (!p_77659_2_.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                p_77659_2_.startUsingItem(p_77659_3_);
            }

            return ActionResult.consume(itemstack);
        } else {
            return ActionResult.fail(itemstack);
        }
    }

    public void performShootingMod(World p_220014_0_, LivingEntity p_220014_1_, Hand p_220014_2_, ItemStack p_220014_3_, float p_220014_4_, float p_220014_5_) {
        performShooting(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, p_220014_4_, p_220014_5_);
    }

    public float getShootingPowerMod(ItemStack p_220013_0_) {
        return 3.15F * this.arrowVelocity;
    }

    @Override
    public void releaseUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity p_77615_3_, int p_77615_4_) {
        int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
        float f = getPowerForTimeMod(i, p_77615_1_);
        if (f >= 1.0F && !isCharged(p_77615_1_) && tryLoadProjectiles(p_77615_3_, p_77615_1_)) {
            setCharged(p_77615_1_, true);
            SoundCategory soundcategory = p_77615_3_ instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            p_77615_2_.playSound((PlayerEntity)null, p_77615_3_.getX(), p_77615_3_.getY(), p_77615_3_.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    public float getPowerForTimeMod(int p_220031_0_, ItemStack p_220031_1_) {
        float f = (float)p_220031_0_ / (float)getChargeDurationMod(p_220031_1_);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public int getChargeDurationMod(ItemStack p_220026_0_) {
        return this.chargeTime;
    }
}
