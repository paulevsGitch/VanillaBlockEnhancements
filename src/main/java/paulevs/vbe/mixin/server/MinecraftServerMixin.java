package paulevs.vbe.mixin.server;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.listeners.CommonListener;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(method = "start", at = @At("TAIL"))
	private void vbe_onInit(CallbackInfoReturnable<Boolean> info) {
		CommonListener.blockRegistered = true;
	}
}
