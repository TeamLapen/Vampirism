package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.util.ItemOrdering;
import de.teamlapen.vampirism.client.ClientConfigHelper;
import de.teamlapen.vampirism.client.gui.screens.radial.edit.ReorderingGuiRadialMenu;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EditSelectActionScreen<T extends IFactionPlayer<T>> extends ReorderingGuiRadialMenu<IAction<?>> {

    public EditSelectActionScreen(T player) {
        super(getOrdering(player), action -> action.getName().plainCopy(), EditSelectActionScreen::drawActionPart, (ordering) -> saveOrdering(player, ordering), (item) -> EditSelectActionScreen.isEnabled(player, item));
    }

    public static void show() {
        FactionPlayerHandler.getOpt(Minecraft.getInstance().player).map(FactionPlayerHandler::getCurrentFactionPlayer).flatMap(optional -> optional).ifPresent(factionPlayer -> {
            Minecraft.getInstance().setScreen(new EditSelectActionScreen(factionPlayer));
        });
    }

    private static void drawActionPart(@Nullable IAction<?> action, GuiGraphics graphics, int posX, int posY, int size, boolean transparent) {
        if (action == null) return;
        ResourceLocation id = RegUtil.id(action);
        ResourceLocation texture = new ResourceLocation(id.getNamespace(), "textures/actions/" + id.getPath() + ".png");
        graphics.blit(texture, posX, posY, 0, 0, 0, 16, 16, 16, 16);
    }

    private static <T extends IFactionPlayer<T>> boolean isEnabled(T player, @NotNull IAction<?> item) {
        return player.getActionHandler().isActionUnlocked((IAction) item);
    }

    private static <T extends IFactionPlayer<T>> ItemOrdering<IAction<?>> getOrdering(T player) {
        return new ItemOrdering<>(ClientConfigHelper.getActionOrder(player.getFaction()), new ArrayList<>(), () -> VampirismRegistries.ACTIONS.get().getValues().stream().filter(action -> action.matchesFaction(player.getFaction())).collect(Collectors.toList()));
    }

    private static <T extends IFactionPlayer<T>> void saveOrdering(T player, ItemOrdering<IAction<?>> ordering) {
        ClientConfigHelper.saveActionOrder(player.getFaction().getID(), ordering.getOrdering());
    }
}
