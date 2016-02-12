package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sends any input related event to the server
 */
public class InputEventPacket implements IMessage {


    private final static String TAG = "InputEventPacket";
    public static String SUCKBLOOD = "sb";
    private final String SPLIT = "-";
    //    public static String TOGGLEAUTOFILLBLOOD = "ta";
//    public static String REVERTBACK = "rb";
//    public static String TOGGLESKILL = "ts";
//    public static String LEAVE_COFFIN = "lc";
//    public static String MINION_CONTROL = "mc";
//    public static final String SWITCHVISION = "sw";
    private String param;
    private String action;

    /**
     * Don't use
     */
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
        if (s.length > 1) {
            param = s[1];
        } else {
            param = "";
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {

        ByteBufUtils.writeUTF8String(buf, action + SPLIT + param);

    }

    public static class Handler extends AbstractServerMessageHandler<InputEventPacket> {

        @Override
        public IMessage handleServerMessage(EntityPlayer player, InputEventPacket message, MessageContext ctx) {
            if (message.action == null)
                return null;
            if (message.action.equals(SUCKBLOOD)) {
                int id = 0;
                try {
                    id = Integer.parseInt(message.param);
                } catch (NumberFormatException e) {
                    VampirismMod.log.e(TAG, "Receiving invalid param", e);
                }
                if (id != 0) {
                    VampirePlayer.get(player).biteEntity(id);
                }
            }
            return null;
        }

        @Override
        protected boolean handleOnMainThread() {
            return true;
        }
    }

}
