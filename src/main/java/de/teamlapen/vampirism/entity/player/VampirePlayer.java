package de.teamlapen.vampirism.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.proxy.CommonProxy;

public class VampirePlayer implements IExtendedEntityProperties {

	public final static String EXT_PROP_NAME = "VampirePlayer";
	/**
	 * 
	 * @param player
	 * @return VampirePlayer property of player
	 */
	public static final VampirePlayer get(EntityPlayer player) {
		return (VampirePlayer) player.getExtendedProperties(VampirePlayer.EXT_PROP_NAME);
	}
	private static final String getSaveKey(EntityPlayer player) {
		// no longer a username field, so use the command sender name instead:
		return player.getCommandSenderName() + ":" + EXT_PROP_NAME;
	}
	public static final void loadProxyData(EntityPlayer player) {
		VampirePlayer playerData = VampirePlayer.get(player);
		NBTTagCompound savedData = CommonProxy.getEntityData(getSaveKey(player));
		if (savedData != null) {
			playerData.loadNBTData(savedData);

		}
		playerData.applyModifiers(playerData.getLevel());
	}
	/**
	 * Registers vampire property to player
	 * 
	 * @param player
	 */
	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(VampirePlayer.EXT_PROP_NAME, new VampirePlayer(player));
	}
	public static void saveProxyData(EntityPlayer player) {
		VampirePlayer playerData = VampirePlayer.get(player);
		NBTTagCompound savedData = new NBTTagCompound();
		playerData.saveNBTData(savedData);
		CommonProxy.storeEntityData(getSaveKey(player), savedData);
	}
	private final EntityPlayer player;

	private final String KEY_LEVEL = "level";

	private final String KEY_BLOOD = "blood";

	private final int MAXBLOOD = 20;

	private final static int BLOOD_WATCHER = 20;

	private final static int LEVEL_WATCHER = 21;

	public VampirePlayer(EntityPlayer player) {
		this.player = player;
		this.player.getDataWatcher().addObject(BLOOD_WATCHER, MAXBLOOD);
		this.player.getDataWatcher().addObject(LEVEL_WATCHER, 0);
	}

	private void addBlood(int a) {
		int blood = getBlood();
		blood += a;
		if (blood > MAXBLOOD) {
			blood = MAXBLOOD;
		}
		setBlood(blood);
	}

	private void applyModifiers(int level) {
		PlayerModifiers.applyModifiers(level, player);

	}

	public int getBlood() {
		return this.player.getDataWatcher().getWatchableObjectInt(BLOOD_WATCHER);
	}

	public int getLevel() {
		return this.player.getDataWatcher().getWatchableObjectInt(LEVEL_WATCHER);
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub

	}

	public void levelUp() {
		int level = getLevel();
		level++;
		this.applyModifiers(level);
		setLevel(level);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		setBlood(properties.getInteger(KEY_LEVEL));
		setLevel(properties.getInteger(KEY_BLOOD));

	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger(KEY_LEVEL, getLevel());
		properties.setInteger(KEY_BLOOD, getBlood());
		compound.setTag(EXT_PROP_NAME, properties);

	}

	private void setBlood(int b) {
		this.player.getDataWatcher().updateObject(LEVEL_WATCHER, b);
	}

	/**
	 * For testing only, make private later
	 * 
	 * @param l
	 */
	public void setLevel(int l) {
		if (l >= 0) {
			this.player.getDataWatcher().updateObject(LEVEL_WATCHER, l);
			this.applyModifiers(l);
		}
	}

	/**
	 * Suck blood from an EntityLiving. Only sucks blood if health is low enough
	 * and if the entity has blood
	 * 
	 * @param e
	 *            Entity to suck blood from
	 */
	public void suckBlood(EntityLiving e) {

		VampireMob mob = VampireMob.get(e);
		int amount = mob.bite();
		if (amount > 0) {
			addBlood(amount);
		}
	}

	/**
	 * Suck blood from an EntityLiving belonging to the given id. Only sucks
	 * blood if health is low enough and if the entity has blood
	 * 
	 * @param e
	 *            Id of Entity to suck blood from
	 */
	public void suckBlood(int entityId) {
		Entity e = player.worldObj.getEntityByID(entityId);
		if (e != null && e instanceof EntityLiving) {
			suckBlood((EntityLiving) e);
		}
	}

}
