package paulevs.vbe.mixin.common;

import net.minecraft.container.CraftingContainer;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingContainer.class)
public class CraftingContainerMixin {
	@Shadow public CraftingInventory inventory;
	@Shadow private Level level;
	
	@Inject(method = "onClosed", at = @At("HEAD"), cancellable = true)
	public void vbe_onClosed(PlayerBase player, CallbackInfo info) {
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
	private void vbe_addOrDrop(PlayerBase player, ItemStack stack) {
		if (stack == null) return;
		if (!player.inventory.addStack(stack)) {
			player.dropItem(stack);
		}
	}
}
