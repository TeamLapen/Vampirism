package de.teamlapen.faction.client.config;

import de.teamlapen.faction.client.config.values.ActionOrderValue;
import de.teamlapen.faction.client.config.values.MinionTaskOrderValue;
import de.teamlapen.faction.common.config.FactionConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig implements FactionConfig.IConfigs {


    //<editor-fold desc="World">

    public final ModConfigSpec.BooleanValue renderTotemFactionName;

    //</editor-fold>

    //<editor-fold desc="GUI">

    public final ModConfigSpec.BooleanValue addFactionMenuButtonToInventory;
    public final ModConfigSpec.IntValue factionMenuButtonXPos;
    public final ModConfigSpec.IntValue factionMenuButtonYPos;


    //<editor-fold desc="Overlays">

    public final ModConfigSpec.BooleanValue renderFactionLevelOverlay;
    public final ModConfigSpec.IntValue factionLevelOverlayXPos;
    public final ModConfigSpec.IntValue factionLevelOverlayYPos;

    public final ModConfigSpec.BooleanValue renderFactionRaidbarOverlay;
    public final ModConfigSpec.BooleanValue renderActionCooldownOverlay;
    public final ModConfigSpec.BooleanValue renderActionDurationOverlay;

    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Internals">

    public final ActionOrderValue actionOrder;
    public final MinionTaskOrderValue minionTaskOrder;

    //</editor-fold>



    public ClientConfig(ModConfigSpec.Builder builder) {

        builder.push("world");
        this.renderTotemFactionName = builder.comment("Render the faction name of totems").define("renderTotemFactionName", true);
        builder.pop();

        builder.push("gui");
        this.addFactionMenuButtonToInventory = builder.comment("Adds the faction menu button to the inventory").define("addFactionMenuButtonToInventory", true);
        this.factionMenuButtonXPos = builder.comment("Sets the faction menu button x coordinate from the center of the screen").defineInRange("factionMenuButtonXPos", 125, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.factionMenuButtonYPos = builder.comment("Sets the faction menu button y coordinate from the center of the screen").defineInRange("factionMenuButtonYPos", -22, Integer.MIN_VALUE, Integer.MAX_VALUE);

        builder.push("overlays");
        this.renderFactionLevelOverlay = builder.comment("Render the faction level overlay in the HUD").define("renderFactionLevelOverlay", true);
        this.factionLevelOverlayXPos = builder.comment("X-Offset of the level indicator from the center in pixels").defineInRange("levelOffsetX", 0, -250, 250);
        this.factionLevelOverlayYPos = builder.comment("Y-Offset of the level indicator from the bottom in pixels").defineInRange("levelOffsetY", 47, 0, 270);
        this.renderFactionRaidbarOverlay = builder.comment("Render the faction raidbar overlay in the HUD").define("renderFactionRaidbarOverlay", true);
        this.renderActionCooldownOverlay = builder.comment("Render the action cooldown overlay in the HUD").define("renderActionCooldownOverlay", true);
        this.renderActionDurationOverlay = builder.comment("Render the action duration overlay in the HUD").define("renderActionDurationOverlay", true);
        builder.pop();
        builder.pop();

        builder.push("internal");
        this.actionOrder = new ActionOrderValue(builder.comment("Action ordering"), "actionOrder");
        this.minionTaskOrder = new MinionTaskOrderValue(builder.comment("Minion task ordering"), "minionTaskOrder");
        builder.pop();
    }

    @Override
    public void refresh() {
        this.minionTaskOrder.refresh();
        this.actionOrder.refresh();
    }

}
