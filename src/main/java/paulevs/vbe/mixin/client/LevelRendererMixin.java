package paulevs.vbe.mixin.client;

import net.minecraft.block.BaseBlock;
import net.minecraft.client.render.LevelRenderer;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import paulevs.vbe.render.VBEBlockRenderer;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow private Level level;
	
	@Inject(method = "renderBlockOutline", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/block/BaseBlock;updateBoundingBox(Lnet/minecraft/level/BlockView;III)V",
		shift = Shift.AFTER
	), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void vbe_renderBlockOutline(PlayerBase player, HitResult hit, int arg3, ItemStack stack, float par5, CallbackInfo info, float var6, int blockID) {
		VBEBlockRenderer.startSelectionRendering(level, BaseBlock.BY_ID[blockID], hit);
	}
	
	@Inject(method = "renderBlockOutline", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/render/LevelRenderer;renderBox(Lnet/minecraft/util/maths/Box;)V",
		shift = Shift.AFTER
	))
	public void vbe_resetBoundingBox(PlayerBase player, HitResult hit, int arg3, ItemStack stack, float par5, CallbackInfo info) {
		VBEBlockRenderer.endSelectionRendering();
	}
}
