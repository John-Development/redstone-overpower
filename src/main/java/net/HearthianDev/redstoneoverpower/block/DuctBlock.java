package net.HearthianDev.redstoneoverpower.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.HearthianDev.redstoneoverpower.block.entity.DuctBlockEntity;
import net.HearthianDev.redstoneoverpower.block.enums.PipeType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.HearthianDev.redstoneoverpower.utils.Initialiser.*;

public class DuctBlock extends BlockWithEntity implements Waterloggable {
    public static final MapCodec<DuctBlock> CODEC = createCodec(DuctBlock::new);
    public static final BooleanProperty ENABLED;
    public static final EnumProperty<PipeType> NORTH;
    public static final EnumProperty<PipeType> EAST;
    public static final EnumProperty<PipeType> SOUTH;
    public static final EnumProperty<PipeType> WEST;
    public static final EnumProperty<PipeType> UP;
    public static final EnumProperty<PipeType> DOWN;
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    public static final Map<Direction, EnumProperty<PipeType>> FACING_PROPERTIES;
    private static final Direction[] FACINGS;

    protected final VoxelShape[] facingsToShape;

    public DuctBlock(Settings settings) {
        super(settings);
        this.facingsToShape = this.generateFacingsToShapeMap();
        this.setDefaultState(this.stateManager.getDefaultState()
            .with(ENABLED, true)
            .with(WATERLOGGED, false)
            .with(NORTH, PipeType.NONE)
            .with(EAST, PipeType.NONE)
            .with(SOUTH, PipeType.NONE)
            .with(WEST, PipeType.NONE)
            .with(UP, PipeType.NONE)
            .with(DOWN, PipeType.NONE)
            .with(FACING, Direction.NORTH)
        );
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    private VoxelShape[] generateFacingsToShapeMap() {
        float radius = 0.25f;

        float f = 0.5f - radius;
        float g = 0.5f + radius;
        VoxelShape voxelShape = Block.createCuboidShape(f * 16.0f, f * 16.0f, f * 16.0f, g * 16.0f, g * 16.0f, g * 16.0f);
        VoxelShape[] voxelShapes = new VoxelShape[FACINGS.length];
        for (int i = 0; i < FACINGS.length; ++i) {
            Direction direction = FACINGS[i];
            voxelShapes[i] = VoxelShapes.cuboid(
                0.5 + Math.min((-radius), (double)direction.getOffsetX() * 0.5),
                0.5 + Math.min((-radius), (double)direction.getOffsetY() * 0.5),
                0.5 + Math.min((-radius), (double)direction.getOffsetZ() * 0.5),
                0.5 + Math.max(radius, (double)direction.getOffsetX() * 0.5),
                0.5 + Math.max(radius, (double)direction.getOffsetY() * 0.5),
                0.5 + Math.max(radius, (double)direction.getOffsetZ() * 0.5)
            );
        }
        VoxelShape[] voxelShapes2 = new VoxelShape[64];
        for (int j = 0; j < 64; ++j) {
            VoxelShape voxelShape2 = voxelShape;
            for (int k = 0; k < FACINGS.length; ++k) {
                if ((j & 1 << k) == 0) continue;
                voxelShape2 = VoxelShapes.union(voxelShape2, voxelShapes[k]);
            }
            voxelShapes2[j] = voxelShape2;
        }

        return voxelShapes2;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.facingsToShape[this.getConnectionMask(state)];
    }

    protected int getConnectionMask(BlockState state) {
        int i = 0;
        for (int j = 0; j < FACINGS.length; ++j) {
            if (state.get(FACING_PROPERTIES.get(FACINGS[j])) == PipeType.NONE) continue;
            i |= 1 << j;
        }

        return i;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DuctBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : DuctBlock.validateTicker(type, DUCT_BLOCK_ENTITY, DuctBlockEntity::serverTick);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction dir = ctx.getSide().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());

        return this.getDefaultState()
            .with(DOWN, getSideMode(world, pos.down(), Direction.DOWN, dir))
            .with(UP, getSideMode(world, pos.up(), Direction.UP, dir))
            .with(NORTH, getSideMode(world, pos.north(), Direction.NORTH, dir))
            .with(EAST, getSideMode(world, pos.east(), Direction.EAST, dir))
            .with(SOUTH, getSideMode(world, pos.south(), Direction.SOUTH, dir))
            .with(WEST, getSideMode(world, pos.west(), Direction.WEST, dir))
            .with(FACING, dir)
            .with(ENABLED, true)
            .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

/*
    Determines if the side is in, out or none.
    If the adjacent block is a container, and the player has triggered a click with it, should be out
    If the adjacent block is a opposite or a hopper oriented to the block, should be in
    In any other case, should be none
 */
    private PipeType getSideMode(World world, BlockPos neighborPos, Direction side,  Direction facing) {
        if (world.getBlockEntity(neighborPos) instanceof DuctBlockEntity ductBlockEntity) {
            if (facing.equals(side)) {
                return PipeType.OUT;
            }
            if (ductBlockEntity.getCachedState().get(FACING).equals(side.getOpposite())) {
                return PipeType.IN;
            }

            return PipeType.NONE;
        }

        if (world.getBlockEntity(neighborPos) instanceof HopperBlockEntity hopperBlockEntity) {
            if (hopperBlockEntity.getCachedState().get(HopperBlock.FACING).equals(side.getOpposite())) {
                return side.getAxis() == Direction.Axis.Y ? PipeType.IN : PipeType.IN_HOPPER;
            }
        }

        return DuctBlockEntity.getInventoryAt(world, neighborPos) != null ? PipeType.OUT : PipeType.NONE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);

            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }

        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        // If duct/hopper facing here, IN
        if (neighborState.isOf(DUCT_BLOCK)) {
            if (neighborState.get(FACING).equals(direction.getOpposite())) {
                if (state.get(FACING).equals(direction)) {
                    return state.with(FACING_PROPERTIES.get(direction), PipeType.OUT);
                }

                return state.with(FACING_PROPERTIES.get(direction), PipeType.IN);
            }

            PipeType opposite;

            if ((opposite = PipeType.getOpposite(neighborState.get(FACING_PROPERTIES.get(direction.getOpposite())))) != null) {
                return state.with(FACING_PROPERTIES.get(direction), opposite);
            }

            return state.with(FACING_PROPERTIES.get(direction), PipeType.NONE);
        }
        if (neighborState.isOf(Blocks.HOPPER) && neighborState.get(HopperBlock.FACING).equals(direction.getOpposite())) {
            return state.with(FACING_PROPERTIES.get(direction), direction.getAxis() == Direction.Axis.Y ? PipeType.IN : PipeType.IN_HOPPER);
        }

