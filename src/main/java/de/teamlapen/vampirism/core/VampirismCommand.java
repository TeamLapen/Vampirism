package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
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
        addSub(new SubCommand() {
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return (args.length == 1) ? getListOfStringsMatchingLastWord(args, getCategories()) : Collections.emptyList();
            }

            @Override
            public boolean canSenderUseCommand(ICommandSender var1) {
                return !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer() || var1.canCommandSenderUseCommand(3, getCommandName());
            }

            @Override
            public String getCommandName() {
                return "resetBalance";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName() + " <all/[category]/help>";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                String cat;
                if (var2 == null || var2.length == 0) {
                    cat = "all";
                } else {
                    cat = var2[0];
                }
                if ("help".equals(cat)) {
                    var1.addChatMessage(new TextComponentString("You can reset Vampirism balance values to the default values. If you have not modified them, this is recommend after every update of Vampirism"));
                    var1.addChatMessage(new TextComponentString("Use '/vampirism resetBalance all' to reset all categories or specify a category with '/vampirism resetBalance <category>' (Tab completion is supported)"));
                }
                boolean p = Balance.resetAndReload(cat);
                if (p) {
                    var1.addChatMessage(new TextComponentString("Successfully reset " + cat + " balance category. Please restart MC."));
                } else {
                    var1.addChatMessage(new TextComponentString("Did not find " + cat + " balance category."));
                }
            }

            private String[] getCategories() {
                Set<String> categories = Balance.getCategories().keySet();
                String[] result = categories.toArray(new String[categories.size() + 2]);
                result[result.length - 1] = "all";
                result[result.length - 2] = "help";
                return result;
            }
        });
        addSub(new SubCommand() {

            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return args.length == 1 ? getListOfStringsMatchingLastWord(args, pfaction_names) : Collections.emptyList();
            }

            @Override
            public boolean canSenderUseCommand(ICommandSender var1) {
                if (!(var1 instanceof EntityPlayer)) {
                    return false;//TODO set level for other players (via console)
                }
                return canCommandSenderUseCheatCommand(var1);
            }

            @Override
            public String getCommandName() {
                return "level";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {

                return getCommandName() + " " + ArrayUtils.toString(pfaction_names) + " <level>";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                EntityPlayer player = (EntityPlayer) var1;//var1 is a player because canCommandSenderUse checks that
                if (var2.length != 2) {
                    sendMessage(var1, "Usage: " + getCommandUsage(var1));
                } else {
                    int level = 0;
                    try {
                        level = Integer.parseInt(var2[1]);
                    } catch (NumberFormatException e) {
                        sendMessage(var1, "<level> has to be a number");
                        return;
                    }
                    //Search factions
                    for (int i = 0; i < pfaction_names.length; i++) {
                        if (pfaction_names[i].equalsIgnoreCase(var2[0])) {
                            IPlayableFaction newFaction = pfactions[i];
                            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
                            if (level == 0 && !handler.canLeaveFaction()) {
                                ((EntityPlayer) var1).addChatComponentMessage(new TextComponentTranslation("text.vampirism.faction.cant_leave").appendSibling(new TextComponentString("(" + handler.getCurrentFaction().name() + ")")));
                                return;
                            }
                            if (level > newFaction.getHighestReachableLevel()) {
                                level = newFaction.getHighestReachableLevel();
                            }
                            if (handler.setFactionAndLevel(newFaction, level)) {
                                ITextComponent msg = var1.getDisplayName().appendSibling(new TextComponentString(" is now a " + pfaction_names[i] + " level " + level));
                                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendChatMsg(msg);
                            } else {
                                ((EntityPlayer) var1).addChatComponentMessage(new TextComponentTranslation("text.vampirism.faction.failed_to_change"));
                            }

                            return;
                        }
                    }
                    sendMessage(var1, "Did not find faction " + var2[0]);

                }
            }
        });
        addSub(new SubCommand() {
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return null;
            }

            @Override
            public boolean canSenderUseCommand(ICommandSender var1) {
                return var1 instanceof EntityPlayer;
            }

            @Override
            public String getCommandName() {
                return "eye";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName() + " <id [0-" + (REFERENCE.EYE_TYPE_COUNT - 1) + "]> ";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                EntityPlayer player = (EntityPlayer) var1;
                if (var2.length != 1) {
                    sendMessage(var1, "Usage: +" + getCommandUsage(var1));
                    return;
                }
                try {
                    int type = Integer.parseInt(var2[0]);
                    if (!VampirePlayer.get(player).setEyeType(type)) {
                        sendMessage(var1, "<id> has to be a valid number between 0 and " + (REFERENCE.EYE_TYPE_COUNT - 1));
                    }
                } catch (NumberFormatException e) {
                    sendMessage(var1, "<id> has to be a number");
                }
            }
        });
        addSub(new SubCommand() {
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return Collections.emptyList();
            }

            @Override
            public boolean canSenderUseCommand(ICommandSender var1) {
                return canCommandSenderUseCommand(var1, PERMISSION_LEVEL_FULL, getCommandName());
            }

            @Override
            public String getCommandName() {
                return "checkForVampireBiome";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName() + " <maxRadius in chunks>";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                if (Configs.disable_vampireForest) {
                    var1.addChatMessage(new TextComponentString("The Vampire Biome is disabled in the config file"));
                } else {
                    int maxDist = 150;
                    if (var2.length > 0) {
                        try {
                            maxDist = Integer.parseInt(var2[0]);
                        } catch (NumberFormatException e) {
                            VampirismMod.log.w("CheckVampireBiome", "Failed to parse max dist %s", var2[0]);
                            var1.addChatMessage(new TextComponentString("Failed to parse max distance. Using " + maxDist));
                        }
                        if (maxDist > 500) {
                            if (var2.length > 1 && "yes".equals(var2[1])) {

                            } else {
                                var1.addChatMessage(new TextComponentString("This will take a long time. Please use '/" + getCommandUsage(var1) + " yes', if you are sure"));
                                return;
                            }
                        }
                    }
                    List<Biome> biomes = new ArrayList<Biome>();
                    biomes.add(ModBiomes.vampireForest);
                    var1.addChatMessage(new TextComponentTranslation("text.vampirism.biome.looking_for_biome"));
                    ChunkPos pos = UtilLib.findNearBiome(var1.getEntityWorld(), (var1).getPosition(), maxDist, biomes, var1);
                    if (pos == null) {
                        var1.addChatMessage(new TextComponentTranslation("text.vampirism.biome.not_found"));
                    } else {
                        var1.addChatMessage(new TextComponentTranslation("text.vampirism.biome.found").appendSibling(new TextComponentString("[" + (pos.chunkXPos << 4) + "," + (pos.chunkZPos << 4) + "]")));
                    }
                }
            }
        });
        addSub(new SubCommand() {
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return Collections.emptyList();
            }

            @Override
            public boolean canSenderUseCommand(ICommandSender var1) {
                return true;
            }

            @Override
            public String getCommandName() {
                return "changelog";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName();
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                if (!VampirismMod.instance.getVersionInfo().isNewVersionAvailable()) {
                    var1.addChatMessage(new TextComponentString("There is no new version available"));
                    return;
                }
                VersionChecker.Version newVersion = VampirismMod.instance.getVersionInfo().getNewVersion();
                List<String> changes = newVersion.getChanges();
                var1.addChatMessage(new TextComponentString(TextFormatting.GREEN + "Vampirism " + newVersion.name + "(" + MinecraftForge.MC_VERSION + ")"));
                for (String c : changes) {
                    var1.addChatMessage(new TextComponentString("-" + c));
                }
                var1.addChatMessage(new TextComponentString(""));
                String template = I18n.translateToLocal("text.vampirism.update_message");
                String homepage = VampirismMod.instance.getVersionInfo().getHomePage();
                template = template.replaceAll("@download@", newVersion.getUrl() == null ? homepage : newVersion.getUrl()).replaceAll("@forum@", homepage);
                ITextComponent component = ITextComponent.Serializer.jsonToComponent(template);
                var1.addChatMessage(component);
            }
        });
        addSub(new SubCommand() {


            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return Collections.emptyList();
            }

            @Override
            public boolean canSenderUseCommand(ICommandSender var1) {
                return true;
            }

            @Override
            public String getCommandName() {
                return "currentDimension";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName();
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                if (var1 instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) var1;
                    if (p.worldObj != null) {
                        var1.addChatMessage(new TextComponentString("Dimension ID: " + p.worldObj.provider.getDimension()));
                    }
                }
            }
        });
        addSub(new SubCommand() {
            @Nonnull
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return Collections.emptyList();
            }

            @Override
            public boolean canSenderUseCommand(ICommandSender var1) {
                return !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer() || var1.canCommandSenderUseCommand(3, getCommandName());
            }

            @Override
            public String getCommandName() {
                return "debug";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName();
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
                boolean enabled = VampirismMod.log.isDebug();
                VampirismMod.log.setDebug(!enabled);
                String msg = enabled ? "Disabled debug mode" : "Enabled debug mode";
                var1.addChatMessage(new TextComponentString(msg));
            }
        });
    }

    @Override
    public String getCommandName() {
        return "vampirism";
    }

    @Override
    protected boolean canCommandSenderUseCheatCommand(ICommandSender sender) {
        if (VampirismMod.inDev) {
            return true;
        }
        return super.canCommandSenderUseCheatCommand(sender);
    }

    protected boolean canCommandSenderUseCommand(ICommandSender sender, int perm, String command) {
        if (VampirismMod.inDev) {
            return true;
        }
        return sender.canCommandSenderUseCommand(perm, command);
    }
}
