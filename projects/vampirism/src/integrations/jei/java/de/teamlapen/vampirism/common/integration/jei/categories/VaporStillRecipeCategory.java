package de.teamlapen.vampirism.common.integration.jei.categories;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.common.util.Color;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.client.gui.screens.VaporStillScreen;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.integration.jei.JEIPotionMix;
import de.teamlapen.vampirism.common.integration.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.display.ItemStackWithSize;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
                VampirismJEIPlugin.DISTILLING,
                Component.translatable("gui.vampirism.jei.category.distilling"),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.VAPOR_STILL.get())),
                130,
                70
        );
        this.background = guiHelper.drawableBuilder(BACKGROUND_TEXTURE, 0, 0, 130, 70)
                .setTextureSize(130, 70)
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
    public void draw(JEIPotionMix recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics, 0, 0);

        this.flames.draw(graphics, 20, 24);
        this.arrow.draw(graphics, 56, 15);

        List<Component> skillLines = buildSkillLines(recipe);
        if (!skillLines.isEmpty()) {
            Minecraft minecraft = Minecraft.getInstance();
            Component label = Component.translatable("gui.vampirism.jei.requirements");
            int x = getWidth() - minecraft.font.width(label) - 2;
            int y = getHeight() - minecraft.font.lineHeight - 2;
            graphics.drawString(minecraft.font, label, x, y, Color.GRAY.getRGB(), false);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, JEIPotionMix recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> skillLines = buildSkillLines(recipe);
        if (skillLines.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        Component label = Component.translatable("gui.vampirism.jei.requirements");
        int labelX = getWidth() - minecraft.font.width(label) - 2;
        int labelY = getHeight() - minecraft.font.lineHeight - 2;

        if (mouseX >= labelX && mouseX <= labelX + minecraft.font.width(label) && mouseY >= labelY && mouseY <= labelY + minecraft.font.lineHeight) {
            tooltip.add(Component.translatable("gui.vampirism.jei.requirements.tooltip"));
            tooltip.addAll(skillLines);
        }
    }

    private List<Component> buildSkillLines(JEIPotionMix recipe) {
        ISkillHandler<IHunterPlayer> skills = null;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            HunterPlayer hunter = HunterPlayer.get(player);
            if (hunter.getLevel() > 0) {
                skills = hunter.getSkillHandler();
            }
        }

        List<Component> skillLines = new ArrayList<>();

        if (recipe.getOriginal().durable && recipe.getOriginal().concentrated) {
            addSkillLine(HunterSkills.CONCENTRATED_DURABLE_BREWING, skillLines, skills);
        } else if (recipe.getOriginal().durable) {
            addSkillLine(HunterSkills.DURABLE_BREWING, skillLines, skills);
        } else if (recipe.getOriginal().concentrated) {
            addSkillLine(HunterSkills.CONCENTRATED_BREWING, skillLines, skills);
        }
        if (recipe.getOriginal().master) {
            addSkillLine(HunterSkills.MASTER_BREWER, skillLines, skills);
        }
        if (recipe.getOriginal().efficient) {
            addSkillLine(HunterSkills.EFFICIENT_BREWING, skillLines, skills);
        }
        return skillLines;
    }

    private void addSkillLine(DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> skill, List<Component> skillLines, @Nullable ISkillHandler<IHunterPlayer> skills) {
        skillLines.add(skill.get().getName().withStyle(skills != null && skills.isSkillEnabled(skill) ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, JEIPotionMix recipe, IFocusGroup focuses) {
        int steps = recipe.getBrewingSteps();
        String stepsString = steps < Integer.MAX_VALUE ? Integer.toString(steps) : "?";
        Component label = Component.translatable("gui.jei.category.brewing.steps", stepsString);

        int labelWidth = Minecraft.getInstance().font.width(label);
        int x = 100 - labelWidth / 2;

        builder.addText(label, labelWidth, 10)
                .setPosition(x, 27)
                .setColor(Color.GRAY.getRGB());
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

        builder.addInputSlot(7, 51).add(recipe.getPotionInput());
        builder.addInputSlot(25, 51).add(recipe.getPotionInput());
        builder.addInputSlot(43, 51).add(recipe.getPotionInput());

        builder.addInputSlot(16, 5).add(mix1);
        builder.addInputSlot(34, 5).add(mix2);

        builder.addOutputSlot(91, 5).add(recipe.getPotionOutput()).setStandardSlotBackground();
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
