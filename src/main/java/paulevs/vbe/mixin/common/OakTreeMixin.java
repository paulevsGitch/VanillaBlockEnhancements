package paulevs.vbe.mixin.common;

import net.minecraft.block.LeavesBlock;
import net.minecraft.level.structure.OakTree;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import paulevs.vbe.block.VBEBlocks;

@Mixin(OakTree.class)
public class OakTreeMixin {
	@Redirect(method = "generate", at = @At(value = "FIELD", target = "Lnet/minecraft/block/LeavesBlock;id:I", opcode = Opcodes.GETFIELD))
	private int injected(LeavesBlock block) {
		return VBEBlocks.OAK_LEAVES.id;
	}
}
