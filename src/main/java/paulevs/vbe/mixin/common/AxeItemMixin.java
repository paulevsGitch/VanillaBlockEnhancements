package paulevs.vbe.mixin.common;

import net.minecraft.block.BaseBlock;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.block.VBEBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(AxeItem.class)
public class AxeItemMixin {
	@Shadow private static BaseBlock[] effectiveBlocks;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void vbe_onInit(CallbackInfo info) {
		List<BaseBlock> blocks = new ArrayList<>(Arrays.asList(effectiveBlocks));
		blocks.add(BaseBlock.WORKBENCH);
		blocks.add(BaseBlock.WOOD_STAIRS);
		blocks.add(BaseBlock.WOOD_DOOR);
		blocks.add(BaseBlock.STANDING_SIGN);
		blocks.add(BaseBlock.WALL_SIGN);
		blocks.add(BaseBlock.FENCE);
		blocks.add(BaseBlock.WOODEN_PRESSURE_PLATE);
		blocks.add(VBEBlocks.OAK_SLAB_FULL);
		blocks.add(VBEBlocks.OAK_SLAB_HALF);
		blocks.add(VBEBlocks.OAK_LOG);
		blocks.add(VBEBlocks.SPRUCE_LOG);
		blocks.add(VBEBlocks.BIRCH_LOG);
		effectiveBlocks = blocks.toArray(BaseBlock[]::new);
	}
}
