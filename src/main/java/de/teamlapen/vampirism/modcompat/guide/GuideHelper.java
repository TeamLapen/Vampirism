package de.teamlapen.vampirism.modcompat.guide;

import de.maxanier.guideapi.page.PageItemStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of helper methods
 */
public class GuideHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Adds multiple strings together seperated by a double line break
     *
     * @param unlocalized Unlocalized strings
     */
    public static @NotNull String append(String @NotNull ... unlocalized) {
        StringBuilder s = new StringBuilder();
        for (String u : unlocalized) {
            s.append(UtilLib.translate(u)).append("\n\n");
        }
        return s.toString();
    }

    /**
     * Create a simple page informing the reader about a task that can be used to obtain an item
     */
    public static @NotNull PageItemStack createItemTaskDescription(@NotNull Task task) {
        assert task.getReward() instanceof ItemReward;
        Ingredient ingredient = Ingredient.of(((ItemReward) task.getReward()).getAllPossibleRewards().stream());
        List<FormattedText> text = new ArrayList<>();
        Component newLine = Component.literal("\n");
        IPlayableFaction<?> f = task.getFaction();
        String type = f == null ? "" : f.getName().getString() + " ";
        text.add(Component.translatable("text.vampirism.task.reward_obtain", type));
        text.add(newLine);
        text.add(newLine);
        text.add(task.getTranslation());
        text.add(newLine);
        text.add(Component.translatable("text.vampirism.task.prerequisites"));
        text.add(newLine);
        TaskUnlocker[] unlockers = task.getUnlocker();
        if (unlockers.length > 0) {
            for (TaskUnlocker u : unlockers) {
                text.add(Component.literal("- ").append(u.getDescription()).append(newLine));
            }


        } else {
            text.add(Component.translatable("text.vampirism.task.prerequisites.none"));
        }
        return new PageItemStack(FormattedText.composite(text), ingredient);
    }
}
