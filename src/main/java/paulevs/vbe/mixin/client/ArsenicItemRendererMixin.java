package paulevs.vbe.mixin.client;

import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = ArsenicItemRenderer.class, remap = false)
public class ArsenicItemRendererMixin {
	@ModifyConstant(method = "renderVanilla", constant = @Constant(floatValue = 0.5F, ordinal = 0))
	private float vbe_changeBlockScale(float constant) {
		return 0.25F;
	}
}
