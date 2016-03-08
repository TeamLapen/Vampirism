package de.teamlapen.vampirism.entity.player.skills;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;

import java.util.HashMap;
import java.util.Map;

/**
 * ISkillRegistry implementation
 */
public class SkillRegistry implements ISkillRegistry {
    private final Map<IPlayableFaction, SkillNode> rootNodes = new HashMap<>();
    private final Map<IPlayableFaction, BiMap<String, ISkill>> skillMap = new HashMap<>();

    public void finish() {
        for (final IPlayableFaction faction : VampirismAPI.factionRegistry().getPlayableFactions()) {
            if (!rootNodes.containsKey(faction)) {
                rootNodes.put(faction, new SkillNode(faction, new ISkill() {


                    @Override
                    public String getID() {
                        return faction.prop() + "dummy";
                    }

                    @Override
                    public void onDisable(ISkillPlayer player) {

                    }

                    @Override
                    public void onEnable(ISkillPlayer player) {

                    }
                }));
            }
            if (!skillMap.containsKey(faction)) {
                skillMap.put(faction, HashBiMap.<String, ISkill>create());
            }
        }
    }

    @Override
    public String getID(IPlayableFaction faction, ISkill skill) {
        return skillMap.get(faction).inverse().get(skill);
    }

    @Override
    public SkillNode getRootSkillNode(IPlayableFaction faction) {
        return rootNodes.get(faction);
    }

    @Override
    public ISkill getSkill(IPlayableFaction faction, String id) {
        return skillMap.get(faction).get(id);
    }

    @Override
    public void registerNode(SkillNode node) {
        BiMap<String, ISkill> map = skillMap.get(node.getFaction());
        if (map == null) {
            map = HashBiMap.create();
            skillMap.put(node.getFaction(), map);
        }
        ISkill[] skills = node.getElements();
        for (ISkill skill : skills) {
            if (map.put(skill.getID(), skill) != null) {
                throw new IllegalArgumentException("There already is a skill registered for " + skill.getID() + " ");
            }
        }
    }

    @Override
    public void setRootSkill(IPlayableFaction faction, ISkill skill) {
        rootNodes.put(faction, new SkillNode(faction, skill));
    }
}
