package de.teamlapen.vampirism.modcompat.guide;

import de.maxanier.guideapi.page.PageItemStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public static String append(String... unlocalized) {
        StringBuilder s = new StringBuilder();
        for (String u : unlocalized) {
            s.append(UtilLib.translate(u)).append("\n\n");
        }
        return s.toString();
    }

    /**
     * Create a simple page informing the reader about a task that can be used to obtain an item
     */
    public static PageItemStack createItemTaskDescription(Task task) {
        assert task.getReward() instanceof ItemReward;
        Ingredient ingredient = Ingredient.of(((ItemReward) task.getReward()).getAllPossibleRewards().stream());
        List<FormattedText> text = new ArrayList<>();
        TextComponent newLine = new TextComponent("\n");
        IPlayableFaction<?> f = task.getFaction();
        String type = f == null ? "" : f.getName().getString() + " ";
        text.add(new TranslatableComponent("text.vampirism.task.reward_obtain", type));
        text.add(newLine);
        text.add(newLine);
        text.add(task.getTranslation());
        text.add(newLine);
        text.add(new TranslatableComponent("text.vampirism.task.prerequisites"));
        text.add(newLine);
        TaskUnlocker[] unlockers = task.getUnlocker();
        if (unlockers.length > 0) {
            for (TaskUnlocker u : unlockers) {
                text.add(new TextComponent("- ").append(u.getDescription()).append(newLine));
            }


        } else {
            text.add(new TranslatableComponent("text.vampirism.task.prerequisites.none"));
        }
        return new PageItemStack(FormattedText.composite(text), ingredient);
    }
}
