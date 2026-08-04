package de.teamlapen.faction.client.gui.components;

import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.common.core.FactionStats;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.misc.extensions.client.IStatsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ActionStatisticsList extends ContainerObjectSelectionList<ActionStatisticsList.Entry> {
    protected final List<StatType<ISkill<?>>> skillColumns;
    protected final List<StatType<IAction<?>>> actionColumns;
    private final Identifier[] iconSprites = new Identifier[] {
            FIdentifier.mod("statistics/skills_unlocked"),
            FIdentifier.mod("statistics/skills_forgotten"),
            FIdentifier.mc("statistics/item_used"),
            FIdentifier.mod("statistics/time"),
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
    private final IStatsScreen screen;
    private final Font font;
    protected final Comparator<SkillRow> itemStatSorter = new ActionRowComparator();
    @Nullable
    protected StatType<?> sortColumn;
    protected int sortOrder;

    public ActionStatisticsList(Minecraft minecraft, StatsScreen screen) {
        super(minecraft, screen.width, screen.height, 33, 22);
        this.skillColumns = List.of(FactionStats.SKILL_UNLOCKED.get(), FactionStats.SKILL_FORGOTTEN.get());
        this.actionColumns = List.of(FactionStats.ACTION_USED.get(), FactionStats.ACTION_TIME.get(), FactionStats.ACTION_COOLDOWN_TIME.get());
        this.font = screen.getFont();
        this.screen = screen;
        Set<Holder<ISkill<?>>> skills = ModRegistries.SKILLS.listElements().filter(x -> skillColumns.stream().mapToInt(y -> this.screen.getStats().getValue(y.get(x.value()))).sum() > 0).collect(Collectors.toSet());
        if (!skills.isEmpty()) {
            this.addEntry(new HeaderEntry());
            skills.forEach(s -> addEntry(new SkillRow(s)));
        }
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {
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
    protected void extractListSeparators(GuiGraphicsExtractor graphics) {
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

    }

    public class HeaderEntry extends Entry {

        private static final Identifier SKILLS_UNLOCKED = FIdentifier.mod("statistics/skills_unlocked");
        private static final Identifier SKILLS_FORGOTTEN = FIdentifier.mod("statistics/skills_forgotten");
        private static final Identifier SKILLS_USED = FIdentifier.mc("statistics/item_used");
        private static final Identifier SKILLS_TIME = FIdentifier.mod("statistics/time");
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
        public void extractContent(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            this.skillsUnlocked.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(0) - 18, this.getContentY() + 1);
            this.skillsUnlocked.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
            this.skillsForgotten.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(1) - 18, this.getContentY() + 1);
            this.skillsForgotten.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
            this.actionUsed.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(2) - 18, this.getContentY() + 1);
            this.actionUsed.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
            this.actionTime.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(3) - 18, this.getContentY() + 1);
            this.actionTime.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
            this.actionCooldownTime.setPosition(this.getContentX() + ActionStatisticsList.this.getColumnX(4) - 18, this.getContentY() + 1);
            this.actionCooldownTime.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
            if (ActionStatisticsList.this.sortColumn != null) {
                int i = ActionStatisticsList.this.getColumnX(ActionStatisticsList.this.getColumnIndex(ActionStatisticsList.this.sortColumn)) - 36;
                Identifier resourcelocation = ActionStatisticsList.this.sortOrder == 1 ? StatsScreen.SORT_UP_SPRITE : StatsScreen.SORT_DOWN_SPRITE;
                GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, resourcelocation, this.getContentX() + i, this.getContentY() + 1, 18, 18);
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
            private final Identifier sprite;

            StatSortButton(int column, Identifier sprite) {
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
            public void extractContents(GuiGraphicsExtractor graphics, int p_281473_, int p_283021_, float partialTicks) {
                Identifier identifier = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.width, this.height);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX(), this.getY(), this.width, this.height);
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
        public void extractContent(@NotNull GuiGraphicsExtractor pGuiGraphicsExtractor, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            this.widget.setPosition(this.getContentX(), this.getContentY());
            this.widget.extractRenderState(pGuiGraphicsExtractor, mouseX, mouseY, partialTick);

            ActionStatisticsList actionStatisticsList = ActionStatisticsList.this;
            int i = actionStatisticsList.children().indexOf(this);

            for (int i1 = 0; i1 < actionStatisticsList.skillColumns.size(); i1++) {
                actionStatisticsList.skillColumns.get(i1).get(this.skill);
                this.renderStat(pGuiGraphicsExtractor,
                        actionStatisticsList.skillColumns.get(i1).get(this.skill),
                        this.getContentX() + actionStatisticsList.getColumnX(i1),
                        this.getContentYMiddle() - 9 / 2,
                        i % 2 == 0);
            }

            for (int i1 = 0; i1 < actionStatisticsList.actionColumns.size(); i1++) {
                Stat<IAction<?>> stat;
                var action = this.skill.getAction();
                if (action != null) {
                    stat = actionStatisticsList.actionColumns.get(i1).get(action.value());
                } else {
                    stat = null;
                }
                this.renderStat(pGuiGraphicsExtractor,
                        stat,
                        this.getContentX() + actionStatisticsList.getColumnX(i1 + actionStatisticsList.skillColumns.size()),
                        this.getContentYMiddle() - 9 / 2,
                        i % 2 == 0);
            }
        }

        protected void renderStat(GuiGraphicsExtractor pGuiGraphicsExtractor, @Nullable Stat<?> pStat, int x, int y, boolean pEvenRow) {
            Component component = pStat == null ? StatsScreen.NO_VALUE_DISPLAY : Component.literal(pStat.format(screen.getStats().getValue(pStat)));
            pGuiGraphicsExtractor.text(font, component, x - font.width(component), y, pEvenRow ? -1 : -4539718);
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
                super(Minecraft.getInstance(), 1, 1, 18, 18, skill.value().getName(), skill, true);
            }

            @Override
            protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
                GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, StatsScreen.SLOT_SPRITE, SkillRow.this.getContentX(), SkillRow.this.getContentY(), 18, 18);
                super.extractWidgetRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
            }

            @Override
            protected void renderTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
                super.renderTooltip(GuiGraphicsExtractor, SkillRow.this.getContentX() + 18, SkillRow.this.getContentY() + 18);
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
                StatType<IAction<?>> stattype1 = SafeCast.cast(ActionStatisticsList.this.sortColumn);
                var actions = item.getAction();
                var actions1 = item1.getAction();
                i = actions != null ? screen.getStats().getValue(stattype1, actions.value()) : -1;
                j = actions1 != null ? screen.getStats().getValue(stattype1, actions1.value()) : -1;
            } else {
                StatType<ISkill<?>> stattype1 = SafeCast.cast(ActionStatisticsList.this.sortColumn);
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
