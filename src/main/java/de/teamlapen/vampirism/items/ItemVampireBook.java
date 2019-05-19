package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
        super(regName, new Properties().maxStackSize(1));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            items.add(VampireBookManager.getInstance().getRandomBook(new Random()).setDisplayName(UtilLib.translated("item.vampirism.vampire_book.name")));
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (stack.hasTag()) {
            NBTTagCompound nbttagcompound = stack.getTag();
            String s = nbttagcompound.getString("title");
            if (!StringUtils.isNullOrEmpty(s)) {
                return new TextComponentString(s);
            }
        }
        return super.getDisplayName(stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTag()) {
            NBTTagCompound nbttagcompound = stack.getTag();
            String s = nbttagcompound.getString("title");
            if (!StringUtils.isNullOrEmpty(s)) {
                return s;
            }
        }

        return super.getItemStackDisplayName(stack);
    }

    @OnlyIn(Dist.CLIENT)
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
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }

    private void resolveContents(ItemStack stack, EntityPlayer player) {
        if (!stack.isEmpty() && stack.getTag() != null) {
            NBTTagCompound nbttagcompound = stack.getTag();
            if (!nbttagcompound.getBoolean("resolved")) {
                nbttagcompound.setBoolean("resolved", true);
                if (validBookTagContents(nbttagcompound)) {
                    NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);

                    for (int slot = 0; slot < nbttaglist.size(); ++slot) {
                        String s = nbttaglist.getString(slot);

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
