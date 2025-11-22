package de.teamlapen.factions.client.gui.screens.taskboard;

import com.google.common.collect.Lists;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.tasks.ITaskInstance;
import de.teamlapen.factions.api.tasks.Task;
import de.teamlapen.factions.api.tasks.TaskRequirement;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.gui.MultilineTooltip;
import de.teamlapen.factions.client.gui.components.ContainerObjectSelectionListWithDummy;
import de.teamlapen.factions.common.inventory.ITaskMenu;
import de.teamlapen.factions.common.tasks.requirements.ItemRequirement;
import de.teamlapen.factions.common.tasks.reward.ItemReward;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class TaskList extends ContainerObjectSelectionListWithDummy<ITaskInstance, TaskList.TaskEntry, TaskList.DummyEntry> {
    private static final WidgetSprites ACCEPT = new WidgetSprites(FResourceLocation.mod("widget/task_action_accept"), FResourceLocation.mod("widget/task_action_accept_highlighted"));
    private static final WidgetSprites COMPLETE = new WidgetSprites(FResourceLocation.mod("widget/task_action_complete"), FResourceLocation.mod("widget/task_action_complete_highlighted"));
    private static final WidgetSprites ABORT = new WidgetSprites(FResourceLocation.mod("widget/task_action_abort"), FResourceLocation.mod("widget/task_action_abort_highlighted"));
    private static final ResourceLocation TASK_BACKGROUND = FResourceLocation.mod("widget/task_background");
    private static final ResourceLocation TASK_DETAILS_BACKGROUND = FResourceLocation.mod("widget/task_details_background");

    protected final ITaskMenu menu;
    protected final IFactionPlayer<?> factionPlayer;
    protected final Registry<Task> registry;

    public TaskList(Minecraft minecraft, ITaskMenu menu, IFactionPlayer<?> factionPlayer, int x, int y, int width, int height, Supplier<List<ITaskInstance>> itemSupplier) {
        super(minecraft, width, height, y, 21, itemSupplier);
        this.menu = menu;
        this.factionPlayer = factionPlayer;
        this.registry = factionPlayer.asEntity().level().registryAccess().lookupOrThrow(FactionRegistries.Keys.TASK);
        this.setX(x);
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
    protected void renderListSeparators(GuiGraphics p_331248_) {
    }

    @Override
    protected boolean isEquivalent(ITaskInstance item1, ITaskInstance item2) {
        return Objects.equals(item1.getId(), item2.getId());
    }

    public class TaskEntry extends ContainerObjectSelectionListWithDummy<ITaskInstance, TaskEntry, DummyEntry>.ItemEntry {

        private final Task task;
        private final WidgetTooltipHolder tooltipHolder = new WidgetTooltipHolder();

        public TaskEntry(ITaskInstance item) {
            super(item);
            this.task = registry.getValue(item.getTask());
            tooltipHolder.set(generateTaskToolTip());
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }

        public void renderBackground(GuiGraphics graphics, Minecraft minecraft, int pTop, int pLeft, int pWidth, int pHeight, int mouseX, int mouseY, float partialTicks) {
            int color;
            if (menu.isCompleted(this.getItem())) {
                color = ARGB.colorFromFloat(1, 0.4f, 0.4f, 0.4f);
            } else {
                boolean isUnique = this.getItem().isUnique(menu.getRegistry());
                boolean remainsTime = this.getItem().getTaskTimeStamp() - minecraft.level.getGameTime() > 0;
                if (menu.canCompleteTask(this.getItem())) {
                    if (isUnique) {
                        color = ARGB.colorFromFloat(1, 1f, 0.855859375f, 0);
                    } else {
                        color = ARGB.colorFromFloat(1, 0, 0.9f, 0);
                    }
                } else if (menu.isTaskNotAccepted(this.getItem())) {
                    if (isUnique) {
                        color = ARGB.colorFromFloat(1, 0.64f, 0.57f, 0.5f);
                    } else {
                        color = ARGB.colorFromFloat(1, 0.55f, 0.55f, 0.55f);
                    }
                } else if (!isUnique && !remainsTime) {
                    color = ARGB.colorFromFloat(1, 1f, 85 / 255f, 85 / 255f);
                } else {
                    if (isUnique) {
                        color = ARGB.colorFromFloat(1, 1f, 0.9f, 0.6f);
                    } else {
                        color = ARGB.colorFromFloat(1, 0.85f, 1f, 0.85f);
                    }
                }
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TASK_BACKGROUND, pLeft, pTop, pWidth, pHeight, color);
        }


        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            Minecraft mc = Minecraft.getInstance();
            int i = TaskList.this.children().indexOf(this);
            this.renderBackground(guiGraphics, mc, getY(), getRowLeft(), getRowWidth(), getHeight() - getY() + 4, mouseX, mouseY, partialTick);

            //render name
            Optional<FormattedCharSequence> text = Optional.ofNullable(mc.font.split(this.task.title(), 131).getFirst());
            text.ifPresent(t -> guiGraphics.drawString(mc.font, t, getRowLeft() + 2, getY() + 4, 3419941, false));//(6839882 & 16711422) >> 1 //8453920 //4226832

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
                guiGraphics.drawString(mc.font, msg, getRowLeft() + getRowWidth() - width - 1, getY() + 12, color, true);
            }

            if (isMouseOver(mouseX, mouseY + 3)) {
                renderToolTips(guiGraphics, mouseX, mouseY);
            }
        }

        protected void renderToolTips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            tooltipHolder.refreshTooltipForNextRenderPass(guiGraphics, mouseX, mouseY, true, false, getRectangle());
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        private Tooltip generateTaskToolTip() {
            Task task = menu.getTask(this.getItem().getTask());
            List<Component> toolTips = new ArrayList<>();
            toolTips.add(this.task.title().plainCopy().withStyle(style -> style.withColor(menu.getFactionColor())));
            task.description().ifPresent(component -> {
                toolTips.add(component);
                toolTips.add(Component.literal(" "));
            });
            if (menu.isTaskNotAccepted(this.getItem())) {
                toolTips.add(Component.translatable("gui.vampirism.taskmaster.not_accepted"));
            } else {
                for (List<TaskRequirement.Requirement<?>> requirements : task.requirements().requirements().values()) {
                    if (requirements == null) continue;
                    TaskRequirement.Type type = requirements.getFirst().getType();
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
                            case ITEMS -> Component.translatable(((Item) requirement.getStat(factionPlayer)).getDescriptionId());
                            default -> requirement.description().plainCopy();
                        };
                        desc = desc.append(" " + completedAmount + "/" + requirement.getAmount(factionPlayer));
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

            List<TaskRequirement.Requirement<?>> all = menu.getTask(item.getTask()).requirements().getAll();
            for (int i = 0; i < all.size(); i++) {
                this.children.add(new RequirementWidget(3 + 3 + i * 20, 3, item, all.get(i)));

            }

            this.children.add(RewardWidget.create(3 + getRowWidth() - 45, 3, item));
            this.children.add(new TaskActionButton(getRowWidth() - 25, 4));
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return this.children;
        }


        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            renderBg(guiGraphics, getY(), getRowLeft(), getRowWidth(), getHeight() + 4, mouseX, mouseY, isHovering, partialTick);
            this.children.forEach(w -> ((MovableWidget) w).setOffset(getRowLeft(), getY()));
            this.children.forEach(a -> a.render(guiGraphics, mouseX, mouseY, partialTick));
            this.children.stream().filter(x -> x.isMouseOver(mouseX, mouseY)).findFirst().ifPresent(w -> {
            });
        }

        protected void renderBg(@NotNull GuiGraphics graphics, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TASK_DETAILS_BACKGROUND, pLeft + 2, pTop, pWidth - 4, pHeight);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return this.children;
        }

        private interface MovableWidget {

            void setOffset(int x, int y);
        }

        private abstract static class ItemWidget extends AbstractWidget implements MovableWidget {
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
                return super.getX() + xOffset;
            }

            @Override
            public int getY() {
                return super.getY() + yOffset;
            }

            @Override
            public void renderWidget(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
                graphics.renderFakeItem(this.stack, this.getX(), this.getY());
            }

            @Override
            public void setX(int p_254495_) {
                super.setX(p_254495_);
            }

            @Override
            protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

            }

            protected abstract List<Component> createTooltip(Item.TooltipContext tooltipContext);

            protected List<Component> renderItemTooltip(@NotNull ItemStack stack, Component text, Item.TooltipContext context, boolean strikeThrough, @Nullable String bonus) {
                List<Component> tooltips = getTooltipFromItem2(context, stack, strikeThrough, bonus);
                tooltips.addFirst(text);
                return tooltips;
            }

            protected @NotNull List<Component> getTooltipFromItem2(Item.TooltipContext context, @NotNull ItemStack itemStack, boolean strikeThough, @Nullable String bonus) {
                List<Component> list = itemStack.getTooltipLines(context, Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
                List<Component> list1 = Lists.newArrayList();
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        MutableComponent t = list.getFirst().copy().append(" " + (bonus != null ? bonus : "") + itemStack.getCount());
                        if (strikeThough) t.withStyle(ChatFormatting.STRIKETHROUGH);
                        list1.add(t);
                    } else {
                        list1.add(strikeThough ? list.get(i).copy().withStyle(ChatFormatting.STRIKETHROUGH) : list.get(i));
                    }
                }
                return list1;
            }
        }

        private static class RewardWidget extends ItemWidget {
            protected static final Component REWARD = Component.translatable("gui.vampirism.taskmaster.reward").withStyle(ChatFormatting.UNDERLINE);

            private final ITaskInstance rewardInstance;
            private final Component reward;

            // for some reason the jvm throws an error when doing this in the constructor, so outsourcing this here
            public static RewardWidget create(int pX, int pY, @NotNull ITaskInstance rewardInstance) {
                ItemStack itemStack = rewardInstance.getReward() instanceof ItemReward.Instance(ItemStack reward1) ? reward1 : Items.PAPER.getDefaultInstance();
                return new RewardWidget(pX, pY, rewardInstance, itemStack);
            }

            private RewardWidget(int pX, int pY, @NotNull ITaskInstance rewardInstance, ItemStack reward) {
                super(pX, pY, reward);
                this.rewardInstance = rewardInstance;
                this.reward = Component.translatable(Util.makeDescriptionId("task", rewardInstance.getTask().location()) + ".reward");
                this.setTooltip(new MultilineTooltip(createTooltip(Item.TooltipContext.of(Minecraft.getInstance().level))));
            }

            @Override
            protected List<Component> createTooltip(Item.TooltipContext tooltipContext) {
                Item.TooltipContext context = Item.TooltipContext.of(Minecraft.getInstance().level);
                if (this.rewardInstance.getReward() instanceof ItemReward.Instance(ItemStack reward1)) {
                    return this.renderItemTooltip(reward1, REWARD, context, false, null);
                } else {
                    return this.renderItemTooltip(context);
                }
            }

            private List<Component> renderItemTooltip(Item.TooltipContext context) {
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
                this.setTooltip(new MultilineTooltip(createTooltip(Item.TooltipContext.of(Minecraft.getInstance().level))));
            }

            @Override
            protected List<Component> createTooltip(Item.TooltipContext tooltipContext) {
                boolean notAccepted = menu.isTaskNotAccepted(this.instance);
                boolean completed = menu.isRequirementCompleted(this.instance, this.requirement);
                int completedAmount = menu.getRequirementStatus(this.instance, this.requirement);
                return switch (this.requirement.getType()) {
                    case ITEMS -> this.renderItemTooltip(((ItemRequirement) requirement).getItemStack(), (completed ? REQUIREMENT_STRIKE : REQUIREMENT), tooltipContext, completed, notAccepted ? null : (completedAmount + "/"));
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
                super(xPos, yPos, 14, 13, ACCEPT, a -> menu.pressButton(getItem()), Component.literal(""));
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
            public void onClick(MouseButtonEvent event, boolean doubleClick) {
                if (event.x() > this.getX() && event.x() < this.getX() + this.width && event.y() > this.getY() && event.y() < this.getY() + this.height) {
                    super.onClick(event, doubleClick);
                }
            }

            @Override
            public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float p_renderButton_3_) {
                ITaskMenu.TaskAction action = menu.buttonAction(getItem());
                WidgetSprites sprites = switch (action) {
                    case ACCEPT -> ACCEPT;
                    case COMPLETE -> COMPLETE;
                    default -> ABORT;
                };

                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprites.get(this.active, this.isHovered), this.getX(), this.getY(), this.width, this.height);

                if (this.isHovered) {
                    this.setTooltip(Tooltip.create(Component.translatable(action.getTranslationKey())));
                }
            }
        }
    }
}