package net.redstoneOverpower.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;
import net.redstoneOverpower.block.SculkChamberBlock;
import net.redstoneOverpower.block.enums.SculkChamberMode;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.Objects;

import static net.redstoneOverpower.utils.Initialiser.*;

public class SculkChamberBlockEntity extends BlockEntity implements GameEventListener.Holder<Vibrations.VibrationListener>, Vibrations, GeoBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Vibrations.ListenerData listenerData;
    private final Vibrations.VibrationListener listener;
    private final Vibrations.Callback callback;
    private int lastVibrationFrequency;

    protected SculkChamberBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.callback = this.createCallback();
        this.listenerData = new Vibrations.ListenerData();
        this.listener = new Vibrations.VibrationListener(this);
    }

    public SculkChamberBlockEntity(BlockPos pos, BlockState state) {
        this(SCULK_CHAMBER_BLOCK_ENTITY, pos, state);
    }

    public Vibrations.Callback createCallback() {
        return new SculkChamberBlockEntity.VibrationCallback(this.getPos());
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
        if (nbt.contains("listener", 10)) {
            DataResult<ListenerData> var10000 = ListenerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")));
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var10000.resultOrPartial(var10001::error).ifPresent((listener) -> this.listenerData = listener);
        }

    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        DataResult<NbtElement> var10000 = ListenerData.CODEC.encodeStart(NbtOps.INSTANCE, this.listenerData);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((listenerNbt) -> nbt.put("listener", listenerNbt));
    }

    @Override
    public ListenerData getVibrationListenerData() {
        return this.listenerData;
    }

    @Override
    public Callback getVibrationCallback() {
        return this.callback;
    }

    @Override
    public VibrationListener getEventListener() {
        return this.listener;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    public void setLastVibrationFrequency(int lastVibrationFrequency) {
        this.lastVibrationFrequency = lastVibrationFrequency;
    }

//    @Nullable
//    @Override
//    public Packet<ClientPlayPacketListener> toUpdatePacket() {
//        // When state changes, notify listeners to allow data sync between server and rendering client
//        // world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
//        return BlockEntityUpdateS2CPacket.create(this);
//    }
//
//    @Override
//    public NbtCompound toInitialChunkDataNbt() {
//        return createNbt();
//    }

    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlayAndHold("open");//.thenLoop("permaopen");
    private static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlayAndHold("close");//.thenLoop("permaclose");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//        controllers.add(new AnimationController<>(this, state => {));
        controllers.add(new AnimationController<>(this, "sculk_chamber", this::predicate)
            .triggerableAnim("open", OPEN_ANIM)
            .triggerableAnim("close", CLOSE_ANIM));
    }

    private <T extends GeoAnimatable> PlayState open(AnimationState<T> animationState, World world, BlockPos pos, BlockState blockState) {
//        world.setBlockState(pos, blockState.with(SculkChamberBlock.MODE, SculkChamberMode.COOLDOWN), Block.NOTIFY_ALL);
        System.out.println("SE ANIMA");
//        animationState.getController().setAnimation(RawAnimation.begin().then("open", Animation.LoopType.HOLD_ON_LAST_FRAME));

        animationState.getController().triggerableAnim("open", OPEN_ANIM);

        return animationState.setAndContinue(OPEN_ANIM);
    }

    private <T extends GeoAnimatable> PlayState close(AnimationState<T> animationState, World world, BlockPos pos, BlockState blockState) {
//        world.setBlockState(pos, blockState.with(SculkChamberBlock.MODE, SculkChamberMode.CHARGED), Block.NOTIFY_ALL);
        System.out.println("SE ANIMA");
//        animationState.getController().setAnimation(RawAnimation.begin().then("close", Animation.LoopType.HOLD_ON_LAST_FRAME));

        animationState.getController().triggerableAnim("close", CLOSE_ANIM);

        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationState<SculkChamberBlockEntity> animationState) {
//        int isOpen = animationState.getData(DataTickets.ANIM_STATE);
//
//        System.out.println("anim state: " + isOpen);
//        boolean isClosed = animationState.getData(DataTickets.CLOSED);

        return PlayState.CONTINUE;

//        BlockPos pos = animationState.getAnimatable().getPos();
//        World world = animationState.getAnimatable().world;
//
//
//        assert world != null;
////        if (world.isClient) {
////            System.out.println("CLIENTE");
////        } else {
////            System.out.println("SERVIDOR");
////        }
//
//        BlockEntity blockEntity = world.getBlockEntity(pos);
//        BlockState blockState = world.getBlockState(pos);
//
//        if (blockEntity instanceof SculkChamberBlockEntity) {
//            boolean shouldOpen = blockState.get(SculkChamberBlock.MODE) == SculkChamberMode.COOLDOWN;
//            boolean shouldClose = blockState.get(SculkChamberBlock.MODE) == SculkChamberMode.CHARGED;
//
//            if (shouldOpen) {
//                return open(animationState, world, pos, blockState);
//            }
//            if (shouldClose) {
//                return close(animationState, world, pos, blockState);
//            }
//        }
//
//        return PlayState.CONTINUE;
    }
