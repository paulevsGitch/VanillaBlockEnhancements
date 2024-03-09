package paulevs.vbe.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.client.ClientInteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.SinglePlayerClientInteractionManager;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.vbe.block.VBEFullSlabBlock;

@Mixin(SinglePlayerClientInteractionManager.class)
public class SinglePlayerClientInteractionManagerMixin extends ClientInteractionManager {
	public SinglePlayerClientInteractionManagerMixin(Minecraft minecraft) {
		super(minecraft);
	}
	
	@Inject(method = "activateBlock", at = @At(value = "HEAD"))
	private void vbe_activateBlock(int i, int j, int k, int l, CallbackInfoReturnable<Boolean> info) {
		VBEFullSlabBlock.player = this.minecraft.player;
	}
	
	@WrapOperation(method = "playerDigBlock", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/block/Block;activate(Lnet/minecraft/level/Level;IIILnet/minecraft/entity/living/player/PlayerEntity;)V"
	))
	private void vbe_disableActivation(Block instance, Level i, int j, int k, int arg2, PlayerEntity player, Operation<Void> original) {}
}
