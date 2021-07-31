package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class VampireFangItem extends VampirismItem {

    private static final String name = "vampire_fang";

    public VampireFangItem() {
        super(name, new Properties().tab(VampirismMod.creativeTab));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {

        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            if (VampirismConfig.SERVER.disableFangInfection.get()) {
                playerIn.displayClientMessage(new TranslationTextComponent("text.vampirism.deactivated_by_serveradmin"), true);
            } else {
                if (Helper.canBecomeVampire(playerIn)) {
                    SanguinareEffect.addRandom(playerIn, true);
                    playerIn.addEffect(new EffectInstance(ModEffects.poison, 60));
                } else {
                    if (Helper.isVampire(playerIn)) {
                        playerIn.displayClientMessage(new TranslationTextComponent("text.vampirism.already_vampire"), true);
                    } else {
                        playerIn.displayClientMessage(new TranslationTextComponent("text.vampirism.immune_to").append(new TranslationTextComponent(ModEffects.sanguinare.getDescriptionId())), true);
                    }
                }
                stack.shrink(1);
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

}
