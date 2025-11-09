package de.teamlapen.vampirism.client.gui.components;

import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.IActionSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.misc.mixin.client.accessor.StatsScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ActionStatisticsList extends ContainerObjectSelectionList<ActionStatisticsList.Entry> {
    protected final List<StatType<ISkill<?>>> skillColumns;
    protected final List<StatType<IAction<?>>> actionColumns;
    private final ResourceLocation[] iconSprites = new ResourceLocation[] {
            VResourceLocation.mod("statistics/skills_unlocked"),
            VResourceLocation.mod("statistics/skills_forgotten"),
            VResourceLocation.mc("statistics/item_used"),
            VResourceLocation.mod("statistics/time"),
            null
    };
    private final ItemStack[] itemSprites = new ItemStack[] {
            null,
            null,
            null,
            null,
            Items.RED_BED.getDefaultInstance()
    };
    protected int headerPressed = -1;
    private final StatsScreenAccessor screen;
    private final Font font;
    protected final Comparator<SkillRow> itemStatSorter = new ActionRowComparator();
    @Nullable
    protected StatType<?> sortColumn;
    protected int sortOrder;

    public ActionStatisticsList(Minecraft minecraft, StatsScreen screen) {
        super(minecraft, screen.width, screen.height, 33, 9 * 4);
        this.skillColumns = List.of(ModStats.SKILL_UNLOCKED.get(), ModStats.SKILL_FORGOTTEN.get());
        this.actionColumns = List.of(ModStats.ACTION_USED.get(), ModStats.ACTION_TIME.get(), ModStats.ACTION_COOLDOWN_TIME.get());
        this.font = screen.font;
        this.screen = (StatsScreenAccessor) screen;
        Set<Holder<ISkill<?>>> skills = new HashSet<>();
        skills.addAll(ModRegistries.SKILLS.listElements().filter(x -> skillColumns.stream().mapToInt(y -> this.screen.getStats().getValue(y.get(x.value()))).sum() > 0).collect(Collectors.toSet()));
        //skills.addAll(ModRegistries.ACTIONS.listElements().filter(x -> actionColumns.stream().mapToInt(y -> this.screen.getStats().getValue(y.get(x.value()))).sum() > 0).map(IAction::asSkill).toList());
        if (!skills.isEmpty()) {
            this.addEntry(new HeaderEntry());
            skills.forEach(s -> addEntry(new SkillRow(s)));
        }
    }

    @Override
    protected void renderListBackground(@NotNull GuiGraphics guiGraphics) {
    }

    int getColumnX(int index) {
        return 75 + 40 * index;
    }

    @Override
    public int getRowWidth() {
        return 280;
    }

    private StatType<?> getColumn(int pIndex) {
        return pIndex < this.skillColumns.size() ? this.skillColumns.get(pIndex) : this.actionColumns.get(pIndex - this.skillColumns.size());
    }

    private int getColumnIndex(StatType<?> pStatType) {
        int i = this.skillColumns.indexOf(pStatType);
        if (i >= 0) {
            return i;
        } else {
            int j = this.actionColumns.indexOf(pStatType);
            return j >= 0 ? j + this.skillColumns.size() : -1;
        }
    }

    protected void sortByColumn(StatType<?> pStatType) {
        if (pStatType != this.sortColumn) {
            this.sortColumn = pStatType;
            this.sortOrder = -1;
        } else if (this.sortOrder == -1) {
            this.sortOrder = 1;
        } else {
            this.sortColumn = null;
            this.sortOrder = 0;
        }

        this.sortItems(this.itemStatSorter);
    }

    protected void sortItems(Comparator<ActionStatisticsList.SkillRow> comparator) {
        List<ActionStatisticsList.SkillRow> list = this.getItemRows();
        list.sort(comparator);
        this.clearEntriesExcept(this.children().getFirst());

        for (ActionStatisticsList.SkillRow skillRow : list) {
            this.addEntry(skillRow);
        }
    }

    private List<ActionStatisticsList.SkillRow> getItemRows() {
        List<ActionStatisticsList.SkillRow> list = new ArrayList<>();
        this.children().forEach(entry -> {
            if (entry instanceof ActionStatisticsList.SkillRow skillRow) {
                list.add(skillRow);
            }
        });
        return list;
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

    }

    public class HeaderEntry extends Entry {

        private static final ResourceLocation SKILLS_UNLOCKED = VResourceLocation.mod("statistics/skills_unlocked");
        private static final ResourceLocation SKILLS_FORGOTTEN = VResourceLocation.mod("statistics/skills_forgotten");
        private static final ResourceLocation SKILLS_USED = VResourceLocation.mc("statistics/item_used");
        private static final ResourceLocation SKILLS_TIME = VResourceLocation.mod("statistics/time");
        private final StatSortButton skillsUnlocked;
        private final StatSortButton skillsForgotten;
        private final StatSortButton actionUsed;
        private final StatSortButton actionTime;
        private final StatSortButton actionCooldownTime;
        private final List<AbstractWidget> children = new ArrayList<>();

        public HeaderEntry() {
            this.skillsUnlocked = new StatSortButton(0, SKILLS_UNLOCKED);
            this.skillsForgotten = new StatSortButton(1, SKILLS_FORGOTTEN);
            this.actionUsed = new StatSortButton(2, SKILLS_USED);
            this.actionTime = new StatSortButton(3, SKILLS_TIME);
            this.actionCooldownTime = new StatSortButton(4, SKILLS_TIME);
            this.children.addAll(List.of(this.skillsUnlocked, this.skillsForgotten, this.actionUsed, this.actionTime, this.actionCooldownTime));
        }

        @Override
        public void renderContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            this.skillsUnlocked.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(0) - 18, this.getContentY() + 1);
            this.skillsUnlocked.render(guiGraphics, mouseX, mouseY, partialTick);
            this.skillsForgotten.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(1) - 18, this.getContentY() + 1);
            this.skillsForgotten.render(guiGraphics, mouseX, mouseY, partialTick);
            this.actionUsed.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(2) - 18, this.getContentY() + 1);
            this.actionUsed.render(guiGraphics, mouseX, mouseY, partialTick);
            this.actionTime.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(3) - 18, this.getContentY() + 1);
            this.actionTime.render(guiGraphics, mouseX, mouseY, partialTick);
            this.actionCooldownTime.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(4) - 18, this.getContentY() + 1);
            this.actionCooldownTime.render(guiGraphics, mouseX, mouseY, partialTick);
            if (ActionStatisticsList.this.sortColumn != null) {
                int i = ActionStatisticsList.this.getColumnX(ActionStatisticsList.this.getColumnIndex(ActionStatisticsList.this.sortColumn)) - 36;
                ResourceLocation resourcelocation = ActionStatisticsList.this.sortOrder == 1 ? StatsScreen.SORT_UP_SPRITE : StatsScreen.SORT_DOWN_SPRITE;
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, resourcelocation, this.getContentX() + i, this.getContentY() + 1, 18, 18);
            }
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return this.children;
        }

        public class StatSortButton extends ImageButton {
            private final ResourceLocation sprite;

            StatSortButton(int column, ResourceLocation sprite) {
                super(
                        18,
                        18,
                        new WidgetSprites(StatsScreen.HEADER_SPRITE, StatsScreen.SLOT_SPRITE),
                        p_440669_ -> ActionStatisticsList.this.sortByColumn(ActionStatisticsList.this.getColumn(column)),
                        ActionStatisticsList.this.getColumn(column).getDisplayName()
                );
                this.sprite = sprite;
                this.setTooltip(Tooltip.create(this.getMessage()));
            }

            @Override
            public void renderWidget(GuiGraphics p_440197_, int p_439800_, int p_440460_, float p_439309_) {
                ResourceLocation resourcelocation = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
                p_440197_.blitSprite(RenderPipelines.GUI_TEXTURED, resourcelocation, this.getX(), this.getY(), this.width, this.height);
                p_440197_.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX(), this.getY(), this.width, this.height);
            }
        }
    }

    public class SkillRow extends Entry {

        private final ISkill<?> skill;
        private final SkillRowWidget widget;

        public SkillRow(Holder<ISkill<?>> action) {
            this.skill = action.value();
            this.widget = new SkillRowWidget(action);
        }

        public ISkill<?> getSkill() {
            return skill;
        }

        @Override
        public void renderContent(GuiGraphics pGuiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            this.widget.setPosition(this.getContentX(), this.getContentY());
            this.widget.render(pGuiGraphics, mouseX, mouseY, partialTick);

            ActionStatisticsList actionStatisticsList = ActionStatisticsList.this;
            int i = actionStatisticsList.children().indexOf(this);

            for (int i1 = 0; i1 < actionStatisticsList.skillColumns.size(); i1++) {
                actionStatisticsList.skillColumns.get(i).get(this.skill);
                this.renderStat(pGuiGraphics,
                        actionStatisticsList.skillColumns.get(i1).get(this.skill),
                        this.getContentX() + actionStatisticsList.getColumnX(i1),
                        this.getContentYMiddle() - 9 / 2,
                        i % 2 == 0);
            }

            for (int i1 = 0; i1 < actionStatisticsList.actionColumns.size(); i1++) {
                Stat<IAction<?>> stat;
                if (this.skill instanceof IActionSkill<?> actionSkill) {
                    stat = actionStatisticsList.actionColumns.get(i1).get(actionSkill.action());
                } else {
                    stat = null;
                }
                this.renderStat(pGuiGraphics,
                        stat,
                        this.getContentX() + actionStatisticsList.getColumnX(i1 + actionStatisticsList.skillColumns.size()),
                        this.getContentYMiddle() - 9 / 2,
                        i % 2 == 0);
            }
        }

        protected void renderStat(GuiGraphics pGuiGraphics, @Nullable Stat<?> pStat, int x, int y, boolean pEvenRow) {
            Component component = pStat == null ? StatsScreen.NO_VALUE_DISPLAY : Component.literal(pStat.format(screen.getStats().getValue(pStat)));
            pGuiGraphics.drawString(font, component, x - font.width(component), y, pEvenRow ? -1 : -4539718);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(this.widget);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(this.widget);
        }

        class SkillRowWidget extends SkillDisplayWidget {

            public SkillRowWidget(Holder<ISkill<?>> skill) {
                super(Minecraft.getInstance(), 1, 1, 18, 18, skill.value().getName(), skill, false);
            }

            @Override
            protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, StatsScreen.SLOT_SPRITE, SkillRow.this.getContentX(), SkillRow.this.getContentY(), 18, 18);
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
                super.renderTooltip(guiGraphics, SkillRow.this.getContentX() + mouseX, SkillRow.this.getContentY() + mouseY);
            }
        }
    }

    public class ActionRowComparator implements Comparator<SkillRow> {
        public int compare(SkillRow pRow1, SkillRow pRow2) {
            ISkill<?> item = pRow1.getSkill();
            ISkill<?> item1 = pRow2.getSkill();
            int i;
            int j;
            if (ActionStatisticsList.this.sortColumn == null) {
                i = 0;
                j = 0;
            } else if (actionColumns.contains(sortColumn)) {
                StatType<IAction<?>> stattype1 = (StatType<IAction<?>>) ActionStatisticsList.this.sortColumn;
                i = item instanceof IActionSkill<?> actionSkill ? screen.getStats().getValue(stattype1, actionSkill.action()) : -1;
                j = item1 instanceof IActionSkill<?> actionSkill ? screen.getStats().getValue(stattype1, actionSkill.action()) : -1;
            } else {
                StatType<ISkill<?>> stattype1 = (StatType<ISkill<?>>) ActionStatisticsList.this.sortColumn;
                i = screen.getStats().getValue(stattype1, item);
                j = screen.getStats().getValue(stattype1, item1);
            }

            return i == j ? ActionStatisticsList.this.sortOrder * Integer.compare(getId(item), getId(item1)) : ActionStatisticsList.this.sortOrder * Integer.compare(i, j);
        }

        private int getId(ISkill<?> action) {
            return ModRegistries.SKILLS.getId(action);
        }
    }
}
