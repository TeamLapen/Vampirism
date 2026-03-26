package de.teamlapen.faction.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class ServerConfig implements FactionConfig.IConfigs {

    // Village
    public final ModConfigSpec.IntValue villageMaxSpawnableVillagers;
    public final ModConfigSpec.BooleanValue villageAllowPoiDestruction;
    public final ModConfigSpec.IntValue villageMaximumTotemRadius;

    // Raid
    public final ModConfigSpec.DoubleValue raidRandomChance;
    public final ModConfigSpec.IntValue raidPhaseOneDuration;
    public final ModConfigSpec.IntValue raidPhaseTwoMembersForceTargetTime;
    public final ModConfigSpec.IntValue raidNotifyDistance;
    public final ModConfigSpec.BooleanValue raidDisableVillageGuards;

    // Faction
    public final ModConfigSpec.BooleanValue factionColorInChat;
    public final ModConfigSpec.BooleanValue factionLordPrefixInChat;
    public final ModConfigSpec.BooleanValue factionPvpOnlyBetweenFactions;
    public final ModConfigSpec.BooleanValue factionUnlockAllSkills;
    public final ModConfigSpec.DoubleValue factionSkillPointsPerLevel;
    public final ModConfigSpec.DoubleValue factionSkillPointsPerLordLevel;

    // Minions
    public final ModConfigSpec.IntValue minionDeathRecoveryTime;
    public final ModConfigSpec.IntValue minionPerLordLevel;
    public final ModConfigSpec.DoubleValue minionOfflineResourceCooldownMultiplier;
    public final ModConfigSpec.IntValue minionEquipmentRepairAmount;

    // Tasks
    public final ModConfigSpec.IntValue taskDurationSinglePlayer;
    public final ModConfigSpec.IntValue taskDurationDedicatedServer;
    public final ModConfigSpec.IntValue taskMasterMaxTasks;

    // Admin
    public final ModConfigSpec.EnumValue<IMobOptions> entitiesIMob;
    public final ModConfigSpec.BooleanValue preventDebugBoundingBoxes;
    public final ModConfigSpec.BooleanValue usePermissions;
    public final ModConfigSpec.BooleanValue enableFactionLogging;

    public ServerConfig(ModConfigSpec.@NotNull Builder builder) {
        builder.push("world");

        builder.push("village");
        this.villageMaxSpawnableVillagers = builder
                .comment("Maximum number of villagers the totem can respawn.")
                .defineInRange("villageMaxSpawnableVillagers", 30, 0, Integer.MAX_VALUE);
        this.villageAllowPoiDestruction = builder
                .comment("When enabled, allows players to destroy point of interest blocks in faction villages they do not own.")
                .define("villageAllowPoiDestruction", false);
        this.villageMaximumTotemRadius = builder
                .comment("Maximum range of a totem used to grow the village.")
                .defineInRange("villageMaximumTotemRadius", 100, 0, Integer.MAX_VALUE);
        builder.pop();

        builder.push("raid");
        this.raidRandomChance = builder
                .comment("Chance per second of a faction raid occurring.")
                .defineInRange("raidRandomChance", 2.77777777778E-4, 0, 1);
        this.raidPhaseOneDuration = builder
                .comment("Duration of phase one of the capturing process, in seconds.")
                .defineInRange("raidPhaseOneDuration", 80, 1, 1000);
        this.raidPhaseTwoMembersForceTargetTime = builder
                .comment("Time in seconds during capture phase two after which capture entities seek a target regardless of distance.")
                .defineInRange("raidPhaseTwoMembersForceTargetTime", 80, 1, 1000);
        this.raidNotifyDistance = builder
                .comment("Squared distance within which players are notified of a village capture.")
                .defineInRange("raidNotifyDistance", 40000, 0, 100000);
        this.raidDisableVillageGuards = builder
                .comment("When enabled, prevents villagers in controlled villages from turning into guard villagers when attacked.")
                .define("raidDisableVillageGuards", false);
        builder.pop();

        builder.pop();

        builder.push("faction");
        this.factionColorInChat = builder
                .comment("When enabled, colors player names in chat based on their current faction.")
                .define("factionColorInChat", true);
        this.factionLordPrefixInChat = builder
                .comment("When enabled, adds a title prefix based on the current lord level to player names in chat.")
                .define("factionLordPrefixInChat", true);
        this.factionPvpOnlyBetweenFactions = builder
                .comment("When enabled, PVP is only allowed between members of different factions. Requires PVP to be enabled in server properties. Not guaranteed to always protect teammates.")
                .define("factionPvpOnlyBetweenFactions", false);
        this.factionUnlockAllSkills = builder
                .comment("When enabled, allows unlocking all skills upon reaching max level.")
                .define("factionUnlockAllSkills", false);
        this.factionSkillPointsPerLevel = builder
                .comment("Number of skill points awarded per level-up. Values other than 2 are unbalanced. To unlock all skills at max level, set to skill-amount / (max-level - 1).")
                .defineInRange("factionSkillPointsPerLevel", 2D, 1D, 20D);
        this.factionSkillPointsPerLordLevel = builder
                .comment("Number of skill points awarded per lord level-up. Values other than 2 are unbalanced. To unlock all skills at max lord level, set to skill-amount / (max-level - 1).")
                .defineInRange("factionSkillPointsPerLordLevel", 2D, 1D, 20D);

        builder.push("minions");
        this.minionDeathRecoveryTime = builder
                .comment("Time in seconds a minion needs to recover after death.")
                .defineInRange("minionDeathRecoveryTime", 220, 1, Integer.MAX_VALUE / 100);
        this.minionPerLordLevel = builder
                .comment("Number of minions a player may have per lord level.")
                .defineInRange("minionPerLordLevel", 1, 0, 100);
        this.minionOfflineResourceCooldownMultiplier = builder
                .comment("Cooldown multiplier for collect resource task types while the owning player is offline.")
                .defineInRange("minionOfflineResourceCooldownMultiplier", 20D, 1D, 100000D);
        this.minionEquipmentRepairAmount = builder
                .comment("Amount by which minion equipment is repaired when completing resource tasks.")
                .defineInRange("minionEquipmentRepairAmount", 10, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("tasks");
        this.taskDurationSinglePlayer = builder
                .comment("Duration in minutes within which a task must be completed in a singleplayer world.")
                .defineInRange("taskDurationSinglePlayer", 120, 1, Integer.MAX_VALUE);
        this.taskDurationDedicatedServer = builder
                .comment("Duration in minutes within which a task must be completed on a dedicated server.")
                .defineInRange("taskDurationDedicatedServer", 1440, 1, Integer.MAX_VALUE);
        this.taskMasterMaxTasks = builder
                .comment("Maximum number of tasks shown at a taskmaster, excluding unique tasks.")
                .defineInRange("taskMasterMaxTasks", 3, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.pop();

        builder.push("admin");
        this.entitiesIMob = builder
                .comment("Controls whether faction entities are recognized as hostile by other mods. See https://github.com/TeamLapen/Vampirism/issues/199. Smart falls back to Never on servers.")
                .defineEnum("entitiesIMob", IMobOptions.SMART);
        this.preventDebugBoundingBoxes = builder
                .comment("When enabled, prevents players from enabling debug bounding box rendering, which can reveal disguised entities.")
                .define("preventDebugBoundingBoxes", false);
        this.usePermissions = builder
                .comment("When enabled, uses the Forge permission system for certain actions. See the wiki for more information.")
                .define("usePermissions", false);
        this.enableFactionLogging = builder
                .comment("When enabled, writes a custom log file for specific faction actions. Only applies to dedicated servers. Requires a server restart.")
                .worldRestart()
                .define("enableFactionLogging", false);
        builder.pop();
    }

    public enum IMobOptions {
        ALWAYS_IMOB, NEVER_IMOB, SMART
    }
}
