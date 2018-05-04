package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * Item with different injection types
 */
public class ItemInjection extends VampirismItem {

    public final static int META_GARLIC = 1;
    public final static int META_SANGUINARE = 2;
    public final static int META_COUNT = 3;
    private final static String regName = "injection";

    public ItemInjection() {
        super(regName);
        this.hasSubtypes = true;
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (int i = 0; i < META_COUNT; i++) {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        switch (stack.getMetadata()) {
            case META_GARLIC:
                return super.getUnlocalizedName(stack) + ".garlic";
            case META_SANGUINARE:
                return super.getUnlocalizedName(stack) + ".sanguinare";
            default:
                return super.getUnlocalizedName(stack);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (stack.getMetadata() == META_SANGUINARE) {
            IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(playerIn);
            if (handler.getCurrentLevel(VReference.HUNTER_FACTION) > 0) {
                playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_REVERT_BACK, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);

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
            ItemStackUtil.decr(stack);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }


}
