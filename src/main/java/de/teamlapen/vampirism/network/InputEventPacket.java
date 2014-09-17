package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
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

			}

			return null;
		}

	}
	public static String SUCKBLOOD = "sb";
	private String param;
	private String action;
	private final String SPLIT = "-";

	private final static String TAG = "InputEventPacket";

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
