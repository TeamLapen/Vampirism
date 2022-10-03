package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class VampireBookScreen extends Screen {

    private final static ResourceLocation pageTexture = new ResourceLocation(REFERENCE.MODID, "textures/gui/vampire_book.png");
    private final int xSize = 245;
    private final int ySize = 192;
    private int guiLeft, guiTop;
    private PageButton buttonNext;
    private PageButton buttonPrev;
    private int pageNumber;
    private final VampireBookManager.@NotNull BookInfo info;
    private List<FormattedText> content;


    public VampireBookScreen(VampireBookManager.@NotNull BookInfo info) {
        super(Component.literal(info.title()));
        this.info = info;
    }


    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, pageTexture);
        blit(stack, guiLeft, guiTop, 0, 0, xSize, ySize);

        pageNumber = Mth.clamp(pageNumber, 0, content.size() - 1);

        if (pageNumber < content.size()) {
            FormattedText toDraw = content.get(pageNumber);
            Font fontRenderer = Minecraft.getInstance().font;

            List<FormattedCharSequence> cutLines = fontRenderer.split(toDraw, 170);
            int y = guiTop + 12 + 5;
            for (FormattedCharSequence cut : cutLines) {
                fontRenderer.draw(stack, cut, guiLeft + 44, y, 0);
                y += 10;
            }
        }

        drawCenteredStringWithoutShadow(stack, font, String.format("%d/%d", pageNumber + 1, content.size()), guiLeft + xSize / 2, guiTop + 5 * ySize / 6, 0);
        drawCenteredString(stack, font, title, guiLeft + xSize / 2, guiTop - 10, Color.WHITE.getRGB());

        buttonPrev.visible = pageNumber != 0;
        buttonNext.visible = pageNumber != content.size() - 1 && !content.isEmpty();

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void init() {
        super.init();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        this.addRenderableWidget(buttonNext = new PageButton(guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, true, (btn) -> {
            if (pageNumber + 1 < content.size()) {
                nextPage();
            }
        }, true));
        this.addRenderableWidget(buttonPrev = new PageButton(guiLeft + xSize / 5, guiTop + 5 * ySize / 6, false, (btn) -> {
            if (pageNumber > 0) {
                prevPage();
            }
        }, true));

        content = Arrays.stream(info.content()).map(Component::literal).flatMap(v -> prepareForLongText(v, 164, 120, 120).stream()).collect(Collectors.toList());

    }

    public static void drawCenteredStringWithoutShadow(@NotNull PoseStack p_238471_0_, @NotNull Font p_238471_1_, @NotNull String p_238471_2_, int p_238471_3_, int p_238471_4_, int p_238471_5_) {
        p_238471_1_.draw(p_238471_0_, p_238471_2_, (float) (p_238471_3_ - p_238471_1_.width(p_238471_2_) / 2), (float) p_238471_4_, p_238471_5_);
    }

    @Override
    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == this.minecraft.options.keyUse.getKey().getValue()) {
            this.minecraft.setScreen(null);
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_RIGHT) && pageNumber + 1 < content.size()) {
            nextPage();
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_LEFT) && pageNumber > 0) {
            prevPage();
            return true;
        }
        return super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);

    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double movement) {

        if (movement < 0) {
            nextPage();
        } else if (movement > 0) {
            prevPage();
        }


        return movement != 0 || super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, movement);

    }

    public void nextPage() {
        if (pageNumber != content.size() - 1 && !content.isEmpty()) {
            pageNumber++;
        }
    }

    public void prevPage() {
        if (pageNumber != 0) {
            pageNumber--;
        }
    }

    /**
     * Copied from {@link de.maxanier.guideapi.api.util.PageHelper}
     * Split the given text into multiple section if it does not fit one page.
     * The first page can have a different number of lines than the subsequent ones if desired
     * Insert new line characters to wrap the text to the available line width.
     *
     * @param text             Text component to process
     * @param lineWidth        Available width (pixel)
     * @param firstHeight      Available height on the first page (pixel)
     * @param subsequentHeight Available height on subsequent pages (pixel)
     * @return Each list element should be drawn on a individual page. Lines are wrapped using '\n'
     */
    private static @NotNull List<FormattedText> prepareForLongText(@NotNull Component text, int lineWidth, int firstHeight, int subsequentHeight) {
        Font fontRenderer = Minecraft.getInstance().font;
        int firstCount = firstHeight / fontRenderer.lineHeight;
        int count = subsequentHeight / fontRenderer.lineHeight;
        List<FormattedText> lines = new ArrayList<>(fontRenderer.getSplitter().splitLines(text, lineWidth, Style.EMPTY));
        List<FormattedText> pages = new ArrayList<>();

        List<FormattedText> pageLines = lines.size() > firstCount ? lines.subList(0, firstCount) : lines;
        pages.add(combineWithNewLine(pageLines));
        pageLines.clear();
        while (lines.size() > 0) {
            pageLines = lines.size() > count ? lines.subList(0, count) : lines;
            pages.add(combineWithNewLine(pageLines));
            pageLines.clear();
        }
        return pages;
    }

    /**
     * Copied from {@link de.maxanier.guideapi.api.util.PageHelper}
     *
     * @param elements The list ist not used itself, but the elements are passed to the new ITextProperties
     * @return a new ITextProperties that combines the given elements with a newline in between
     */
    private static @NotNull FormattedText combineWithNewLine(@NotNull List<FormattedText> elements) {
        FormattedText newLine = Component.literal("\n");
        List<FormattedText> copy = new ArrayList<>(elements.size() * 2);
        for (int i = 0; i < elements.size() - 1; i++) {
            copy.add(elements.get(i));
            copy.add(newLine);
        }
        copy.add(elements.get(elements.size() - 1));
        return FormattedText.composite(copy);
    }

}
