package de.teamlapen.vampirism.core;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.skills.SkillManager;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.tests.Tests;
import de.teamlapen.vampirism.tileentity.TileTent;
import de.teamlapen.vampirism.util.VampireBookManager;
import de.teamlapen.vampirism.world.GarlicChunkHandler;
import de.teamlapen.vampirism.world.VampirismWorldData;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;
import de.teamlapen.vampirism.world.gen.structure.StructureManager;
import de.teamlapen.vampirism.world.gen.structure.VampirismTemplate;
import de.teamlapen.vampirism.world.villages.VampirismVillage;
import de.teamlapen.vampirism.world.villages.VampirismVillageHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.command.CommandTreeHelp;

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
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                VampirismVillage v = VampirismVillageHelper.getNearestVillage(player);
                if (v == null) {
                    sender.sendMessage(new TextComponentString("No village found"));
                } else {
                    sender.sendMessage(new TextComponentString(v.makeDebugString(player.getPosition())));

                }
            }

            @Override
            public String getName() {
                return "info-village";
            }

        });
        addSubcommand(new SubCommand() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                int entityMonster = player.getEntityWorld().countEntities(EnumCreatureType.MONSTER, false);
                int entityMonsterSpawn = player.getEntityWorld().countEntities(EnumCreatureType.MONSTER, true);
                int entityHunter = player.getEntityWorld().countEntities(VReference.HUNTER_CREATURE_TYPE, false);
                int entityHunterSpawn = player.getEntityWorld().countEntities(VReference.HUNTER_CREATURE_TYPE, true);
                int entityVampire = player.getEntityWorld().countEntities(VReference.VAMPIRE_CREATURE_TYPE, false);
                int entityVampireSpawn = player.getEntityWorld().countEntities(VReference.VAMPIRE_CREATURE_TYPE, true);
                sender.sendMessage(new TextComponentString(String.format("Monster: %s (%s), Hunter: %s (%s), Vampire: %s (%s)", entityMonster, entityMonsterSpawn, entityHunter, entityHunterSpawn, entityVampire, entityVampireSpawn)));

            }

            @Override
            public String getName() {
                return "info-entities";
            }

        });
        addSubcommand(new SubCommand() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                VampirismMod.log.t("************************************************************");
                VampirismMod.log.t("");
                VampirismMod.log.t("Marker %s");
                if (args.length > 0) VampirismMod.log.t(joinNiceString(args));
                VampirismMod.log.t("");
                VampirismMod.log.t("***********************************************************");
            }

            @Override
            public String getName() {
                return "marker";
            }

        });
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


            private void print(ICommandSender var1, String id) {
                List<Profiler.Result> l = FMLCommonHandler.instance().getMinecraftServerInstance().profiler.getProfilingData(id);
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
                    ((SkillManager) VampirismAPI.skillManager()).printSkills(factionPlayer.getFaction(), sender);
                    return;
                }
                if ("disableall".equals(args[0])) {
                    (factionPlayer.getSkillHandler()).resetSkills();
                    return;
                }
                ISkill skill = VampirismRegistries.SKILLS.getValue(new ResourceLocation(args[0]));
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

                IPlayableFaction faction = FactionPlayerHandler.get(sender).getCurrentFaction();
                VampirismAPI.skillManager().getSkillsForFaction(faction).forEach(skill -> {
                    list.add(skill.getRegistryName().toString());
                });


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

        });

        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                List l = player.getEntityWorld().getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(3, 2, 3));
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
            public String getName() {
                return "info-entity";
            }


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                List<Entity> l = player.getEntityWorld().getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(3, 2, 3));
                for (Entity o : l) {
                    NBTTagCompound nbt = new NBTTagCompound();
                    o.writeToNBT(nbt);
                    VampirismMod.log.i("InfoEntity", "Data %s", nbt);
                }
                sender.sendMessage(new TextComponentString("Printed info to log"));
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

        });
        addSubcommand(new SubCommand() {


            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                List<EntityVillager> l = player.getEntityWorld().getEntitiesWithinAABB(EntityVillager.class, player.getEntityBoundingBox().grow(3, 2, 3));
                for (EntityVillager v : l) {
                    if (v instanceof IHunter || v instanceof IVampire) continue;
                    VampirismVillage.makeAggressive(v, null);

                }
            }

            @Override
            public String getName() {
                return "makeVillagerAgressive";
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
        addSubcommand(new SubCommand() {
            @Override
            public String getName() {
                return "place";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return "place <structure>";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (args.length == 0) {
                    throw new WrongUsageException("Missing structure name");
                }
                EntityPlayer p = getCommandSenderAsPlayer(sender);
                try {
                    StructureManager.Structure s = StructureManager.Structure.valueOf(args[0]);
                    VampirismTemplate template = StructureManager.get(s);
                    if (template == null) {
                        throw new CommandException("Structure " + s + " was not loaded");
                    }
                    template.addBlocksToWorld(p.world, p.getPosition().offset(EnumFacing.NORTH), new PlacementSettings());

                } catch (IllegalArgumentException e) {
                    throw new CommandException("Structure " + args[0] + " not found.");
                }


            }
        });

        addSubcommand(new SubCommand() {
            @Override
            public String getName() {
                return "halloween";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer p = getCommandSenderAsPlayer(sender);
                //EntityDraculaHalloween draculaHalloween = (EntityDraculaHalloween) UtilLib.spawnEntityBehindEntity(p, new ResourceLocation(REFERENCE.MODID, ModEntities.SPECIAL_DRACULA_HALLOWEEN));
                //draculaHalloween.setOwnerId(p.getUniqueID());
                VampLib.proxy.getParticleHandler().spawnParticle(p.world, ModParticles.HALLOWEEN, p.posX, p.posY, p.posZ);
            }
        });

        addSubcommand(new SubCommand() {
            @Override
            public String getName() {
                return "overtakeVillage";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer p = getCommandSenderAsPlayer(sender);
                VampirismVillage v = VampirismVillageHelper.getNearestVillage(p);
                if (v == null) {
                    sender.sendMessage(new TextComponentString("Could not find any village near you"));
                } else {
                    v.forcefullyOvertake();
                    sender.sendMessage(new TextComponentString("Forcefully overtook village"));
                }
            }
        });
        addSubcommand(new SubCommand() {
            @Override
            public String getName() {
                return "setSwordCharged";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                ItemStack held = player.getHeldItemMainhand();
                if (args.length != 1) {
                    throw new WrongUsageException("Only one argument (charge) accepted");
                }
                float charge;
                try {
                    charge = Float.parseFloat(args[0]);
                } catch (NumberFormatException e) {
                    throw new WrongUsageException("Argument has to be a float");
                }

                if (held.getItem() instanceof VampirismVampireSword) {
                    ((VampirismVampireSword) held.getItem()).setCharged(held, charge);
                    player.setHeldItem(EnumHand.MAIN_HAND, held);
                } else {
                    sender.sendMessage(new TextComponentString("You have to hold a vampire sword in your main hand"));
                }
            }
        });
        addSubcommand(new SubCommand() {
            @Override
            public String getName() {
                return "setSwordTrained";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                ItemStack held = player.getHeldItemMainhand();
                if (args.length != 1) {
                    throw new WrongUsageException("Only one argument (trained) accepted");
                }
                float charge;
                try {
                    charge = Float.parseFloat(args[0]);
                } catch (NumberFormatException e) {
                    throw new WrongUsageException("Argument has to be a float");
                }

                if (held.getItem() instanceof VampirismVampireSword) {
                    ((VampirismVampireSword) held.getItem()).setTrained(held, player, charge);
                    player.setHeldItem(EnumHand.MAIN_HAND, held);
                } else {
                    sender.sendMessage(new TextComponentString("You have to hold a vampire sword in your main hand"));
                }
            }
        });

        //Add last
        addSubcommand(new CommandTreeHelp(this));
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
        public String getUsage(ICommandSender sender) {
            return getName();
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
