package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillRegistry;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
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
                var1.addChatMessage(new TextComponentString("Tick"));
                print(var1, "tick");
                var1.addChatMessage(new TextComponentString("Garlic"));
                print(var1, "vampirism_checkGarlic");

            }

            private void print(ICommandSender var1, String id) {
                List<Profiler.Result> l = FMLCommonHandler.instance().getMinecraftServerInstance().theProfiler.getProfilingData(id);
                for (Profiler.Result r : l) {
                    var1.addChatMessage(new TextComponentString("" + r.profilerName + ": " + r.usePercentage + "|" + r.totalUsePercentage));
                }
            }
        });
        addSub(new SubCommand() {
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return (args.length == 1) ? getListOfStringsMatchingLastWord(args, getOptions(sender)) : null;
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
                    var1.addChatMessage(new TextComponentString("You have to be in a faction"));
                    return;
                }
                if (var2.length == 0) {
                    throw new WrongUsageException(getCommandUsage(var1));
                }
                if ("list".equals(var2[0])) {
                    ((SkillRegistry) VampirismAPI.skillRegistry()).printSkills(factionPlayer.getFaction(), var1);
                    return;
                }
                if ("disableall".equals(var2[0])) {
                    (factionPlayer.getSkillHandler()).resetSkills();
                    return;
                }
                ISkill skill = VampirismAPI.skillRegistry().getSkill(factionPlayer.getFaction(), var2[0]);
                if (skill == null) {
                    var1.addChatMessage(new TextComponentString("Skill with id " + var2[0] + " could not be found for faction " + factionPlayer.getFaction().name()));
                    return;
                }
                if (factionPlayer.getSkillHandler().isSkillEnabled(skill)) {
                    factionPlayer.getSkillHandler().disableSkill(skill);
                    var1.addChatMessage(new TextComponentString("Disabled skill"));
                    return;
                }
                ISkillHandler.Result result = factionPlayer.getSkillHandler().canSkillBeEnabled(skill);
                if (result == ISkillHandler.Result.OK) {
                    factionPlayer.getSkillHandler().enableSkill(skill);
                    var1.addChatMessage(new TextComponentString("Enabled skill"));
                } else {
                    var1.addChatMessage(new TextComponentString("Could not enable skill " + result));
                }

            }

            private List getOptions(ICommandSender sender) {
                List list = new ArrayList();
                list.add("list");
                list.add("disableall");
                try {
                    IFactionPlayer factionPlayer = FactionPlayerHandler.get(getCommandSenderAsPlayer(sender)).getCurrentFactionPlayer();
                    ((SkillRegistry) VampirismAPI.skillRegistry()).addSkills(factionPlayer.getFaction(), list);

                } catch (PlayerNotFoundException e) {
                }
                return list;
            }
        });
        addSub(new SubCommand() {
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return null;
            }

            @Override
            public boolean canCommandSenderUseCommand(ICommandSender var1) {
                return isSenderCreative(var1) && var1 instanceof EntityPlayer;
            }

            @Override
            public String getCommandName() {
                return "emtpyBloodBar";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName();
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
                VampirePlayer player = VampirePlayer.get(getCommandSenderAsPlayer(var1));
                if (player.getLevel() > 0) {
                    player.getBloodStats().setBloodLevel(0);
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
