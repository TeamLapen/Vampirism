package de.teamlapen.vampirism.client.gui.screens.taskboard;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.components.ContainerObjectSelectionListWithDummy;
import de.teamlapen.lib.lib.util.MultilineTooltip;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.entity.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.inventory.TaskMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.gui.ScreenUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class TaskList extends ContainerObjectSelectionListWithDummy<ITaskInstance, TaskList.TaskEntry, TaskList.DummyEntry> {
    protected static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");

    protected final TaskMenu menu;
    protected final IFactionPlayer<?> factionPlayer;
    protected final Registry<Task> registry;

    public TaskList(Minecraft minecraft, TaskMenu menu, IFactionPlayer<?> factionPlayer, int x, int y, int width, int height, int itemHeight, Supplier<List<ITaskInstance>> itemSupplier) {
        super(minecraft, width, height, y, y + height, itemHeight, itemSupplier);
        this.menu = menu;
        this.factionPlayer = factionPlayer;
        this.registry = factionPlayer.getRepresentingPlayer().level.registryAccess().registryOrThrow(VampirismRegistries.TASK_ID);
        setLeftPos(x);
    }

    @Override
    protected TaskEntry createItem(ITaskInstance item) {
        return new TaskEntry(item);
    }

    @Override
    protected DummyEntry createDummy(ITaskInstance item) {
        return new DummyEntry(item);
    }

    @Override
    protected boolean isEquivalent(ITaskInstance item1, ITaskInstance item2) {
        return Objects.equals(item1.getId(), item2.getId());
    }

    public class TaskEntry extends ContainerObjectSelectionListWithDummy<ITaskInstance, TaskEntry, DummyEntry>.ItemEntry {

        private final Task task;
        private final Tooltip tooltip;

        public TaskEntry(ITaskInstance item) {
            super(item);
            this.task = registry.get(item.getTask());
            this.tooltip = generateTaskToolTip();
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }

        public void renderBackground(PoseStack stack, Minecraft minecraft, int pTop, int pLeft, int pWidth, int pHeight, int mouseX, int mouseY, float partialTicks) {
            if (menu.isCompleted(this.getItem())) {
                RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 1);
            } else {
                boolean isUnique = this.getItem().isUnique(menu.getRegistry());
                boolean remainsTime = this.getItem().getTaskTimeStamp() - minecraft.level.getGameTime() > 0;
                if (menu.canCompleteTask(this.getItem())) {
                    if (isUnique) {
                        RenderSystem.setShaderColor(1f, 0.855859375f, 0, 1);
                    } else {
                        RenderSystem.setShaderColor(0, 0.9f, 0, 1);
                    }
                } else if (menu.isTaskNotAccepted(this.getItem())) {
                    if (isUnique) {
                        RenderSystem.setShaderColor(0.64f, 0.57f, 0.5f, 1);
                    } else {
                        RenderSystem.setShaderColor(0.55f, 0.55f, 0.55f, 1);
                    }
                } else if (!isUnique && !remainsTime) {
                    RenderSystem.setShaderColor(1f, 85 / 255f, 85 / 255f, 1);
                } else {
                    if (isUnique) {
                        RenderSystem.setShaderColor(1f, 0.9f, 0.6f, 1f);
                    } else {
                        RenderSystem.setShaderColor(0.85f, 1f, 0.85f, 1f);
                    }
                }
            }
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TASKMASTER_GUI_TEXTURE);
            GuiComponent.blit(stack, pLeft, pTop, 0, 17, 187, Math.min(pWidth - 1, 135), pHeight, 256, 256);
            GuiComponent.blit(stack, pLeft + pWidth - Math.min(pWidth - 1, 135), pTop, 0, 17 + (135 - Math.min(pWidth - 1, 134)), 187, Math.min(pWidth - 1, 135), pHeight, 256, 256);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            Minecraft mc = Minecraft.getInstance();
            this.renderBackground(pPoseStack, mc, pTop, pLeft, pWidth, pHeight + 4, pMouseX, pMouseY, pPartialTick);

            //render name
            Optional<FormattedCharSequence> text = Optional.ofNullable(mc.font.split(this.task.getTitle(), 131).get(0));
            text.ifPresent(t -> mc.font.draw(pPoseStack, t, pLeft + 2, pTop + 4, 3419941));//(6839882 & 16711422) >> 1 //8453920 //4226832

            //render progress
            if (!menu.isTaskNotAccepted(this.getItem()) && !this.getItem().isUnique(menu.getRegistry())) {
                long remainingTime = this.getItem().getTaskTimeStamp() - Minecraft.getInstance().level.getGameTime();
                Component msg;
                if (remainingTime >= 0) {
                    remainingTime = remainingTime / 20;
                    long hours = remainingTime / 60 / 60;
                    long minutes = remainingTime / 60 % (60);
                    long seconds = remainingTime % (60);
                    String time = hours + ":";
                    if (minutes < 10) time += "0";
                    time += minutes + ":";
                    if (seconds < 10) time += "0";
                    time += seconds;
                    msg = Component.literal(time);
                } else {
                    msg = Component.translatable("text.vampirism.task_failed");
                }
                int width = mc.font.width(msg);
                int color = 11184810;
                if (remainingTime < this.getItem().getTaskDuration() / 20F * 0.1F) {
                    color = 16733525;
                }
                mc.font.drawShadow(pPoseStack, msg, pLeft + pWidth - width - 1, pTop + 12, color);
            }

            if (isMouseOver(pMouseX, pMouseY)) {
                renderToolTips(mc, pMouseX, pMouseY);
            }
        }

        protected void renderToolTips(Minecraft minecraft, int mouseX, int mouseY) {
            minecraft.screen.setTooltipForNextRenderPass(this.tooltip, DefaultTooltipPositioner.INSTANCE, this.isFocused());
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        private Tooltip generateTaskToolTip() {
            Task task = menu.getTask(this.getItem().getTask());
            List<Component> toolTips = new ArrayList<>();
            toolTips.add(this.task.getTitle().plainCopy().withStyle(style -> style.withColor(menu.getFactionColor())));
            task.getDescription().ifPresent(component -> {
                toolTips.add(component);
                toolTips.add(Component.literal(" "));
            });
            if (menu.isTaskNotAccepted(this.getItem())) {
                toolTips.add(Component.translatable("gui.vampirism.taskmaster.not_accepted"));
            } else {
                for (List<TaskRequirement.Requirement<?>> requirements : task.getRequirement().requirements().values()) {
                    if (requirements == null) continue;
                    TaskRequirement.Type type = requirements.get(0).getType();
                    boolean completed = menu.areRequirementsCompleted(this.getItem(), type);
                    MutableComponent title = Component.translatable(type.getTranslationKey()).append(":");

                    if (completed) {
                        title.withStyle(ChatFormatting.STRIKETHROUGH);
                    }
                    toolTips.add(title);
                    for (TaskRequirement.Requirement<?> requirement : requirements) {
                        MutableComponent desc;
                        int completedAmount = menu.getRequirementStatus(this.getItem(), requirement);
                        desc = switch (type) {
                            case ITEMS -> ((Item) requirement.getStat(factionPlayer)).getDescription().plainCopy().append(" " + completedAmount + "/" + requirement.getAmount(factionPlayer));
                            default -> requirement.description().plainCopy();
                        };
                        if (completed || menu.isRequirementCompleted(this.getItem(), requirement)) {
                            desc.withStyle(ChatFormatting.STRIKETHROUGH);
                        }
                        toolTips.add(Component.literal("  ").append(desc));
                    }
                }
            }
            return new MultilineTooltip(toolTips);
        }
    }

    public class DummyEntry extends ContainerObjectSelectionListWithDummy<ITaskInstance, TaskEntry, DummyEntry>.DummyEntry {

        private final List<AbstractWidget> children = new ArrayList<>();

        public DummyEntry(ITaskInstance item) {
            super(item);

            List<TaskRequirement.Requirement<?>> all = menu.getTask(item.getTask()).getRequirement().getAll();
            for (int i = 0; i < all.size(); i++) {
                this.children.add(new RequirementWidget(3 + 3 + i * 20, 3, item, all.get(i)));

            }

            this.children.add(new RewardWidget(3 + getRowWidth() - 45, 3, item));
            this.children.add(new TaskActionButton(getRowWidth() - 25, 4));
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return this.children;
        }


        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            renderBg(pPoseStack, pIndex, pTop, pLeft, pWidth, pHeight + 4, pMouseX, pMouseY, pIsMouseOver, pPartialTick);
            this.children.forEach(w -> ((MovableWidget) w).setOffset(pLeft, pTop));
            this.children.forEach(a -> a.render(pPoseStack, pMouseX, pMouseY, pPartialTick));
        }

        protected void renderBg(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            ScreenUtils.blitWithBorder(pPoseStack, TASKMASTER_GUI_TEXTURE, pLeft + 2, pTop, 17, 229, pWidth - 4, pHeight, 136, 21, 1, 2, 3, 2, 0);
            ScreenUtils.blitWithBorder(pPoseStack, TASKMASTER_GUI_TEXTURE, pLeft + pWidth - 40, pTop, 17, 229, 40 - 2, pHeight, 136, 21, 1, 2, 3, 2, 0);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return this.children;
        }

        private interface MovableWidget {

            void setOffset(int x, int y);
        }

        private abstract class ItemWidget extends AbstractWidget implements MovableWidget {
            protected static final ItemStack PAPER = new ItemStack(Items.PAPER);

            private final ItemStack stack;
            private int xOffset;
            private int yOffset;

            public ItemWidget(int pX, int pY, ItemStack stack) {
                super(pX, pY, 16, 16, Component.empty());
                this.stack = stack;
            }

            @Override
            public void setOffset(int x, int y) {
                this.xOffset = x;
                this.yOffset = y;
            }

            @Override
            public int getX() {
                return super.getX() + this.xOffset;
            }

            @Override
            public int getY() {
                return super.getY() + this.yOffset;
            }

            @Override
            public void renderWidget(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
                Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(pPoseStack, this.stack, this.getX(), this.getY());

                if (this.isHovered) {
                    this.setTooltip(new MultilineTooltip(createTooltip()));
                }
            }

            @Override
            public void setX(int p_254495_) {
                super.setX(p_254495_);
            }

            @Override
            protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

            }

            protected abstract List<Component> createTooltip();

            protected List<Component> renderItemTooltip(@NotNull ItemStack stack, Component text, boolean strikeThrough, @Nullable String bonus) {
                List<Component> tooltips = getTooltipFromItem2(stack, strikeThrough, bonus);
                tooltips.add(0, text);
                return tooltips;
            }

            protected @NotNull List<Component> getTooltipFromItem2(@NotNull ItemStack itemStack, boolean strikeThough, @Nullable String bonus) {
                List<Component> list = itemStack.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
                List<Component> list1 = Lists.newArrayList();
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        MutableComponent t = list.get(0).copy().append(" " + (bonus != null ? bonus : "") + itemStack.getCount());
                        if (strikeThough) t.withStyle(ChatFormatting.STRIKETHROUGH);
                        list1.add(t);
                    } else {
                        list1.add(strikeThough ? list.get(i).copy().withStyle(ChatFormatting.STRIKETHROUGH) : list.get(i));
                    }
                }
                return list1;
            }
        }

        private class RewardWidget extends ItemWidget {
            protected static final Component REWARD = Component.translatable("gui.vampirism.taskmaster.reward").withStyle(ChatFormatting.UNDERLINE);

            private final ITaskInstance rewardInstance;
            private final Component reward;

            public RewardWidget(int pX, int pY, @NotNull ITaskInstance rewardInstance) {
                super(pX, pY, rewardInstance.getReward() instanceof ItemReward.Instance items ? items.reward() : PAPER);
                this.rewardInstance = rewardInstance;
                this.reward = Component.translatable(Util.makeDescriptionId("task", rewardInstance.getTask().location()) + ".reward");
            }

            @Override
            protected List<Component> createTooltip() {
                if (this.rewardInstance.getReward() instanceof ItemReward.Instance item) {
                    return this.renderItemTooltip(item.reward(), REWARD, false, null);
                } else {
                    return this.renderItemTooltip();
                }
            }

            private List<Component> renderItemTooltip() {
                List<Component> tooltips = Lists.newArrayList(REWARD);
                tooltips.add(this.reward);
                return tooltips;
            }


        }

        private class RequirementWidget extends ItemWidget {
            protected static final Component REQUIREMENT = Component.translatable("gui.vampirism.taskmaster.requirement").withStyle(ChatFormatting.UNDERLINE);
            protected static final Component REQUIREMENT_STRIKE = REQUIREMENT.plainCopy().withStyle(ChatFormatting.STRIKETHROUGH);
            private final TaskRequirement.Requirement<?> requirement;
            private final ITaskInstance instance;
            protected static final ItemStack SKULL_ITEM = new ItemStack(Blocks.SKELETON_SKULL);

            public RequirementWidget(int pX, int pY, ITaskInstance instance, TaskRequirement.@NotNull Requirement<?> requirement) {
                super(pX, pY, switch (requirement.getType()) {
                    case ITEMS -> ((ItemRequirement) requirement).getItemStack();
                    case ENTITY, ENTITY_TAG -> SKULL_ITEM;
                    default -> PAPER;
                });
                this.requirement = requirement;
                this.instance = instance;
            }

            @Override
            protected List<Component> createTooltip() {
                boolean notAccepted = menu.isTaskNotAccepted(this.instance);
                boolean completed = menu.isRequirementCompleted(this.instance, this.requirement);
                int completedAmount = menu.getRequirementStatus(this.instance, this.requirement);
                return switch (this.requirement.getType()) {
                    case ITEMS -> this.renderItemTooltip(((ItemRequirement) requirement).getItemStack(), (completed ? REQUIREMENT_STRIKE : REQUIREMENT), completed, notAccepted ? null : (completedAmount + "/"));
                    case STATS, ENTITY_TAG, ENTITY ->
                            this.renderGenericRequirementTooltip(this.requirement.getType(), this.requirement.description().plainCopy().append((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(factionPlayer)), completed);
                    default -> this.renderDefaultRequirementToolTip(this.instance, requirement, completed);
                };
            }

            private List<Component> renderDefaultRequirementToolTip(@NotNull ITaskInstance task, TaskRequirement.@NotNull Requirement<?> requirement, boolean strikeThrough) {
                List<Component> tooltips = Lists.newArrayList();
                tooltips.add((strikeThrough ? REQUIREMENT_STRIKE : REQUIREMENT));
                MutableComponent text = requirement.description().plainCopy();
                if (strikeThrough) {
                    text.withStyle(ChatFormatting.STRIKETHROUGH);
                }
                tooltips.add(text);
                return tooltips;
            }

            private List<Component> renderGenericRequirementTooltip(TaskRequirement.@NotNull Type type, @NotNull MutableComponent text, boolean strikeThrough) {
                List<Component> tooltips = Lists.newArrayList();
                MutableComponent title = Component.translatable(type.getTranslationKey()).append(":");
                if (strikeThrough) {
                    text.withStyle(ChatFormatting.STRIKETHROUGH);
                    title.withStyle(ChatFormatting.STRIKETHROUGH);
                }
                tooltips.add((strikeThrough ? REQUIREMENT_STRIKE : REQUIREMENT));
                tooltips.add(title.withStyle(ChatFormatting.ITALIC));
                tooltips.add(Component.literal("  ").append(text));
                return tooltips;
            }
        }

        private class TaskActionButton extends ImageButton implements MovableWidget {

            private int xOffset;
            private int yOffset;

            public TaskActionButton(int xPos, int yPos) {
                super(xPos, yPos, 14, 13, 0, 0, 0, TASKMASTER_GUI_TEXTURE, 0, 0, a -> menu.pressButton(getItem()), Component.literal(""));
            }

            @Override
            public void setOffset(int x, int y) {
                this.xOffset = x;
                this.yOffset = y;
            }

            @Override
            public int getX() {
                return super.getX() + this.xOffset;
            }

            @Override
            public int getY() {
                return super.getY() + this.yOffset;
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                if (mouseX > this.getX() && mouseX < this.getX() + this.width && mouseY > this.getY() && mouseY < this.getY() + this.height) {
                    super.onClick(mouseX, mouseY);
                }
            }

            @Override
            public void renderWidget(@NotNull PoseStack mStack, int mouseX, int mouseY, float p_renderButton_3_) {
                TaskMenu.TaskAction action = menu.buttonAction(getItem());
                RenderSystem.enableDepthTest();
                RenderSystem.setShaderTexture(0, TASKMASTER_GUI_TEXTURE);
                int j = switch (action) {
                    case ACCEPT -> 190;
                    case COMPLETE -> 176;
                    default -> 204;
                };

                blit(mStack, this.getX(), this.getY(), (float) j, (float) (this.isHovered ? 13 : 0), this.width, this.height, 256, 256);
                RenderSystem.disableDepthTest();

                if (this.isHovered) {
                    this.setTooltip(Tooltip.create(Component.translatable(action.getTranslationKey())));
                }
            }
        }
    }
}