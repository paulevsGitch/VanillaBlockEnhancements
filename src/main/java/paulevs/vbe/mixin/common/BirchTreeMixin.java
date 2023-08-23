package paulevs.vbe.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.level.structure.BirchTree;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import paulevs.vbe.block.VBEBlocks;

@Mixin(BirchTree.class)
public class BirchTreeMixin {
	@ModifyExpressionValue(
		method = "generate",
		at = @At(value = "FIELD", target = "Lnet/minecraft/block/LeavesBlock;id:I")
	)
	private int mfb_replaceLeaves(int originalID) {
		return VBEBlocks.BIRCH_LEAVES.id;
	}
}
