package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;

public class InputEventPacket implements IMessage {
	public static class Handler implements IMessageHandler<InputEventPacket, IMessage> {

		@Override
		public IMessage onMessage(InputEventPacket message, MessageContext ctx) {
			if (message.action.equals(SUCKBLOOD)) {
				int id = 0;
				try {
					id = Integer.parseInt(message.param);
				} catch (NumberFormatException e) {
					Logger.e(TAG, "Receiving invalid param", e);
				}
				if (id != 0) {
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					VampirePlayer.get(player).suckBlood(id);
				}
			} else if (message.action.equals(TOGGLEAUTOFILLBLOOD)) {
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				VampirePlayer.get(player).onToggleAutoFillBlood();
			} else if (message.action.equals(REVERTBACK)) {
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				VampirePlayer.get(player).setLevel(0);
				player.attackEntityFrom(DamageSource.magic, 1000);
			} else if (message.action.equals(TOGGLESKILL)) {
				int id =-1;
				try {
					id = Integer.parseInt(message.param);
				} catch (NumberFormatException e) {
					Logger.e(TAG, "Receiving invalid param", e);
				}
				if (id >= 0) {
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					VampirePlayer.get(player).onSkillToggled(id);
				}
				else{
					Logger.w(TAG, "Skill with id "+id+" does not exist");
				}
			}

			return null;
		}

	}

	public static String SUCKBLOOD = "sb";
	public static String TOGGLEAUTOFILLBLOOD = "ta";
	public static String REVERTBACK = "rb";
	public static String TOGGLESKILL = "ts";
	private final static String TAG = "InputEventPacket";
	private String param;
	private String action;

	private final String SPLIT = "-";

	public InputEventPacket() {

	}

	public InputEventPacket(String action, String param) {
		this.action = action;
		this.param = param;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		String[] s = ByteBufUtils.readUTF8String(buf).split(SPLIT);
		action = s[0];
		param = s[1];
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeUTF8String(buf, action + SPLIT + param);

	}

}
