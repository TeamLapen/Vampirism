package de.teamlapen.vampirism.common.integration.jei.categories;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.common.util.Color;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.world.items.ExtendedPotionMix;
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
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VaporStillRecipeCategory extends AbstractRecipeCategory<JEIPotionMix> {

    private static final Identifier BACKGROUND_TEXTURE = VIdentifier.mod("textures/gui/jei/distilling.png");
    private static final String REAGENT_SLOT = "reagent";

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

        if (!requiredSkills(recipe).isEmpty() || recipe.hasReducedCost()) {
            Minecraft minecraft = Minecraft.getInstance();
            Component label = Component.translatable("gui.vampirism.jei.requirements");
            int x = getWidth() - minecraft.font.width(label) - 2;
            int y = getHeight() - minecraft.font.lineHeight - 2;
            //noinspection ConstantConditions
            graphics.text(minecraft.font, label, x, y, ARGB.color(1.0f, labelColor(recipe, recipeSlotsView).getColor()), false);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, JEIPotionMix recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> skillLines = buildSkillLines(recipe);
        List<Component> discountLines = buildDiscountLines(recipe);
        if (skillLines.isEmpty() && discountLines.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        Component label = Component.translatable("gui.vampirism.jei.requirements");
        int labelX = getWidth() - minecraft.font.width(label) - 2;
        int labelY = getHeight() - minecraft.font.lineHeight - 2;

        if (mouseX >= labelX && mouseX <= labelX + minecraft.font.width(label) && mouseY >= labelY && mouseY <= labelY + minecraft.font.lineHeight) {
            if (!skillLines.isEmpty()) {
                tooltip.add(Component.translatable("gui.vampirism.jei.requirements.tooltip"));
                tooltip.addAll(skillLines);
            }
            if (!discountLines.isEmpty()) {
                if (!skillLines.isEmpty()) {
                    tooltip.add(Component.empty());
                }
                tooltip.add(Component.translatable("gui.vampirism.jei.reduced_cost.tooltip"));
                tooltip.addAll(discountLines);
            }
        }
    }

    private @Nullable ISkillHandler<IHunterPlayer> hunterSkills() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return null;

        HunterPlayer hunter = HunterPlayer.get(player);
        return hunter.getLevel() > 0 ? hunter.getSkillHandler() : null;
    }

    private List<Holder<? extends ISkill<IHunterPlayer>>> requiredSkills(JEIPotionMix recipe) {
        ExtendedPotionMix mix = recipe.getOriginal();
        List<Holder<? extends ISkill<IHunterPlayer>>> required = new ArrayList<>();

        if (mix.durable) {
            required.add(HunterSkills.DURABLE_BREWING);
        } else if (mix.concentrated) {
            required.add(HunterSkills.CONCENTRATED_BREWING);
        }
        if (mix.master) {
            required.add(HunterSkills.MASTER_BREWER);
        }
        if (mix.sovereign) {
            required.add(HunterSkills.ULTIMATE_BREWER);
        }

        return required;
    }

    private boolean hasSkill(@Nullable ISkillHandler<IHunterPlayer> skills, Holder<? extends ISkill<IHunterPlayer>> skill) {
        if (skills == null) return false;
        if (skills.isSkillEnabled(skill)) return true;

        return (Objects.equals(skill, HunterSkills.DURABLE_BREWING) || Objects.equals(skill, HunterSkills.CONCENTRATED_BREWING)) && skills.isSkillEnabled(HunterSkills.CONCENTRATED_DURABLE_BREWING);
    }

    private boolean showingReducedCost(JEIPotionMix recipe, IRecipeSlotsView recipeSlotsView) {
        List<Integer> amounts = recipe.getMix1Amounts();
        if (amounts.size() < 2) return false;

        int reduced = amounts.get(1);

        return recipeSlotsView.findSlotByName(REAGENT_SLOT).flatMap(IRecipeSlotView::getDisplayedItemStack).map(stack -> stack.getCount() == reduced).orElse(false);
    }

    private ChatFormatting labelColor(JEIPotionMix recipe, IRecipeSlotsView recipeSlotsView) {
        ISkillHandler<IHunterPlayer> skills = hunterSkills();
        boolean satisfied = requiredSkills(recipe).stream().allMatch(skill -> hasSkill(skills, skill));

        if (satisfied && showingReducedCost(recipe, recipeSlotsView)) {
            satisfied = hasSkill(skills, HunterSkills.EFFICIENT_BREWING);
        }

        return satisfied ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED;
    }

    private List<Component> buildSkillLines(JEIPotionMix recipe) {
        ISkillHandler<IHunterPlayer> skills = hunterSkills();
        List<Component> skillLines = new ArrayList<>();

        for (Holder<? extends ISkill<IHunterPlayer>> skill : requiredSkills(recipe)) {
            addSkillLine(skill, skillLines, skills);
        }

        return skillLines;
    }

    private List<Component> buildDiscountLines(JEIPotionMix recipe) {
        if (!recipe.hasReducedCost()) return List.of();

        List<Component> discountLines = new ArrayList<>();
        addSkillLine(HunterSkills.EFFICIENT_BREWING, discountLines, hunterSkills());

        return discountLines;
    }

    private void addSkillLine(Holder<? extends ISkill<IHunterPlayer>> skill, List<Component> skillLines, @Nullable ISkillHandler<IHunterPlayer> skills) {
        skillLines.add(skill.value().getName().copy().withStyle(hasSkill(skills, skill) ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, JEIPotionMix recipe, IFocusGroup focuses) {
        int steps = recipe.getBrewingSteps();
        String stepsString = steps < Integer.MAX_VALUE ? Integer.toString(steps) : "?";
        Component label = Component.translatable("gui.jei.category.brewing.steps", stepsString);

        int labelWidth = Minecraft.getInstance().font.width(label);
        int x = 100 - labelWidth / 2;

        builder.addText(label, labelWidth, 10).setPosition(x, 27).setColor(Color.GRAY.getRGB());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JEIPotionMix recipe, IFocusGroup focuses) {
        ContextMap contextMap = SlotDisplayContext.fromLevel(Objects.requireNonNull(Minecraft.getInstance().level));

        var mix1 = new SlotDisplay.Composite(
                recipe.getMix1().display()
                        .resolve(contextMap, SlotDisplay.ItemStackContentsFactory.INSTANCE)
                        .flatMap(x -> recipe.getMix1Amounts().stream().map(amount -> (SlotDisplay) new ItemStackWithSize(x.typeHolder(), amount)))
                        .collect(Collectors.toList()));
        var mix2 = new SlotDisplay.Composite(
                recipe.getMix2().display()
                        .resolve(contextMap, SlotDisplay.ItemStackContentsFactory.INSTANCE)
                        .flatMap(x -> recipe.getMix2Amounts().stream().map(amount -> (SlotDisplay) new ItemStackWithSize(x.typeHolder(), amount)))
                        .collect(Collectors.toList()));

        builder.addInputSlot(7, 51).add(recipe.getPotionInput());
        builder.addInputSlot(25, 51).add(recipe.getPotionInput());
        builder.addInputSlot(43, 51).add(recipe.getPotionInput());

        builder.addInputSlot(16, 5).add(mix1).setSlotName(REAGENT_SLOT);
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
