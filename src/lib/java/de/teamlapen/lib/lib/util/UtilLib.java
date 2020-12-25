package de.teamlapen.lib.lib.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General Utility Class
 */
public class UtilLib {

    private final static Logger LOGGER = LogManager.getLogger();

    public static String entityToString(Entity e) {
        if (e == null) {
            return "Entity is null";
        }
        return e.toString();
    }

    public static boolean doesBlockHaveSolidTopSurface(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).isSolidSide(worldIn, pos, Direction.UP);
    }


    @OnlyIn(Dist.CLIENT)
    public static void drawTexturedModalRect(Matrix4f matrix, float zLevel, int x, int y, int textureX, int textureY, int width, int height, int texWidth, int texHeight) {
        float f = 1 / (float) texWidth;
        float f1 = 1 / (float) texHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(matrix, x, y + height, zLevel).tex((float) (textureX) * f, (float) (textureY + height) * f1).endVertex();
        vertexbuffer.pos(matrix, x + width, y + height, zLevel).tex((float) (textureX + width) * f, (float) (textureY + height) * f1).endVertex();
        vertexbuffer.pos(matrix, x + width, y, zLevel).tex((float) (textureX + width) * f, (float) (textureY) * f1).endVertex();
        vertexbuffer.pos(matrix, x, y, zLevel).tex((float) (textureX) * f, (float) (textureY) * f1).endVertex();
        tessellator.draw();
    }

    /**
     * Gets players looking spot (blocks only).
     *
     * @param player
     * @param restriction Max distance or 0 for player reach distance or -1 for not restricted
     * @return The position as a MovingObjectPosition, null if not existent cf: https ://github.com/bspkrs/bspkrsCore/blob/master/src/main/java/bspkrs /util/CommonUtils.java
     */
    public static RayTraceResult getPlayerLookingSpot(PlayerEntity player, double restriction) {
        float scale = 1.0F;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scale;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scale;
        double x = player.prevPosX + (player.getPosX() - player.prevPosX) * scale;
        double y = player.prevPosY + (player.getPosY() - player.prevPosY) * scale + 1.62D;
        double z = player.prevPosZ + (player.getPosZ() - player.prevPosZ) * scale;
        Vector3d vector1 = new Vector3d(x, y, z);
        float cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float cosPitch = -MathHelper.cos(-pitch * 0.017453292F);
        float sinPitch = MathHelper.sin(-pitch * 0.017453292F);
        float pitchAdjustedSinYaw = sinYaw * cosPitch;
        float pitchAdjustedCosYaw = cosYaw * cosPitch;
        double distance = 500D;
        if (restriction == 0 && player instanceof ServerPlayerEntity) {
            distance = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() - 0.5f;
        } else if (restriction > 0) {
            distance = restriction;
        }

        Vector3d vector2 = vector1.add(pitchAdjustedSinYaw * distance, sinPitch * distance, pitchAdjustedCosYaw * distance);
        return player.getEntityWorld().rayTraceBlocks(new RayTraceContext(vector1, vector2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
    }

    public static BlockPos getRandomPosInBox(World w, AxisAlignedBB box) {
        int x = (int) box.minX + w.rand.nextInt((int) (box.maxX - box.minX) + 1);
        int z = (int) box.minZ + w.rand.nextInt((int) (box.maxZ - box.minZ) + 1);
        int y = w.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z) + 5;
        BlockPos.Mutable pos = new BlockPos.Mutable(x, y, z);
        while (y > box.minY && !w.getBlockState(pos).isNormalCube(w, pos)) {
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

        for (PlayerEntity entityplayer : world.getPlayers()) {
            if (!entityplayer.isSpectator()) {
                int x = MathHelper.floor(entityplayer.getPosX() / 16.0D);
                int z = MathHelper.floor(entityplayer.getPosZ() / 16.0D);

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
    Vector3d getItemPosition(LivingEntity entity, boolean mainHand) {
        boolean left = (mainHand ? entity.getPrimaryHand() : entity.getPrimaryHand().opposite()) == HandSide.LEFT;
        boolean firstPerson = entity instanceof PlayerEntity && ((PlayerEntity) entity).isUser() && Minecraft.getInstance().gameSettings.getPointOfView().func_243192_a();
        Vector3d dir = firstPerson ? entity.getForward() : Vector3d.fromPitchYaw(new Vector2f(entity.rotationPitch, entity.renderYawOffset));
        dir = dir.rotateYaw((float) (Math.PI / 5f) * (left ? 1f : -1f)).scale(0.75f);
        return dir.add(entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ());

    }

    public static <T extends MobEntity> Entity spawnEntityBehindEntity(LivingEntity entity, EntityType<T> toSpawn, SpawnReason reason) {

        BlockPos behind = getPositionBehindEntity(entity, 2);
        MobEntity e = toSpawn.create(entity.getEntityWorld());
        World world = entity.getEntityWorld();
        e.setPosition(behind.getX(), entity.getPosY(), behind.getZ());

        if (e.canSpawn(world, reason) && e.isNotColliding(world)) {
            entity.getEntityWorld().addEntity(e);
            return e;
        } else {
            int y = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, behind).getY();
            e.setPosition(behind.getX(), y, behind.getZ());
            if (e.canSpawn(world, reason) && e.isNotColliding(world)) {
                world.addEntity(e);
                if (world instanceof ServerWorld) onInitialSpawn((ServerWorld) world, e, reason);
                return e;
            }
        }
        e.remove();
        return null;
    }

    /**
     * Call {@link MobEntity#onInitialSpawn(net.minecraft.world.IServerWorld, DifficultyInstance, SpawnReason, ILivingEntityData, CompoundNBT)} if applicable
     */
    private static void onInitialSpawn(ServerWorld world, Entity e, SpawnReason reason) {
        if (e instanceof MobEntity) {
            ((MobEntity) e).onInitialSpawn(world, e.getEntityWorld().getDifficultyForLocation(e.getPosition()), reason, null, null);
        }
    }

    public static BlockPos getPositionBehindEntity(LivingEntity p, float distance) {
        float yaw = p.rotationYawHead;
        float cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        double x = p.getPosX() + sinYaw * distance;
        double z = p.getPosZ() + cosYaw * distance;
        return new BlockPos(x, p.getPosY(), z);
    }

    /**
     * @param world           World
     * @param box             Area where the creature should spawn
     * @param e               Entity that has a EntityType<? extends EntityLiving>
     * @param maxTry          Max position tried
     * @param avoidedEntities Avoid being to close or seen by these entities. If no valid spawn location is found, this is ignored
     * @param reason          Spawn reason
     * @return Successful spawn
     */
    public static boolean spawnEntityInWorld(ServerWorld world, AxisAlignedBB box, Entity e, int maxTry, @Nonnull List<? extends LivingEntity> avoidedEntities, SpawnReason reason) {
        if (!world.isAreaLoaded((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ)) {
            return false;
        }
        boolean flag = false;
        int i = 0;
        BlockPos backupPos = null; //
        while (!flag && i++ < maxTry) {
            BlockPos c = getRandomPosInBox(world, box); //TODO select a better location (more viable)
            if (world.isAreaLoaded(c, 5) && WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementType(e.getType()), world, c, e.getType())) {//i see no other way
                e.setPosition(c.getX(), c.getY() + 0.2, c.getZ());
                if (!(e instanceof MobEntity) || (((MobEntity) e).canSpawn(world, reason) && ((MobEntity) e).isNotColliding(e.getEntityWorld()))) {
                    backupPos = c; //Store the location in case we do not find a better one
                    for (LivingEntity p : avoidedEntities) {

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
            flag = true;
        }

        if (flag) {
            world.addEntity(e);
            onInitialSpawn(world, e, reason);
            return true;
        }
        return false;
    }

    /**
     * @param world           World
     * @param box             Area where the creature should spawn
     * @param entityType      EntityType of entity to be created
     * @param maxTry          Max position tried
     * @param avoidedEntities Avoid being to close or seen by these entities. If no valid spawn location is found, this is ignored
     * @param reason          Spawn reason
     * @return The spawned creature or null if not successful
     */
    @Nullable
    public static Entity spawnEntityInWorld(ServerWorld world, AxisAlignedBB box, EntityType entityType, int maxTry, @Nonnull List<? extends LivingEntity> avoidedEntities, SpawnReason reason) {
        Entity e = entityType.create(world);
        if (spawnEntityInWorld(world, box, e, maxTry, avoidedEntities, reason)) {
            return e;
        } else {
            if (e != null) {
                e.remove();
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
    public static boolean teleportTo(MobEntity entity, double x, double y, double z, boolean sound) {
        double d3 = entity.getPosX();
        double d4 = entity.getPosY();
        double d5 = entity.getPosZ();
        entity.setRawPosition(x, y, z);
        boolean flag = false;
        BlockPos blockPos = entity.getPosition(); //getPos
        double ty = y;


        if (entity.getEntityWorld().isBlockLoaded(blockPos)) {
            boolean flag1 = false;

            while (!flag1 && blockPos.getY() > 0) {
                BlockState blockState = entity.getEntityWorld().getBlockState(blockPos.down());
                if (blockState.getMaterial().blocksMovement())
                    flag1 = true;
                else {
                    entity.setRawPosition(x, --ty, z);
                    blockPos = blockPos.down();
                }
            }

            if (flag1) {
                entity.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());

                if (entity.getEntityWorld().hasNoCollisions(entity) && !entity.getEntityWorld().containsAnyLiquid(entity.getBoundingBox()))
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
                double d7 = d3 + (entity.getPosX() - d3) * d6 + (entity.getRNG().nextDouble() - 0.5D) * entity.getWidth() * 2.0D;
                double d8 = d4 + (entity.getPosY() - d4) * d6 + entity.getRNG().nextDouble() * entity.getHeight();
                double d9 = d5 + (entity.getPosZ() - d5) * d6 + (entity.getRNG().nextDouble() - 0.5D) * entity.getWidth() * 2.0D;
                entity.getEntityWorld().addParticle(ParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
            }

            if (sound) {
                entity.getEntityWorld().playSound(d3, d4, d5, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1F, 1F, false);
                entity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
            }

            return true;
        }
    }

    /**
     * Spawn multiple particles, with a small offset between
     */
    public static void spawnParticles(World world, IParticleData particle, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int amount, float maxOffset) {
        double x = xCoord;
        double y = yCoord;
        double z = zCoord;
        for (int i = 0; i < amount; i++) {
            world.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
            Random ran = world.rand;
            x = xCoord + (ran.nextGaussian() * maxOffset);
            y = yCoord + (ran.nextGaussian() * maxOffset);
            z = zCoord + (ran.nextGaussian() * maxOffset);
        }
    }

    public static void spawnParticlesAroundEntity(LivingEntity e, IParticleData particle, double maxDistance, int amount) {


        short short1 = (short) amount;
        for (int l = 0; l < short1; ++l) {
            double d6 = l / (short1 - 1.0D) - 0.5D;
            float f = (e.getRNG().nextFloat() - 0.5F) * 0.2F;
            float f1 = (e.getRNG().nextFloat() - 0.5F) * 0.2F;
            float f2 = (e.getRNG().nextFloat() - 0.5F) * 0.2F;
            double d7 = e.getPosX() + (maxDistance) * d6 + (e.getRNG().nextDouble() - 0.5D) * e.getWidth() * 2.0D;
            double d8 = e.getPosY() + (maxDistance / 2) * d6 + e.getRNG().nextDouble() * e.getHealth();
            double d9 = e.getPosZ() + (maxDistance) * d6 + (e.getRNG().nextDouble() - 0.5D) * e.getWidth() * 2.0D;
            e.getEntityWorld().addParticle(particle, d7, d8, d9, f, f1, f2);
        }
    }

    /**
     * Sends the component message to all players except the given one.
     * Only use on server or common side
     *
     * @param player
     * @param message
     */
    public static void sendMessageToAllExcept(PlayerEntity player, ITextComponent message) {
        for (Object o : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (!o.equals(player)) {
                ((PlayerEntity) o).sendMessage(message, Util.DUMMY_UUID);
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
    public static boolean canReallySee(LivingEntity entity, LivingEntity target, boolean alsoRaytrace) {
        if (alsoRaytrace && !entity.canEntityBeSeen(target)) {
            return false;
        }
        Vector3d look1 = new Vector3d(-Math.sin(entity.rotationYawHead / 180 * Math.PI), 0, Math.cos(entity.rotationYawHead / 180 * Math.PI));
        Vector3d dist = new Vector3d(target.getPosX() - entity.getPosX(), 0, target.getPosZ() - entity.getPosZ());
        //look1.yCoord = 0;
        look1 = look1.normalize();
        dist = dist.normalize();

        //Check if the vector is left or right of look1
        double alpha = Math.acos(look1.dotProduct(dist));
        return alpha < Math.PI / 1.8;

    }

    /**
     * Returns null, but makes it look like non null
     * <p>
     * If this causes issues when compiling with IntelliJ check the following link and rebuild the entire project afterwards
     * <p>
     * https://github.com/TeamLapen/Vampirism#intellij
     * <p>
     * Make sure Settings -> Build, Execution, Deployment -> Compiler -> 'Add runtime assertions for not-null-annotated methods and parameters' is disabled (Unfortunately required)
     */
    @SuppressWarnings("ConstantConditions")
    public static @Nonnull
    <T> T getNull() {
        return null;
    }

    /**
     * Stores the given pos with in the tagcompound using base.
     * Can be retrieved again with {@link UtilLib#readPos(CompoundNBT, String)}
     *
     * @param nbt
     * @param base
     * @param pos
     */
    public static void write(CompoundNBT nbt, String base, BlockPos pos) {
        nbt.putInt(base + "_x", pos.getX());
        nbt.putInt(base + "_y", pos.getY());
        nbt.putInt(base + "_z", pos.getZ());
    }

    /**
     * Reads a position written by {@link UtilLib#write(CompoundNBT, String, BlockPos)}.
     *
     * @param nbt
     * @param base
     * @return
     */
    public static BlockPos readPos(CompoundNBT nbt, String base) {
        return new BlockPos(nbt.getInt(base + "_x"), nbt.getInt(base + "_y"), nbt.getInt(base + "_z"));
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
     * @param fullY    If it should reach from yDisplay 0 to 265 or use the distance for yDisplay as well
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

    private static ChunkPos isBiomeAt(ServerWorld world, int x, int z, List<Biome> biomes) {
        BlockPos pos = (world.getChunkProvider()).getChunkGenerator().getBiomeProvider().findBiomePosition(x, world.getSeaLevel(), z, 32, b -> biomes.contains(b), new Random());//findBiomePosition
        if (pos != null) {
            return new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        }
        return null;
    }

    /**
     * Search for a vampire biome by checking every second chunk starting at the player and moving in cicles to the outside
     *
     * @param world
     * @param center  Pos to start with
     * @param maxDist Max radius
     * @return
     */
    public static ChunkPos findNearBiome(ServerWorld world, BlockPos center, int maxDist, List<Biome> biomes) {
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
                    LOGGER.trace("Took {} ms to find a vampire biome {} {}", (int) (System.currentTimeMillis() - start), loc.x, loc.z);
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
                    LOGGER.trace("Took {} ms to find a vampire biome {} {}", (int) (System.currentTimeMillis() - start), loc.x, loc.z);
                    return loc;
                }
                if (cx == i - 1 && cz < 0) {
                    cz = i;
                    cx = i - 1;
                }
            }
            if ((i * 10) % maxDist == 0) {
                long op = (((long) i) * i + i) / 2;
                double perc = ((double) op / maxop) * 100;
                LOGGER.trace("Search {} percent finished", (int) perc);
            }

        }
        LOGGER.trace("Took {} ms to not find a vampire biome", (int) (System.currentTimeMillis() - start));
        return null;
    }

    public static boolean isPlayerOp(PlayerEntity player) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile()) != null;
    }

    public static boolean isSameInstanceAsServer() {
        return ServerLifecycleHooks.getCurrentServer() != null;
    }

    private final static Pattern oldFormatPattern = Pattern.compile("%[sd]");

    public static String translate(String key, Object... format) {
        String pattern = ForgeI18n.getPattern(key);
        if (format.length == 0) {
            return pattern;
        } else {
            try {
                pattern = replaceDeprecatedFormatter(pattern);
                return ForgeI18n.parseFormat(pattern, Arrays.stream(format).map(o -> o instanceof ITextComponent ? ((ITextComponent) o).getString() : o).toArray());
            } catch (IllegalArgumentException e) {
                LOGGER.error("Illegal format found `{}`", pattern);
                return pattern;
            }
        }

    }

    private static String replaceDeprecatedFormatter(String text) {
        StringBuffer sb = null;
        Matcher m = oldFormatPattern.matcher(text);
        int i = 0;
        while (m.find()) {
            String t = m.group();
            t = "{" + i++ + "}";

            if (sb == null) {
                sb = new StringBuffer(text.length());
            }
            m.appendReplacement(sb, t);
        }
        if (sb == null) {
            return text;
        } else {
            m.appendTail(sb);
            return sb.toString();
        }
    }


    /**
     * Rotate voxel. Credits to JTK222|Lukas
     * Cache the result
     *
     * @param shape
     * @param rotation
     * @return
     */
    public static VoxelShape rotateShape(VoxelShape shape, RotationAmount rotation) {
        Set<VoxelShape> rotatedShapes = new HashSet<VoxelShape>();

        shape.forEachBox((x1, y1, z1, x2, y2, z2) -> {
            x1 = (x1 * 16) - 8;
            x2 = (x2 * 16) - 8;
            z1 = (z1 * 16) - 8;
            z2 = (z2 * 16) - 8;

            if (rotation == RotationAmount.NINETY)
                rotatedShapes.add(Block.makeCuboidShape(8 - z1, y1 * 16, 8 + x1, 8 - z2, y2 * 16, 8 + x2));
            else if (rotation == RotationAmount.HUNDRED_EIGHTY)
                rotatedShapes.add(Block.makeCuboidShape(8 - x1, y1 * 16, 8 - z1, 8 - x2, y2 * 16, 8 - z2));
            else if (rotation == RotationAmount.TWO_HUNDRED_SEVENTY)
                rotatedShapes.add(Block.makeCuboidShape(8 + z1, y1 * 16, 8 - x1, 8 + z2, y2 * 16, 8 - x2));
        });

        return rotatedShapes.stream().reduce((v1, v2) -> {
            return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
        }).get();
    }

    public static String aiTaskListToStringDebug(GoalSelector tasks) {
        Collection c = ObfuscationReflectionHelper.getPrivateValue(GoalSelector.class, tasks, "executingTaskEntries");
        Iterator var1 = c.iterator();
        if (!var1.hasNext()) {
            return "[]";
        } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');

            while (true) {
                Object var3 = var1.next();
                var2.append(var3 == c ? "(this Collection)" : (((PrioritizedGoal) var3).getGoal()));// Test
                if (!var1.hasNext()) {
                    return var2.append(']').toString();
                }

                var2.append(',').append(' ');
            }
        }
    }

    public static boolean isInsideStructure(Entity entity, Structure<?> s) {
        StructureStart<?> start = getStructureStartAt(entity, s);
        return start != null && start.isValid();
    }

    public static boolean isInsideStructure(World w, BlockPos p, Structure<?> s) {
        StructureStart<?> start = getStructureStartAt(w, p, s);
        return start != null && start.isValid();
    }

    @Nullable
    public static StructureStart<?> getStructureStartAt(Entity entity, Structure<?> s) {
        return getStructureStartAt(entity.getEntityWorld(), entity.getPosition(), s);
    }

    @Nullable
    public static StructureStart<?> getStructureStartAt(World w, BlockPos pos, Structure<?> s) {
        if (w instanceof ServerWorld) {
            return getStructureStartAt((ServerWorld)w, pos, s);
        }
        return null;
    }

    @Nonnull
    public static StructureStart<?> getStructureStartAt(ServerWorld w, BlockPos pos, Structure<?> s) {
        return ( w).func_241112_a_()/*getStructureManager*/.func_235010_a_(pos, false, s);
    }

    /**
     * Makes sure the given stack has a NBT Tag Compound
     *
     * @param stack
     * @return The stacks NBT Tag
     */
    public static CompoundNBT checkNBT(ItemStack stack) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        return stack.getTag();
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
    public static int[] mbToInt(@Nonnull MutableBoundingBox bb) {
        return new int[]{bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ};
    }

    @Nonnull
    public static AxisAlignedBB intToBB(@Nonnull int[] array) {
        return new AxisAlignedBB(array[0], array[1], array[2], array[3], array[4], array[5]);
    }

    @Nonnull
    public static MutableBoundingBox intToMB(@Nonnull int[] array) {
        return new MutableBoundingBox(array[0], array[1], array[2], array[3], array[4], array[5]);
    }

    @Nonnull
    public static MutableBoundingBox AABBtoMB(@Nonnull AxisAlignedBB bb) {
        return new MutableBoundingBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);
    }

    @Nonnull
    public static AxisAlignedBB MBtoAABB(@Nonnull MutableBoundingBox bb) {
        return new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }

    /**
     * Draws a TextComponent split over multiple lines
     *
     * @return The height of the rendered text
     */
    public static int renderMultiLine(FontRenderer fontRenderer, MatrixStack stack, ITextComponent text, int textLength, int x, int y, int color) {
        int d = 0;
        for (IReorderingProcessor ireorderingprocessor : fontRenderer.trimStringToWidth(text, textLength)) {
            fontRenderer.func_238422_b_(stack, ireorderingprocessor, x, y + d, color);
            d += fontRenderer.FONT_HEIGHT;
        }
        return d;
    }

    public enum RotationAmount {
        NINETY,
        HUNDRED_EIGHTY,
        TWO_HUNDRED_SEVENTY
    }

    @Nullable
    public static DyeColor getColorForItem(Item item) {
        if (!Tags.Items.DYES.contains(item)) return null;
        Optional<DyeColor> color = Arrays.stream(DyeColor.values()).filter(dye -> dye.getTag().contains(item)).findFirst();
        if (color.isPresent()) return color.get();
        LOGGER.warn("Could not determine color of {}", item.getRegistryName());
        return null;
    }

    public static boolean isValidResourceLocation(String loc) {
        return ResourceLocation.tryCreate(loc) != null;
    }
}
