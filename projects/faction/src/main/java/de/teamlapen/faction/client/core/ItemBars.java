package de.teamlapen.faction.client.core;

import de.teamlapen.faction.client.color.ColorWheel;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

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

    private static final Map<Identifier, ItemBarProvider> PROVIDERS = new TreeMap<>();

    public static int staged(int color, float progress) {
        if (progress >= 2 / 3f) return color;
        if (progress >= 1 / 3f) return ColorWheel.rotate(color, STAGE_ROTATION / 2);

        return ColorWheel.rotate(color, STAGE_ROTATION);
    }

    @ApiStatus.Internal
    public static void init() {
        ModLoader.postEvent(new RegisterItemBarsEvent(PROVIDERS));
    }

    @ApiStatus.Internal
    public static void render(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        if (PROVIDERS.isEmpty()) return;

        List<ItemBar> bars = new ArrayList<>();
        for (ItemBarProvider provider : PROVIDERS.values()) {
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

    public static class RegisterItemBarsEvent extends Event implements IModBusEvent {

        private final Map<Identifier, ItemBarProvider> providers;

        @ApiStatus.Internal
        public RegisterItemBarsEvent(Map<Identifier, ItemBarProvider> providers) {
            this.providers = providers;
        }

        public void register(Identifier id, ItemBarProvider provider) {
            if (this.providers.putIfAbsent(id, provider) != null) {
                throw new IllegalArgumentException("Duplicate item bar provider " + id);
            }
        }
    }

    @FunctionalInterface
    public interface ItemBarProvider {

        void addBars(ItemStack stack, Consumer<ItemBar> bars);
    }

    public record ItemBar(float progress, int color) {

        public ItemBar {
            progress = Math.clamp(progress, 0f, 1f);
        }
    }
}
