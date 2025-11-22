package de.teamlapen.factions.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class ServerConfig {

    public final ModConfigSpec.BooleanValue unlockAllSkills;
    public final ModConfigSpec.BooleanValue pvpOnlyBetweenFactions;
    public final ModConfigSpec.BooleanValue pvpOnlyBetweenFactionsIncludeHumans;
    public final ModConfigSpec.BooleanValue factionColorInChat;
    public final ModConfigSpec.BooleanValue lordPrefixInChat;
    public final ModConfigSpec.EnumValue<IMobOptions> entityIMob;
    public final ModConfigSpec.BooleanValue allowVillageDestroyBlocks;
    public final ModConfigSpec.BooleanValue usePermissions;
    public final ModConfigSpec.BooleanValue preventRenderingDebugBoundingBoxes;

    public final ModConfigSpec.BooleanValue disableVillageGuards;

    public final ModConfigSpec.IntValue viMaxVillagerRespawn;
    public final ModConfigSpec.DoubleValue viRandomRaidChance;
    public final ModConfigSpec.IntValue viPhase1Duration;
    public final ModConfigSpec.IntValue viNotifyDistanceSQ;
    public final ModConfigSpec.IntValue viForceTargetTime;
    public final ModConfigSpec.IntValue viMaxTotemRadius;


    public final ModConfigSpec.IntValue miDeathRecoveryTime;
    public final ModConfigSpec.IntValue miMinionPerLordLevel;
    public final ModConfigSpec.IntValue taskDurationSinglePlayer;
    public final ModConfigSpec.IntValue taskDurationDedicatedServer;

    public final ModConfigSpec.IntValue taskMasterMaxTaskAmount;
    public final ModConfigSpec.DoubleValue miResourceCooldownOfflineMult;

    public final ModConfigSpec.DoubleValue skillPointsPerLevel;
    public final ModConfigSpec.DoubleValue skillPointsPerLordLevel;

    public final ModConfigSpec.IntValue miEquipmentRepairAmount;

    public ServerConfig(ModConfigSpec.@NotNull Builder builder) {
        pvpOnlyBetweenFactions = builder.comment("If PVP should only be allowed between factions. PVP has to be enabled in the server properties for this. Not guaranteed to always protect player from teammates").define("pvpOnlyBetweenFactions", false);
        pvpOnlyBetweenFactionsIncludeHumans = builder.comment("If pvpOnlyBetweenFactions is enabled, this decides whether human players can be attacked and attack others").define("pvpOnlyBetweenFactionsIncludeHumans", false);
        factionColorInChat = builder.comment("Whether to color player names in chat based on their current faction").define("factionColorInChat", true);
        lordPrefixInChat = builder.comment("Whether to add a prefix title based on the current lord level to the player names").define("lordPrefixInChat", true);
        entityIMob = builder.comment("Changes if entities are recognized as hostile by other mods. See https://github.com/TeamLapen/Vampirism/issues/199. Smart falls back to Never on servers ").defineEnum("entitiesIMob", IMobOptions.SMART);
        preventRenderingDebugBoundingBoxes = builder.comment("Prevent players from enabling the rendering of debug bounding boxes. This can allow them to see certain entities they are not supposed to see (e.g. disguised hunter").define("preventDebugBoundingBoxes", false);
        allowVillageDestroyBlocks = builder.comment("Allow players to destroy point of interest blocks in faction villages if they no not have the faction village").define("allowVillageDestroyBlocks", false);
        usePermissions = builder.comment("Use the forge permission system for certain actions. Take a look at the wiki for more information").define("usePermissions", false);
        unlockAllSkills = builder.comment("If enabled, you will be able to unlock all skills at max level").define("allSkillsAtMaxLevel", false);
        disableVillageGuards = builder.comment("Prevent villagers in hunter controlled villages to turn into guard villager when the village is attacked").define("disableVillageGuards", false);
        viMaxVillagerRespawn = builder.comment("Maximum of Villager the Totem can respawn").defineInRange("maxVillagerRespawn", 30, 0, Integer.MAX_VALUE);
        viRandomRaidChance = builder.comment("Chance (per tick) of a faction raid to occur").defineInRange("randomRaidChance", 0.000138888888889, 0, 1);

        viPhase1Duration = builder.comment("Duration of phase 1 of the capturing process in 2*seconds").defineInRange("phase1Duration", 80, 1, 1000);
        viNotifyDistanceSQ = builder.comment("Squared distance of village capture notification").defineInRange("notifyDistanceSQ", 40000, 0, 100000);
        viForceTargetTime = builder.comment("Time in 2*seconds in capture phase 2 after which the capture entities should find a target regardless of distance").defineInRange("forceTargetTime", 80, 1, 1000);
        viMaxTotemRadius = builder.comment("Maximum range of a Totem to grow the village").defineInRange("maxTotemRadius", 100, 0, Integer.MAX_VALUE);
        miDeathRecoveryTime = builder.comment("Time in seconds a minion needs to recover from death.").defineInRange("deathRecoveryTime", 220, 1, Integer.MAX_VALUE / 100);
        miMinionPerLordLevel = builder.comment("How many minions a player can have per lord level. Probably don't want to go very high").defineInRange("minionPerLordLevel", 1, 0, 100);
        taskDurationSinglePlayer = builder.comment("Duration a task can be completed in a singleplayer world. In Minutes").defineInRange("taskDurationSinglePlayer", 120, 1, Integer.MAX_VALUE);
        taskDurationDedicatedServer = builder.comment("Duration a task can be completed on a dedicated server. In Minutes").defineInRange("taskDurationDedicatedServer", 1440, 1, Integer.MAX_VALUE);
        taskMasterMaxTaskAmount = builder.comment("Maximum amount of task shown at a taskmaster, except unique tasks").defineInRange("taskMasterMaxTaskAmount", 3, 1, Integer.MAX_VALUE);
        miResourceCooldownOfflineMult = builder.comment("Cooldown multiplier for collect resource task types while player is offline").defineInRange("resourceCooldownOfflineMult", 20D, 1D, 100000D);
        miEquipmentRepairAmount = builder.comment("How much the equipments should be repaired on minion resource tasks").defineInRange("equipmentRepairAmount", 10, 1, Integer.MAX_VALUE);

        skillPointsPerLevel = builder.comment("Players receive n skill points for each level-up. Anything except 2 is unbalanced, but to unlock all skills on maxlevel this value should be set to skill-amount/(max-level - 1)").defineInRange("skillPointsPerLevel", 2D, 1D, 20D);
        skillPointsPerLordLevel = builder.comment("Players receive n skill points for each lord level-up. Anything except 2 is unbalanced, but to unlock all skills on max lord level this value should be set to skill-amount/(max-level - 1)").defineInRange("skillPointsPerLordLevel", 2D, 1D, 20D);


    }

    public enum IMobOptions {
        ALWAYS_IMOB, NEVER_IMOB, SMART
    }
}
