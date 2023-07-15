package paulevs.vbe.mixin.client;

import net.minecraft.block.BaseBlock;
import net.minecraft.client.MultiPlayerClientInteractionManager;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiPlayerClientInteractionManager.class)
public class MultiPlayerClientInteractionManagerMixin {
	@Redirect(method = "playerDigBlock", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/block/BaseBlock;activate(Lnet/minecraft/level/Level;IIILnet/minecraft/entity/player/PlayerBase;)V"
	))
	private void injected(BaseBlock instance, Level i, int j, int k, int arg2, PlayerBase playerBase) {}
}
