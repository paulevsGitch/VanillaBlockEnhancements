package paulevs.vbe.mixin.common;

import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {
	@Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
	public void vbe_onSteppedOn(Level level, int x, int y, int z, Entity entity, CallbackInfo info) {
		info.cancel();
	}
}
