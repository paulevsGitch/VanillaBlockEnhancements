package paulevs.vbe.mixin.common;

import net.minecraft.container.CraftingContainer;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.VBE;

@Mixin(CraftingContainer.class)
public class CraftingContainerMixin {
	@Shadow public CraftingInventory inventory;
	@Shadow private Level level;
	
	@Inject(method = "onClosed", at = @At("HEAD"), cancellable = true)
	public void vbe_onClosed(PlayerEntity player, CallbackInfo info) {
		if (!VBE.DISABLE_WORKBENCH_DROP.getValue()) return;
		info.cancel();
		if (this.level.isRemote) return;
		PlayerInventory inventory = player.inventory;
		vbe_addOrDrop(player, inventory.getCursorItem());
		inventory.setCursorItem(null);
		for (byte index = 0; index < 9; index++) {
			vbe_addOrDrop(player, this.inventory.getItem(index));
		}
	}
	
	@Unique
	private void vbe_addOrDrop(PlayerEntity player, ItemStack stack) {
		if (stack == null) return;
		if (!player.inventory.addStack(stack)) {
			player.dropItem(stack);
		}
	}
}
