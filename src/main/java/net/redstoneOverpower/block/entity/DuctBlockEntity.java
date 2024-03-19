package net.redstoneOverpower.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.redstoneOverpower.block.DuctBlock;
import net.redstoneOverpower.block.enums.PipeType;
import net.redstoneOverpower.block.screen.DuctScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

import static net.redstoneOverpower.utils.Initialiser.DUCT_BLOCK_ENTITY;

public class DuctBlockEntity extends LootableContainerBlockEntity {
  public static final int TRANSFER_COOLDOWN = 8;
  public static final int INVENTORY_SIZE = 1;
  public static final int FILTER_ENABLED = 1;
  public static final int FILTER_DISABLED = 0;
  public static final ArrayList<Direction> TRANSFER_PRIORITY = new ArrayList<>(Arrays.asList(
    Direction.DOWN,
    Direction.NORTH,
    Direction.EAST,
    Direction.SOUTH,
    Direction.WEST,
    Direction.UP
  ));

  private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
  private int transferCooldown = -1;
  private long lastTickTime;
  protected final PropertyDelegate propertyDelegate;
  protected int slotState;

  public DuctBlockEntity(BlockPos pos, BlockState state) {
    super(DUCT_BLOCK_ENTITY, pos, state);

    this.slotState = FILTER_DISABLED;
    this.propertyDelegate = new PropertyDelegate() {
      public int get(int index) {
        return index == 0 ? slotState : FILTER_DISABLED;
      }

      public void set(int index, int value) {
        if (index == 0) {
          setSlotState(value);
        }
      }

      public int size() {
        return INVENTORY_SIZE;
      }
    };
  }

  public void setSlotState (int value) {
    this.slotState = value;
  }

