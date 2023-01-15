package de.teamlapen.vampirism.client.gui.screens.taskboard;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.components.ScrollWidget;
import de.teamlapen.lib.lib.util.MultilineTooltip;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.entity.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemRewardInstance;
import de.teamlapen.vampirism.inventory.TaskMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.gui.ScreenUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2dc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TaskDescriptionWidget extends AbstractContainerWidget implements ScrollWidget.ItemWidget<ITaskInstance> {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");

    protected static final ItemStack PAPER = new ItemStack(Items.PAPER);
    protected static final ItemStack SKULL_ITEM = new ItemStack(Blocks.SKELETON_SKULL);


    private final ITaskInstance taskInstance;
    private final TaskMenu taskMenu;
    private final IFactionPlayer<?> factionPlayer;
    private final Supplier<Vector2dc> scrollAmount;
    private final List<AbstractWidget> widgets = new ArrayList<>();

    public TaskDescriptionWidget(int pX, int pY, int pWidth, ITaskInstance taskInstance, Supplier<Vector2dc> scrollAmount, TaskMenu taskMenu, IFactionPlayer<?> factionPlayer) {
        super(pX, pY, pWidth, 21, taskInstance.getTask().getDescription());
        this.taskInstance = taskInstance;
        this.taskMenu = taskMenu;
        this.factionPlayer = factionPlayer;
        this.scrollAmount = scrollAmount;

        List<TaskRequirement.Requirement<?>> all = this.taskInstance.getTask().getRequirement().getAll();
        for (int i = 0; i < all.size(); i++) {
            this.widgets.add(new RequirementWidget(3 + 3 + i * 20, 3, taskInstance, all.get(i)));

        }

        this.widgets.add(new RewardWidget(3 + width - 40, 3, this.taskInstance));
        this.widgets.add(new TaskActionButton(this.width - 18, 4));
    }

    @Override
    public ITaskInstance getItem() {
        return this.taskInstance;
    }

    @Override
    protected @NotNull List<? extends AbstractWidget> getContainedChildren() {
        return this.widgets;
    }

    private void clickButton(Button id) {
        this.taskMenu.pressButton(this.taskInstance);
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        this.renderBg(poseStack, mc, mouseX, mouseY);

        super.renderButton(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, @NotNull Minecraft pMinecraft, int pMouseX, int pMouseY) {
        ScreenUtils.blitWithBorder(pPoseStack, TASKMASTER_GUI_TEXTURE, this.getX(), this.getY(), 17, 229, this.width, 21, 136, 21, 1, 2, 3, 2, this.getBlitOffset());
        ScreenUtils.blitWithBorder(pPoseStack, TASKMASTER_GUI_TEXTURE, this.getX() + this.width - 44, this.getY(), 17, 229, 44, 21, 136, 21, 1, 2, 3, 2, this.getBlitOffset());
    }

    private class TaskActionButton extends ImageButton {

        public TaskActionButton(int xPos, int yPos) {
            super(xPos, yPos, 14, 13, 0, 0, 0, TASKMASTER_GUI_TEXTURE, 0, 0, TaskDescriptionWidget.this::clickButton, Component.literal(""));
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (mouseX > this.getX() && mouseX < this.getX() + this.width && mouseY > this.getY() && mouseY < this.getY() + this.height) {
                super.onClick(mouseX, mouseY);
            }
        }

        @Override
        public void renderButton(@NotNull PoseStack mStack, int mouseX, int mouseY, float p_renderButton_3_) {
            TaskMenu.TaskAction action = TaskDescriptionWidget.this.taskMenu.buttonAction(TaskDescriptionWidget.this.taskInstance);
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

    private abstract class ItemWidget extends AbstractWidget {

        private final ItemStack stack;

        public ItemWidget(int pX, int pY, ItemStack stack) {
            super(pX, pY, 16, 16, Component.empty());
            this.stack = stack;
        }

        @Override
        public void renderButton(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(this.stack, (int) (this.getX() + TaskDescriptionWidget.this.scrollAmount.get().x()), (int) (this.getY() + TaskDescriptionWidget.this.scrollAmount.get().y()));

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

    private class RequirementWidget extends ItemWidget {
        protected static final Component REQUIREMENT = Component.translatable("gui.vampirism.taskmaster.requirement").withStyle(ChatFormatting.UNDERLINE);
        protected static final Component REQUIREMENT_STRIKE = REQUIREMENT.plainCopy().withStyle(ChatFormatting.STRIKETHROUGH);
        private final TaskRequirement.Requirement<?> requirement;
        private final ITaskInstance instance;

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
            boolean notAccepted = TaskDescriptionWidget.this.taskMenu.isTaskNotAccepted(this.instance);
            boolean completed = TaskDescriptionWidget.this.taskMenu.isRequirementCompleted(this.instance, this.requirement);
            int completedAmount = TaskDescriptionWidget.this.taskMenu.getRequirementStatus(this.instance, this.requirement);
            return switch (requirement.getType()) {
                case ITEMS -> this.renderItemTooltip(((ItemRequirement) requirement).getItemStack(), (completed ? REQUIREMENT_STRIKE : REQUIREMENT), completed, notAccepted ? null : (completedAmount + "/"));
                case ENTITY ->
                        this.renderGenericRequirementTooltip(TaskRequirement.Type.ENTITY, ((EntityType<?>) requirement.getStat(TaskDescriptionWidget.this.factionPlayer)).getDescription().plainCopy().append((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(TaskDescriptionWidget.this.factionPlayer)), completed);
                case ENTITY_TAG ->
                    //noinspection unchecked
                        this.renderGenericRequirementTooltip(TaskRequirement.Type.ENTITY_TAG, Component.translatable("tasks.vampirism." + ((TagKey<EntityType<?>>) requirement.getStat(TaskDescriptionWidget.this.factionPlayer)).location()).append((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(TaskDescriptionWidget.this.factionPlayer)), completed);
                case STATS ->
                        this.renderGenericRequirementTooltip(TaskRequirement.Type.STATS, Component.translatable("stat." + requirement.getStat(TaskDescriptionWidget.this.factionPlayer).toString().replace(':', '.')).append((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(TaskDescriptionWidget.this.factionPlayer)), completed);
                default -> this.renderDefaultRequirementToolTip(this.instance, requirement, completed);
            };
        }

        private List<Component> renderDefaultRequirementToolTip(@NotNull ITaskInstance task, TaskRequirement.@NotNull Requirement<?> requirement, boolean strikeThrough) {
            List<Component> tooltips = Lists.newArrayList();
            tooltips.add((strikeThrough ? REQUIREMENT_STRIKE : REQUIREMENT));
            MutableComponent text = Component.translatable(task.getTask().getTranslationKey() + ".req." + requirement.getId().toString().replace(':', '.'));
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

    private class RewardWidget extends ItemWidget {
        protected static final Component REWARD = Component.translatable("gui.vampirism.taskmaster.reward").withStyle(ChatFormatting.UNDERLINE);

        private final ITaskInstance rewardInstance;

        public RewardWidget(int pX, int pY, @NotNull ITaskInstance rewardInstance) {
            super(pX, pY, rewardInstance.getReward() instanceof ItemRewardInstance items ? items.getReward() : PAPER);
            this.rewardInstance = rewardInstance;
        }

        @Override
        protected List<Component> createTooltip() {
            if (this.rewardInstance.getReward() instanceof ItemRewardInstance item) {
                return this.renderItemTooltip(item.getReward(), REWARD, false, null);
            } else {
                return this.renderItemTooltip(this.rewardInstance.getTask());
            }
        }

        private List<Component> renderItemTooltip(@NotNull Task task) {
            List<Component> tooltips = Lists.newArrayList(REWARD);
            tooltips.add(Component.translatable(task.getTranslationKey() + ".reward"));
            return tooltips;
        }


    }
}
