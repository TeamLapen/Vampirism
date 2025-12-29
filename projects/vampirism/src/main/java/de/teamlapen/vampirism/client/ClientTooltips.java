package de.teamlapen.vampirism.client;

import de.teamlapen.vampirism.client.renderer.tooltips.QuarrelPouchClientTooltip;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.BlockDescription;
import de.teamlapen.vampirism.common.util.ShiftDescription;
import de.teamlapen.vampirism.common.world.items.tooltip.QuarrelPouchTooltip;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.GatherEffectScreenTooltipsEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ClientTooltips {

    @SubscribeEvent
    public void onToolTip(ItemTooltipEvent event) {
        TooltipDisplay orDefault = event.getItemStack().getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);

        if (orDefault.shows(ModDataComponents.BLOCK_DESCRIPTION.get()) && event.getItemStack().get(ModDataComponents.BLOCK_DESCRIPTION) instanceof BlockDescription blockDescription) {
            blockDescription.addTooltips(event.getItemStack(), event.getContext(), orDefault, event.getFlags(), event.getToolTip()::add);
        }

        if (orDefault.shows(ModDataComponents.SHIFT_DESCRIPTION.get()) && event.getItemStack().get(ModDataComponents.SHIFT_DESCRIPTION) instanceof ShiftDescription shiftDescription) {
            shiftDescription.addTooltips(event.getItemStack(), event.getEntity(), event.getContext(), event.getFlags(), event.getToolTip()::add);
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
