package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.SOpenVampireBookPacket;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class VampireBookItem extends Item {

    public static boolean validBookTagContents(CompoundNBT nbt) {
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT compoundnbt = stack.getTag();
            String s = compoundnbt.getString("author");
            if (!StringUtils.isNullOrEmpty(s)) {
                tooltip.add((new TranslationTextComponent("book.byAuthor", s)).withStyle(TextFormatting.GRAY));
            }

            tooltip.add((new StringTextComponent("Vampirism knowledge").withStyle(TextFormatting.GRAY)));
        }

    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            items.add(VampireBookManager.getInstance().getRandomBookItem(new Random()));
        }
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT nbttagcompound = stack.getTag();
            String s = nbttagcompound.getString("title");
            if (!StringUtils.isNullOrEmpty(s)) {
                return new StringTextComponent(s);
            }
        }
        return super.getName(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayerEntity) {
            String id = VampireBookManager.OLD_ID;
            if (stack.hasTag()) {
                id = stack.getTag().getString("id");
            }
            VampirismMod.dispatcher.sendTo(new SOpenVampireBookPacket(id), (ServerPlayerEntity) playerIn);
        }
        return new ActionResult(ActionResultType.SUCCESS, stack);
    }

    public static CompoundNBT createTagFromContext(VampireBookManager.BookContext context) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("id", context.id);
        nbt.putString("author", context.book.getAuthor());
        nbt.putString("title", context.book.getTitle());
        return nbt;
    }
}
