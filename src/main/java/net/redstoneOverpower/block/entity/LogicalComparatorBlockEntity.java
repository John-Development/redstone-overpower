package net.redstoneOverpower.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import static net.redstoneOverpower.utils.Initialiser.LOGICAL_COMPARATOR_BLOCK_ENTITY;

public class LogicalComparatorBlockEntity extends BlockEntity {
    private int outputSignal;

    public LogicalComparatorBlockEntity(BlockPos pos, BlockState state) {
        super(LOGICAL_COMPARATOR_BLOCK_ENTITY, pos, state);
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("OutputSignal", this.outputSignal);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.outputSignal = nbt.getInt("OutputSignal");
    }

    public int getOutputSignal() {
        return this.outputSignal;
    }

    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }
}
