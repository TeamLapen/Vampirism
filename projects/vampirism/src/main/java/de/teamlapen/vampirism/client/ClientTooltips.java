package de.teamlapen.vampirism.client;

import de.teamlapen.vampirism.client.renderer.tooltips.QuarrelPouchClientTooltip;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.world.items.tooltip.QuarrelPouchTooltip;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.GatherEffectScreenTooltipsEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

public class ClientTooltips {

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
