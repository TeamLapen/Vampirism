package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.world.attachments.ModDamageSources;
import de.teamlapen.vampirism.common.world.blocks.VelmorraAltarBlock;
import de.teamlapen.vampirism.common.world.blocks.VelmorraPortalBlock;
import de.teamlapen.vampirism.common.world.items.component.RitualKnifeType;
import de.teamlapen.vampirism.common.world.portal.VelmorraPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Holds the three dimension knives of the Velmorra portal ritual.
 * The block entity is the source of truth; the block state only mirrors the knives for the models.
 */
public class VelmorraAltarBlockEntity extends BlockEntity {

    private static final int CHANNEL_DURATION = 60;
    private static final int PORTAL_CHECK_INTERVAL = 20;
    /**
     * Health that remains after inserting a knife (one heart)
     */
    private static final float MIN_REMAINING_HEALTH = 2f;
    private static final String KEY_CHANNEL_TICKS = "channel_ticks";
    private static final String KEY_PORTAL_POS = "portal_pos";

    private final NonNullList<ItemStack> knives = NonNullList.withSize(RitualKnifeType.values().length, ItemStack.EMPTY);
    private int channelTicks;
    /**
     * A position inside the portal opened by this altar. Used to detect when the portal closes (e.g. after Dracula is defeated).
     */
    private @Nullable BlockPos portalPos;

