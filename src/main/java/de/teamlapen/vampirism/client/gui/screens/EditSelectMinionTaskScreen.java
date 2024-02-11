package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.api.entity.minion.IFactionMinionTask;
import de.teamlapen.vampirism.api.entity.minion.INoGlobalCommandTask;
import de.teamlapen.vampirism.api.util.ItemOrdering;
import de.teamlapen.vampirism.client.ClientConfigHelper;
import de.teamlapen.vampirism.client.gui.screens.radial.edit.ReorderingGuiRadialMenu;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.client.gui.screens.SelectMinionTaskRadialScreen.CUSTOM_ENTRIES;

public class EditSelectMinionTaskScreen extends ReorderingGuiRadialMenu<SelectMinionTaskRadialScreen.Entry> {

    public EditSelectMinionTaskScreen(FactionPlayerHandler player) {
        super(getOrdering(player), entry -> entry.getText().plainCopy(), EditSelectMinionTaskScreen::drawActionPart, (ordering) -> saveOrdering(player, ordering), (item) -> EditSelectMinionTaskScreen.isEnabled(player, item));
    }

    public static void show() {
        Minecraft.getInstance().setScreen(new EditSelectMinionTaskScreen(FactionPlayerHandler.get(Minecraft.getInstance().player)));
    }

    private static void drawActionPart(@Nullable SelectMinionTaskRadialScreen.Entry entry, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        if (entry == null) return;
        graphics.blit(entry.getIconLoc(), posX, posY, 0, 0, 0, 16, 16, 16, 16);
    }

    private static boolean isEnabled(FactionPlayerHandler player, @NotNull SelectMinionTaskRadialScreen.Entry item) {
        return player.getCurrentFactionPlayer().flatMap(fp -> Optional.ofNullable(item.getTask()).map(task -> task.isAvailable(fp.getFaction(), player))).orElse(true);
    }

    private static ItemOrdering<SelectMinionTaskRadialScreen.Entry> getOrdering(FactionPlayerHandler player) {
        return new ItemOrdering<>(ClientConfigHelper.getMinionTaskOrder(player.getCurrentFaction()), new ArrayList<>(), () -> Stream.concat(ModRegistries.MINION_TASKS.stream().filter(s -> !(s instanceof INoGlobalCommandTask)).filter(s -> {
            if (s instanceof IFactionMinionTask<?,?> factionTask) {
                if(factionTask.getFaction() == null) {
                    return true;
                } else {
                    return factionTask.getFaction() == player.getCurrentFaction();
                }
            } else {
                return true;
            }
        }).map(SelectMinionTaskRadialScreen.Entry::new), CUSTOM_ENTRIES.values().stream()).collect(Collectors.toList()));
    }

    private static void saveOrdering(FactionPlayerHandler player, ItemOrdering<SelectMinionTaskRadialScreen.Entry> ordering) {
        ClientConfigHelper.saveMinionTaskOrder(player.getCurrentFaction(), ordering.getOrdering());
    }
}