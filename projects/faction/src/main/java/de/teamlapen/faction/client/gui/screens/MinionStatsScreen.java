package de.teamlapen.faction.client.gui.screens;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.client.gui.screens.taskboard.SeparatorWidget;
import de.teamlapen.faction.common.core.FactionItems;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.faction.common.network.packets.server.ServerboundUpgradeMinionStatPacket;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


public abstract class MinionStatsScreen<T extends MinionData, Q extends MinionEntity<T>> extends Screen {

    private static final Identifier BACKGROUND = FIdentifier.mod("background/default");
    protected static final WidgetSprites RESET = new WidgetSprites(FIdentifier.mod("widget/reset"), FIdentifier.mod("widget/reset_disabled"), FIdentifier.mod("widget/reset_highlighted"));
    protected static final WidgetSprites ADD = new WidgetSprites(FIdentifier.mod("widget/add"), FIdentifier.mod("widget/add_disabled"), FIdentifier.mod("widget/add_highlighted"));

    protected final Q entity;
    @Nullable
    protected final ILastScreenProvider backScreen;
    private Button reset;
    protected T minionData;

    private final GridLayout layout = new GridLayout();
    @UnknownNullability
    private ImageWidget background;
    @UnknownNullability
    private StringWidget levelWidget;
    @UnknownNullability
    private StringWidget skillPointWidget;
    private final List<StatRow> statRows = new ArrayList<>();

    protected MinionStatsScreen(Q entity, @Nullable ILastScreenProvider backScreen) {
        super(Component.translatable("gui.factionapi.minion.stats"));
        this.entity = entity;
        this.minionData = entity.getMinionData().orElseThrow();
        this.backScreen = backScreen;
        this.initStatRows();
    }


    protected abstract void initStatRows();

    protected abstract int getRemainingStatPoints();

    protected abstract int getLevel();

    protected abstract int getMaxLevel();

    protected void updateStats() {
        this.levelWidget.setMessage(Component.literal((getLevel() + 1) + "/" + (getMaxLevel() + 1)));
        this.skillPointWidget.setMessage(Component.literal("(" + getRemainingStatPoints() + ")"));
        this.statRows.forEach(StatRow::changed);
    }

    private void updateStat(StatRow row) {
        FactionsMod.proxy.sendToServer(new ServerboundUpgradeMinionStatPacket(entity.getId(), statRows.indexOf(row)));
        updateStats();
    }

    protected void addStatRow(StatRow statRow) {
        this.statRows.add(statRow);
    }

    @Override
    protected void rebuildWidgets() {
        this.layout.arrangeElements();
        fitLayout();
    }

    @Override
    public void tick() {
        updateStats();

        this.reset.active = entity.getMinionData().map(MinionData::hasUsedSkillPoints).orElse(false) && getOblivionPotion().isPresent();
    }

