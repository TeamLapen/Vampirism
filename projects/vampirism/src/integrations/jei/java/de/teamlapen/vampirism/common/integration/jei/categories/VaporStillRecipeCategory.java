package de.teamlapen.vampirism.common.integration.jei.categories;

import de.teamlapen.faction.common.util.Color;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.gui.screens.VaporStillScreen;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.integration.jei.JEIPotionMix;
import de.teamlapen.vampirism.common.integration.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.display.ItemStackWithSize;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VaporStillRecipeCategory extends AbstractRecipeCategory<JEIPotionMix> {

    private static final Identifier BACKGROUND_TEXTURE = VIdentifier.mod("textures/gui/jei/distilling.png");

    private final IDrawable background;
    private final IDrawable arrow;
    private final IDrawable flames;

    public VaporStillRecipeCategory(IGuiHelper guiHelper) {
        super(
                VampirismJEIPlugin.POTION,
                Component.translatable("gui.vampirism.jei.category.distilling"),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.VAPOR_STILL.get())),
                150,
                70
        );
        this.background = guiHelper.drawableBuilder(BACKGROUND_TEXTURE, 0, 0, 150, 70)
                .setTextureSize(150, 70)
                .build();

        var flamesStatic = guiHelper.drawableBuilder(fixSpriteId(VaporStillScreen.SPRITE_FLAMES), 0, 0, 26, 15)
                .setTextureSize(26, 15)
                .build();
        this.flames = guiHelper.createAnimatedDrawable(flamesStatic, new FlamesTickTimer(guiHelper), IDrawableAnimated.StartDirection.BOTTOM);

        var arrowStatic = guiHelper.drawableBuilder(fixSpriteId(VaporStillScreen.SPRITE_PROGRESS), 0, 0, 9, 29)
                .setTextureSize(9, 29)
                .build();
        this.arrow = guiHelper.createAnimatedDrawable(arrowStatic, 400, IDrawableAnimated.StartDirection.TOP, false);
    }

    private static Identifier fixSpriteId(Identifier spriteLoc) {
        return spriteLoc.withPrefix("textures/gui/sprites/").withSuffix(".png");
    }

    @Override
    public void draw(JEIPotionMix recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.background.draw(graphics, 0, 0);

        this.flames.draw(graphics, 104, 23);
        this.arrow.draw(graphics, 86, 15);

        Minecraft minecraft = Minecraft.getInstance();
        List<Component> skillLines = buildSkillLines(recipe);

        if (!skillLines.isEmpty()) {
            int y = 4;
            graphics.drawString(minecraft.font, Component.translatable("gui.vampirism.jei.requirements"), 2, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight + 1;
            for (Component line : skillLines) {
                graphics.drawString(minecraft.font, line, 2, y, Color.GRAY.getRGB(), false);
                y += minecraft.font.lineHeight + 1;
            }
        }
    }

    private List<Component> buildSkillLines(JEIPotionMix recipe) {
        java.util.List<Component> skillLines = new java.util.ArrayList<>();
        if (recipe.getOriginal().durable && recipe.getOriginal().concentrated) {
            skillLines.add(HunterSkills.CONCENTRATED_DURABLE_BREWING.get().getName());
        } else if (recipe.getOriginal().durable) {
            skillLines.add(HunterSkills.DURABLE_BREWING.get().getName());
        } else if (recipe.getOriginal().concentrated) {
            skillLines.add(HunterSkills.CONCENTRATED_BREWING.get().getName());
        }
        if (recipe.getOriginal().master) {
            skillLines.add(HunterSkills.MASTER_BREWER.get().getName());
        }
        if (recipe.getOriginal().efficient) {
            skillLines.add(HunterSkills.EFFICIENT_BREWING.get().getName());
        }
        return skillLines;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JEIPotionMix recipe, IFocusGroup focuses) {
        ContextMap contextMap = SlotDisplayContext.fromLevel(Objects.requireNonNull(Minecraft.getInstance().level));

        var mix1 = new SlotDisplay.Composite(
                recipe.getMix1().display()
                        .resolve(contextMap, SlotDisplay.ItemStackContentsFactory.INSTANCE)
                        .map(x -> new ItemStackWithSize(x.getItemHolder(), recipe.getMix1Amount()))
                        .collect(Collectors.toList()));
        var mix2 = new SlotDisplay.Composite(
                recipe.getMix2().display()
                        .resolve(contextMap, SlotDisplay.ItemStackContentsFactory.INSTANCE)
                        .map(x -> new ItemStackWithSize(x.getItemHolder(), recipe.getMix2Amount()))
                        .collect(Collectors.toList()));

        builder.addInputSlot(91, 51).add(recipe.getPotionInput());
        builder.addInputSlot(109, 51).add(recipe.getPotionInput());
        builder.addInputSlot(127, 51).add(recipe.getPotionInput());

        builder.addInputSlot(100, 5).add(mix1);
        builder.addInputSlot(118, 5).add(mix2);

        builder.addOutputSlot(7, 51).add(recipe.getPotionOutput()).setStandardSlotBackground();
    }

    private record FlamesTickTimer(ITickTimer internalTimer) implements ITickTimer {

        private static final int FLAMES_SPRITE_HEIGHT = 15;
        private static final int FLAMES_FRAME_COUNT = 7;
        private static final int[] FLAME_HEIGHTS;

        static {
            FLAME_HEIGHTS = new int[FLAMES_FRAME_COUNT];
            for (int frame = 0; frame < FLAMES_FRAME_COUNT; frame++) {
                FLAME_HEIGHTS[frame] = FLAMES_SPRITE_HEIGHT - (frame * FLAMES_SPRITE_HEIGHT / (FLAMES_FRAME_COUNT - 1));
            }
        }

        private FlamesTickTimer(IGuiHelper internalTimer) {
            this(internalTimer.createTickTimer(FLAMES_FRAME_COUNT * 2, FLAMES_FRAME_COUNT - 1, false));
        }

        @Override
        public int getValue() {
            return FLAME_HEIGHTS[internalTimer.getValue()];
        }

        @Override
        public int getMaxValue() {
            return FLAME_HEIGHTS[0];
        }
    }
}
