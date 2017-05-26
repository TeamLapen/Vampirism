package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.EntityHunterVillager;
import de.teamlapen.vampirism.player.skills.SkillRegistry;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.tests.Tests;
import de.teamlapen.vampirism.tileentity.TileTent;
import de.teamlapen.vampirism.util.VampireBookManager;
import de.teamlapen.vampirism.world.GarlicChunkHandler;
import de.teamlapen.vampirism.world.VampirismWorldData;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Command for testing and debugging
 */
public class TestCommand extends BasicCommand {

    public TestCommand() {
        if (VampirismMod.inDev) {
            aliases.add("vtest");
        }
        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                World w = getCommandSenderAsPlayer(sender).getEntityWorld();
                VampirismWorldData worldData = VampirismWorldData.get(w);
                BlockPos dungeonPos = worldData.getRandomVampireDungeon(getCommandSenderAsPlayer(sender).getRNG());
                ItemStack itemstack = ItemMap.setupNewMap(w, (double) dungeonPos.getX(), (double) dungeonPos.getZ(), (byte) 2, true, true);
                ItemMap.renderBiomePreviewMap(w, itemstack);
                MapData.addTargetDecoration(itemstack, dungeonPos, "+", MapDecoration.Type.TARGET_X);
                getCommandSenderAsPlayer(sender).dropItem(itemstack, false);
            }

