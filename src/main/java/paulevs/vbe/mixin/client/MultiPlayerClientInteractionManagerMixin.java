package paulevs.vbe.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.client.MultiPlayerClientInteractionManager;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerClientInteractionManager.class)
public class MultiPlayerClientInteractionManagerMixin {
	@WrapOperation(method = "playerDigBlock", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/block/Block;activate(Lnet/minecraft/level/Level;IIILnet/minecraft/entity/living/player/PlayerEntity;)V"
	))
	private void vbe_disableActivation(Block instance, Level i, int j, int k, int arg2, PlayerEntity player, Operation<Void> original) {}
}
