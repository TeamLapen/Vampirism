package de.teamlapen.factions.common.core;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.skills.ISkillPointProvider;
import de.teamlapen.factions.api.factions.skills.ISkillTree;
import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import de.teamlapen.factions.common.config.FactionConfig;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.tags.FactionSkillTreeTags;
import net.minecraft.core.Holder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionSkillPointProvider {

    public static final DeferredRegister<ISkillPointProvider> SKILL_POINT_PROVIDERS = DeferredRegister.create(FactionRegistries.Keys.SKILL_POINT_PROVIDER, REFERENCE.MOD_ID);

    public static final DeferredHolder<ISkillPointProvider, ISkillPointProvider> NONE = SKILL_POINT_PROVIDERS.register("none", () -> (factionPlayer, tree) -> 0);
    public static final DeferredHolder<ISkillPointProvider, ISkillPointProvider> LEVELING = SKILL_POINT_PROVIDERS.register("leveling", () -> (factionPlayer, tree) -> tree.is(FactionSkillTreeTags.DEFAULT) ? (int) (Math.max(0, factionPlayer.getLevel() - 1) * FactionConfig.SERVER.skillPointsPerLevel.get()) : 0);
    public static final DeferredHolder<ISkillPointProvider, ISkillPointProvider> LORD_LEVELING = SKILL_POINT_PROVIDERS.register("lord_leveling", () -> (factionPlayer, tree) -> tree.is(FactionSkillTreeTags.DEFAULT) ? (int) (Math.max(0, FactionPlayerHandler.get(factionPlayer.asEntity()).getLordLevel() - 1) * FactionConfig.SERVER.skillPointsPerLordLevel.get()) : 0);
    public static final DeferredHolder<ISkillPointProvider, ISkillPointProvider> CONFIG_UNLOCK_ALL = SKILL_POINT_PROVIDERS.register("config_unlock_all", () -> new ISkillPointProvider() {

        @Override
        public int getSkillPoints(IFactionPlayer<?> factionPlayer, Holder<ISkillTree> skillTree) {
            return 0;
        }

        @Override
        public boolean ignoreSkillPointLimit(IFactionPlayer<?> factionPlayer, Holder<ISkillTree> skillTree) {
            return FactionConfig.SERVER.unlockAllSkills.get() && factionPlayer.getLevel() == factionPlayer.getMaxLevel();
        }
    });

    static void register(IEventBus bus) {
        SKILL_POINT_PROVIDERS.register(bus);
    }

}
