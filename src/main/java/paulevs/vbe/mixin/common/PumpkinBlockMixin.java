package paulevs.vbe.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.PumpkinBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PumpkinBlock.class)
public class PumpkinBlockMixin {
	@ModifyExpressionValue(
		method = "canPlaceAt",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/level/Level;canSuffocate(III)Z")
	)
	private boolean vbe_canPlaceAt(boolean original) {
		return true;
	}
}
