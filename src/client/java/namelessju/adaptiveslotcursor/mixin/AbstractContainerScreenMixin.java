package namelessju.adaptiveslotcursor.mixin;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Shadow @Final
    protected AbstractContainerMenu menu;
    @Shadow
    protected Slot hoveredSlot;
    @Shadow
    private ItemStack draggingItem;

    @Inject(at = @At("RETURN"), method = "extractContents")
    private void handleSlotCursor(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (hoveredSlot == null) return;
        
        ItemStack cursorItem = draggingItem.isEmpty() ? menu.getCarried() : draggingItem;
        if (!cursorItem.isEmpty()) {
            if (hoveredSlot.mayPlace(cursorItem)) {
                // dragging item & can be placed here
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }
            else {
                ItemStack slotItem = hoveredSlot.getItem();
                if (!slotItem.isEmpty()) {
                    // dragging item, can not be placed here but may pick up items
                    graphics.requestCursor(
                        ItemStack.isSameItemSameComponents(cursorItem, slotItem)
                            && cursorItem.getCount() < cursorItem.getMaxStackSize()
                        ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED
                    );
                }
                else {
                    // dragging item, can not be placed here & no items to pick up
                    graphics.requestCursor(CursorTypes.NOT_ALLOWED);
                }
            }
        }
        else if (hoveredSlot.hasItem()) {
            // empty cursor and hovering item in slot
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }
}