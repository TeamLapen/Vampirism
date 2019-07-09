package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;


public class VampireBookItem extends VampirismItem {
    private static final String regName = "vampire_book";

    public static boolean validBookTagContents(CompoundNBT nbt) {
        if (!WritableBookItem.isNBTValid(nbt)) {
            return false;
        } else if (!nbt.contains("title")) {
            return false;
        } else {
            String s = nbt.getString("title");
            return s.length() <= 32 && nbt.contains("author");
        }
    }

    public VampireBookItem() {
        super(regName, new Properties().maxStackSize(1).group(VampirismMod.creativeTab));
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
            CompoundNBT nbttagcompound = stack.getTag();
            String s = nbttagcompound.getString("title");
            if (!StringUtils.isNullOrEmpty(s)) {
                return new StringTextComponent(s);
            }
        }
        return super.getDisplayName(stack);
    }


    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            this.resolveContents(stack, playerIn);
        }

        //playerIn.openGui(VampirismMod.instance, ModGuiHandler.ID_VAMPIRE_BOOK, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);//TODO 1.14
        return new ActionResult(ActionResultType.SUCCESS, stack);
    }

    private void resolveContents(ItemStack stack, PlayerEntity player) {
        if (!stack.isEmpty() && stack.getTag() != null) {
            CompoundNBT nbttagcompound = stack.getTag();
            if (!nbttagcompound.getBoolean("resolved")) {
                nbttagcompound.putBoolean("resolved", true);
                if (validBookTagContents(nbttagcompound)) {
                    ListNBT nbttaglist = nbttagcompound.getList("pages", 8);

                    for (int slot = 0; slot < nbttaglist.size(); ++slot) {
                        String s = nbttaglist.getString(slot);

                        Object lvt_7_1_;
                        try {
                            ITextComponent var11 = ITextComponent.Serializer.fromJsonLenient(s);
                            lvt_7_1_ = TextComponentUtils.updateForEntity(null, var11, player);
                        } catch (Exception var9) {
                            lvt_7_1_ = new StringTextComponent(s);
                        }

                        nbttaglist.set(slot, new StringNBT(ITextComponent.Serializer.toJson((ITextComponent) lvt_7_1_)));
                    }

                    nbttagcompound.put("pages", nbttaglist);
                    if (player instanceof ServerPlayerEntity && player.getHeldItemMainhand() == stack) {
                        Slot var10 = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                        ((ServerPlayerEntity) player).connection.sendPacket(new SSetSlotPacket(0, var10.slotNumber, stack));
                    }
                }
            }
        }

    }
}
