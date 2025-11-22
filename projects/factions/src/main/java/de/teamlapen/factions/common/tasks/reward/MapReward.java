package de.teamlapen.factions.common.tasks.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.tasks.ITaskRewardInstance;
import de.teamlapen.factions.common.core.FactionTasks;
import de.teamlapen.factions.common.util.MapUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.jetbrains.annotations.Nullable;

public class MapReward extends ItemReward {

    public static final MapCodec<MapReward> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    TagKey.codec(Registries.STRUCTURE).fieldOf("destination").forGetter(i -> i.destination),
                    Codec.STRING.fieldOf("displayName").forGetter(i -> i.displayName),
                    MapDecorationType.CODEC.fieldOf("decorationType").forGetter(i -> i.decorationType)
            ).apply(inst, MapReward::new));

    public final TagKey<Structure> destination;
    private final String displayName;
    private final Holder<MapDecorationType> decorationType;

    public MapReward(TagKey<Structure> destination, String displayName, Holder<MapDecorationType> decorationType) {
        super(new ItemStack(Items.FILLED_MAP));
        this.destination = destination;
        this.displayName = displayName;
        this.decorationType = decorationType;
    }

    @Override
    public ITaskRewardInstance createInstance(@Nullable IFactionPlayer<?> player) {
        return new Instance(destination, displayName, decorationType);
    }

    @Override
    public MapCodec<MapReward> codec() {
        return FactionTasks.MAP_REWARD.get();
    }

    public record Instance(TagKey<Structure> destination, String displayName, Holder<MapDecorationType> decorationType) implements ITaskRewardInstance {

        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        TagKey.codec(Registries.STRUCTURE).fieldOf("destination").forGetter(i -> i.destination),
                        Codec.STRING.fieldOf("displayName").forGetter(i -> i.displayName),
                        MapDecorationType.CODEC.fieldOf("decorationType").forGetter(i -> i.decorationType)
                ).apply(inst, Instance::new));

        @Override
        public void applyReward(IFactionPlayer<?> player) {
            ItemStack reward = MapUtil.getMap(player.asEntity(), destination, displayName, decorationType, 150);
            if (reward != null) {
                if (!player.asEntity().addItem(reward.copy())) {
                    player.asEntity().drop(reward.copy(), true);
                }
            }
        }

        @Override
        public MapCodec<? extends ITaskRewardInstance> codec() {
            return FactionTasks.MAP_REWARD_INSTANCE.get();
        }
    }
}
