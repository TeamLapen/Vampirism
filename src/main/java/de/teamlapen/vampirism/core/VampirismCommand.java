package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.command.CommandTreeHelp;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Central command for this mod
 */
public class VampirismCommand extends BasicCommand {


    public VampirismCommand() {
        if (VampirismMod.inDev) {
            aliases.add("v");
        }
        final IPlayableFaction[] pfactions = VampirismAPI.factionRegistry().getPlayableFactions();
        final String[] pfaction_names = new String[pfactions.length];
        for (int i = 0; i < pfactions.length; i++) {
            pfaction_names[i] = pfactions[i].name();
        }
        addSubcommand(new SubCommand(PERMISSION_LEVEL_ADMIN) {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
                String cat;
                if (args == null || args.length == 0) {
                    cat = "all";
                } else {
                    cat = args[0];
                }
                if ("help".equals(cat)) {
                    sender.sendMessage(new TextComponentString("You can reset Vampirism balance values to the default values. If you have not modified them, this is recommend after every update of Vampirism"));
                    sender.sendMessage(new TextComponentString("Use '/vampirism resetBalance all' to reset all categories or specify a category with '/vampirism resetBalance <category>' (Tab completion is supported)"));
                    return;
                }
                boolean p = Balance.resetAndReload(cat);
                if (p) {
                    notifyCommandListener(sender, this, "command.vampirism.base.reset_balance.success", cat);
                } else {
                    notifyCommandListener(sender, this, "command.vampirism.base.reset_balance.not_found", cat);
                }
            }

            @Override
            public String getName() {
                return "resetBalance";
            }

            @Override
            public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
                return (args.length == 1) ? getListOfStringsMatchingLastWord(args, getCategories()) : java.util.Arrays.asList(getCategories());
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName() + " <all/[category]/help>";
            }

            private String[] getCategories() {
                Set<String> categories = Balance.getCategories().keySet();
                String[] result = categories.toArray(new String[categories.size() + 2]);
                result[result.length - 1] = "all";
                result[result.length - 2] = "help";
                return result;
            }
        });

        addSubcommand(new SubCommand(PERMISSION_LEVEL_CHEAT) {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (args.length < 2 || args.length > 3) {
                    throw new WrongUsageException(getUsage(sender));
                }
                EntityPlayer player = args.length == 3 ? getPlayer(server, sender, args[2]) : getCommandSenderAsPlayer(sender);
                int level;
                try {
                    level = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    throw new NumberInvalidException();
                }
                //Search factions
                for (int i = 0; i < pfaction_names.length; i++) {
                    if (pfaction_names[i].equalsIgnoreCase(args[0])) {
                        IPlayableFaction newFaction = pfactions[i];
                        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
                        if (level == 0 && !handler.canLeaveFaction()) {
                            throw new CommandException("command.vampirism.base.level.cant_leave", new TextComponentTranslation(handler.getCurrentFaction().getUnlocalizedName()));
                        }
                        if (level > newFaction.getHighestReachableLevel()) {
                            level = newFaction.getHighestReachableLevel();
                        }
                        if (handler.setFactionAndLevel(newFaction, level)) {
                            ITextComponent msg = player.getDisplayName().appendSibling(new TextComponentString(" is now a " + pfaction_names[i] + " level " + level));
                            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(msg);
                        } else {
                            throw new CommandException("commands.vampirism.failed_to_execute");
                        }

                        return;
                    }
                }
                throw new CommandException("command.vampirism.base.level.faction_not_found", args[0]);


            }

            @Override
            public String getName() {
                return "level";
            }

            @Override
            public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
                return args.length == 1 ? getListOfStringsMatchingLastWord(args, pfaction_names) : java.util.Arrays.asList(pfaction_names);
            }

            @Override
            public String getUsage(ICommandSender sender) {

                return getName() + " " + ArrayUtils.toString(pfaction_names) + " <level> [<player>]";
            }
        });

        addSubcommand(new SubCommand(PERMISSION_LEVEL_CHEAT) {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (args.length > 1) {
                    throw new WrongUsageException(getUsage(sender));
                }
                EntityPlayer player = args.length == 1 ? getPlayer(server, sender, args[0]) : getCommandSenderAsPlayer(sender);

                IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(player);
                int currentLevel = handler.getCurrentLevel();
                if (currentLevel == 0) {
                    throw new CommandException("command.vampirism.base.levelup.nofaction");
                } else if (currentLevel == handler.getCurrentFaction().getHighestReachableLevel()) {
                    sender.sendMessage(new TextComponentTranslation("command.vampirism.base.levelup.max"));
                } else {
                    if (handler.setFactionLevel(handler.getCurrentFaction(), currentLevel + 1)) {
                        ITextComponent msg = player.getDisplayName().appendSibling(new TextComponentString(" is now a " + handler.getCurrentFaction().name() + " level " + (currentLevel + 1)));
                        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(msg);
                    } else {
                        throw new CommandException("commands.vampirism.failed_to_execute");
                    }
                }

            }

            @Override
            public String getName() {
                return "levelup";
            }

            @Override
            public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
                return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
            }

            @Override
            public String getUsage(ICommandSender sender) {

                return getName() + " [<player>]";
            }
        });


        addSubcommand(new SubCommand(0) {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

                EntityPlayer player = getCommandSenderAsPlayer(sender);
                if (args.length != 1) {
                    throw new WrongUsageException(getUsage(sender));
                }
                try {
                    int type = Integer.parseInt(args[0]);
                    if (!VampirePlayer.get(player).setEyeType(type)) {
                        throw new NumberInvalidException("command.vampirism.base.eye.types", REFERENCE.EYE_TYPE_COUNT - 1);
                    }
                    notifyCommandListener(sender, this, "command.vampirism.base.eye.success", type);

                } catch (NumberFormatException e) {
                    throw new NumberInvalidException();
                }
            }

            @Override
            public String getName() {
                return "eye";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName() + " <id [0-" + (REFERENCE.EYE_TYPE_COUNT - 1) + "]> ";
            }
        });
        addSubcommand(new SubCommand(0) {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                if (args.length != 1) {
                    throw new WrongUsageException(getUsage(sender));
                }
                try {
                    int type = Integer.parseInt(args[0]);
                    if (!VampirePlayer.get(player).setFangType(type)) {
                        throw new NumberInvalidException("command.vampirism.base.fang.types", REFERENCE.FANG_TYPE_COUNT - 1);
                    }
                    notifyCommandListener(sender, this, "command.vampirism.base.fang.success", type);
                } catch (NumberFormatException e) {
                    throw new NumberInvalidException();
                }
            }

            @Override
            public String getName() {
                return "fang";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName() + " <id [0-" + (REFERENCE.FANG_TYPE_COUNT - 1) + "]> ";
            }
        });

        addSubcommand(new SubCommand(0) {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

                EntityPlayer player = getCommandSenderAsPlayer(sender);
                if (args.length != 1) {
                    throw new WrongUsageException(getUsage(sender));
                }
                String arg = args[0];
                if ("true".equals(arg) || "1".equals(arg)) {
                    VampirePlayer.get(player).setGlowingEyes(true);
                    notifyCommandListener(sender, this, "command.vampirism.base.glowing_eyes.enabled", true);

                } else if ("false".equals(arg) || "0".equals(arg)) {
                    VampirePlayer.get(player).setGlowingEyes(false);
                    notifyCommandListener(sender, this, "command.vampirism.base.glowing_eyes.disabled", false);

                } else {
                    throw new WrongUsageException(getUsage(sender));
                }
            }

            @Override
            public String getName() {
                return "glowingEye";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName() + " true/false ";
            }
        });

        addSubcommand(new SubCommand(PERMISSION_LEVEL_ADMIN) {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
                if (Configs.disable_vampireForest) {
                    notifyCommandListener(sender, this, "command.vampirism.base.vampire_biome.disabled");
                } else {
                    int maxDist = 150;
                    if (args.length > 0) {
                        try {
                            maxDist = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            VampirismMod.log.w("CheckVampireBiome", "Failed to parse max dist %s", args[0]);
                            notifyCommandListener(sender, this, "command.vampirism.base.vampire_biome.parse_dist", maxDist);
                        }
                        if (maxDist > 500) {
                            if (args.length <= 1 || !"yes".equals(args[1])) {
                                notifyCommandListener(sender, this, "command.vampirism.base.vampire_biome.time_warning", getName(), maxDist);
                                return;
                            }
                        }
                    }
                    List<Biome> biomes = new ArrayList<>();
                    biomes.add(ModBiomes.vampireForest);
                    notifyCommandListener(sender, this, "command.vampirism.base.vampire_biome.searching");
                    ChunkPos pos = UtilLib.findNearBiome(sender.getEntityWorld(), (sender).getPosition(), maxDist, biomes, sender);
                    if (pos == null) {
                        notifyCommandListener(sender, this, "command.vampirism.base.vampire_biome.not_found");
                    } else {
                        notifyCommandListener(sender, this, "command.vampirism.base.vampire_biome.found", new TextComponentString("[" + (pos.getXStart()) + "," + (pos.getZStart()) + "]"));
                    }
                }
            }

            @Override
            public String getName() {
                return "checkForVampireBiome";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName() + " <maxRadius in chunks>";
            }
        });
        addSubcommand(new SubCommand(0) {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
                if (!VampirismMod.instance.getVersionInfo().isNewVersionAvailable()) {
                    sender.sendMessage(new TextComponentString("There is no new version available"));
                    return;
                }
                VersionChecker.Version newVersion = VampirismMod.instance.getVersionInfo().getNewVersion();
                List<String> changes = newVersion.getChanges();
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Vampirism " + newVersion.name + "(" + MinecraftForge.MC_VERSION + ")"));
                for (String c : changes) {
                    sender.sendMessage(new TextComponentString("-" + c));
                }
                sender.sendMessage(new TextComponentString(""));
                String template = UtilLib.translate("text.vampirism.update_message");
                String homepage = VampirismMod.instance.getVersionInfo().getHomePage();
                template = template.replaceAll("@download@", newVersion.getUrl() == null ? homepage : newVersion.getUrl()).replaceAll("@forum@", homepage);
                ITextComponent component = ITextComponent.Serializer.jsonToComponent(template);
                sender.sendMessage(component);
            }

            @Override
            public String getName() {
                return "changelog";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });
        addSubcommand(new SubCommand(0) {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) sender;
                    sender.sendMessage(new TextComponentString("Dimension ID: " + p.getEntityWorld().provider.getDimension()));
                }
            }

            @Override
            public String getName() {
                return "currentDimension";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });
        addSubcommand(new SubCommand(PERMISSION_LEVEL_ADMIN) {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                boolean enabled = VampirismMod.log.isDebug();
                VampirismMod.log.setDebug(!enabled);
                String msg = enabled ? "command.vampirism.base.debug.true" : "command.vampirism.base.debug.false";
                notifyCommandListener(sender, this, msg);
            }

            @Override
            public String getName() {
                return "debug";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });
        addSubcommand(new SubCommand(PERMISSION_LEVEL_ALL) {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

                EntityPlayer player = getCommandSenderAsPlayer(sender);
                if (args.length == 2) {

                    @Nullable ResourceLocation id = new ResourceLocation(args[1]);
                    if ("null".equals(args[1])) {
                        id = null;
                    }
                    if (id == null || VampirismAPI.actionManager().getRegistry().containsKey(id)) {
                        if ("1".equals(args[0])) {
                            FactionPlayerHandler.get(player).setBoundAction1(id, true);
                        } else if ("2".equals(args[0])) {
                            FactionPlayerHandler.get(player).setBoundAction2(id, true);
                        } else {
                            throw new WrongUsageException("Valid keys: 1 or 2");
                        }
                        sender.sendMessage(new TextComponentTranslation("command.vampirism.base.bind_action.success", args[1], args[0]));
                    } else {
                        sender.sendMessage(new TextComponentTranslation("command.vampirism.base.bind_action.not_existing", args[1]));
                    }
                } else {
                    sender.sendMessage(new TextComponentTranslation("command.vampirism.base.bind_action.help"));
                    sender.sendMessage(new TextComponentString("/vampirism " + getUsage(sender)));
                }
            }

            @Override
            public String getName() {
                return "bind-action";
            }

            @Override
            public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
                if (args.length == 1) {
                    return getListOfStringsMatchingLastWord(args, "1", "2");
                } else if (args.length == 2) {
                    return getListOfStringsMatchingLastWord(args, VampirismAPI.actionManager().getRegistry().getKeys());
                }
                return Collections.emptyList();
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName() + " <1/2> " + " <action-id>";
            }
        });


        //Add last
        addSubcommand(new CommandTreeHelp(this));
    }


    @Override
    public String getName() {
        return "vampirism";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/vampirism <sub>";
    }


    public abstract static class SubCommand extends CommandBase {
        private final int perm;

        private SubCommand(int perm) {
            this.perm = perm;
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return VampirismMod.inDev || sender.canUseCommand(getRequiredPermissionLevel(), "vampirism." + getName());
        }

        @Override
        public int getRequiredPermissionLevel() {
            return perm;
        }
    }
}