    public VelmorraAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.VELMORRA_ALTAR.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VelmorraAltarBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.tick(serverLevel);
        }
    }

    private void tick(ServerLevel level) {
        if (this.channelTicks > 0) {
            if (!hasAllKnives()) {
                cancelChannel();
                return;
            }
            spawnChannelParticles(level);
            if (--this.channelTicks == 0) {
                finishChannel(level);
            }
            setChanged();
        } else if (level.getGameTime() % PORTAL_CHECK_INTERVAL == 0 && getBlockState().getValue(VelmorraAltarBlock.OPEN)) {
            if (!isPortalOpen(level)) {
                // portal was closed elsewhere, raise the knives again
                level.playSound(null, this.worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1f, 1f);
                updateState();
                setChanged();
            }
        }
    }

    /**
     * Inserts a dimension knife into the altar
     *
     * @return if the knife was inserted
     */
    public boolean insertKnife(Player player, ItemStack stack) {
        if (!(this.level instanceof ServerLevel serverLevel)) return false;
        RitualKnifeType type = stack.get(ModDataComponents.RITUAL_KNIFE_TYPE);
        if (type == null || !isEligible(player)) {
            return false;
        }
        if (!this.knives.get(type.getId()).isEmpty()) {
            player.sendOverlayMessage(Component.translatable("message.vampirism.velmorra_altar.already_inserted"));
            return false;
        }
        if (!player.isCreative()) {
            float health = player.getHealth();
            if (health <= MIN_REMAINING_HEALTH) {
                player.sendOverlayMessage(Component.translatable("message.vampirism.velmorra_altar.too_weak"));
                return false;
            }
            float damage = Math.min(ModConfig.balance().velmorraAltarKnifeDamage.get().floatValue(), health - MIN_REMAINING_HEALTH);
            if (damage > 0) {
                DamageHandler.hurtModded(serverLevel, player, ModDamageSources::ritualKnife, damage);
            }
        }
        this.knives.set(type.getId(), stack.consumeAndReturn(1, player));
        serverLevel.playSound(null, this.worldPosition, SoundEvents.TRIDENT_HIT, SoundSource.BLOCKS, 1f, 0.8f);
        updateState();
        setChanged();
        return true;
    }

    /**
     * Returns the last knife (anchoring, passage, seeking) to the player's hand. Knives cannot be removed while the portal is open.
     *
     * @return if a knife was removed
     */
    public boolean removeKnife(Player player) {
        if (!(this.level instanceof ServerLevel serverLevel) || !player.getMainHandItem().isEmpty()) return false;
        if (isPortalOpen(serverLevel)) {
            player.sendOverlayMessage(Component.translatable("message.vampirism.velmorra_altar.knives_locked"));
            return false;
        }
        for (int i = this.knives.size() - 1; i >= 0; i--) {
            ItemStack knife = this.knives.get(i);
            if (!knife.isEmpty()) {
                this.knives.set(i, ItemStack.EMPTY);
                player.setItemInHand(InteractionHand.MAIN_HAND, knife);
                cancelChannel();
                serverLevel.playSound(null, this.worldPosition, SoundEvents.TRIDENT_RETURN, SoundSource.BLOCKS, 1f, 1f);
                updateState();
                setChanged();
                return true;
            }
        }
        return false;
    }

    /**
     * Starts the ritual if all knives are inserted and a closed portal frame is nearby
     *
     * @return if the ritual was started
     */
    public boolean tryActivate(Player player) {
        if (!(this.level instanceof ServerLevel serverLevel) || this.channelTicks > 0 || !isEligible(player)) {
            return false;
        }
        List<RitualKnifeType> missing = Arrays.stream(RitualKnifeType.values()).filter(type -> this.knives.get(type.getId()).isEmpty()).toList();
        if (!missing.isEmpty()) {
            Component names = ComponentUtils.formatList(missing, type -> Component.translatable(type.getTranslationKey()).withStyle(type.getColor()));
            player.sendOverlayMessage(Component.translatable("message.vampirism.velmorra_altar.missing_knives", names));
            return false;
        }
        var shape = VelmorraPortalShape.findAnyShape(serverLevel, this.worldPosition);
        if (shape.isEmpty()) {
            player.sendOverlayMessage(Component.translatable("message.vampirism.velmorra_altar.no_portal"));
            return false;
        }
        if (shape.get().isActive()) {
            // portal is already open, bind it to this altar
            this.portalPos = shape.get().getInnerPos();
            updateState();
            setChanged();
            player.sendOverlayMessage(Component.translatable("message.vampirism.velmorra_altar.already_open"));
            return false;
        }
        this.channelTicks = CHANNEL_DURATION;
        serverLevel.playSound(null, this.worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1f, 0.7f);
        updateState();
        setChanged();
        return true;
    }

    private void finishChannel(ServerLevel level) {
        VelmorraPortalShape.findEmptyPortalShape(level, this.worldPosition).ifPresent(shape -> {
            shape.activate(level);
            this.portalPos = shape.getInnerPos();
            level.playSound(null, this.worldPosition, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1f, 1f);
        });
        updateState();
    }

    private void cancelChannel() {
        if (this.channelTicks > 0) {
            this.channelTicks = 0;
            updateState();
            setChanged();
        }
    }

    private void closePortal(ServerLevel level) {
        if (this.portalPos != null) {
            VelmorraPortalShape.removePortalBlocks(level, this.portalPos, 6);
            this.portalPos = null;
        } else {
            VelmorraPortalShape.findActivePortalShape(level, this.worldPosition).ifPresent(shape -> shape.deactivate(level));
        }
    }

    /**
     * Checks if the portal opened by this altar still exists. Forgets the portal if it was closed elsewhere (e.g. after Dracula was defeated).
     */
    private boolean isPortalOpen(ServerLevel level) {
        if (this.portalPos != null && level.isLoaded(this.portalPos) && !(level.getBlockState(this.portalPos).getBlock() instanceof VelmorraPortalBlock)) {
            this.portalPos = null;
        }
        return this.portalPos != null;
    }

    private void spawnChannelParticles(ServerLevel level) {
        double x = this.worldPosition.getX() + 0.5;
        double y = this.worldPosition.getY() + 1.2;
        double z = this.worldPosition.getZ() + 0.5;
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y, z, 4, 0.35, 0.25, 0.35, 0.02);
        if (this.channelTicks % 5 == 0) {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 2, 0.3, 0.1, 0.3, 0.01);
        }
    }

    private boolean hasAllKnives() {
        return this.knives.stream().noneMatch(ItemStack::isEmpty);
    }

    /**
     * Only lords of the highest level may use the altar
     */
    private boolean isEligible(Player player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        IPlayableFaction<?> faction = handler.getFaction() != null ? handler.getFaction().value() : null;
        if (faction == null || handler.getPlayerLord().map(ILordPlayer::getLordLevel).orElse(0) != faction.getHighestLordLevel()) {
            player.sendOverlayMessage(Component.translatable("message.vampirism.velmorra_altar.weak"));
            return false;
        }
        return true;
    }

    /**
     * Mirrors the stored knives, the channel and the portal state into the block state
     */
    private void updateState() {
        if (this.level == null) return;
        BlockState state = getBlockState();
        boolean anyKnife = false;
        for (RitualKnifeType type : RitualKnifeType.values()) {
            boolean present = !this.knives.get(type.getId()).isEmpty();
            anyKnife |= present;
            state = state.setValue(VelmorraAltarBlock.knifeProperty(type), present);
        }
        // once blood was offered the altar stays bloody
        state = state.setValue(VelmorraAltarBlock.HAS_BLOOD, anyKnife || state.getValue(VelmorraAltarBlock.HAS_BLOOD))
                .setValue(VelmorraAltarBlock.CHANNELING, this.channelTicks > 0)
                .setValue(VelmorraAltarBlock.OPEN, this.portalPos != null && this.channelTicks <= 0);
        if (state != getBlockState()) {
            this.level.setBlock(this.worldPosition, state, Block.UPDATE_ALL);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level instanceof ServerLevel serverLevel) {
            Containers.dropContents(serverLevel, pos, this.knives);
            closePortal(serverLevel);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.knives.clear();
        ContainerHelper.loadAllItems(input, this.knives);
        this.channelTicks = input.getIntOr(KEY_CHANNEL_TICKS, 0);
        this.portalPos = input.read(KEY_PORTAL_POS, BlockPos.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.knives);
        output.putInt(KEY_CHANNEL_TICKS, this.channelTicks);
        if (this.portalPos != null) {
            output.store(KEY_PORTAL_POS, BlockPos.CODEC, this.portalPos);
        }
    }
}
