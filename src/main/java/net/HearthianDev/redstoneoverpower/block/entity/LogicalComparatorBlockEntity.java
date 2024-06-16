package net.HearthianDev.redstoneoverpower.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import static net.HearthianDev.redstoneoverpower.utils.Initialiser.LOGICAL_COMPARATOR_BLOCK_ENTITY;

public class LogicalComparatorBlockEntity extends BlockEntity {
    private int outputSignal;

    public LogicalComparatorBlockEntity(BlockPos pos, BlockState state) {
        super(LOGICAL_COMPARATOR_BLOCK_ENTITY, pos, state);
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("OutputSignal", this.outputSignal);
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.outputSignal = nbt.getInt("OutputSignal");
    }

    public int getOutputSignal() {
        return this.outputSignal;
    }

    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }
}
