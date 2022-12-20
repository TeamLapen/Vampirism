package de.teamlapen.lib.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MultilineTooltip extends Tooltip {
    private final List<Component> components;
    @Nullable
    private List<FormattedCharSequence> cachedTooltip;

    public MultilineTooltip(List<Component> components) {
        super(Component.empty(), null);
        this.components = components;
    }

    public @NotNull List<FormattedCharSequence> toCharSequence(@NotNull Minecraft minecraft) {
        if (this.cachedTooltip == null) {
            this.cachedTooltip = this.components.stream().map(Component::getVisualOrderText).toList();
        }

        return this.cachedTooltip;
    }
}
