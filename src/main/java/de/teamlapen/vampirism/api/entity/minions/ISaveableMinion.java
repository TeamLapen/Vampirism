package de.teamlapen.vampirism.api.entity.minions;

/**
 * {@link IMinion} that is saved in the lord's nbt tag, and is always related to the minion
 */
public interface ISaveableMinion extends IMinion {
    enum Call {
        DEFEND_LORD, ATTACK_NON_PLAYER, ATTACK, FOLLOW
    }
}
