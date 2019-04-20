package de.teamlapen.lib.lib.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * General Utility Class
 */
public class UtilLib {
    public static String entityToString(Entity e) {
        if (e == null) {
            return "Entity is null";
        }
        return e.toString();
    }

    public static boolean doesBlockHaveSolidTopSurface(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).isSideSolid(worldIn, pos, EnumFacing.UP);
    }


    @SideOnly(Side.CLIENT)
    public static void drawTexturedModalRect(float zLevel, int x, int y, int textureX, int textureY, int width, int height, int texWidth, int texHeight) {
        float f = 1 / (float) texWidth;
        float f1 = 1 / (float) texHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double) (x), (double) (y + height), (double) zLevel).tex((double) ((float) (textureX) * f), (double) ((float) (textureY + height) * f1)).endVertex();
        vertexbuffer.pos((double) (x + width), (double) (y + height), (double) zLevel).tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + height) * f1)).endVertex();
        vertexbuffer.pos((double) (x + width), (double) (y), (double) zLevel).tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY) * f1)).endVertex();
        vertexbuffer.pos((double) (x), (double) (y), (double) zLevel).tex((double) ((float) (textureX) * f), (double) ((float) (textureY) * f1)).endVertex();
        tessellator.draw();
    }

    /**
     * Gets players looking spot (blocks only).
     *
     * @param player
     * @param restriction Max distance or 0 for player reach distance or -1 for not restricted
     * @return The position as a MovingObjectPosition, null if not existent cf: https ://github.com/bspkrs/bspkrsCore/blob/master/src/main/java/bspkrs /util/CommonUtils.java
     */
    public static RayTraceResult getPlayerLookingSpot(EntityPlayer player, double restriction) {
        float scale = 1.0F;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scale;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scale;
        double x = player.prevPosX + (player.posX - player.prevPosX) * scale;
        double y = player.prevPosY + (player.posY - player.prevPosY) * scale + 1.62D;
        double z = player.prevPosZ + (player.posZ - player.prevPosZ) * scale;
        Vec3d vector1 = new Vec3d(x, y, z);
        float cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float cosPitch = -MathHelper.cos(-pitch * 0.017453292F);
        float sinPitch = MathHelper.sin(-pitch * 0.017453292F);
        float pitchAdjustedSinYaw = sinYaw * cosPitch;
        float pitchAdjustedCosYaw = cosYaw * cosPitch;
        double distance = 500D;
        if (restriction == 0 && player instanceof EntityPlayerMP) {
            distance = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();
        } else if (restriction > 0) {
            distance = restriction;
        }

        Vec3d vector2 = vector1.add(pitchAdjustedSinYaw * distance, sinPitch * distance, pitchAdjustedCosYaw * distance);
        return player.getEntityWorld().rayTraceBlocks(vector1, vector2);
    }

    public static BlockPos getRandomPosInBox(World w, AxisAlignedBB box) {
        int x = (int) box.minX + w.rand.nextInt((int) (box.maxX - box.minX) + 1);
        int z = (int) box.minZ + w.rand.nextInt((int) (box.maxZ - box.minZ) + 1);
        int y = w.getHeight(x, z) + 5;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
        while (y > box.minY && !w.getBlockState(pos).isNormalCube()) {
            pos.setPos(x, --y, z);
        }

        if (y < box.minY || y > box.maxY - 1) {
            pos.setPos(x, (int) box.minY + w.rand.nextInt((int) (box.maxY - box.minY) + 1), z);
        }
        return pos.up();
    }

    /**
     * @return Number of chunks loaded by players
     */
    public static int countPlayerLoadedChunks(World world) {
        List<ChunkPos> chunks = Lists.newArrayList();
        int i = 0;

        for (EntityPlayer entityplayer : world.playerEntities) {
            if (!entityplayer.isSpectator()) {
                int x = MathHelper.floor(entityplayer.posX / 16.0D);
                int z = MathHelper.floor(entityplayer.posZ / 16.0D);

                for (int dx = -8; dx <= 8; ++dx) {
                    for (int dz = -8; dz <= 8; ++dz) {
                        ChunkPos chunkpos = new ChunkPos(dx + x, dz + z);

                        if (!chunks.contains(chunkpos)) {
                            ++i;
                            chunks.add(chunkpos);


                        }
                    }
                }
            }
        }
        return i;
    }

    /**
     * Returns an approximate absolute (world) position of the held item.
     * This assumes a ModelBiped like model and a normal item.
     *
     * @param entity   Assumes a ModelBiped like creature
     * @param mainHand If main hand position
     * @return Absolute position in the world
     */
    public static @Nonnull
    Vec3d getItemPosition(EntityLivingBase entity, boolean mainHand) {
        boolean left = (mainHand ? entity.getPrimaryHand() : entity.getPrimaryHand().opposite()) == EnumHandSide.LEFT;
        boolean firstPerson = entity instanceof EntityPlayer && ((EntityPlayer) entity).isUser() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
        Vec3d dir = firstPerson ? entity.getForward() : Vec3d.fromPitchYaw(new Vec2f(entity.rotationPitch, entity.renderYawOffset));
        dir = dir.rotateYaw((float) (Math.PI / 5f) * (left ? 1f : -1f)).scale(0.75f);
        return dir.add(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);

    }

    public static Entity spawnEntityBehindEntity(EntityLivingBase p, ResourceLocation id) {

        BlockPos behind = getPositionBehindEntity(p, 2);
        EntityLiving e = (EntityLiving) EntityList.createEntityByIDFromName(id, p.getEntityWorld());

        e.setPosition(behind.getX(), p.posY, behind.getZ());

        if (e.getCanSpawnHere() && e.isNotColliding()) {
            p.getEntityWorld().spawnEntity(e);
            return e;
        } else {
            int y = p.getEntityWorld().getHeight(behind).getY();
            e.setPosition(behind.getX(), y, behind.getZ());
            if (e.getCanSpawnHere() && e.isNotColliding()) {
                p.getEntityWorld().spawnEntity(e);
                onInitialSpawn(e);
                return e;
            }
        }
        e.setDead();
        return null;
    }

    /**
     * Call {@link EntityLiving#onInitialSpawn(DifficultyInstance, IEntityLivingData)} if applicable
     */
    private static void onInitialSpawn(Entity e) {
        if (e instanceof EntityLiving) {
            ((EntityLiving) e).onInitialSpawn(e.getEntityWorld().getDifficultyForLocation(e.getPosition()), null);
        }
    }

    public static BlockPos getPositionBehindEntity(EntityLivingBase p, float distance) {
        float yaw = p.rotationYawHead;
        float cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        double x = p.posX + sinYaw * distance;
        double z = p.posZ + cosYaw * distance;
        return new BlockPos(x, p.posY, z);
    }

    /**
     * @param world           World
     * @param box             Area where the creature should spawn
     * @param e               Entity
     * @param maxTry          Max position tried
     * @param avoidedEntities Avoid being to close or seen by these entities. If no valid spawn location is found, this is ignored
     * @return Successful spawn
     */
    public static boolean spawnEntityInWorld(World world, AxisAlignedBB box, Entity e, int maxTry, @Nonnull List<EntityLivingBase> avoidedEntities) {
        if (!world.isAreaLoaded((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ, true)) {
            return false;
        }
        boolean flag = false;
        int i = 0;
        BlockPos backupPos=null; //
        while (!flag && i++ < maxTry) {
            BlockPos c = getRandomPosInBox(world, box); //TODO select a better location (more viable)
            if (world.isAreaLoaded(c, 5) && WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(e.getClass()), world, c)) {
                e.setPosition(c.getX(), c.getY() + 0.2, c.getZ());
                if (!(e instanceof EntityLiving) || (((EntityLiving) e).getCanSpawnHere() && ((EntityLiving) e).isNotColliding())) {
                    backupPos = c; //Store the location in case we do not find a better one
                    for (EntityLivingBase p : avoidedEntities) {

                        if (!(p.getDistanceSq(e) < 500 && p.canEntityBeSeen(e))) {
                            flag = true;
                        }
                    }
                }
            }
        }
        if (!flag && backupPos != null) {
            //If we did not find a "hidden" position, use the last valid position (if available)
            e.setPosition(backupPos.getX(), backupPos.getY() + 0.2, backupPos.getZ());
            flag=true;
        }

        if (flag) {
            world.spawnEntity(e);
            onInitialSpawn(e);
            return true;
        }
        return false;
    }

    /**
     * @param world           World
     * @param box             Area where the creature should spawn
     * @param id              ID of entity to be created
     * @param maxTry          Max position tried
     * @param avoidedEntities Avoid being to close or seen by these entities. If no valid spawn location is found, this is ignored
     * @return The spawned creature or null if not successful
     */
    @Nullable
    public static Entity spawnEntityInWorld(World world, AxisAlignedBB box, ResourceLocation id, int maxTry, @Nonnull List<EntityLivingBase> avoidedEntities) {
        Entity e = EntityList.createEntityByIDFromName(id, world);
        if (spawnEntityInWorld(world, box, e, maxTry,avoidedEntities)) {
            return e;
        } else {
            if (e != null) {
                e.setDead();
            }
            return null;
        }
    }

    /**
     * Teleports the entity
     *
     * @param entity
     * @param x
     * @param y
     * @param z
     * @param sound  If a teleport sound should be played
     * @return Wether the teleport was successful or not
     */
    public static boolean teleportTo(EntityLiving entity, double x, double y, double z, boolean sound) {
        double d3 = entity.posX;
        double d4 = entity.posY;
        double d5 = entity.posZ;
        entity.posX = x;
        entity.posY = y;
        entity.posZ = z;
        boolean flag = false;
        BlockPos blockPos = entity.getPosition();


        if (entity.getEntityWorld().isBlockLoaded(blockPos)) {
            boolean flag1 = false;

            while (!flag1 && blockPos.getY() > 0) {
                IBlockState blockState = entity.getEntityWorld().getBlockState(blockPos.down());
                if (blockState.getMaterial().blocksMovement())
                    flag1 = true;
                else {
                    --entity.posY;
                    blockPos = blockPos.down();
                }
            }

            if (flag1) {
                entity.setPosition(entity.posX, entity.posY, entity.posZ);

                if (entity.getEntityWorld().collidesWithAnyBlock(entity.getEntityBoundingBox()) && !entity.getEntityWorld().containsAnyLiquid(entity.getEntityBoundingBox()))
                    flag = true;
            }
        }

        if (!flag) {
            entity.setPosition(d3, d4, d5);
            return false;
        } else {
            short short1 = 128;

            for (int l = 0; l < short1; ++l) {
                double d6 = l / (short1 - 1.0D);
                float f = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
                float f1 = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
                float f2 = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (entity.posX - d3) * d6 + (entity.getRNG().nextDouble() - 0.5D) * entity.width * 2.0D;
                double d8 = d4 + (entity.posY - d4) * d6 + entity.getRNG().nextDouble() * entity.height;
                double d9 = d5 + (entity.posZ - d5) * d6 + (entity.getRNG().nextDouble() - 0.5D) * entity.width * 2.0D;
                entity.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
            }

            if (sound) {
                entity.getEntityWorld().playSound(d3, d4, d5, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 1F, 1F, false);
                entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1F, 1F);
            }

            return true;
        }
    }

    /**
     * Spawn multiple particles, with a small offset between
     */
    public static void spawnParticles(World world, EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int amount, float maxOffset, int... extra) {
        double x = xCoord;
        double y = yCoord;
        double z = zCoord;
        for (int i = 0; i < amount; i++) {
            world.spawnParticle(particleType, x, y, z, xSpeed, ySpeed, zSpeed, extra);
            Random ran = world.rand;
            x = xCoord + (ran.nextGaussian() * maxOffset);
            y = yCoord + (ran.nextGaussian() * maxOffset);
            z = zCoord + (ran.nextGaussian() * maxOffset);
        }
    }

    public static void spawnParticlesAroundEntity(EntityLivingBase e, EnumParticleTypes particleType, double maxDistance, int amount) {


        short short1 = (short) amount;
        for (int l = 0; l < short1; ++l) {
            double d6 = l / (short1 - 1.0D) - 0.5D;
            float f = (e.getRNG().nextFloat() - 0.5F) * 0.2F;
            float f1 = (e.getRNG().nextFloat() - 0.5F) * 0.2F;
            float f2 = (e.getRNG().nextFloat() - 0.5F) * 0.2F;
            double d7 = e.posX + (maxDistance) * d6 + (e.getRNG().nextDouble() - 0.5D) * e.width * 2.0D;
            double d8 = e.posY + (maxDistance / 2) * d6 + e.getRNG().nextDouble() * e.height;
            double d9 = e.posZ + (maxDistance) * d6 + (e.getRNG().nextDouble() - 0.5D) * e.width * 2.0D;
            e.getEntityWorld().spawnParticle(particleType, d7, d8, d9, f, f1, f2);
        }
    }

    /**
     * Sends the component message to all players except the given one.
     * Only use on server or common side
     *
     * @param player
     * @param message
     */
    public static void sendMessageToAllExcept(EntityPlayer player, ITextComponent message) {
        for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            if (!o.equals(player)) {
                ((EntityPlayer) o).sendMessage(message);
            }
        }
    }

    public static void sendMessageToAll(ITextComponent message) {
        sendMessageToAllExcept(null, message);
    }

    /**
     * Checks if the target entity is in the field of view (180 degree) of the base entity. Only works reliable for players (due to server-client sync)
     *
     * @param entity
     * @param target
     * @param alsoRaytrace Raytraces first
     * @return
     */
    public static boolean canReallySee(EntityLivingBase entity, EntityLivingBase target, boolean alsoRaytrace) {
        if (alsoRaytrace && !entity.canEntityBeSeen(target)) {
            return false;
        }
        Vec3d look1 = new Vec3d(-Math.sin(entity.rotationYawHead / 180 * Math.PI), 0, Math.cos(entity.rotationYawHead / 180 * Math.PI));
        Vec3d dist = new Vec3d(target.posX - entity.posX, 0, target.posZ - entity.posZ);
        //look1.yCoord = 0;
        look1 = look1.normalize();
        dist = dist.normalize();

        //Check if the vector is left or right of look1
        double alpha = Math.acos(look1.dotProduct(dist));
        return alpha < Math.PI / 1.8;

    }

    /**
     * Returns null, but makes it look like non null
     *
     * If this causes issues when compiling with IntelliJ check the following link and rebuild the entire project afterwards
     *
     * https://github.com/TeamLapen/Vampirism#intellij
     *
     * Make sure Settings -> Build, Execution, Deployment -> Compiler -> 'Add runtime assertions for not-null-annotated methods and parameters' is disabled (Unfortunately required)
     */
    @SuppressWarnings("ConstantConditions")
    public static @Nonnull
    <T> T getNull() {
        return null;
    }

    /**
     * Stores the given pos with in the tagcompound using base.
     * Can be retrieved again with {@link UtilLib#readPos(NBTTagCompound, String)}
     *
     * @param nbt
     * @param base
     * @param pos
     */
    public static void write(NBTTagCompound nbt, String base, BlockPos pos) {
        nbt.setInteger(base + "_x", pos.getX());
        nbt.setInteger(base + "_y", pos.getY());
        nbt.setInteger(base + "_z", pos.getZ());
    }

    /**
     * Reads a position written by {@link UtilLib#write(NBTTagCompound, String, BlockPos)}.
     *
     * @param nbt
     * @param base
     * @return
     */
    public static BlockPos readPos(NBTTagCompound nbt, String base) {
        return new BlockPos(nbt.getInteger(base + "_x"), nbt.getInteger(base + "_y"), nbt.getInteger(base + "_z"));
    }

    /**
     * Prefixes each of the strings with the given prefix
     *
     * @param prefix
     * @param strings
     * @return
     */
    public static String[] prefix(String prefix, String... strings) {
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = prefix + strings[i];
        }
        return result;
    }

    /**
     * Creates a predicate which checks for the given class
     *
     * @param clazz
     * @return
     */
    public static Predicate getPredicateForClass(final Class clazz) {
        return clazz::isInstance;
    }

    /**
     * Return a square bounding box around the given center with the given distance
     *
     * @param center
     * @param distance
     * @param fullY    If it should reach from y 0 to 265 or use the distance for y as well
     * @return
     */
    public static AxisAlignedBB createBB(BlockPos center, int distance, boolean fullY) {
        return new AxisAlignedBB(center.getX() - distance, fullY ? 0 : center.getY() - distance, center.getZ() - distance, center.getX() + distance, fullY ? 256 : center.getY() + distance, center.getZ() + distance);
    }

    public static boolean isNonNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) return false;
        }
        return true;
    }

    private static ChunkPos isBiomeAt(World world, int x, int z, List<Biome> biomes) {
        BlockPos pos = world.getBiomeProvider().findBiomePosition(x, z, 32, biomes, new Random());
        if (pos != null) {
            return new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        }
        return null;
    }

    /**
     * Search for a vampire biome by checking every second chunk starting at the player and moving in cicles to the outside
     *
     * @param world
     * @param center   Pos to start with
     * @param maxDist  Max radius
     * @param listener Will be notified about status updates. Can be null
     * @return
     */
    public static ChunkPos findNearBiome(World world, BlockPos center, int maxDist, List<Biome> biomes, ICommandSender listener) {
        long start = System.currentTimeMillis();
        maxDist = (maxDist / 20) * 20;//Round it
        long maxop = (((long) maxDist) * maxDist + maxDist) / 2;
        ChunkPos loc;
        for (int i = 0; i < maxDist; i += 4) {
            int cx = -i;
            for (int cz = -i; cz <= i; cz++) {
                if (cz % 4 != 0) continue;
                loc = isBiomeAt(world, center.getX() + (cx << 4), center.getZ() + (cz << 4), biomes);
                if (loc != null) {
                    VampLib.log.d("UtilLib", "Took %d ms to find a vampire biome %d %d", (int) (System.currentTimeMillis() - start), loc.x, loc.z);
                    return loc;
                }
                if (cz == i && cx < 0) {
                    cz = -i;
                    cx = i;
                }
            }
            int cz = -i;
            for (int cx2 = -i + 1; cx2 < i; cx2++) {
                if (cx2 % 4 != 0) continue;
                loc = isBiomeAt(world, center.getX() + (cx2 << 4), center.getZ() + (cz << 4), biomes);
                if (loc != null) {
                    VampLib.log.d("UtilLib", "Took %d ms to find a vampire biome %d %d", (int) (System.currentTimeMillis() - start), loc.x, loc.z);
                    return loc;
                }
                if (cx == i - 1 && cz < 0) {
                    cz = i;
                    cx = i - 1;
                }
            }
            if (listener != null && (i * 10) % maxDist == 0) {
                long op = (((long) i) * i + i) / 2;
                double perc = ((double) op / maxop) * 100;
                VampirismMod.log.i("UtilLib", "Search %s percent finished", (int) perc);
                //listener.addChatMessage(new TextComponentString(((int) perc) + "% finished")); //TODO maybe add back async
            }

        }
        VampLib.log.d("UtilLib", "Took %d ms to not find a vampire biome", (int) (System.currentTimeMillis() - start));
        return null;
    }

    public static boolean isPlayerOp(EntityPlayer player) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile()) != null;
    }

    public static boolean isSameInstanceAsServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance() != null;
    }

    public static String translate(String key) {
        if (I18n.canTranslate(key)) {
            return I18n.translateToLocal(key);
        } else {
            return I18n.translateToFallback(key);
        }
    }

    public static String translateFormatted(String key, Object... format) {
        String s = translate(key);
        try {
            return String.format(s, format);
        } catch (IllegalFormatException e) {
            VampLib.log.e("Translate", e, "Formatting Error for %s and arguments(%s)", key, format);
            return "Formatting Error: " + e.getMessage();
        }
    }

    public static String aiTaskListToStringDebug(EntityAITasks tasks) {
        Collection c = ReflectionHelper.getPrivateValue(EntityAITasks.class, tasks, "executingTaskEntries");
        Iterator var1 = c.iterator();
        if (!var1.hasNext()) {
            return "[]";
        } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');

            while (true) {
                Object var3 = var1.next();
                var2.append(var3 == c ? "(this Collection)" : ((EntityAITasks.EntityAITaskEntry) var3).action);
                if (!var1.hasNext()) {
                    return var2.append(']').toString();
                }

                var2.append(',').append(' ');
            }
        }
    }

    /**
     * Makes sure the given stack has a NBT Tag Compound
     *
     * @param stack
     * @return The stacks NBT Tag
     */
    public static NBTTagCompound checkNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    public static float[] getColorComponents(int color) {
        int i = (color & 16711680) >> 16;
        int j = (color & 65280) >> 8;
        int k = (color & 255);
        return new float[]{(float) i / 255.0F, (float) j / 255.0F, (float) k / 255.0F};
    }

    @Nonnull
    public static int[] bbToInt(@Nonnull AxisAlignedBB bb) {
        return new int[]{(int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ};
    }

    @Nonnull
    public static AxisAlignedBB intToBB(int[] array) {
        return new AxisAlignedBB(array[0], array[1], array[2], array[3], array[4], array[5]);

    }
}
