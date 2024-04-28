package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.items.component.VampireBookContents;
import de.teamlapen.vampirism.network.ClientboundOpenVampireBookPacket;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class VampireBookItem extends Item implements ModDisplayItemGenerator.CreativeTabItemProvider {

    public VampireBookItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        VampireBookContents contents = stack.get(ModDataComponents.VAMPIRE_BOOK);
        if (contents != null) {
            String s = contents.author();
            if (!StringUtil.isNullOrEmpty(s)) {
                tooltip.add((Component.translatable("book.byAuthor", s)).withStyle(ChatFormatting.GRAY));
            }

            tooltip.add((Component.literal("Vampirism knowledge").withStyle(ChatFormatting.GRAY)));
        }

    }

    @Override
    public void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        Collection<ItemStack> items = VampireBookManager.getInstance().getAllBookItems();
        items.stream().findAny().ifPresent(stack -> output.accept(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY));
        items.forEach(stack -> output.accept(stack, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY));
    }

    public ItemStack contentInstance(VampireBookManager.BookContext context) {
        ItemStack stack = getDefaultInstance();
        VampireBookContents.addFromBook(stack, context);
        return stack;
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
        VampireBookContents contents = stack.get(ModDataComponents.VAMPIRE_BOOK);
        if (contents != null) {
            if (!StringUtil.isNullOrEmpty(contents.title())) {
                return Component.literal(contents.title());
            }
        }
        return super.getName(stack);
    }

    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundOpenVampireBookPacket(VampireBookContents.get(stack).id()));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

}
