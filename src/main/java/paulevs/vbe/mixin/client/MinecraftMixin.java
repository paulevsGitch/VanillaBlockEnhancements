package paulevs.vbe.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.listeners.CommonListener;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "init", at = @At("TAIL"))
	private void vbe_onInit(CallbackInfo info) {
		CommonListener.blockRegistered = true;
	}
}
