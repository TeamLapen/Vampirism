package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkillPointProvider;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IMarshallPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSkillPointProvider {

    public static final DeferredRegister<ISkillPointProvider> SKILL_POINT_PROVIDERS = DeferredRegister.create(FactionRegistries.Keys.SKILL_POINT_PROVIDER, REFERENCE.MODID);

    public static final DeferredHolder<ISkillPointProvider, ISkillPointProvider> DRACULA = SKILL_POINT_PROVIDERS.register("dracula", () -> (factionPlayer, tree) -> tree.is(ModSkillTreeTags.DRACULA) ? factionPlayer.getExtension(IDraculaPlayer.class).map(IDraculaPlayer::getSkillPoints).orElse(0) : 0);
    public static final DeferredHolder<ISkillPointProvider, ISkillPointProvider> MARSHALL = SKILL_POINT_PROVIDERS.register("marshall", () -> (factionPlayer, tree) -> tree.is(ModSkillTreeTags.MARSHALL) ? factionPlayer.getExtension(IMarshallPlayer.class).map(IMarshallPlayer::getSkillPoints).orElse(0) : 0);


    static void register(IEventBus bus) {
        SKILL_POINT_PROVIDERS.register(bus);
    }

}
