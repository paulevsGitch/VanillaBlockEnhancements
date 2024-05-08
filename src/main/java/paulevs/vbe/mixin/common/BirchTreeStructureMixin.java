package paulevs.vbe.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.level.structure.BirchTreeStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import paulevs.vbe.block.VBEBlocks;

@Mixin(BirchTreeStructure.class)
public class BirchTreeStructureMixin {
	@ModifyExpressionValue(
		method = "generate",
		at = @At(value = "FIELD", target = "Lnet/minecraft/block/LeavesBlock;id:I")
	)
	private int mfb_replaceLeaves(int originalID) {
		return VBEBlocks.BIRCH_LEAVES.id;
	}
}