  @Override
  protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
    return new DuctScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    if (!this.readLootTable(nbt)) {
      Inventories.readNbt(nbt, this.inventory);
    }
    this.transferCooldown = nbt.getInt("TransferCooldown");
    this.setSlotState(nbt.getInt("slot_state"));
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    if (!this.writeLootTable(nbt)) {
      Inventories.writeNbt(nbt, this.inventory);
    }
    nbt.putInt("TransferCooldown", this.transferCooldown);
    nbt.putInt("slot_state", this.slotState);
  }

  @Override
  public int size() {
    return this.inventory.size();
  }

  @Override
  public ItemStack removeStack(int slot, int amount) {
    this.generateLoot(null);
    return Inventories.splitStack(this.method_11282(), slot, amount);
  }

  @Override
  public void setStack(int slot, ItemStack stack) {
    this.generateLoot(null);
    this.method_11282().set(slot, stack);
    if (stack.getCount() > this.getMaxCountPerStack()) {
      stack.setCount(this.getMaxCountPerStack());
    }
  }

  @Override
  protected Text getContainerName() {
    return Text.translatable("container.redstoneoverpower.duct");
  }

  public static void serverTick(World world, BlockPos pos, BlockState state, DuctBlockEntity blockEntity) {
    --blockEntity.transferCooldown;
    blockEntity.lastTickTime = world.getTime();
    if (!blockEntity.needsCooldown()) {
      blockEntity.setTransferCooldown(0);
      DuctBlockEntity.insertMain(world, pos, state, blockEntity);
    }
  }

  @Nullable
  public static Inventory getInventoryAt(World world, BlockPos pos) {
    return DuctBlockEntity.getInventoryAt(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
  }

  @Nullable
  private static Inventory getInventoryAt(World world, double x, double y, double z) {
    List<Entity> list;
    BlockEntity blockEntity;
    Inventory inventory = null;
    BlockPos blockPos = BlockPos.ofFloored(x, y, z);
    BlockState blockState = world.getBlockState(blockPos);
    Block block = blockState.getBlock();
    if (block instanceof InventoryProvider) {
      inventory = ((InventoryProvider) block).getInventory(blockState, world, blockPos);
    } else if (blockState.hasBlockEntity() && (blockEntity = world.getBlockEntity(blockPos)) instanceof Inventory && (inventory = (Inventory) blockEntity) instanceof ChestBlockEntity && block instanceof ChestBlock) {
      inventory = ChestBlock.getInventory((ChestBlock)block, blockState, world, blockPos, true);
    }
    if (inventory == null && !(list = world.getOtherEntities(null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.VALID_INVENTORIES)).isEmpty()) {
      inventory = (Inventory) list.get(world.random.nextInt(list.size()));
    }
    return inventory;
  }

  @Override
  protected DefaultedList<ItemStack> method_11282() {
    return this.inventory;
  }

  @Override
  protected void setInvStackList(DefaultedList<ItemStack> list) {
    this.inventory = list;
  }

  private static void insertMain(World world, BlockPos pos, BlockState state, DuctBlockEntity blockEntity) {
    if (world.isClient) {
      return;
    }

    boolean canMoveItem = (blockEntity.slotState == FILTER_ENABLED && blockEntity.inventory.get(0).getCount() > 1) || blockEntity.slotState == FILTER_DISABLED;

    if (!blockEntity.needsCooldown() && state.get(DuctBlock.ENABLED) && canMoveItem) {
      boolean bl = false;
      if (!blockEntity.isEmpty()) {
        bl = DuctBlockEntity.insert(world, pos, state, blockEntity);
      }
      if (bl) {
        blockEntity.setTransferCooldown(TRANSFER_COOLDOWN);
        DuctBlockEntity.markDirty(world, pos, state);

      }
    }
  }

  private static boolean insert(World world, BlockPos pos, BlockState state, Inventory inventory) {
    for (Direction direction : TRANSFER_PRIORITY) {
      if (state.get(DuctBlock.FACING_PROPERTIES.get(direction)) != PipeType.OUT) {
          continue;
      }

      Inventory inventory2 = DuctBlockEntity.getInventoryAt(world, pos.offset(direction));

      if (inventory2 != null && !DuctBlockEntity.isInventoryFull(inventory2, direction)) {
        for (int i = 0; i < inventory.size(); ++i) {
          if (inventory.getStack(i).isEmpty()) continue;
          ItemStack itemStack = inventory.getStack(i).copy();
          ItemStack itemStack2 = DuctBlockEntity.transfer(inventory, inventory2, inventory.removeStack(i, 1), direction);
          if (itemStack2.isEmpty()) {
            inventory2.markDirty();

            return true;
          }
          inventory.setStack(i, itemStack);
        }
      }
    }

    return false;
  }

  private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
    if (inventory instanceof SidedInventory) {
      return IntStream.of(((SidedInventory)inventory).getAvailableSlots(side));
    }

    return IntStream.range(0, inventory.size());
  }

  private static boolean isInventoryFull(Inventory inventory, Direction direction) {
    return DuctBlockEntity.getAvailableSlots(inventory, direction).allMatch(slot -> {
      ItemStack itemStack = inventory.getStack(slot);

      return itemStack.getCount() >= itemStack.getMaxCount();
    });
  }

  private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
    if (!inventory.isValid(slot, stack)) {
      return false;
    }

    return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsert(slot, stack, side);
  }

  /*
   * Enabled aggressive block sorting
   * Lifted jumps to return sites
   */
  public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
    if (to instanceof SidedInventory sidedTo) {
      if (side != null) {
        int[] is = sidedTo.getAvailableSlots(side.getOpposite());
        int i = 0;
        while (i < is.length) {
          if (stack.isEmpty()) return stack;
          stack = DuctBlockEntity.transfer(from, to, stack, is[i], side);
          ++i;
        }

        return stack;
      }
    }

    int j = to.size();
    int i = 0;
    while (i < j) {
      if (stack.isEmpty()) return stack;
      stack = DuctBlockEntity.transfer(from, to, stack, i, side);
      ++i;
    }

    return stack;
  }

  private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
    ItemStack itemStack = to.getStack(slot);

    if (DuctBlockEntity.canInsert(to, stack, slot, side)) {
      int j;
      boolean bl = false;
      boolean bl2 = to.isEmpty();

      if (itemStack.isEmpty()) {
        to.setStack(slot, stack);
        stack = ItemStack.EMPTY;
        bl = true;
      } else if (DuctBlockEntity.canMergeItems(itemStack, stack)) {
        int i = stack.getMaxCount() - itemStack.getCount();
        j = Math.min(stack.getCount(), i);
        stack.decrement(j);
        itemStack.increment(j);
        bl = j > 0;
      }

      if (bl) {
        DuctBlockEntity hopperBlockEntity;
        if (bl2 && to instanceof DuctBlockEntity && !(hopperBlockEntity = (DuctBlockEntity)to).isDisabled()) {
          j = 0;
          if (from instanceof DuctBlockEntity hopperBlockEntity2) {
            if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
              j = 1;
            }
          }
          hopperBlockEntity.setTransferCooldown(8 - j);
        }
        to.markDirty();
      }
    }

    return stack;
  }

  private static boolean canMergeItems(ItemStack first, ItemStack second) {
    return first.getCount() <= first.getMaxCount() && ItemStack.canCombine(first, second);
  }

  private void setTransferCooldown(int transferCooldown) {
    this.transferCooldown = transferCooldown;
  }

  private boolean needsCooldown() {
    return this.transferCooldown > 0;
  }

  private boolean isDisabled() {
    return this.transferCooldown > TRANSFER_COOLDOWN;
  }
}
