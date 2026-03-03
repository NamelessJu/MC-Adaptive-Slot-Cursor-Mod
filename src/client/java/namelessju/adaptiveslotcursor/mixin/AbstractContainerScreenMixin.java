package namelessju.adaptiveslotcursor.mixin;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphics;
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
    
    @Inject(at = @At("RETURN"), method = "renderContents")
    private void adaptiveslotcursor$afterRenderContents(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (hoveredSlot != null) {
            ItemStack cursorItem = draggingItem.isEmpty() ? menu.getCarried() : draggingItem;
            if (!cursorItem.isEmpty()) {
                if (!hoveredSlot.mayPlace(cursorItem)) {
                    guiGraphics.requestCursor(CursorTypes.NOT_ALLOWED);
                }
                else {
                    guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
                }
            }
            else if (hoveredSlot.hasItem()) {
                guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
            }
        }
    }
}