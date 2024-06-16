package net.HearthianDev.redstoneoverpower.block.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.HearthianDev.redstoneoverpower.block.entity.DuctBlockEntity;

import static net.HearthianDev.redstoneoverpower.utils.Initialiser.DUCT_SCREEN_HANDLER;

public class DuctScreenHandler extends ScreenHandler {
  public static final int SLOT_COUNT = 1;
  private final Inventory inventory;
  private final PropertyDelegate propertyDelegate;

  public DuctScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, new SimpleInventory(SLOT_COUNT), new ArrayPropertyDelegate(SLOT_COUNT));
  }

  public DuctScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
    super(DUCT_SCREEN_HANDLER, syncId);
    this.propertyDelegate = propertyDelegate;
    this.inventory = inventory;
    checkSize(inventory, SLOT_COUNT);
    inventory.onOpen(playerInventory.player);
    this.addProperties(propertyDelegate);
    this.addSlots(playerInventory);
  }

  private void addSlots(PlayerInventory playerInventory) {
    int j;

    // Duct inventory
    this.addSlot(new DuctSlot(inventory, 0, 44 + 2 * 18, 20, this));
    // Player inventory
    for (j = 0; j < 3; ++j) {
      for (int k = 0; k < 9; ++k) {
        this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, j * 18 + 51));
      }
    }
    // Player hotbar
    for (j = 0; j < 9; ++j) {
      this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 109));
    }
  }

  @Override
  public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
    if (slotIndex == 0) {
      if (actionType == SlotActionType.PICKUP && getCursorStack().isEmpty() && !getSlot(slotIndex).hasStack()) {
        this.toggleSlot(slotIndex);
      }
    }

    super.onSlotClick(slotIndex, button, actionType, player);
  }

  public void toggleSlot(int slot) {
    DuctSlot ductSlot = (DuctSlot)this.getSlot(slot);
    this.propertyDelegate.set(ductSlot.id, this.isSlotDisabled(slot) ? DuctBlockEntity.FILTER_DISABLED : DuctBlockEntity.FILTER_ENABLED);
    this.sendContentUpdates();
  }

  public boolean isSlotDisabled(int slot) {
    if (slot == 0) {
      return this.propertyDelegate.get(slot) == DuctBlockEntity.FILTER_ENABLED;
    } else {
      return false;
    }
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int slot) {
    ItemStack itemStack = ItemStack.EMPTY;
    Slot slot2 = this.slots.get(slot);
    if (slot2.hasStack()) {
      ItemStack itemStack2 = slot2.getStack();
      itemStack = itemStack2.copy();
      if (slot < this.inventory.size()
        ? !this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true)
        : !this.insertItem(itemStack2, 0, this.inventory.size(), false)
      ) {
        return ItemStack.EMPTY;
      }
      if (itemStack2.isEmpty()) {
        slot2.setStack(ItemStack.EMPTY);
      } else {
        slot2.markDirty();
      }
    }
    return itemStack;
  }

  @Override
  public void onClosed(PlayerEntity player) {
    super.onClosed(player);
    this.inventory.onClose(player);
  }
}
