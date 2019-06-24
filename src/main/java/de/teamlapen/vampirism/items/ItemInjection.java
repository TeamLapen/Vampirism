package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Item with different injection types
 */
public class ItemInjection extends VampirismItem {


    private final static String regName = "injection";
    private final TYPE type;

    public ItemInjection(TYPE type) {
        super(regName + "_" + type.getName(), new Properties());
        this.type = type;
    }




    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (type == TYPE.SANGUINARE) {
            IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(playerIn);
            if (handler.getCurrentLevel(VReference.HUNTER_FACTION) > 0) {
                //playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_REVERT_BACK, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ); TODO 1.14

            } else {
                if (Helper.canBecomeVampire(playerIn)) {
                    if (Configs.disable_fang_infection) {
                        playerIn.sendStatusMessage(new TextComponentTranslation("text.vampirism.deactivated_by_serveradmin"), true);
                    } else {
                        PotionSanguinare.addRandom(playerIn, true);
                        playerIn.addPotionEffect(new PotionEffect(MobEffects.POISON, 60));
                    }
                } else if (Helper.isVampire(playerIn)) {
                    playerIn.sendMessage(new TextComponentTranslation("text.vampirism.already_vampire"));

                }
            }
            stack.shrink(1);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
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
