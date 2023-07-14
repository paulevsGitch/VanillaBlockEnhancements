package paulevs.vbe.mixin.server;

import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.minecraft.server.ServerPlayerInterractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInterractionManager.class)
public class ServerPlayerInteractionManagerMixin {
	@Inject(
		method = "useOnBlock(Lnet/minecraft/entity/player/PlayerBase;Lnet/minecraft/level/Level;Lnet/minecraft/item/ItemStack;IIII)Z",
		at = @At("HEAD"), cancellable = true
	)
	private void vbe_onUseBlock(PlayerBase player, Level level, ItemStack stack, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> info) {
		if (player.isChild()) info.setReturnValue(stack.useOnBlock(player, level, x, y, z, side));
	}
}
