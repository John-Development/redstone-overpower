package net.redstoneOverpower.block.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class DuctSlot extends Slot {
  private final DuctScreenHandler ductScreenHandler;

  public DuctSlot(Inventory inventory, int index, int x, int y, DuctScreenHandler ductScreenHandler) {
    super(inventory, index, x, y);
    this.ductScreenHandler = ductScreenHandler;
  }

  public void markDirty() {
    super.markDirty();
    this.ductScreenHandler.onContentChanged(this.inventory);
  }
}