    @Override
    protected void init() {
        this.background = this.layout.addChild(ImageWidget.sprite(0, 0, BACKGROUND), 0, 0);
        GridLayout layout = this.layout.addChild(new GridLayout(), 0, 0, this.layout.newCellSettings().padding(8))
                .spacing(10);

        layout.addChild(new StringWidget(this.title, this.font),0,0);
        this.reset = layout.addChild(new ImageButton(18,18, RESET, s -> {
            FactionsMod.proxy.sendToServer(new ServerboundUpgradeMinionStatPacket(entity.getId(), -1));
            getOblivionPotion().ifPresent(stack -> stack.shrink(1));//server syncs after the screen is closed
        }, Component.translatable("gui.factionapi.minion_screen.reset_stats", Component.translatable(FactionItems.OBLIVION_POTION.get().getDescriptionId()))) {
            @Override
            public boolean shouldTakeFocusAfterInteraction() {
                return false;
            }
        }, 0,1, layout.newCellSettings().alignHorizontallyRight());


        var statsLayout = layout.addChild(new GridLayout(), 1,0,1,2, layout.newCellSettings().alignHorizontallyCenter())
                .rowSpacing(2)
                .columnSpacing(4);
        statsLayout.defaultCellSetting()
                .alignHorizontallyCenter();

        statsLayout.addChild(new StringWidget(Component.translatable("gui.factionapi.minion_stats.level"), this.font), 0, 0);
        this.levelWidget = statsLayout.addChild(new StringWidget(Component.empty(), this.font), 0, 1);
        this.skillPointWidget = statsLayout.addChild(new StringWidget(Component.empty(), this.font), 0, 3);

        statsLayout.addChild(new SeparatorWidget(200),1, 0, 1, 4, layout.newCellSettings().alignHorizontallyCenter());

        for (int i = 0; i < this.statRows.size(); i++) {
            createStatRow(statsLayout, i + 2,this.statRows.get(i));
        }

        GridLayout buttonsLayout = layout.addChild(new GridLayout(),2,0,1,2, layout.newCellSettings().alignHorizontallyCenter());
        buttonsLayout.columnSpacing(20);
        buttonsLayout.addChild(new ExtendedButton(0,0, 80, 20,  Component.translatable("gui.back"), x -> {
            if (this.minecraft != null && this.backScreen != null) {
                this.backScreen.returnToLastScreen();
            }
        }), 0, 0, buttonsLayout.newCellSettings().alignHorizontallyCenter());
        buttonsLayout.addChild(new ExtendedButton(0,0, 80, 20,  Component.translatable("gui.done"), x -> this.onClose()),0,1, buttonsLayout.newCellSettings().alignHorizontallyCenter());

        updateStats();
        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addRenderableWidget);
        fitLayout();
    }

    private void fitLayout() {
        int layoutWidth = this.layout.getWidth();
        int layoutHeight = this.layout.getHeight();
        this.background.setWidth(layoutWidth);
        this.background.setHeight(layoutHeight);
//        this.separatorWidget.setWidth(layoutWidth - 20);
        FrameLayout.centerInRectangle(this.layout, (this.width - layoutWidth) / 2, (this.height - layoutHeight) / 2, layoutWidth, layoutHeight);
    }

    private @NotNull Optional<ItemStack> getOblivionPotion() {
        return Optional.ofNullable(entity.getMinionData().flatMap(data -> Optional.ofNullable(InventoryHelper.getFirst(data.getInventory(), FactionItems.OBLIVION_POTION.get()))).orElse(InventoryHelper.getFirst(this.minecraft.player.getInventory(), FactionItems.OBLIVION_POTION.get())));
    }

    private void createStatRow(GridLayout layout, int row, StatRow statRow) {
        layout.addChild(new StringWidget(statRow.name, font), row, 0);
        var value = layout.addChild(new StringWidget(Component.empty(), font), row, 1);
        var level = layout.addChild(new StringWidget(Component.empty(), font), row, 2);
        var button = layout.addChild(new ImageButton(18,18, ADD, s -> updateStat(statRow), Component.empty()) {
            @Override
            public boolean shouldTakeFocusAfterInteraction() {
                return false;
            }
        }, row,3);

        statRow.subscribe(() -> {
            value.setMessage(Component.literal(statRow.value));
            level.setMessage(Component.translatable("gui.factionapi.minion_stats.stat_level", statRow.currentLevel + 1, statRow.totalLevels + 1));
            button.active = statRow.canUpgrade() && this.getRemainingStatPoints() > 0;
        });
    }

    protected static class StatRow {

        private final Component name;
        private final int totalLevels;
        private final Supplier<String> valueSupplier;
        private final Supplier<Integer> currentLevelSupplier;
        private String value = "";
        private int currentLevel;
        @Nullable
        private Runnable onChange;

        public StatRow(Component name, int totalLevels, Supplier<String> valueSupplier, Supplier<Integer> currentLevelSupplier) {
            this.totalLevels = totalLevels;
            this.name = name;
            this.valueSupplier = valueSupplier;
            this.currentLevelSupplier = currentLevelSupplier;
        }

        public boolean canUpgrade() {
            return currentLevel < totalLevels;
        }

        public void subscribe(Runnable onChange) {
            this.onChange = onChange;
        }

        public void changed() {
            value = this.valueSupplier.get();
            currentLevel = this.currentLevelSupplier.get();
            if (this.onChange != null) {
                this.onChange.run();
            }
        }
    }

}