//    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> geoAnimatableAnimationState) {
//        BlockPos pos = this.getPos();
//        assert world != null;
//        BlockEntity blockEntity = world.getBlockEntity(pos);
//        BlockState blockState = world.getBlockState(pos);
//
////        geoAnimatableAnimationState.getController().isPlayingTriggeredAnimation()
//
//
//        if (blockEntity instanceof SculkChamberBlockEntity) {
////            boolean shouldAnimate = blockState.get(SculkChamberBlock.SHOULD_ANIMATE);
//            boolean shouldOpen = blockState.get(SculkChamberBlock.MODE) == SculkChamberMode.COOLDOWN_ANIM;
//            boolean shouldClose = blockState.get(SculkChamberBlock.MODE) == SculkChamberMode.CHARGED_ANIM;
//
//            if (shouldOpen) {
//                open(geoAnimatableAnimationState, blockState);
//
//                return PlayState.CONTINUE;
//            }
//            if (shouldClose) {
//                close(geoAnimatableAnimationState, blockState);
//
//                return PlayState.CONTINUE;
//            }
//
////            if (shouldAnimate) {
////                world.setBlockState(pos, blockState.with(SculkChamberBlock.SHOULD_ANIMATE, false), Block.NOTIFY_ALL);
//
//
////            }
//        }
//
//        return PlayState.CONTINUE;
//    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    protected class VibrationCallback implements Vibrations.Callback {
        public static final int RANGE = 16;
        protected final BlockPos pos;
        private final PositionSource positionSource;

        public VibrationCallback(BlockPos pos) {
            this.pos = pos;
            this.positionSource = new BlockPositionSource(pos);
        }

        public int getRange() {
            return RANGE;
        }

        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        public boolean triggersAvoidCriterion() {
            return true;
        }

        // Decides if the block can accept a sound
        public boolean accepts(ServerWorld world, BlockPos pos, GameEvent event, @Nullable GameEvent.Emitter emitter) {
            BlockState blockState = SculkChamberBlockEntity.this.getCachedState();

            if (blockState.getBlock() instanceof SculkChamberBlock) {
                return (!pos.equals(this.pos)
                    || event != GameEvent.BLOCK_DESTROY
                    && event != GameEvent.BLOCK_PLACE)
                    && SculkChamberBlock.canStoreSound(blockState);
            }

            return !pos.equals(this.pos) || event != GameEvent.BLOCK_DESTROY && event != GameEvent.BLOCK_PLACE;
        }

        public void accept(ServerWorld world, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            BlockState blockState = SculkChamberBlockEntity.this.getCachedState();

            if (SculkChamberBlock.canStoreSound(blockState)) {
                SculkChamberBlockEntity.this.setLastVibrationFrequency(Vibrations.getFrequency(event));
                if (blockState.getBlock() instanceof SculkChamberBlock sculkChamberBlock) {
                    sculkChamberBlock.setCharged(sourceEntity, world, this.pos, blockState, SculkChamberBlockEntity.this.getLastVibrationFrequency());
                }
            }
        }

        public void onListen() {
            SculkChamberBlockEntity.this.markDirty();
        }

        public boolean requiresTickingChunksAround() {
            return true;
        }
    }
}
