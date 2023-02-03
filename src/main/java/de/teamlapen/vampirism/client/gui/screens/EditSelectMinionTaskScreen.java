package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.minion.IFactionMinionTask;
import de.teamlapen.vampirism.api.entity.minion.INoGlobalCommandTask;
import de.teamlapen.vampirism.api.util.ItemOrdering;
import de.teamlapen.vampirism.client.ClientConfigHelper;
import de.teamlapen.vampirism.client.gui.screens.radial.edit.ReorderingGuiRadialMenu;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
        FactionPlayerHandler.getOpt(Minecraft.getInstance().player).ifPresent(playerHandler -> {
            Minecraft.getInstance().setScreen(new EditSelectMinionTaskScreen(playerHandler));
        });
    }

    private static void drawActionPart(@Nullable SelectMinionTaskRadialScreen.Entry entry, PoseStack stack, int posX, int posY, int size, boolean transparent) {
        if (entry == null) return;
        ResourceLocation texture = entry.getIconLoc();
        RenderSystem.setShaderTexture(0, texture);
        blit(stack, posX, posY, 0, 0, 0, 16, 16, 16, 16);
    }

    private static boolean isEnabled(FactionPlayerHandler player, @NotNull SelectMinionTaskRadialScreen.Entry item) {
        return player.getCurrentFactionPlayer().flatMap(fp -> Optional.ofNullable(item.getTask()).map(task -> task.isAvailable(fp.getFaction(), player))).orElse(true);
    }

    private static ItemOrdering<SelectMinionTaskRadialScreen.Entry> getOrdering(FactionPlayerHandler player) {
        return new ItemOrdering<>(ClientConfigHelper.getMinionTaskOrder(player.getCurrentFaction()), new ArrayList<>(), () -> Stream.concat(VampirismRegistries.MINION_TASKS.get().getValues().stream().filter(s -> !(s instanceof INoGlobalCommandTask)).filter(s -> {
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