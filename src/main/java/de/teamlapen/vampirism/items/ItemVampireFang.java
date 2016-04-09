package de.teamlapen.vampirism.items;

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

public class ItemVampireFang extends VampirismItem {

    private static final String name = "vampireFang";

    public ItemVampireFang() {
        super(name);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {

        if (Helper.canBecomeVampire(playerIn)) {
            PotionSanguinare.addRandom(playerIn, true);
            playerIn.addPotionEffect(new PotionEffect(MobEffects.poison, 60));
        } else {
            if (Helper.isVampire(playerIn)) {
                playerIn.addChatMessage(new TextComponentTranslation("text.vampirism.already_vampire"));
            } else {
                playerIn.addChatMessage(new TextComponentTranslation("text.vampirism.immune_to_").appendSibling(new TextComponentTranslation(ModPotions.sanguinare.getName())));
            }
        }
        itemStackIn.stackSize--;
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

}
