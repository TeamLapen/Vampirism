package de.teamlapen.faction.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class ServerConfig implements FactionConfig.IConfigs {


    //<editor-fold desc="World">

    //<editor-fold desc="Village">

    public final ModConfigSpec.IntValue villageMaxSpawnableVillagers;
    public final ModConfigSpec.BooleanValue villageAllowPoiDestruction;
    public final ModConfigSpec.IntValue villageMaximumTotemRadius;

    //<editor-fold desc="Raid">

    public final ModConfigSpec.DoubleValue raidRandomChance;
    public final ModConfigSpec.IntValue raidPhaseOneDuration;
    public final ModConfigSpec.IntValue raidNotifyDistance;
    public final ModConfigSpec.IntValue raidPhaseTwoMembersForceTargetTime;
    public final ModConfigSpec.BooleanValue raidDisableVillageGuards;

    //</editor-fold>

    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Factions">

    public final ModConfigSpec.BooleanValue factionColorInChat;
    public final ModConfigSpec.BooleanValue factionLordPrefixInChat;
    public final ModConfigSpec.DoubleValue factionSkillPointsPerLevel;
    public final ModConfigSpec.DoubleValue factionSkillPointsPerLordLevel;
    public final ModConfigSpec.BooleanValue factionUnlockAllSkills;
    public final ModConfigSpec.BooleanValue factionPvpOnlyBetweenFactions;

    //<editor-fold desc="Minions">

    public final ModConfigSpec.IntValue minionDeathRecoveryTime;
    public final ModConfigSpec.IntValue minionPerLordLevel;

    public final ModConfigSpec.DoubleValue minionResourceCooldownOfflineMult;
    public final ModConfigSpec.IntValue minionEquipmentRepairAmount;

    //</editor-fold>

    //<editor-fold desc="Tasks">

    public final ModConfigSpec.IntValue taskDurationSinglePlayer;
    public final ModConfigSpec.IntValue taskDurationDedicatedServer;

    public final ModConfigSpec.IntValue taskMasterMaxTaskAmount;

    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Admin">

    public final ModConfigSpec.EnumValue<IMobOptions> entityIMob;
    public final ModConfigSpec.BooleanValue preventRenderingDebugBoundingBoxes;
    public final ModConfigSpec.BooleanValue enableFactionLogging;
    public final ModConfigSpec.BooleanValue usePermissions;

    //</editor-fold>


    public ServerConfig(ModConfigSpec.@NotNull Builder builder) {


        builder.push("world");

        builder.push("village");
        villageMaxSpawnableVillagers = builder.comment("Maximum of Villager the Totem can respawn").defineInRange("villageMaxSpawnableVillagers", 30, 0, Integer.MAX_VALUE);
        villageAllowPoiDestruction = builder.comment("Allow players to destroy point of interest blocks in faction villages if they no not have the faction village").define("villageAllowPoiDestruction", false);
        villageMaximumTotemRadius = builder.comment("Maximum range of a Totem to grow the village").defineInRange("villageMaximumTotemRadius", 100, 0, Integer.MAX_VALUE);

        builder.push("raid");
        raidRandomChance = builder.comment("Chance (per second) of a faction raid to occur").defineInRange("raidRandomChance", 2.77777777778E-4, 0, 1);
        raidPhaseOneDuration = builder.comment("Duration of phase 1 of the capturing process in seconds").defineInRange("raidPhaseOneDuration", 80, 1, 1000);
        raidPhaseTwoMembersForceTargetTime = builder.comment("Time in seconds in capture phase 2 after which the capture entities should find a target regardless of distance").defineInRange("raidPhaseTwoMembersForceTargetTime", 80, 1, 1000);
        raidNotifyDistance = builder.comment("Squared distance of village capture notification send to players").defineInRange("raidNotifyDistance", 40000, 0, 100000);
        raidDisableVillageGuards = builder.comment("Prevent villagers in controlled villages to turn into guard villager when the village is attacked").define("raidDisableVillageGuards", false);

        builder.pop();
        builder.pop();
        builder.pop();

        builder.push("faction");
        factionColorInChat = builder.comment("Whether to color player names in chat based on their current faction").define("factionColorInChat", true);
        factionLordPrefixInChat = builder.comment("Whether to add a prefix title based on the current lord level to the player names").define("factionLordPrefixInChat", true);
        factionPvpOnlyBetweenFactions = builder.comment("If PVP should only be allowed between factions. PVP has to be enabled in the server properties for this. Not guaranteed to always protect player from teammates").define("factionPvpOnlyBetweenFactions", false);
        factionUnlockAllSkills = builder.comment("If enabled, you will be able to unlock all skills at max level").define("factionUnlockAllSkills", false);
        factionSkillPointsPerLevel = builder.comment("Players receive n skill points for each level-up. Anything except 2 is unbalanced, but to unlock all skills on maxlevel this value should be set to skill-amount/(max-level - 1)").defineInRange("factionSkillPointsPerLevel", 2D, 1D, 20D);
        factionSkillPointsPerLordLevel = builder.comment("Players receive n skill points for each lord level-up. Anything except 2 is unbalanced, but to unlock all skills on max lord level this value should be set to skill-amount/(max-level - 1)").defineInRange("factionSkillPointsPerLordLevel", 2D, 1D, 20D);

        builder.push("minions");
        minionDeathRecoveryTime = builder.comment("Time in seconds a minion needs to recover from death.").defineInRange("minionDeathRecoveryTime", 220, 1, Integer.MAX_VALUE / 100);
        minionPerLordLevel = builder.comment("How many minions a player can have per lord level. Probably don't want to go very high").defineInRange("minionPerLordLevel", 1, 0, 100);
        minionResourceCooldownOfflineMult = builder.comment("Cooldown multiplier for collect resource task types while player is offline").defineInRange("minionResourceCooldownOfflineMult", 20D, 1D, 100000D);
        minionEquipmentRepairAmount = builder.comment("How much the equipments should be repaired on minion resource tasks").defineInRange("minionEquipmentRepairAmount", 10, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("tasks");
        taskDurationSinglePlayer = builder.comment("Duration a task can be completed in a singleplayer world. In Minutes").defineInRange("taskDurationSinglePlayer", 120, 1, Integer.MAX_VALUE);
        taskDurationDedicatedServer = builder.comment("Duration a task can be completed on a dedicated server. In Minutes").defineInRange("taskDurationDedicatedServer", 1440, 1, Integer.MAX_VALUE);
        taskMasterMaxTaskAmount = builder.comment("Maximum amount of task shown at a taskmaster, except unique tasks").defineInRange("taskMasterMaxTaskAmount", 3, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.pop();


        builder.push("admin");
        this.entityIMob = builder.comment("Changes if entities are recognized as hostile by other mods. See https://github.com/TeamLapen/Vampirism/issues/199. Smart falls back to Never on servers ").defineEnum("entitiesIMob", IMobOptions.SMART);
        this.preventRenderingDebugBoundingBoxes = builder.comment("Prevent players from enabling the rendering of debug bounding boxes. This can allow them to see certain entities they are not supposed to see (e.g. disguised hunter").define("preventDebugBoundingBoxes", false);
        this.usePermissions = builder.comment("Use the forge permission system for certain actions. Take a look at the wiki for more information.").define("usePermissions", false);
        this.enableFactionLogging = builder.comment("Enable a custom faction log file that logs specific faction actions", "Only on dedicated servers", "Requires server restart").worldRestart().define("enableFactionLogging", false);
        builder.pop();
    }

    public enum IMobOptions {
        ALWAYS_IMOB, NEVER_IMOB, SMART
    }
}
