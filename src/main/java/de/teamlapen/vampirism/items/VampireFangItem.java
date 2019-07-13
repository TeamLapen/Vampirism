package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class VampireFangItem extends VampirismItem {

    private static final String name = "vampire_fang";

    public VampireFangItem() {
        super(name, new Properties().group(VampirismMod.creativeTab));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            if (VampirismConfig.SERVER.disableFangInfection.get()) {
                playerIn.sendStatusMessage(new TranslationTextComponent("text.vampirism.deactivated_by_serveradmin"), true);
            } else {
                if (Helper.canBecomeVampire(playerIn)) {
                    PotionSanguinare.addRandom(playerIn, true);
                    playerIn.addPotionEffect(new EffectInstance(Effects.POISON, 60));
                } else {
                    if (Helper.isVampire(playerIn)) {
                        playerIn.sendMessage(new TranslationTextComponent("text.vampirism.already_vampire"));
                    } else {
                        playerIn.sendMessage(new TranslationTextComponent("text.vampirism.immune_to").appendSibling(new TranslationTextComponent(ModPotions.sanguinare.getName())));
                    }
                }
                stack.shrink(1);
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

}
