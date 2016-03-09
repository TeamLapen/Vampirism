package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillRegistry;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
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
        addSub(new SubCommand() {
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return null;
            }

            @Override
            public boolean canCommandSenderUseCommand(ICommandSender var1) {
                return isSenderCreative(var1) && var1 instanceof EntityPlayerMP;
            }

            @Override
            public String getCommandName() {
                return "skill";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName() + " <skillname>";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
                IFactionPlayer factionPlayer = FactionPlayerHandler.get(getCommandSenderAsPlayer(var1)).getCurrentFactionPlayer();
                if (factionPlayer == null) {
                    var1.addChatMessage(new ChatComponentText("You have to be in a faction"));
                    return;
                }
                if (var2.length == 0) {
                    throw new WrongUsageException(getCommandUsage(var1));
                }
                if ("list".equals(var2[0])) {
                    ((SkillRegistry) VampirismAPI.skillRegistry()).printSkills(factionPlayer.getFaction(), var1);
                    return;
                }
                ISkill skill = VampirismAPI.skillRegistry().getSkill(factionPlayer.getFaction(), var2[0]);
                if (skill == null) {
                    var1.addChatMessage(new ChatComponentText("Skill with id " + var2[0] + " could not be found for faction " + factionPlayer.getFaction().name()));
                    return;
                }
                if (factionPlayer.getSkillHandler().isSkillEnabled(skill)) {
                    factionPlayer.getSkillHandler().disableSkill(skill);
                    var1.addChatMessage(new ChatComponentText("Disabled skill"));
                    return;
                }
                if (factionPlayer.getSkillHandler().canSkillBeEnabled(skill)) {
                    factionPlayer.getSkillHandler().enableSkill(skill);
                    var1.addChatMessage(new ChatComponentText("Enabled skill"));
                } else {
                    var1.addChatMessage(new ChatComponentText("Could not enable skill"));
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
