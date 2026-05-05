package de.teamlapen.vampirism.common.integration.guide.pages;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.pages.PageText;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.factions.tasks.TaskUnlocker;
import de.teamlapen.faction.common.factions.tasks.reward.ItemReward;
import de.teamlapen.vampirism.common.tags.ModTaskTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PageTask extends PageText {

    private boolean setup = false;
    @NotNull
    private final ResourceKey<Task> taskKey;
    private final IngredientCycler ingredientCycler = new IngredientCycler();
    private final List<ItemStack> stacks = new ArrayList<>();

    public PageTask(@NotNull ResourceKey<Task> task) {
        super(Component.empty(), 60);
        this.taskKey = task;
    }

    private void setupTask(RegistryAccess access) {
        access.lookup(FactionRegistries.Keys.TASK).flatMap(registry -> registry.get(this.taskKey)).ifPresent(holder -> {
            Task task = holder.value();
            stacks.addAll(((ItemReward) task.reward()).getAllPossibleRewards());
            List<FormattedText> text = new ArrayList<>();
            Component newLine = Component.literal("\n");
            if (holder.is(ModTaskTags.HAS_FACTION)) {
                // Old ModRegistries.FACTIONS.stream().filter(x -> x.getTag(VampirismRegistries.Keys.TASK).filter(holder::is).isPresent())
                //Check if this task is contained in the faction task tag
                text.add(Component.translatable("gui.vampirism.guide.task.reward_obtain_for", String.join(", ", FactionRegistries.FACTION.get().stream().filter(x -> FactionsMod.services().factionTags().get(FactionRegistries.FACTION.get().wrapAsHolder(x), FactionRegistries.Keys.TASK).map(holder::is).isPresent()).map(IFaction::getNamePlural).map(Component::getString).toList()) + " ")); //TODO is this correct, can this be done simpler? We want to find all factions that have this task
            } else {
                text.add(Component.translatable("gui.vampirism.guide.task.reward_obtain_all"));
            }
            text.add(newLine);
            text.add(newLine);
            text.add(task.title().copy().withStyle(ChatFormatting.BOLD));
            text.add(newLine);
            text.add(Component.translatable("gui.vampirism.guide.task.prerequisites").append(":\n"));
            List<TaskUnlocker> unlockers = task.unlocker();
            if (!unlockers.isEmpty()) {
                for (TaskUnlocker u : unlockers) {
                    text.add(Component.literal("- ").append(u.getDescription()).append(newLine).withStyle(ChatFormatting.ITALIC));
                }

            } else {
                text.add(Component.translatable("gui.vampirism.guide.task.prerequisites.none").withStyle(ChatFormatting.ITALIC));
            }
            this.draw = FormattedText.composite(text);
            this.setup = true;
        });
    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        if (this.setup) {
            ingredientCycler.tick(screen.getMinecraft().level.getGameTime());
            ItemStack s = ingredientCycler.getCycledIngredientStack(stacks, 0);
            GuiHelper.drawScaledItemStack(graphics, s, pageLeft - 39 + 101, pageTop - 13 + 20, 3);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PageTask other && Objects.equals(other.taskKey, this.taskKey);
    }

    @Override
    public void onInit(RegistryAccess registryAccess, Book book, CategoryBase category, EntryBase entry, Player player) {
        super.onInit(registryAccess, book, category, entry, player);
        this.setupTask(registryAccess);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        return 31 * result + taskKey.hashCode();
    }
}
