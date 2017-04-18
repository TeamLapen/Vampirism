package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemVampireFang extends VampirismItem {

    private static final String name = "vampire_fang";

    public ItemVampireFang() {
        super(name);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            if (Helper.canBecomeVampire(playerIn)) {
                PotionSanguinare.addRandom(playerIn, true);
                playerIn.addPotionEffect(new PotionEffect(MobEffects.POISON, 60));
            } else {
                if (Helper.isVampire(playerIn)) {
                    playerIn.sendMessage(new TextComponentTranslation("text.vampirism.already_vampire"));
                } else {
                    playerIn.sendMessage(new TextComponentTranslation("text.vampirism.immune_to_").appendSibling(new TextComponentTranslation(ModPotions.sanguinare.getName())));
                }
            }
            ItemStackUtil.decr(stack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

}
