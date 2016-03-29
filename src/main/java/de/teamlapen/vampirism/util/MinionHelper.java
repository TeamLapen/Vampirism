package de.teamlapen.vampirism.util;


import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides several static util methods for minion related things
 */
public class MinionHelper {

//    /**
//     * Returns a new IEntitySelector for minion AI target tasks
//     *
//     * @param minion
//     *            The minion which the selector will be given
//     * @param targetClass
//     *            Class the target should have or extend.
//     * @param selectPlayer
//     *            If players and their minions should be selected as well. You do not need to set targetClass to EntityPlayer or any subclass of it.
//     * @param excludeVampires
//     *            If vampire npc should be excluded. Does not exclude vampires if the minions lord is a vampire lord
//     * @return
//     */
//    public static IEntitySelector getEntitySelectorForMinion(final IMinion minion, final Class<? extends Entity> targetClass, final boolean selectPlayer, final boolean excludeVampires) {
//        return new IEntitySelector() {
//
//            @Override
//            public boolean isEntityApplicable(Entity entity) {
//                IMinion m = MinionHelper.getMinionFromEntity(entity);
//                if (selectPlayer) {
//                    if (MinionHelper.isLordSafe(m, minion.getLord())) return false;
//                    if (MinionHelper.isLordSafe(minion, entity)) {
//                        return false;
//                    }
//                    if (entity instanceof EntityPlayer)
//                        return true;
//
//                } else {
//                    if (entity instanceof EntityPlayer)
//                        return false;
//                    if (MinionHelper.isLordPlayer(m))
//                        return false;
//                }
//                if (excludeVampires) {
//                    if (entity instanceof EntityVampire) {
//                        IMinionLord l = minion.getLord();
//                        return l != null && l instanceof VampirePlayer && ((VampirePlayer) l).isVampireLord();
//                    }
//
//                }
//                return targetClass.isAssignableFrom(entity.getClass());
//            }
//
//        };
//    }


    /**
     * Checks if the given minion's lord is a player
     *
     * @param m Can be null
     * @return
     */
    public static boolean isLordPlayer(@Nullable IMinion m) {
        return m != null && m.getLord() instanceof IFactionPlayer;
    }

    /**
     * Checks if the given entity is the minion's lord.
     *
     * @param m minion
     * @param e lord
     * @return
     */
    public static boolean isLordSafe(@Nonnull IMinion m, @Nullable Entity e) {
        IMinionLord l = m.getLord();
        return l != null && l.getRepresentingEntity().equals(e);

    }

    /**
     * Checks if the given lord is the minion's lord.
     *
     * @param e Minion
     * @param l Lord
     * @return
     */
    public static boolean isLordSafe(@Nullable EntityLivingBase e, @Nonnull IMinionLord l) {
        if (e == null) return false;
        if (e instanceof IMinion) {
            return l.equals(((IMinion) e).getLord());
        }
        return false;
    }

    /**
     * Checks if the given lord is the minions lord.
     *
     * @param m minion
     * @param l lord
     * @return
     */
    public static boolean isLordSafe(@Nullable IMinion m, @Nullable IMinionLord l) {
        if (m == null || l == null)
            return false;
        return l.equals(m.getLord());
    }

    /**
     * Sends a translated message to the lord if he exists and is a player
     *
     * @param m
     * @param message Each message is handled as ChatComponentTranslation unless it starts with '\'
     */
    public static void sendMessageToLord(IMinion m, String... message) {
        if (message == null || message.length < 1)
            return;
        IMinionLord l = m.getLord();
        if (l != null && l.getRepresentingEntity() instanceof EntityPlayer) {
            EntityLivingBase entity = entity(m);
            IChatComponent c1;
            if (entity.hasCustomName()) {
                c1 = new ChatComponentText(entity.getCustomNameTag());
            } else {
                c1 = new ChatComponentTranslation("text.vampirism.minion");
            }

            c1.appendText(": ");
            c1.getChatStyle().setColor((EnumChatFormatting.GREEN));
            ChatComponentStyle c2;
            if (message[0].startsWith("\\")) {
                c2 = new ChatComponentText(message[0].replace("\\", ""));
            } else {
                c2 = new ChatComponentTranslation(message[0]);
            }
            for (int i = 1; i < message.length; i++) {
                if (message[i].startsWith("\\")) {
                    c2.appendSibling(new ChatComponentText(message[i].replace("\\", "")));
                } else {
                    c2.appendSibling(new ChatComponentTranslation(message[i]));
                }

            }
            c1.appendSibling(c2);
            c2.getChatStyle().setColor(EnumChatFormatting.WHITE);
            ((EntityPlayer) l.getRepresentingEntity()).addChatComponentMessage(c1);
        }
    }

    /**
     * Simply casts the given minion to EntityLivingBase.
     * Throws an illegal state exception if the minion does not extend {@link EntityLivingBase} to inform the modder about that mistake
     *
     * @param minion
     * @return
     */
    public static EntityLivingBase entity(@Nonnull IMinion minion) {
        if (minion instanceof EntityLivingBase) {
            return (EntityLivingBase) minion;
        }

        throw new IllegalStateException("All classes that implement IMinion, have to extend EntityLivingBase. " + minion + " does not.");
    }
}