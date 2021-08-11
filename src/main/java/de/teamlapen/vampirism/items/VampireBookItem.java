package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.OpenVampireBookPacket;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.util.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item.Properties;

public class VampireBookItem extends VampirismItem {
    private static final String regName = "vampire_book";

    public static boolean validBookTagContents(CompoundTag nbt) {
        if (!WritableBookItem.makeSureTagIsValid(nbt)) {
            return false;
        } else if (!nbt.contains("title")) {
            return false;
        } else {
            String s = nbt.getString("title");
            return s.length() <= 32 && nbt.contains("author");
        }
    }

    public VampireBookItem() {
        super(regName, new Properties().rarity(Rarity.UNCOMMON).stacksTo(1).tab(VampirismMod.creativeTab));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundTag compoundnbt = stack.getTag();
            String s = compoundnbt.getString("author");
            if (!StringUtil.isNullOrEmpty(s)) {
                tooltip.add((new TranslatableComponent("book.byAuthor", s)).withStyle(ChatFormatting.GRAY));
            }

            tooltip.add((new TextComponent("Vampirism knowledge").withStyle(ChatFormatting.GRAY)));
        }

    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            items.add(VampireBookManager.getInstance().getRandomBook(new Random()));
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbttagcompound = stack.getTag();
            String s = nbttagcompound.getString("title");
            if (!StringUtil.isNullOrEmpty(s)) {
                return new TextComponent(s);
            }
        }
        return super.getName(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayer) {
            this.resolveContents(stack, playerIn);
            VampirismMod.dispatcher.sendTo(new OpenVampireBookPacket(stack), (ServerPlayer) playerIn);
        }
        return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
    }

    private void resolveContents(ItemStack stack, Player player) {
        if (!stack.isEmpty() && stack.getTag() != null) {
            CompoundTag nbttagcompound = stack.getTag();
            if (!nbttagcompound.getBoolean("resolved")) {
                nbttagcompound.putBoolean("resolved", true);
                if (validBookTagContents(nbttagcompound)) {
                    ListTag nbttaglist = nbttagcompound.getList("pages", 8);

                    for (int slot = 0; slot < nbttaglist.size(); ++slot) {
                        String s = nbttaglist.getString(slot);

                        Object lvt_7_1_;
                        try {
                            Component var11 = Component.Serializer.fromJsonLenient(s);
                            lvt_7_1_ = ComponentUtils.updateForEntity(null, var11, player, 0);
                        } catch (Exception var9) {
                            lvt_7_1_ = new TextComponent(s);
                        }

                        nbttaglist.set(slot, StringTag.valueOf(Component.Serializer.toJson((Component) lvt_7_1_)));
                    }

                    nbttagcompound.put("pages", nbttaglist);
                    if (player instanceof ServerPlayer && player.getMainHandItem() == stack) {
                        Slot var10 = player.containerMenu.slots.get(player.getInventory().selected);
                        ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(0, player.containerMenu.incrementStateId(), var10.index, stack));
                    }
                }
            }
        }

    }
}