            @Override
            public String getName() {
                return "giveTestTargetMap";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });
        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
                sender.sendMessage(new TextComponentString("Tick"));
                print(sender, "tick");
                sender.sendMessage(new TextComponentString("Garlic"));
                print(sender, "vampirism_checkGarlic");

            }

            @Override
            public String getName() {
                return "garlic_profiler";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }

            private void print(ICommandSender var1, String id) {
                List<Profiler.Result> l = FMLCommonHandler.instance().getMinecraftServerInstance().theProfiler.getProfilingData(id);
                for (Profiler.Result r : l) {
                    var1.sendMessage(new TextComponentString("" + r.profilerName + ": " + r.usePercentage + "|" + r.totalUsePercentage));
                }
            }
        });
        addSubcommand(new SubCommand() {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                IFactionPlayer factionPlayer = FactionPlayerHandler.get(getCommandSenderAsPlayer(sender)).getCurrentFactionPlayer();
                if (factionPlayer == null) {
                    sender.sendMessage(new TextComponentString("You have to be in a faction"));
                    return;
                }
                if (args.length == 0) {
                    throw new WrongUsageException(getUsage(sender));
                }
                if ("list".equals(args[0])) {
                    ((SkillRegistry) VampirismAPI.skillRegistry()).printSkills(factionPlayer.getFaction(), sender);
                    return;
                }
                if ("disableall".equals(args[0])) {
                    (factionPlayer.getSkillHandler()).resetSkills();
                    return;
                }
                ISkill skill = VampirismAPI.skillRegistry().getSkill(factionPlayer.getFaction(), args[0]);
                if (skill == null) {
                    sender.sendMessage(new TextComponentString("Skill with id " + args[0] + " could not be found for faction " + factionPlayer.getFaction().name()));
                    return;
                }
                if (factionPlayer.getSkillHandler().isSkillEnabled(skill)) {
                    factionPlayer.getSkillHandler().disableSkill(skill);
                    sender.sendMessage(new TextComponentString("Disabled skill"));
                    return;
                }
                ISkillHandler.Result result = factionPlayer.getSkillHandler().canSkillBeEnabled(skill);
                if (result == ISkillHandler.Result.OK) {
                    factionPlayer.getSkillHandler().enableSkill(skill);
                    sender.sendMessage(new TextComponentString("Enabled skill"));
                } else {
                    sender.sendMessage(new TextComponentString("Could not enable skill " + result));
                }

            }

            @Override
            public String getName() {
                return "skill";
            }

            @Override
            public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
                return sender instanceof EntityPlayer ? (args.length == 1 ? getListOfStringsMatchingLastWord(args, getOptions((EntityPlayer) sender)) : getOptions((EntityPlayer) sender)) : Collections.emptyList();
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName() + " <skillname>";
            }

            private List<String> getOptions(EntityPlayer sender) {
                List<String> list = new ArrayList<>();
                list.add("list");
                list.add("disableall");

                IFactionPlayer factionPlayer = FactionPlayerHandler.get(sender).getCurrentFactionPlayer();
                ((SkillRegistry) VampirismAPI.skillRegistry()).addSkills(factionPlayer.getFaction(), list);


                return list;
            }
        });
        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                VampirePlayer player = VampirePlayer.get(getCommandSenderAsPlayer(sender));
                if (player.getLevel() > 0) {
                    player.getBloodStats().setBloodLevel(0);
                }
            }

            @Override
            public String getName() {
                return "emptyBloodBar";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });

        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                List l = player.getEntityWorld().getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expand(3, 2, 3));
                for (Object o : l) {
                    if (o instanceof EntityCreature) {

                        ResourceLocation id = EntityList.getKey((Entity) o);
                        sender.sendMessage(new TextComponentString(id.toString()));
                    } else {
                        sender.sendMessage(new TextComponentString("Not biteable " + o.getClass().getName()));
                    }
                }
            }

            @Override
            public String getName() {
                return "entity";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 0;
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });

        addSubcommand(new SubCommand() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                ResourceLocation res = player.getEntityWorld().getBiome(player.getPosition()).getRegistryName();
                sender.sendMessage(new TextComponentString(res.toString()));
            }

            @Override
            public String getName() {
                return "biome";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 0;
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });
        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                List<EntityVillager> l = player.getEntityWorld().getEntitiesWithinAABB(EntityVillager.class, player.getEntityBoundingBox().expand(3, 2, 3));
                for (EntityVillager v : l) {
                    EntityHunterVillager hunter = EntityHunterVillager.makeHunter(v);
                    v.setDead();
                    v.getEntityWorld().spawnEntity(hunter);
                }
            }

            @Override
            public String getName() {
                return "makeVillagerAgressive";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });
        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                IFactionPlayer factionPlayer = FactionPlayerHandler.get(player).getCurrentFactionPlayer();
                if (factionPlayer != null) {
                    IActionHandler handler = factionPlayer.getActionHandler();
                    if (handler != null) {
                        handler.resetTimers();
                        sender.sendMessage(new TextComponentString("Reset Timers"));
                    }
                }
            }

            @Override
            public String getName() {
                return "resetActions";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });
        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                RayTraceResult result = UtilLib.getPlayerLookingSpot(player, 5);
                if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {

                    TileEntity tent = player.getEntityWorld().getTileEntity(result.getBlockPos());
                    if (tent != null && tent instanceof TileTent) {
                        ((TileTent) tent).setSpawn(true);
                        sender.sendMessage(new TextComponentString("Success"));
                    }

                }
            }

            @Override
            public String getName() {
                return "tent";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });
        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                player.inventory.addItemStackToInventory(VampireBookManager.getInstance().getRandomBook(player.getRNG()));
            }

            @Override
            public String getName() {
                return "vampireBook";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return PERMISSION_LEVEL_CHEAT;
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });

        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (VampirismWorldGen.debug) {
                    VampirismWorldGen.debug = false;
                    notifyCommandListener(sender, this, "command.vampirism.test.gen_debug.false");
                } else {
                    VampirismWorldGen.debug = true;
                    notifyCommandListener(sender, this, "command.vampirism.test.gen_debug.true");
                }
            }

            @Override
            public String getName() {
                return "debugGen";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });

        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer p = getCommandSenderAsPlayer(sender);
                Tests.runTests(p.getEntityWorld(), p);
            }

            @Override
            public String getName() {
                return "runTests";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName();
            }
        });

        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (sender instanceof EntityPlayer) {
                    sender.sendMessage(new TextComponentString("Garlic strength: " + VampirismAPI.getGarlicChunkHandler(sender.getEntityWorld()).getStrengthAtChunk(new ChunkPos(sender.getPosition()))));
                }
                if (args != null && args.length > 0 && "print".equals(args[0])) {
                    ((GarlicChunkHandler) VampirismAPI.getGarlicChunkHandler(sender.getEntityWorld())).printDebug(sender);
                }

            }

            @Override
            public String getName() {
                return "garlicCheck";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return PERMISSION_LEVEL_CHEAT;
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return getName() + "(print)";
            }
        });
    }

    @Override
    public String getName() {
        return "vampirism-test";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName() + " <sub>";
    }


    public abstract static class SubCommand extends CommandBase {

        private SubCommand() {
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return VampirismMod.inDev || sender.canUseCommand(getRequiredPermissionLevel(), "vampirism-test." + getName());
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 3;
        }
    }

}
