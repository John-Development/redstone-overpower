package net.redstoneOverpower.block.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import net.minecraft.screen.slot.Slot;

import static net.redstoneOverpower.utils.Initialiser.COPPER_HOPPER_SCREEN_HANDLER;

public class CopperHopperScreenHandler extends ScreenHandler {
  public static final int SLOT_COUNT = 4;
  private final Inventory inventory;

  public CopperHopperScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, new SimpleInventory(SLOT_COUNT));
  }

  public CopperHopperScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
    super(COPPER_HOPPER_SCREEN_HANDLER, syncId);
    int j;
    this.inventory = inventory;
    CopperHopperScreenHandler.checkSize(inventory, SLOT_COUNT);
    inventory.onOpen(playerInventory.player);

    // Hopper inventory
    for (j = 0; j < SLOT_COUNT; ++j) {
      this.addSlot(new Slot(inventory, j, 44 + 9 + j * 18, 20));
    }
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
