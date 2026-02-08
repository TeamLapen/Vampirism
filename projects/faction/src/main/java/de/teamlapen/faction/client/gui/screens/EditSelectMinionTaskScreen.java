package de.teamlapen.faction.client.gui.screens;

import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.client.gui.screens.radial.edit.ReorderingGuiRadialMenu;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.util.ItemOrdering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class EditSelectMinionTaskScreen extends ReorderingGuiRadialMenu<SelectMinionTaskRadialScreen.Entry> {

    public EditSelectMinionTaskScreen(FactionPlayerHandler player) {
        super(getOrdering(player), entry -> entry.getText().plainCopy(), EditSelectMinionTaskScreen::drawActionPart, (ordering) -> saveOrdering(player, ordering), (item) -> EditSelectMinionTaskScreen.isEnabled(player, item));
    }

    public static void show() {
        Minecraft.getInstance().setScreen(new EditSelectMinionTaskScreen(FactionPlayerHandler.get(Minecraft.getInstance().player)));
    }

    private static void drawActionPart(@Nullable SelectMinionTaskRadialScreen.Entry entry, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        if (entry == null) return;
        GuiRenderer.blit(graphics, entry.getIconLoc(), posX, posY, 16, 16, 16, 16);
    }

    private static boolean isEnabled(FactionPlayerHandler handler, @NotNull SelectMinionTaskRadialScreen.Entry item) {
        return handler.getLordPlayer().flatMap(player -> Optional.ofNullable(item.getTask()).map(task -> task.value().isAvailable(player))).orElse(true);
    }

    private static ItemOrdering<SelectMinionTaskRadialScreen.Entry> getOrdering(FactionPlayerHandler player) {
        return new ItemOrdering<>(FactionConfig.client().minionTaskOrder.get(player.getFaction()), new ArrayList<>(),() -> FactionConfig.client().minionTaskOrder.allowedValues(player.getFaction()));
    }

    private static void saveOrdering(FactionPlayerHandler player, ItemOrdering<SelectMinionTaskRadialScreen.Entry> ordering) {
        FactionConfig.client().minionTaskOrder.setAndSave(player.getFaction(), ordering.getOrdering());
    }
}