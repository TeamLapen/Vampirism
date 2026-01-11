package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.blockentity.BatCageBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Optional;
import java.util.function.Consumer;

public class BatCageItem extends BlockItem implements IEntityInteractable {

    public BatCageItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        if (stack.has(ModDataComponents.HELD_ENTITY) && (player == null || player.isShiftKeyDown())) {
            if (releaseEntity(stack, context.getLevel(), context.getClickLocation().add(0.0, 0.3, 0.0), context.getHorizontalDirection(), player)) {
                return InteractionResult.SUCCESS_SERVER;
            }
        } else {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    // TODO: Fix this. The entity just doesn't get released
    public boolean releaseEntity(ItemStack stack, Level level, Vec3 pos, Direction direction, @Nullable Player player) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return false;

        CompoundTag entityTag = stack.get(ModDataComponents.HELD_ENTITY);
        if (entityTag == null) return false;

        ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), entityTag);
        Optional<EntityType<?>> typeOpt = EntityType.by(valueInput);

        if (typeOpt.isPresent()) {
            Mob mob = (Mob) typeOpt.get().create(serverLevel, EntityType.createDefaultStackConfig(level, stack, null), new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z()), EntitySpawnReason.BUCKET, true, false);

            if (mob != null) {
                Quaternionf rotation = direction.getRotation();
                mob.setXRot(rotation.x());
                mob.setYRot(rotation.y());

                serverLevel.addFreshEntityWithPassengers(mob);
                mob.playAmbientSound();

                if (player == null || !player.getAbilities().instabuild) {
                    stack.remove(ModDataComponents.HELD_ENTITY);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public InteractionResult onEntityInteract(ItemStack stack, Entity target, Player player, Level level, InteractionHand hand) {
        if (!level.isClientSide() && BatCageBlockEntity.canContainEntity(target) && !stack.has(ModDataComponents.HELD_ENTITY)) {
            ItemStack capturedStack = stack.copyWithCount(1);
            if (BatCageItem.captureEntity(target, capturedStack)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                player.getInventory().placeItemBackInInventory(capturedStack);
            }

            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }

    public static boolean captureEntity(Entity entity, ItemStack stack) {
        Level level = entity.level();
        if (level.isClientSide() || stack.has(ModDataComponents.HELD_ENTITY)) {
            return false;
        }
        if (entity instanceof Player || !entity.isAlive()) {
            return false;
        }

        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        if (!entity.saveAsPassenger(output)) {
            return false;
        }

        CompoundTag tag = BatCageBlockEntity.sanitizeEntityTag(output.buildResult());
        stack.set(ModDataComponents.HELD_ENTITY, tag);
        entity.discard();
        return true;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.has(ModDataComponents.HELD_ENTITY) ? 1 : super.getMaxStackSize(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        CompoundTag entityTag =  stack.get(ModDataComponents.HELD_ENTITY);
        if (entityTag != null) {
            Component name = Component.translatable("text.vampirism.unknown");
            if (entityTag.contains("CustomName")) {
                name = Component.literal(entityTag.getString("CustomName").orElse(""));
            } else if (entityTag.contains("id")) {
                Optional<String> entityId = entityTag.getString("id");
                if (entityId.isPresent()) {
                    Identifier id = Identifier.tryParse(entityId.get());
                    if (id != null) {
                        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(id);
                        name = type.getDescription();
                    }
                }
            }
            tooltipAdder.accept(Component.translatable("tooltip.vampirism.bat_cage.contains_bat", name.copy().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
