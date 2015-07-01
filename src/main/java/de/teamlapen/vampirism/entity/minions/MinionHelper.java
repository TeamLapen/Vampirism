package de.teamlapen.vampirism.entity.minions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.player.VampirePlayer;

public class MinionHelper {

	/**
	 * Returns a new IEntitySelector for minion AI target tasks
	 * 
	 * @param minion
	 *            The minion which the selector will be given
	 * @param targetClass
	 *            Class the target should have or extend.
	 * @param selectPlayer
	 *            If players and their minions should be selected as well. You do not need to set targetClass to EntityPlayer or any subclass of it.
	 * @param excludeVampires
	 *            If vampire npc should be excluded. Does not exclude vampires if the minions lord is a vampire lord
	 * @return
	 */
	public static IEntitySelector getEntitySelectorForMinion(final IMinion minion, final Class<? extends Entity> targetClass, final boolean selectPlayer, final boolean excludeVampires) {
		return new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				IMinion m = MinionHelper.getMinionFromEntity(entity);
				if (selectPlayer) {
					if (MinionHelper.isLordSafe(m, minion.getLord())) {
						return false;
					}
					if (entity instanceof EntityPlayer)
						return true;

				} else {
					if (entity instanceof EntityPlayer)
						return false;
					if (MinionHelper.isLordPlayer(m))
						return false;
				}
				if (excludeVampires) {
					if (entity instanceof EntityVampire) {
						IMinionLord l = minion.getLord();
						return l != null && l instanceof VampirePlayer && ((VampirePlayer) l).isVampireLord();
					}

				}
				return targetClass.isAssignableFrom(entity.getClass());
			}

		};
	}

	/**
	 * Tries to get the IMinion instance from the given entity
	 * 
	 * @param e
	 * @return
	 */
	public static @Nullable IMinion getMinionFromEntity(Entity e) {
		if (e instanceof IMinion)
			return (IMinion) e;

		if (e instanceof EntityCreature) {
			VampireMob m = VampireMob.get((EntityCreature) e);
			if (m.isMinion())
				return m;
		}
		return null;
	}

	/**
	 * Returns a list of VampireMobs which are minions of the given lord
	 * 
	 * @param lord
	 * @param distance
	 * @return
	 */
	public static List<VampireMob> getNearMobMinions(IMinionLord lord, int distance) {
		List<VampireMob> list = new ArrayList<VampireMob>();
		List list2 = lord.getRepresentingEntity().worldObj.getEntitiesWithinAABB(EntityCreature.class, lord.getRepresentingEntity().boundingBox.expand(distance, distance, distance));
		for (Object o : list2) {
			VampireMob m = VampireMob.get((EntityCreature) o);
			if (m.isMinion() && isLordSafe(m, lord)) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * Checks if the given minion's lord is a player
	 * 
	 * @param m
	 *            Can be null
	 * @return
	 */
	public static boolean isLordPlayer(@Nullable IMinion m) {
		if (m != null && m.getLord() instanceof VampirePlayer) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the given entity is the minions lord. Contains null check.
	 * 
	 * @param m
	 * @param e
	 * @return
	 */
	public static boolean isLordSafe(@NonNull IMinion m, @Nullable EntityLivingBase e) {
		IMinionLord l = m.getLord();
		if (l != null && l.getRepresentingEntity().equals(e))
			return true;
		return false;
	}

	/**
	 * Checks if the given lord is the minions lord. Contains null check.
	 * 
	 * @param m
	 * @param l
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
	 * @param message
	 *            Each message is handled as ChatComponentTranslation unless it starts with '\'
	 */
	public static void sendMessageToLord(IMinion m, String... message) {
		if (message == null || message.length < 1)
			return;
		IMinionLord l = m.getLord();
		if (l != null && l.getRepresentingEntity() instanceof EntityPlayer) {
			ChatComponentStyle c1;
			if (m.getRepresentingEntity().hasCustomNameTag()) {
				c1 = new ChatComponentText(m.getRepresentingEntity().getCustomNameTag());
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
}
