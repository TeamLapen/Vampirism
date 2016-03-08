package de.teamlapen.vampirism.api.entity.player.skills;

/**
 * Skill that can be unlocked
 */
public interface ISkill<T extends ISkillPlayer> {

    /**
     * @return Unique id
     */
    String getID();

    /**
     * Called when the skill is disenabled (Server: on load from nbt/on disabling all skills e.g. via the gui. Client: on update from server)
     *
     * @param player
     */
    void onDisable(T player);

    /**
     * Called when the skill is enabled (Server: on load from nbt/on enabling it via the gui. Client: on update from server)
     *
     * @param player
     */
    void onEnable(T player);
}
