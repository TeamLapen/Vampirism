package de.teamlapen.faction.client.core;

import de.teamlapen.faction.api.client.ItemBar;
import de.teamlapen.faction.api.client.ItemBarProvider;
import de.teamlapen.faction.api.event.RegisterItemBarsEvent;
import de.teamlapen.faction.client.color.ColorWheel;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Draws bars over item slots, stacked upwards from the vanilla durability bar. All are sorted by their {@link Identifier},
 * and the bars of a single provider keep the order they are handed to the consumer, bottom first.
 */
public class ItemBars {

    private static final int BAR_X = 2;
    private static final int BAR_Y = 13;
    private static final int BAR_WIDTH = 13;
    private static final int BAR_HEIGHT = 2;
    private static final int BACKGROUND_COLOR = 0xFF000000;
    private static final float STAGE_ROTATION = 25f;

    private final Map<Identifier, ItemBarProvider> providers = new TreeMap<>();

    public static int staged(int color, float progress) {
        if (progress >= 2 / 3f) return color;
        if (progress >= 1 / 3f) return ColorWheel.rotate(color, STAGE_ROTATION / 2);

        return ColorWheel.rotate(color, STAGE_ROTATION);
    }

    @ApiStatus.Internal
    public void init() {
        ModLoader.postEvent(new RegisterItemBarsEvent(this.providers));
    }

    @ApiStatus.Internal
    public void render(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        if (this.providers.isEmpty()) return;

        List<ItemBar> bars = new ArrayList<>();
        for (ItemBarProvider provider : this.providers.values()) {
            provider.addBars(stack, bars::add);
        }

        int row = stack.isBarVisible() ? 1 : 0;
        for (ItemBar bar : bars) {
            int left = x + BAR_X;
            int top = y + BAR_Y - row * BAR_HEIGHT;
            int width = bar.progress() <= 0 ? 0 : Math.max(1, Math.round(BAR_WIDTH * bar.progress()));
            graphics.fill(RenderPipelines.GUI, left, top, left + BAR_WIDTH, top + BAR_HEIGHT, BACKGROUND_COLOR);
            graphics.fill(RenderPipelines.GUI, left, top, left + width, top + 1, bar.color());
            row++;
        }
    }
}
