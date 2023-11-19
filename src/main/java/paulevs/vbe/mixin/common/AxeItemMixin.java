package paulevs.vbe.mixin.common;

import net.minecraft.block.Block;
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
	@Shadow private static Block[] effectiveBlocks;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void vbe_onInit(CallbackInfo info) {
		List<Block> blocks = new ArrayList<>(Arrays.asList(effectiveBlocks));
		blocks.add(Block.WORKBENCH);
		blocks.add(Block.WOOD_STAIRS);
		blocks.add(Block.WOOD_DOOR);
		blocks.add(Block.STANDING_SIGN);
		blocks.add(Block.WALL_SIGN);
		blocks.add(Block.FENCE);
		blocks.add(Block.WOODEN_PRESSURE_PLATE);
		blocks.add(VBEBlocks.OAK_SLAB_FULL);
		blocks.add(VBEBlocks.OAK_SLAB_HALF);
		blocks.add(VBEBlocks.OAK_LOG);
		blocks.add(VBEBlocks.SPRUCE_LOG);
		blocks.add(VBEBlocks.BIRCH_LOG);
		effectiveBlocks = blocks.toArray(Block[]::new);
	}
}
