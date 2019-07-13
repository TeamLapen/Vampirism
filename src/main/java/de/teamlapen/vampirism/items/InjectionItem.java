package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Item with different injection types
 */
public class InjectionItem extends VampirismItem {


    private final static String regName = "injection";
    private final TYPE type;

    public InjectionItem(TYPE type) {
        super(regName + "_" + type.getName(), new Properties().group(VampirismMod.creativeTab));
        this.type = type;
    }




    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (type == TYPE.SANGUINARE) {
            IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(playerIn);
            if (handler.getCurrentLevel(VReference.HUNTER_FACTION) > 0) {
                //playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_REVERT_BACK, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ); TODO 1.14

            } else {
                if (Helper.canBecomeVampire(playerIn)) {
                    if (VampirismConfig.SERVER.disableFangInfection.get()) {
                        playerIn.sendStatusMessage(new TranslationTextComponent("text.vampirism.deactivated_by_serveradmin"), true);
                    } else {
                        PotionSanguinare.addRandom(playerIn, true);
                        playerIn.addPotionEffect(new EffectInstance(Effects.POISON, 60));
                    }
                } else if (Helper.isVampire(playerIn)) {
                    playerIn.sendMessage(new TranslationTextComponent("text.vampirism.already_vampire"));

                }
            }
            stack.shrink(1);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }


    public enum TYPE implements IStringSerializable {
        EMPTY("empty"), GARLIC("garlic"), SANGUINARE("sanguinare");

        private final String name;

        TYPE(String name) {
            this.name = name;
        }

        @Override
        @Nonnull
        public String getName() {
            return name;
        }
    }
}
