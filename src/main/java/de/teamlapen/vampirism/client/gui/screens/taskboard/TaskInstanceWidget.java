package de.teamlapen.vampirism.client.gui.screens.taskboard;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.components.ScrollWidget;
import de.teamlapen.lib.lib.util.MultilineTooltip;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.inventory.TaskMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2dc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TaskInstanceWidget extends AbstractWidget implements ScrollWidget.ItemWidget<ITaskInstance> {
    protected static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");

    protected final ITaskInstance taskInstance;
    protected final Supplier<Vector2dc> scrollAmount;
    protected final Consumer<TaskInstanceWidget> onClick;
    protected final Screen screen;
    protected final TaskMenu menu;
    protected final IFactionPlayer<?> factionPlayer;

    public TaskInstanceWidget(int pX, int pY, int pWidth, ITaskInstance taskInstance, Supplier<Vector2dc> scrollAmount, Consumer<TaskInstanceWidget> onClick, Screen screen, TaskMenu menu, IFactionPlayer<?> factionPlayer) {
        super(pX, pY, pWidth, 21, taskInstance.getTask().getDescription());
        this.taskInstance = taskInstance;
        this.scrollAmount = scrollAmount;
        this.onClick = onClick;
        this.screen = screen;
        this.menu = menu;
        this.factionPlayer = factionPlayer;
    }

    @Override
    public ITaskInstance getItem() {
        return this.taskInstance;
    }

    @Override
    public void renderButton(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft mc = Minecraft.getInstance();
        this.renderBg(pPoseStack, mc, pMouseX, pMouseY);

        //render name
        Optional<FormattedCharSequence> text = Optional.ofNullable(this.screen.font.split(this.taskInstance.getTask().getTranslation(), 131).get(0));
        text.ifPresent(t -> mc.font.draw(pPoseStack, t, getX() + 2, getY() + 4, 3419941));//(6839882 & 16711422) >> 1 //8453920 //4226832

        //render progress
        if (!this.menu.isTaskNotAccepted(this.taskInstance) && !this.taskInstance.isUnique()) {
            long remainingTime = this.taskInstance.getTaskTimeStamp() - Minecraft.getInstance().level.getGameTime();
            Component msg;
            if (remainingTime >= 0) {
                remainingTime = remainingTime / 20;
                long hours = remainingTime / 60 / 60;
                long minutes = remainingTime / 60 % (60);
                long seconds = remainingTime % (60);
                String time = "" + hours + ":";
                if (minutes < 10) time += "0";
                time += minutes + ":";
                if (seconds < 10) time += "0";
                time += seconds;
                msg = Component.literal(time);
            } else {
                msg = Component.translatable("text.vampirism.task_failed");
            }
            int width = this.screen.font.width(msg);
            int color = 11184810;
            if (remainingTime < this.taskInstance.getTaskDuration() / 20F * 0.1F) {
                color = 16733525;
            }
            this.screen.font.drawShadow(pPoseStack, msg, getX() + 134 - width, getY() + 12, color);
        }

        if (isHovered) {
            this.setTooltip(generateTaskToolTip());
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        this.onClick.accept(this);
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, @NotNull Minecraft pMinecraft, int pMouseX, int pMouseY) {

        if (this.menu.isCompleted(this.taskInstance)) {
            RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 1);
        } else {
            boolean isUnique = this.taskInstance.isUnique();
            boolean remainsTime = this.taskInstance.getTaskTimeStamp() - pMinecraft.level.getGameTime() > 0;
            if (this.menu.canCompleteTask(this.taskInstance)) {
                if (isUnique) {
                    RenderSystem.setShaderColor(1f, 0.855859375f, 0, 1);
                } else {
                    RenderSystem.setShaderColor(0, 0.9f, 0, 1);
                }
            } else if (this.menu.isTaskNotAccepted(this.taskInstance)) {
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
        GuiComponent.blit(pPoseStack, this.getX(), this.getY(), this.screen.getBlitOffset(), 17, 187, Math.min(this.width - 1, 135), 21, 256, 256);
        GuiComponent.blit(pPoseStack, this.getX() + this.width - Math.min(this.width - 1, 135), this.getY(), this.screen.getBlitOffset(), 17 + (135 - Math.min(this.width - 1, 134)), 187, Math.min(this.width - 1, 135), 21, 256, 256);
        RenderSystem.setShaderColor(1, 1, 1, this.alpha);
    }

    private Tooltip generateTaskToolTip() {
        Task task = this.taskInstance.getTask();
        List<Component> toolTips = new ArrayList<>();
        toolTips.add(task.getTranslation().plainCopy().withStyle(style -> style.withColor(this.menu.getFactionColor())));
        if (task.useDescription()) {
            toolTips.add(task.getDescription());
            toolTips.add(Component.literal(" "));
        }
        if (this.menu.isTaskNotAccepted(this.taskInstance)) {
            toolTips.add(Component.translatable("gui.vampirism.taskmaster.not_accepted"));
        } else {
            for (List<TaskRequirement.Requirement<?>> requirements : task.getRequirement().requirements().values()) {
                if (requirements == null) continue;
                TaskRequirement.Type type = requirements.get(0).getType();
                boolean completed = this.menu.areRequirementsCompleted(this.taskInstance, type);
                MutableComponent title = Component.translatable(type.getTranslationKey()).append(":");

                if (completed) {
                    title.withStyle(ChatFormatting.STRIKETHROUGH);
                }
                toolTips.add(title);
                for (TaskRequirement.Requirement<?> requirement : requirements) {
                    MutableComponent desc;
                    int completedAmount = this.menu.getRequirementStatus(this.taskInstance, requirement);
                    desc = switch (type) {
                        case ITEMS -> ((Item) requirement.getStat(this.factionPlayer)).getDescription().plainCopy().append(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer));
                        case STATS -> Component.translatable("stat." + requirement.getStat(this.factionPlayer).toString().replace(':', '.')).append(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer));
                        case ENTITY -> (((EntityType<?>) requirement.getStat(this.factionPlayer)).getDescription().plainCopy().append(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer)));
                        case ENTITY_TAG ->
                            //noinspection unchecked
                                Component.translatable("tasks.vampirism." + ((TagKey<EntityType<?>>) requirement.getStat(this.factionPlayer)).location()).append(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer));
                        default -> Component.translatable(task.getTranslationKey() + ".req." + requirement.getId().toString().replace(':', '.'));
                    };
                    if (completed || this.menu.isRequirementCompleted(this.taskInstance, requirement)) {
                        desc.withStyle(ChatFormatting.STRIKETHROUGH);
                    }
                    toolTips.add(Component.literal("  ").append(desc));
                }
            }
        }
        return new MultilineTooltip(toolTips);
    }
}
