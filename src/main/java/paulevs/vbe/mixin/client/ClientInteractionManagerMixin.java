package paulevs.vbe.mixin.client;

import net.minecraft.client.ClientInteractionManager;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientInteractionManager.class)
public class ClientInteractionManagerMixin {
	@Inject(
		method = "useOnBlock",
		at = @At("HEAD"), cancellable = true
	)
	private void vbe_onUseBlock(PlayerEntity player, Level level, ItemStack stack, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> info) {
		if (stack != null && player.isChild()) info.setReturnValue(stack.useOnBlock(player, level, x, y, z, side));
	}
}
