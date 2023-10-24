package net.redstoneOverpower.block;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.redstoneOverpower.block.entity.LogicalComparatorBlockEntity;
import net.redstoneOverpower.block.enums.LogicalComparatorMode;

public class LogicalComparatorBlock extends AbstractRedstoneGateBlock implements BlockEntityProvider {
    public static final EnumProperty<LogicalComparatorMode> MODE;

    public LogicalComparatorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
            .with(FACING, Direction.NORTH)
            .with(POWERED, false)
            .with(MODE, LogicalComparatorMode.AND)
        );
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LogicalComparatorBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE, POWERED);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        } else {
            state = state.cycle(MODE);
            world.setBlockState(pos, state, 3);
            this.update(world, pos, state);
            return ActionResult.success(world.isClient);
        }
    }

    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        Direction rightDir = direction.rotateYClockwise();
        Direction leftDir = direction.rotateYCounterclockwise();
        int rightInput = world.getEmittedRedstonePower(pos.offset(rightDir), rightDir, this.getSideInputFromGatesOnly());
        int leftInput = world.getEmittedRedstonePower(pos.offset(leftDir), leftDir, this.getSideInputFromGatesOnly());
        boolean right = rightInput > 0;
        boolean left = leftInput > 0;

        return switch(state.get(MODE)) {
            case AND -> right && left;
            case OR -> right || left;
            case XOR -> right != left;
            case NAND -> !(right && left);
            case NOR -> !(right || left);
            case XNOR -> right == left;
        };
    }

    private void update(World world, BlockPos pos, BlockState state) {
        int i = this.hasPower(world, pos, state) ? 15 : 0;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        int j = 0;
        if (blockEntity instanceof LogicalComparatorBlockEntity logicalComparatorBlockEntity) {
            j = logicalComparatorBlockEntity.getOutputSignal();
            logicalComparatorBlockEntity.setOutputSignal(i);
        }

        if (j != i) {
            boolean bl = this.hasPower(world, pos, state);
            boolean bl2 = state.get(POWERED);
            if (bl2 && !bl) {
                world.setBlockState(pos, state.with(POWERED, false), 2);
            } else if (!bl2 && bl) {
                world.setBlockState(pos, state.with(POWERED, true), 2);
            }

            this.updateTarget(world, pos, state);
        }
    }

    static {
        MODE = EnumProperty.of("mode", LogicalComparatorMode.class);
    }
}
