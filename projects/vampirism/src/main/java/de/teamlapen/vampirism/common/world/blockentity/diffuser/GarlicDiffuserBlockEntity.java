package de.teamlapen.vampirism.common.world.blockentity.diffuser;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.datamaps.IGarlicDiffuserFuel;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModDataMaps;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.inventory.diffuser.DiffuserMenu;
import de.teamlapen.vampirism.common.world.inventory.diffuser.GarlicDiffuserMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GarlicDiffuserBlockEntity extends DiffuserBlockEntity {

    private EnumStrength strength;
    private int radius;
    private int emitterId = 0;

    public GarlicDiffuserBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(pPos, pBlockState, EnumStrength.NONE, 0);
    }

    public GarlicDiffuserBlockEntity(BlockPos pPos, BlockState pBlockState, EnumStrength strength, int radius) {
        super(ModBlockEntities.GARLIC_DIFFUSER.get(), pPos, pBlockState);
        this.strength = strength;
        this.radius = radius;
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.store("garlicStrength", EnumStrength.CODEC, this.strength);
        output.putInt("garlicRadius", this.radius);
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.strength = input.read("garlicStrength", EnumStrength.CODEC).orElse(EnumStrength.NONE);
        this.radius = input.getIntOr("garlicRadius", 0);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.vampirism.garlic_diffuser");
    }

    @Override
    protected @NotNull DiffuserMenu createMenu(int pContainerId, @NotNull Inventory pInventory, @NotNull LockDataHolder lockData) {
        return new GarlicDiffuserMenu(pContainerId, pInventory, this, this.dataAccess, lockData);
    }

    @Override
    protected int getBurnDuration(ItemStack itemStack) {
        IGarlicDiffuserFuel data = itemStack.typeHolder().getData(ModDataMaps.GARLIC_DIFFUSER_FUEL_MAP);
        return data != null ? data.burnDuration() : 0;
    }

    public boolean isInRange(BlockPos blockPos) {
        return ChunkPos.containing(this.getBlockPos()).getChessboardDistance(ChunkPos.containing(blockPos)) <= this.radius;
    }

    @Override
    public boolean canOpen(@NotNull Player pPlayer) {
        return tryAccess(pPlayer, ModFactions.HUNTER, getName()) && super.canOpen(pPlayer);
    }

    @Override
    public void onTouched(Player pPlayer) {
        VampirePlayer vampire = VampirePlayer.get(pPlayer);
        if (vampire.getLevel() > 0) {
            DamageHandler.affectVampireGarlicDirect(vampire, this.strength);
        }
    }

    @Override
    protected void activateEffect(Level level, BlockPos blockPos, BlockState blockState) {
        if (emitterId == 0) {
            int baseX = (getBlockPos().getX() >> 4);
            int baseZ = (getBlockPos().getZ() >> 4);
            ChunkPos[] chunks = new ChunkPos[(2 * this.radius + 1) * (2 * this.radius + 1)];
            int i = 0;
            for (int x = -this.radius; x <= this.radius; x++) {
                for (int z = -this.radius; z <= this.radius; z++) {
                    chunks[i++] = new ChunkPos(x + baseX, z + baseZ);
                }
            }
            this.emitterId = LevelGarlic.get(level).registerGarlicBlock(this.strength, List.of(chunks));
        }
    }

    @Override
    public void deactivateEffect(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.emitterId != 0) {
            LevelGarlic.get(level).removeGarlicBlock(this.emitterId);
            this.emitterId = 0;
        }
    }

    @Override
    public int getParticleNumber(Level level, BlockPos blockPos, BlockState blockState, DiffuserBlockEntity blockEntity) {
        return 2 + 3 * strength.getStrength();
    }
}
