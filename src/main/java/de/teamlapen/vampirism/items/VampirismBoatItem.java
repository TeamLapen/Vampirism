package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.entity.VampirismBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public class VampirismBoatItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    private  final BoatType type;

    public VampirismBoatItem(BoatType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public BoatType getType() {
        return type;
    }

    /**
     * from {@link net.minecraft.world.item.BoatItem#use(net.minecraft.world.level.Level, net.minecraft.world.entity.player.Player, net.minecraft.world.InteractionHand)}
     */
    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level p_77659_1_, Player p_77659_2_, InteractionHand p_77659_3_) {
        ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
        HitResult hitresult = getPlayerPOVHitResult(p_77659_1_, p_77659_2_, ClipContext.Fluid.ANY);
        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Vec3 vec3 = p_77659_2_.getViewVector(1.0F);
            double d0 = 5.0D;
            List<Entity> list = p_77659_1_.getEntities(p_77659_2_, p_77659_2_.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
            if (!list.isEmpty()) {
                Vec3 vec31 = p_77659_2_.getEyePosition();

                for(Entity entity : list) {
                    AABB aabb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
                    if (aabb.contains(vec31)) {
                        return InteractionResultHolder.pass(itemstack);
                    }
                }
            }

            if (hitresult.getType() == HitResult.Type.BLOCK) {
                VampirismBoatEntity boat = new VampirismBoatEntity(p_77659_1_, hitresult.getLocation().x, hitresult.getLocation().y, hitresult.getLocation().z);
                boat.setType(this.type);
                boat.setYRot(p_77659_2_.getYRot());
                if (!p_77659_1_.noCollision(boat, boat.getBoundingBox())) {
                    return InteractionResultHolder.fail(itemstack);
                } else {
                    if (!p_77659_1_.isClientSide) {
                        p_77659_1_.addFreshEntity(boat);
                        p_77659_1_.gameEvent(p_77659_2_, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                        if (!p_77659_2_.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                    }

                    p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(itemstack, p_77659_1_.isClientSide());
                }
            } else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }

    public enum BoatType {
        DARK_SPRUCE, CURSED_SPRUCE;

        public static BoatType byId(int id) {
            if (id >= values().length) {
                return DARK_SPRUCE;
            } else {
                return values()[id];
            }
        }
    }
}
