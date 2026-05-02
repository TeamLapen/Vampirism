package de.teamlapen.vampirism.client.renderer;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModBlockTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = REFERENCE.MODID)
public class OutlineRenderer {

    private static final int HIGHLIGHT_RADIUS = 40;
    private static final int UPDATE_INTERVAL = 10; // Updates every 10 ticks
    private static final double UPDATE_DISTANCE_SQR = 16; // The squared distance player walked that forces the cache to update

    private static final Set<BlockPos> cachedHighlightPositions = new HashSet<>();
    private static int updateTimer = 0;
    private static Vec3 lastCheckPosition = Vec3.ZERO;
    private static boolean wasHoldingFinder = false;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent.AfterOpaqueFeatures event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ClientLevel level = mc.level;

        if (player == null || level == null || mc.options.hideGui) {
            return;
        }

        boolean isHoldingDevice = isHoldingFinder(player);

        if (!isHoldingDevice) {
            if (wasHoldingFinder) {
                cachedHighlightPositions.clear();
                updateTimer = 0;
            }
            wasHoldingFinder = false;
            return;
        }

        wasHoldingFinder = true;
        Vec3 currentPos = player.getEyePosition(1.0F);

        updateTimer++;
        boolean movedSignificantly = lastCheckPosition.distanceToSqr(currentPos) > UPDATE_DISTANCE_SQR;

        if (updateTimer >= UPDATE_INTERVAL || movedSignificantly) {
            updateHighlightCache(level, currentPos);
            updateTimer = 0;
            lastCheckPosition = currentPos;
        }

        renderCachedHighlights(level, currentPos);
    }

    private static void updateHighlightCache(Level level, Vec3 playerPos) {
        cachedHighlightPositions.clear();

        BlockPos centerPos = BlockPos.containing(playerPos);
        int radius = HIGHLIGHT_RADIUS;

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        double radiusSq = radius * radius;

        for (int x = centerPos.getX() - radius; x <= centerPos.getX() + radius; x++) {
            for (int y = centerPos.getY() - radius; y <= centerPos.getY() + radius; y++) {
                for (int z = centerPos.getZ() - radius; z <= centerPos.getZ() + radius; z++) {
                    mutablePos.set(x, y, z);

                    double dx = x - playerPos.x;
                    double dy = y - playerPos.y;
                    double dz = z - playerPos.z;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    if (distSq > radiusSq) {
                        continue;
                    }

                    BlockState state = level.getBlockState(mutablePos);

                    if (state.is(ModBlockTags.GARLIC_FINDER_HIGHLIGHTED)) {
                        cachedHighlightPositions.add(mutablePos.immutable());
                    }
                }
            }
        }
    }

    private static void renderCachedHighlights(Level level, Vec3 playerPos) {
        if (cachedHighlightPositions.isEmpty()) {
            return;
        }

        GizmoStyle style = GizmoStyle.fill(ARGB.color(1.0F, ModConfig.helper().getGarlicFinderAuraColor()));

        for (BlockPos pos : cachedHighlightPositions) {
            double distance = playerPos.distanceToSqr(pos.getCenter());
            if (distance <= 6) continue;

            BlockState state = level.getBlockState(pos);
            VoxelShape shape = state.getShape(level, pos);

            if (!shape.isEmpty()) {
                shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    AABB box = new AABB(
                            pos.getX() + minX,
                            pos.getY() + minY,
                            pos.getZ() + minZ,
                            pos.getX() + maxX,
                            pos.getY() + maxY,
                            pos.getZ() + maxZ
                    );

                    box = box.inflate(0.002);

                    Gizmos.cuboid(box, style, true).setAlwaysOnTop();
                });
            }
        }
    }

    private static boolean isHoldingFinder(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (isFinder(mainHand)) {
            return true;
        }

        ItemStack offHand = player.getOffhandItem();
        if (isFinder(offHand)) {
            return true;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isFinder(stack)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isFinder(ItemStack stack) {
        return stack.is(ModItems.GARLIC_FINDER);
    }
}