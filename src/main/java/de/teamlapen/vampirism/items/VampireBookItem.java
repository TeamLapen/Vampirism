package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.SOpenVampireBookPacket;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VampireBookItem extends Item {

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
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1).tab(VampirismMod.creativeTab));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundTag compoundnbt = stack.getTag();
            String s = compoundnbt.getString("author");
            if (!StringUtil.isNullOrEmpty(s)) {
                tooltip.add((Component.translatable("book.byAuthor", s)).withStyle(ChatFormatting.GRAY));
            }

            tooltip.add((Component.literal("Vampirism knowledge").withStyle(ChatFormatting.GRAY)));
        }

    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (this.allowedIn(group)) {
            items.add(VampireBookManager.getInstance().getRandomBook(RandomSource.create()));
        }
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbttagcompound = stack.getTag();
            String s = nbttagcompound.getString("title");
            if (!StringUtil.isNullOrEmpty(s)) {
                return Component.literal(s);
            }
        }
        return super.getName(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@Nonnull ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayer) {
            this.resolveContents(stack, playerIn);
            VampirismMod.dispatcher.sendTo(new SOpenVampireBookPacket(stack), (ServerPlayer) playerIn);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
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

                        Component lvt_7_1_;
                        try {
                            Component var11 = Component.Serializer.fromJsonLenient(s);
                            lvt_7_1_ = ComponentUtils.updateForEntity(null, var11, player, 0);
                        } catch (Exception var9) {
                            lvt_7_1_ = Component.literal(s);
                        }

                        nbttaglist.set(slot, StringTag.valueOf(Component.Serializer.toJson(lvt_7_1_)));
                    }

                    nbttagcompound.put("pages", nbttaglist);
                }
            }
        }

    }
}
