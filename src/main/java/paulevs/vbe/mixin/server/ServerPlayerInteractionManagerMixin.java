package paulevs.vbe.mixin.server;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.minecraft.server.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.VBEFullSlabBlock;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
	@Shadow public PlayerEntity player;
	
	@Inject(
		method = "useOnBlock",
		at = @At("HEAD"), cancellable = true
	)
	private void vbe_onUseBlock(PlayerEntity player, Level level, ItemStack stack, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> info) {
		if (stack != null && player.isChild()) info.setReturnValue(stack.useOnBlock(player, level, x, y, z, side));
	}
	
	@Inject(method = "activateBlock", at = @At(value = "HEAD"))
	private void activateBlock(int i, int j, int k, int l, CallbackInfo info) {
		VBEFullSlabBlock.player = this.player;
	}
}
