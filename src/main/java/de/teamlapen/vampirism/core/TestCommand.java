package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.command.ICommandSender;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.List;

/**
 * Command for testing and debugging
 */
public class TestCommand extends BasicCommand {

    public TestCommand() {
        if (VampirismMod.inDev) {
            aliases.add("vtest");
        }
        addSub(new SubCommand() {
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return null;
            }

            @Override
            public boolean canCommandSenderUseCommand(ICommandSender var1) {
                return isSenderCreative(var1);
            }

            @Override
            public String getCommandName() {
                return "garlic_profiler";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName();
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                var1.addChatMessage(new ChatComponentText("Tick"));
                print(var1, "tick");
                var1.addChatMessage(new ChatComponentText("Garlic"));
                print(var1, "vampirism_checkGarlic");

            }

            private void print(ICommandSender var1, String id) {
                List<Profiler.Result> l = MinecraftServer.getServer().theProfiler.getProfilingData(id);
                for (Profiler.Result r : l) {
                    var1.addChatMessage(new ChatComponentText("" + r.field_76331_c + ": " + r.field_76332_a + "|" + r.field_76330_b));
                }
            }
        });
    }

    @Override
    public String getCommandName() {
        return "vampirism-test";
    }

    protected boolean isSenderCreative(ICommandSender sender) {
        if (VampirismMod.inDev)
            return true;
        return super.isSenderCreative(sender);
    }
}
