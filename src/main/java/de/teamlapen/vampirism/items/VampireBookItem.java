package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.component.VampireBook;
import de.teamlapen.vampirism.network.ClientboundOpenVampireBookPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class VampireBookItem extends Item implements ModDisplayItemGenerator.CreativeTabItemProvider {

    private static final RandomSource RANDOM = RandomSource.create();

    public VampireBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        IVampireBook vampireBook = VampireBook.get(stack);

        if (vampireBook.isEmpty()) {
            if (level.isClientSide) {
                player.displayClientMessage(Component.translatable("text.vampirism.vampire_book.failed_to_load"), true);
            }
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundOpenVampireBookPacket(vampireBook));
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public Component getName(ItemStack stack) {
        IVampireBook vampireBook = VampireBook.get(stack);
        return vampireBook.isEmpty() ? super.getName(stack) : vampireBook.title();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add((Component.translatable("book.byAuthor", VampireBook.get(stack).author())).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add((Component.translatable("text.vampirism.vampire_book_description").withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public @Nullable String getCreatorModId(HolderLookup.Provider registries, ItemStack itemStack) {
        return VampireBook.get(itemStack).id().getNamespace();
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        Optional<? extends HolderLookup.RegistryLookup<IVampireBook>> registryLookup = parameters.holders().lookup(VampirismRegistries.Keys.VAMPIRE_BOOK);

        registryLookup.ifPresent(lookup -> {
            List<Holder.Reference<IVampireBook>> list = lookup.listElements().toList();
            output.accept(createBook(list.get(RANDOM.nextInt(0, list.size())).value()), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
            lookup.listElements().forEach(vampireBook -> output.accept(createBook(vampireBook.value()), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY));
        });
    }

    public static ItemStack createBook(IVampireBook vampireBook) {
        ItemStack stack = new ItemStack(ModItems.VAMPIRE_BOOK.get());
        VampireBook.addToStack(stack, vampireBook);
        return stack;
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
