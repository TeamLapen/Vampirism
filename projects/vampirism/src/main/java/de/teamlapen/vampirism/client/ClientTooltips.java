package de.teamlapen.vampirism.client;

import de.teamlapen.vampirism.client.renderer.tooltips.QuarrelPouchClientTooltip;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.util.BlockDescription;
import de.teamlapen.vampirism.common.util.ShiftDescription;
import de.teamlapen.vampirism.common.world.blocks.IDescriptionProvider;
import de.teamlapen.vampirism.common.world.items.tooltip.QuarrelPouchTooltip;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.GatherEffectScreenTooltipsEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;
import java.util.stream.Stream;

public class ClientTooltips {

    @SubscribeEvent
    public void onToolTip(ItemTooltipEvent event) {
        if (event.getItemStack().get(ModDataComponents.SHIFT_DESCRIPTION) instanceof ShiftDescription shiftDescription) {
            TooltipDisplay orDefault = event.getItemStack().getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
            shiftDescription.addTooltips(event.getItemStack(), event.getEntity(), event.getContext(), orDefault, event.getFlags(), event.getToolTip()::add, event.getItemStack().getItem() instanceof IDescriptionProvider s ? s.getDescriptionParameters() : new Object[0]);
        }

        if (event.getItemStack().get(ModDataComponents.BLOCK_DESCRIPTION) instanceof BlockDescription blockDescription) {
            TooltipDisplay orDefault = event.getItemStack().getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
            blockDescription.addTooltips(event.getItemStack(), event.getContext(), orDefault, event.getFlags(), event.getToolTip()::add);
        }
    }

    @SubscribeEvent
    public void registerShiftTooltips(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltipComponents = event.getToolTip();

        Stream<ItemLike> descriptionItems = Stream.of(
                ModBlocks.HUNTER_TABLE,
                ModBlocks.MED_CHAIR,
                ModBlocks.MOTHER_TROPHY,
                ModBlocks.BLOOD_GRINDER,
                ModBlocks.BLOOD_SIEVE,
                ModItems.FABRIC_FILTER,
                ModItems.BLOOD_BUCKET,
                ModItems.SYRINGE_EMPTY,
                ModItems.SYRINGE_BLOOD,
                ModItems.INJECTION_GARLIC,
                ModItems.INJECTION_SANGUINARE
        );

        if (descriptionItems.anyMatch(item -> stack.is(item.asItem()))) {
//            DescriptionUtil.addDescriptionTooltip(event.getItemStack().getItem(), event.getContext(), event.getFlags(), event.getToolTip()); FIXME
        }
    }

    public void registerTooltipRenderer(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(QuarrelPouchTooltip.class, QuarrelPouchClientTooltip::new);
    }

    @SubscribeEvent
    public void gatherEffectTooltips(GatherEffectScreenTooltipsEvent event) {
        if (event.getEffectInstance().is(ModEffects.SANGUINARE)) {
            event.getTooltip().removeAll(event.getTooltip().stream().skip(1).toList());
        }
    }
}
