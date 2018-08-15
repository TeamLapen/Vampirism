package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;


public class ItemVampireBook extends VampirismItem {
    private static final String regName = "vampire_book";

    public static boolean validBookTagContents(NBTTagCompound nbt) {
        if (!ItemWritableBook.isNBTValid(nbt)) {
            return false;
        } else if (!nbt.hasKey("title", 8)) {
            return false;
        } else {
            String s = nbt.getString("title");
            return (s != null && s.length() <= 32) && nbt.hasKey("author", 8);
        }
    }

    public ItemVampireBook() {
        super(regName);
        this.setMaxStackSize(1);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            String s = nbttagcompound.getString("title");
            if (!StringUtils.isNullOrEmpty(s)) {
                return s;
            }
        }

        return super.getItemStackDisplayName(stack);
    }


    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(VampireBookManager.getInstance().getRandomBook(new Random()).setStackDisplayName(UtilLib.translate("item.vampirism.vampire_book.name")));

        }
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            this.resolveContents(stack, playerIn);
        }

        playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_VAMPIRE_BOOK, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }

    private void resolveContents(ItemStack stack, EntityPlayer player) {
        if (!stack.isEmpty() && stack.getTagCompound() != null) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            if (!nbttagcompound.getBoolean("resolved")) {
                nbttagcompound.setBoolean("resolved", true);
                if (validBookTagContents(nbttagcompound)) {
                    NBTTagList nbttaglist = nbttagcompound.getTagList("pages", 8);

                    for (int slot = 0; slot < nbttaglist.tagCount(); ++slot) {
                        String s = nbttaglist.getStringTagAt(slot);

                        Object lvt_7_1_;
                        try {
                            ITextComponent var11 = ITextComponent.Serializer.fromJsonLenient(s);
                            lvt_7_1_ = TextComponentUtils.processComponent(player, var11, player);
                        } catch (Exception var9) {
                            lvt_7_1_ = new TextComponentString(s);
                        }

                        nbttaglist.set(slot, new NBTTagString(ITextComponent.Serializer.componentToJson((ITextComponent) lvt_7_1_)));
                    }

                    nbttagcompound.setTag("pages", nbttaglist);
                    if (player instanceof EntityPlayerMP && player.getHeldItemMainhand() == stack) {
                        Slot var10 = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(0, var10.slotNumber, stack));
                    }
                }
            }
        }

    }
}
