package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillManager implements ISkillManager {

    private final static Logger LOGGER = LogManager.getLogger(SkillManager.class);
    private final Map<ResourceLocation, ISkillType> skillTypes = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IFactionPlayer<T>> @NotNull List<ISkill<T>> getSkillsForFaction(IPlayableFaction<T> faction) {
        return RegUtil.values(ModRegistries.SKILLS).stream().filter(action -> action.getFaction().map(f -> f == faction).orElse(true)).map(action -> (ISkill<T>) action).collect(Collectors.toList());
    }


    @Nullable
    @Override
    public ISkillType getSkillType(@NotNull ResourceLocation id) {
        return this.skillTypes.get(id);
    }

    @NotNull
    @Override
    public ISkillType registerSkillType(@NotNull ISkillType type) {
        if (this.skillTypes.containsKey(type.getRegistryName())) {
            throw new IllegalStateException("A skill type with the id " + type.getRegistryName() + " has already been registered");
        }
        this.skillTypes.put(type.getRegistryName(), type);
        return type;
    }
}
