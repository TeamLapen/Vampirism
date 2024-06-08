package de.teamlapen.vampirism.modcompat.guide.pages;

import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.gui.BaseScreen;
import de.maxanier.guideapi.page.PageItemStack;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.core.tags.ModTaskTags;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemReward;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PageTaskItemStack extends PageItemStack {

    private boolean setup = false;
    @NotNull
    private final ResourceKey<Task> taskKey;

    public PageTaskItemStack(@NotNull ResourceKey<Task> task) {
        super(Component.empty(), ItemStack.EMPTY);
        this.taskKey = task;
    }

    private void setupTask(RegistryAccess access) {
        access.registry(VampirismRegistries.Keys.TASK).flatMap(registry -> registry.getHolder(this.taskKey)).ifPresent(holder -> {
            var task = holder.value();
            this.ingredient = Ingredient.of(((ItemReward) task.getReward()).getAllPossibleRewards().stream());
            List<FormattedText> text = new ArrayList<>();
            Component newLine = Component.literal("\n");
            if (holder.is(ModTaskTags.HAS_FACTION)) {
                text.add(Component.translatable("text.vampirism.task.reward_obtain_for", String.join(", ", Arrays.stream(VampirismAPI.factionRegistry().getFactions()).filter(x -> x.getTag(VampirismRegistries.Keys.TASK).filter(holder::is).isPresent()).map(IFaction::getNamePlural).map(Component::getString).toList()) + " "));
            } else {
                text.add(Component.translatable("text.vampirism.task.reward_obtain_all"));
            }
            text.add(newLine);
            text.add(newLine);
            text.add(task.getTitle());
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
            this.draw = FormattedText.composite(text);
            this.setup = true;
        });
    }

    @Override
    public void draw(GuiGraphics guiGraphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj) {
        if (!this.setup) {
            this.setupTask(registryAccess);
        }
        super.draw(guiGraphics, registryAccess, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PageTaskItemStack other && Objects.equals(other.taskKey, this.taskKey);
    }

    @Override
    public int hashCode() {
        return 31 * taskKey.hashCode();
    }
}
