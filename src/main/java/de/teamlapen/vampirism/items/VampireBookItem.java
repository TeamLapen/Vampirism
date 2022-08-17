package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ClientboundOpenVampireBookPacket;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class VampireBookItem extends Item {

    public static boolean validBookTagContents(@NotNull CompoundTag nbt) {
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
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
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        if (this.allowedIn(group)) {
            items.add(VampireBookManager.getInstance().getRandomBookItem(RandomSource.create()));
        }
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
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
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayer) {
            String id = VampireBookManager.OLD_ID;
            if (stack.hasTag()) {
                id = stack.getTag().getString("id");
            }
            VampirismMod.dispatcher.sendTo(new ClientboundOpenVampireBookPacket(id), (ServerPlayer) playerIn);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    public static @NotNull CompoundTag createTagFromContext(VampireBookManager.@NotNull BookContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", context.id());
        nbt.putString("author", context.book().author());
        nbt.putString("title", context.book().title());
        return nbt;
    }
}
