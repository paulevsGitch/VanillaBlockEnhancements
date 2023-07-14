package paulevs.vbe.mixin.common;

import net.minecraft.block.PumpkinBlock;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PumpkinBlock.class)
public class PumpkinBlockMixin {
	@Redirect(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/level/Level;canSuffocate(III)Z"))
	private boolean injected(Level level, int x, int y, int z) {
		return true;
	}
}
