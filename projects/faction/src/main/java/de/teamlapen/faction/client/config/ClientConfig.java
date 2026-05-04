package de.teamlapen.faction.client.config;

import de.teamlapen.faction.client.config.values.ActionOrderValue;
import de.teamlapen.faction.client.config.values.MinionTaskOrderValue;
import de.teamlapen.faction.common.config.FactionConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig implements FactionConfig.IConfigs {

    public final ModConfigSpec.BooleanValue renderTotemFactionName;

    // GUI
    public final ModConfigSpec.BooleanValue addFactionMenuButtonToInventory;
    public final ModConfigSpec.IntValue factionMenuButtonXPos;
    public final ModConfigSpec.IntValue factionMenuButtonYPos;

    // Overlays
    public final ModConfigSpec.BooleanValue showFactionLevelOverlay;
    public final ModConfigSpec.IntValue factionLevelOverlayXPos;
    public final ModConfigSpec.IntValue factionLevelOverlayYPos;
    public final ModConfigSpec.BooleanValue showFactionRaidBarOverlay;
    public final ModConfigSpec.BooleanValue showActionCooldownOverlay;
    public final ModConfigSpec.BooleanValue showActionDurationOverlay;

    // Internal
    public final ActionOrderValue actionOrder;
    public final MinionTaskOrderValue minionTaskOrder;

    public ClientConfig(ModConfigSpec.Builder builder) {
        this.renderTotemFactionName = builder
                .comment("When enabled, renders the owning faction's name above totem blocks.")
                .define("renderTotemFactionName", true);

        builder.push("gui");
        this.addFactionMenuButtonToInventory = builder
                .comment("When enabled, adds a shortcut button to the faction menu in the player inventory screen.")
                .define("addFactionMenuButtonToInventory", true);
        this.factionMenuButtonXPos = builder
                .comment("Horizontal offset of the faction menu button from the center of the screen, in pixels.")
                .defineInRange("factionMenuButtonXPos", 125, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.factionMenuButtonYPos = builder
                .comment("Vertical offset of the faction menu button from the center of the screen, in pixels.")
                .defineInRange("factionMenuButtonYPos", -22, Integer.MIN_VALUE, Integer.MAX_VALUE);
        builder.pop();

        builder.push("overlays");
        this.showFactionLevelOverlay = builder
                .comment("When enabled, renders the faction level indicator in the HUD.")
                .define("showFactionLevelOverlay", true);
        this.factionLevelOverlayXPos = builder
                .comment("Horizontal offset of the faction level indicator from the center of the screen, in pixels.")
                .defineInRange("factionLevelOverlayXPos", 0, -250, 250);
        this.factionLevelOverlayYPos = builder
                .comment("Vertical offset of the faction level indicator from the bottom of the screen, in pixels.")
                .defineInRange("factionLevelOverlayYPos", 47, 0, 270);
        this.showFactionRaidBarOverlay = builder
                .comment("When enabled, renders the faction raid bar overlay in the HUD.")
                .define("showFactionRaidBarOverlay", true);
        this.showActionCooldownOverlay = builder
                .comment("When enabled, renders the action cooldown indicator in the HUD.")
                .define("showActionCooldownOverlay", true);
        this.showActionDurationOverlay = builder
                .comment("When enabled, renders the action duration indicator in the HUD.")
                .define("showActionDurationOverlay", true);
        builder.pop();

        builder.push("internal");
        this.actionOrder = new ActionOrderValue(
                builder.comment("Defines the display order of faction actions in the HUD and menus."),
                "actionOrder");
        this.minionTaskOrder = new MinionTaskOrderValue(
                builder.comment("Defines the display order of minion tasks in the minion management screen."),
                "minionTaskOrder");
        builder.pop();
    }

    @Override
    public void refresh() {
        this.actionOrder.refresh();
        this.minionTaskOrder.refresh();
    }
}
