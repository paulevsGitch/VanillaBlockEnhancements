package paulevs.vbe.mixin.common;

import net.minecraft.item.ItemStack;
import net.minecraft.util.io.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.utils.ItemConverter;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Shadow public int itemId;
	@Shadow private int damage;
	
	@Inject(method = "<init>(III)V", at = @At("TAIL"))
	private void bve_onStackInit1(int i, int j, int k, CallbackInfo info) {
		int converted = ItemConverter.getID(itemId, damage);
		if (converted == -1) return;
		this.damage = ItemConverter.getDamage(itemId, damage);
		this.itemId = converted;
	}
	
	@Inject(method = "<init>(Lnet/minecraft/util/io/CompoundTag;)V", at = @At("TAIL"))
	private void bve_onStackInit2(CompoundTag tag, CallbackInfo info) {
		int converted = ItemConverter.getID(itemId, damage);
		if (converted == -1) return;
		this.damage = ItemConverter.getDamage(itemId, damage);
		this.itemId = converted;
	}
}
