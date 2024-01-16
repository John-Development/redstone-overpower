package net.redstoneOverpower.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import net.redstoneOverpower.block.entity.SculkChamberBlockEntity;
import net.redstoneOverpower.block.enums.SculkChamberMode;
import org.jetbrains.annotations.Nullable;

import static net.redstoneOverpower.utils.Initialiser.SCULK_CHAMBER_BLOCK_ENTITY;

public class SculkChamberBlock extends BlockWithEntity {
    public static final EnumProperty<SculkChamberMode> MODE;

    private static final float[] RESONATION_NOTE_PITCHES = Util.make(new float[16], frequency -> {
        int[] is = new int[]{0, 0, 2, 4, 6, 7, 9, 10, 12, 14, 15, 18, 19, 21, 22, 24};
        for (int i = 0; i < 16; ++i) {
            frequency[i] = NoteBlock.getNotePitch(is[i]);
        }
    });

    public SculkChamberBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
            .with(MODE, SculkChamberMode.LISTEN)
        );
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MODE);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SculkChamberBlockEntity(pos, state);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (getMode(state) != SculkChamberMode.CHARGED) {
            if (getMode(state) == SculkChamberMode.COOLDOWN) {
                world.setBlockState(pos, state.with(MODE, world.isReceivingRedstonePower(pos) ? SculkChamberMode.ISOLATED : SculkChamberMode.LISTEN), Block.NOTIFY_LISTENERS);
                world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.2f + 0.8f);
            }
            return;
        }
        SculkChamberBlock.setCooldown(world, pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient) {
            return SculkChamberBlock.validateTicker(
                type,
                SCULK_CHAMBER_BLOCK_ENTITY,
                (worldx, pos, statex, blockEntity) -> Vibrations.Ticker.tick(worldx, blockEntity.getVibrationListenerData(), blockEntity.getVibrationCallback())
            );
        }
        return null;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        boolean isPowered = world.isReceivingRedstonePower(pos);

        if (!isPowered && getMode(state) == SculkChamberMode.ISOLATED) {
            world.setBlockState(pos, state.with(MODE, SculkChamberMode.LISTEN), Block.NOTIFY_ALL);
        } else if (isPowered && getMode(state) == SculkChamberMode.LISTEN) {
            world.setBlockState(pos, state.with(MODE, SculkChamberMode.ISOLATED), Block.NOTIFY_ALL);
        }
        if ((state.get(MODE) == SculkChamberMode.CHARGED) && world.isReceivingRedstonePower(pos)) {
            SculkChamberBlock.setCooldown(world, pos, state);

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof SculkChamberBlockEntity sculkChamberBlockEntity)) {
                return;
            }
            SculkChamberBlock.updateNeighbors(world, pos, state);
            world.emitGameEvent(
                Registries.GAME_EVENT.get(new Identifier("minecraft", "resonate_" + sculkChamberBlockEntity.getLastVibrationFrequency())),
                pos,
                GameEvent.Emitter.of(state)
            );
        }
    }

    public static void setCooldown(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(MODE, SculkChamberMode.COOLDOWN), Block.NOTIFY_ALL);
        world.scheduleBlockTick(pos, state.getBlock(), getCooldownTime());
        SculkChamberBlock.updateNeighbors(world, pos, state);
    }

    private static void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        world.updateNeighborsAlways(pos, block);
        world.updateNeighborsAlways(pos.down(), block);
    }

    public static SculkChamberMode getMode(BlockState state) {
        return state.get(MODE);
    }

    public static boolean canStoreSound(BlockState state) {
        return SculkChamberBlock.getMode(state) == SculkChamberMode.LISTEN || SculkChamberBlock.getMode(state) == SculkChamberMode.ISOLATED;
    }

    public static int getCooldownTime() {
        return 40;
    }

    public void setCharged(@Nullable Entity sourceEntity, World world, BlockPos pos, BlockState state, int frequency) {
        world.setBlockState(pos, state.with(MODE, SculkChamberMode.CHARGED), Block.NOTIFY_ALL);
        SculkChamberBlock.updateNeighbors(world, pos, state);
        SculkChamberBlock.tryResonate(sourceEntity, world, pos, frequency);
        world.emitGameEvent(sourceEntity, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, pos);
        world.playSound(
            null,
            (double)pos.getX() + 0.5,
            (double)pos.getY() + 0.5,
            (double)pos.getZ() + 0.5,
            SoundEvents.BLOCK_SCULK_SENSOR_CLICKING,
            SoundCategory.BLOCKS,
            1.0f,
            world.random.nextFloat() * 0.2f + 0.8f
        );
    }

    public static void tryResonate(@Nullable Entity sourceEntity, World world, BlockPos pos, int frequency) {
        for (Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isIn(BlockTags.VIBRATION_RESONATORS)) continue;
            world.emitGameEvent(Vibrations.getResonation(frequency), blockPos, GameEvent.Emitter.of(sourceEntity, blockState));
            world.playSound(null, blockPos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 1.0f, RESONATION_NOTE_PITCHES[frequency]);
        }
    }

    static {
        MODE = EnumProperty.of("mode", SculkChamberMode.class);
    }
}
