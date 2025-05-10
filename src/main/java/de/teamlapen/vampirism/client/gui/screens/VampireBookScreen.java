package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.general.IBookBackground;
import de.teamlapen.vampirism.api.general.IBookContents;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class VampireBookScreen extends Screen {

    private final int xSize;
    private final int ySize;
    private int guiLeft;
    private int guiTop;

    private VampireBookPageButton buttonForward;
    private VampireBookPageButton buttonBack;

    private final @NotNull IVampireBook vampireBook;
    private final IBookBackground background;
    private final List<IBookContents.IImageEntry> images;
    private List<FormattedText> content;
    private int pageNumber;

    public VampireBookScreen(@NotNull IVampireBook vampireBook) {
        super(vampireBook.title());
        this.vampireBook = vampireBook;
        this.background = vampireBook.background();
        this.images = vampireBook.images();
        this.xSize = background.textureWidth();
        this.ySize = background.textureHeight();
    }

    @Override
    protected void init() {
        super.init();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        buttonForward = this.addRenderableWidget(new VampireBookPageButton(guiLeft + xSize - background.pageButtonXOffset() - VampireBookPageButton.WIDTH, guiTop + ySize - background.pageButtonYOffset() - VampireBookPageButton.HEIGHT, true, button -> {
            if (pageNumber + 1 < content.size()) {
                pageForward();
            }
        }));
        buttonForward.visible = false;
        buttonBack = this.addRenderableWidget(new VampireBookPageButton(guiLeft + background.pageButtonXOffset(), guiTop + ySize - background.pageButtonYOffset() - VampireBookPageButton.HEIGHT, false, button -> {
            if (pageNumber > 0) {
                pageBack();
            }
        }));
        buttonBack.visible = false;

        content = vampireBook.contents().stream().flatMap(v -> prepareForLongText(v, background.textWidth() - 6, background.textHeight(), background.textHeight()).stream()).collect(Collectors.toList());

        updatePageButtonVisibility();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        pageNumber = Mth.clamp(pageNumber, 0, content.size() - 1);

        if (background.twoPages()) {
            if (pageNumber == 0) {
                drawPage(graphics, guiLeft + background.firstPageTextX(), guiTop + background.textY(), content.getFirst());
                drawPageNumber(graphics, guiLeft + xSize - background.pageNumberXOffset(), String.valueOf((pageNumber + 1)));
            } else {
                int leftPageIndex = pageNumber;
                int rightPageIndex = pageNumber + 1;

                if (leftPageIndex < content.size()) {
                    drawPage(graphics, guiLeft + background.leftPageTextX(), guiTop + background.textY(), content.get(leftPageIndex));
                    drawPageNumber(graphics, guiLeft + background.pageNumberXOffset(), String.valueOf((leftPageIndex + 1)));
                }
                if (rightPageIndex < content.size()) {
                    drawPage(graphics, guiLeft + background.rightPageTextX(), guiTop + background.textY(), content.get(rightPageIndex));
                    drawPageNumber(graphics, guiLeft + xSize - background.pageNumberXOffset(), String.valueOf((rightPageIndex + 1)));
                }
            }
        } else {
            if (pageNumber < content.size()) {
                drawPage(graphics, guiLeft + background.leftPageTextX(), guiTop + background.textY(), content.get(pageNumber));
                drawPageNumber(graphics, guiLeft + xSize / 2, String.valueOf(pageNumber + 1));
            }
        }

        graphics.drawCenteredString(font, title, guiLeft + xSize / 2, guiTop - 10, Color.WHITE.getRGB());

        updatePageButtonVisibility();
    }

    private void updatePageButtonVisibility() {
        boolean atLastPage = pageNumber >= content.size() - 1;
        boolean atFirstPage = pageNumber == 0;

        buttonBack.visible = !atFirstPage;
        if (background.twoPages()) {
            buttonForward.visible = content.size() % 2 == 0 ? !atLastPage : (pageNumber < content.size() - 2);
        } else {
            buttonForward.visible = !atLastPage;
        }
    }

    private void drawPage(GuiGraphics graphics, int x, int y, FormattedText text) {
        List<FormattedCharSequence> lines = this.font.split(text, background.textWidth());
        int currentY = y;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(this.font, line, x, currentY, background.textColor(), false);
            currentY += 10;
        }
    }

    private void drawPageNumber(GuiGraphics graphics, int x, String number) {
        graphics.drawString(this.font, number, x - this.font.width(number) / 2, this.guiTop + this.ySize - background.pageNumberYOffset(), background.textColor(), false);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        ResourceLocation backgroundTexture = null;
        if (pageNumber == 0 && background.textureFirstPage().isPresent()) {
            backgroundTexture = background.textureFirstPage().get();
        } else if (pageNumber + 1 >= content.size() && background.textureLastPage().isPresent()) {
            backgroundTexture = background.textureLastPage().get();
        }
        if (backgroundTexture == null) {
            backgroundTexture = background.texture();
        }

        guiGraphics.blit(RenderType::guiTextured, backgroundTexture, guiLeft, guiTop, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);

        for (IBookContents.IImageEntry entry : images) {
            if (entry.page() == pageNumber || (background.twoPages() && (entry.page() == pageNumber + 1))) {
                guiGraphics.blit(RenderType::guiTextured, entry.texture(), guiLeft + entry.xOffset(), guiTop + entry.yOffset(), 0, 0, entry.width(), entry.height(), entry.width(), entry.height());
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.minecraft != null && (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == this.minecraft.options.keyUse.getKey().getValue())) {
            this.minecraft.setScreen(null);
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_RIGHT) && pageNumber + 1 < content.size()) {
            pageForward();
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_LEFT) && pageNumber > 0) {
            pageBack();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void pageBack() {
        if (background.twoPages()) {
            if (pageNumber == 1) {
                pageNumber = 0;
            } else if (pageNumber - 2 >= 0) {
                pageNumber -= 2;
            } else {
                pageNumber = 0;
            }
        } else {
            if (pageNumber > 0) {
                pageNumber--;
            }
        }
    }

    public void pageForward() {
        if (background.twoPages()) {
            if (pageNumber == 0) {
                pageNumber = 1;
            } else if (pageNumber + 2 < content.size()) {
                pageNumber += 2;
            } else if (pageNumber + 1 < content.size()) {
                pageNumber++;
            }
        } else {
            if (pageNumber + 1 < content.size()) {
                pageNumber++;
            }
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
     * @return Each list element should be drawn on an individual page. Lines are wrapped using '\n'
     */
    @SuppressWarnings({"JavadocReference"})
    private static @NotNull List<FormattedText> prepareForLongText(@NotNull Component text, int lineWidth, int firstHeight, int subsequentHeight) {
        Font fontRenderer = Minecraft.getInstance().font;
        int firstCount = firstHeight / fontRenderer.lineHeight;
        int count = subsequentHeight / fontRenderer.lineHeight;
        List<FormattedText> lines = new ArrayList<>(fontRenderer.getSplitter().splitLines(text, lineWidth, Style.EMPTY));
        List<FormattedText> pages = new ArrayList<>();

        List<FormattedText> pageLines = lines.size() > firstCount ? lines.subList(0, firstCount) : lines;
        pages.add(combineWithNewLine(pageLines));
        pageLines.clear();
        while (!lines.isEmpty()) {
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
    @SuppressWarnings("JavadocReference")
    private static @NotNull FormattedText combineWithNewLine(@NotNull List<FormattedText> elements) {
        FormattedText newLine = Component.literal("\n");
        List<FormattedText> copy = new ArrayList<>(elements.size() * 2);
        for (int i = 0; i < elements.size() - 1; i++) {
            copy.add(elements.get(i));
            copy.add(newLine);
        }
        copy.add(elements.getLast());
        return FormattedText.composite(copy);
    }

    public static class VampireBookPageButton extends Button {

        private static final ResourceLocation PAGE_FORWARD_HIGHLIGHTED_SPRITE = VResourceLocation.mod("widget/vampire_book_page_forward_highlighted");
        private static final ResourceLocation PAGE_FORWARD_SPRITE = VResourceLocation.mod("widget/vampire_book_page_forward");
        private static final ResourceLocation PAGE_BACKWARD_HIGHLIGHTED_SPRITE = VResourceLocation.mod("widget/vampire_book_page_backward_highlighted");
        private static final ResourceLocation PAGE_BACKWARD_SPRITE = VResourceLocation.mod("widget/vampire_book_page_backward");

        private static final int WIDTH = 23;
        private static final int HEIGHT = 13;

        private final boolean isForward;

        protected VampireBookPageButton(int x, int y, boolean isForward, Button.OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.isForward = isForward;
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ResourceLocation resourcelocation;
            if (this.isForward) {
                resourcelocation = this.isHovered() ? PAGE_FORWARD_HIGHLIGHTED_SPRITE : PAGE_FORWARD_SPRITE;
            } else {
                resourcelocation = this.isHovered() ? PAGE_BACKWARD_HIGHLIGHTED_SPRITE : PAGE_BACKWARD_SPRITE;
            }

            guiGraphics.blitSprite(RenderType::guiTextured, resourcelocation, this.getX(), this.getY(), WIDTH, HEIGHT);
        }

        @Override
        public void playDownSound(SoundManager handler) {
            handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }
}