        return state.with(FACING_PROPERTIES.get(direction), DuctBlockEntity.getInventoryAt((World) world, neighborPos) != null
            ? PipeType.OUT
            : PipeType.NONE
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        if (player.getStackInHand(player.getActiveHand()).getItem().asItem().equals(this.asItem()) && world.getBlockEntity(pos.offset(hit.getSide())) instanceof DuctBlockEntity) {
            world.setBlockState(pos, state.with(FACING_PROPERTIES.get(hit.getSide()), PipeType.IN));

            return ActionResult.CONSUME;
        }

        if (world.getBlockEntity(pos) instanceof DuctBlockEntity ductBlockEntity) {
            player.openHandledScreen(ductBlockEntity);
        }

        return ActionResult.CONSUME;
    }

    //This method will drop all items onto the ground when the block is broken
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof DuctBlockEntity ductBlockEntity) {
                ItemScatterer.spawn(world, pos, ductBlockEntity);
                // update comparators
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        this.updateEnabled(world, pos, state);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(World world, BlockPos pos, BlockState state) {
        boolean isPowered = !world.isReceivingRedstonePower(pos);

        if (isPowered != state.get(ENABLED)) {
            world.setBlockState(pos, state.with(ENABLED, isPowered), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ENABLED, NORTH, EAST, SOUTH, WEST, UP, DOWN, FACING, WATERLOGGED);
    }

    static {
        ENABLED = Properties.ENABLED;
        NORTH = EnumProperty.of("north", PipeType.class);
        EAST = EnumProperty.of("east", PipeType.class);
        SOUTH = EnumProperty.of("south", PipeType.class);
        WEST = EnumProperty.of("west", PipeType.class);
        UP = EnumProperty.of("up", PipeType.class);
        DOWN = EnumProperty.of("down", PipeType.class);
        FACING = Properties.FACING;
        WATERLOGGED = Properties.WATERLOGGED;
        FACING_PROPERTIES = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), directions -> {
            directions.put(Direction.NORTH, NORTH);
            directions.put(Direction.EAST, EAST);
            directions.put(Direction.SOUTH, SOUTH);
            directions.put(Direction.WEST, WEST);
            directions.put(Direction.UP, UP);
            directions.put(Direction.DOWN, DOWN);
        }));
        FACINGS = Direction.values();
    }
}
