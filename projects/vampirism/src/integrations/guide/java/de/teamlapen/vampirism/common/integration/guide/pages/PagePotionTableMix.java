package de.teamlapen.vampirism.common.integration.guide.pages;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.pages.Page;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.PageHelper;
import de.maxanier.guideapi.api.util.SubTexture;
import de.teamlapen.vampirism.api.world.items.ExtendedPotionMix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;


public class PagePotionTableMix extends Page {
    private final static Logger LOGGER = LogManager.getLogger();
    private static final SubTexture POTION_GRID = new SubTexture(Identifier.fromNamespaceAndPath("vampirismguide", "textures/gui/potion_table.png"), 0, 0, 89, 75);
    private final Component description;
    private final ExtendedPotionMix @NotNull [] recipes;
    private long lastCycle = -1L;
    private ItemStack input;
    private ItemStack output;
    private ItemStack in1;
    private ItemStack in2;
    private int recipeCycle;
    private int currentIngredient1Index;
    @NotNull
    private ItemStack @NotNull [] ingredients1 = new ItemStack[0];
    private int currentIngredient2Index;
    @NotNull
    private ItemStack @NotNull [] ingredients2 = new ItemStack[0];

    public PagePotionTableMix(Component description, ExtendedPotionMix @NotNull ... recipes) {
        assert recipes.length > 0;
        this.description = description;
        this.recipes = recipes;
        deriveItemStacks(recipes[0]);
        in1 = ingredients1[0];
        in2 = ingredients2[0];
    }

    public PagePotionTableMix(Component description, @NotNull List<ExtendedPotionMix> recipes) {
        this(description, recipes.toArray(new ExtendedPotionMix[0]));
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {

        //Update cycle
        long time = screen.getMinecraft().level != null ? screen.getMinecraft().level.getGameTime() : 0L;
        if (this.lastCycle < 0L || this.lastCycle < time - 60L) {
            if (this.lastCycle > 0L) {
                this.cycle();
            }

            this.lastCycle = time;
        }


        int xStart = screen.pageXCenter() - 44;
        int yStart = pageTop - 13 + 20;
        POTION_GRID.draw(graphics, xStart, yStart);

        List<Component> tooltip = null;
        int x = xStart + 7;
        int y = yStart + 55;
        GuiHelper.drawItemStack(graphics, input, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(input);
        x += 21;
        GuiHelper.drawItemStack(graphics, input, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(input);
        x += 21;
        GuiHelper.drawItemStack(graphics, input, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(input);
        x = xStart + 29;
        y = yStart + 4;
        GuiHelper.drawItemStack(graphics, in1, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(in1);
        x = xStart + 4;
        y = yStart + 12;
        GuiHelper.drawItemStack(graphics, in2, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(in2);
        x = xStart + 71;
        y = yStart + 29;
        GuiHelper.drawItemStack(graphics, output, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(output);
        PageHelper.drawFormattedText(graphics, pageLeft - 39 + 43, yStart + 80, description, book.getTextColor());

        if (tooltip != null) {
            tooltips.addAll(tooltip);
        }

    }

    @NotNull
    private final List<Component> tooltips = Lists.newArrayList();

    @Override
    public void drawExtras(GuiGraphicsExtractor graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        super.drawExtras(graphics, book, category, entry, pageLeft, pageTop, mouseX, mouseY, screen, fontRendererObj);

        graphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                tooltips,
                Optional.empty(),
                mouseX,
                mouseY,
                null
        );
        tooltips.clear();;
    }

    private void cycle() {
        if (++currentIngredient2Index >= ingredients2.length) {
            currentIngredient2Index = 0;
            if (++currentIngredient1Index >= ingredients1.length) {
                currentIngredient1Index = 0;
                if (++recipeCycle >= recipes.length) {
                    this.recipeCycle = 0;
                }
                deriveItemStacks(recipes[recipeCycle]);

            }
        }
        in1 = ingredients1[currentIngredient1Index];
        in2 = ingredients2[currentIngredient2Index];
    }

    private void deriveItemStacks(@NotNull ExtendedPotionMix recipe) {
        input = PotionContents.createItemStack(Items.POTION, recipe.input);
        output = PotionContents.createItemStack(Items.POTION, recipe.output);
        ingredients1 = recipe.reagent1.get().items().map(Holder::value).map(ItemStack::new).peek(stack -> stack.setCount(recipe.reagent1Count)).toArray(ItemStack[]::new);
        ingredients2 = recipe.reagent2.get().items().map(Holder::value).map(ItemStack::new).peek(stack -> stack.setCount(recipe.reagent2Count)).toArray(ItemStack[]::new);
        if (ingredients1.length == 0) {
            ingredients1 = new ItemStack[] {ItemStack.EMPTY};
        }
        if (ingredients2.length == 0) {
            ingredients2 = new ItemStack[] {ItemStack.EMPTY};
        }
    }

}