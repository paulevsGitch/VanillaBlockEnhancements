package paulevs.vbe.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.item.tool.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.vbe.block.VBEBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin {
	@Shadow private static Block[] effectiveBlocks;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void vbe_onInit(CallbackInfo info) {
		List<Block> blocks = new ArrayList<>(Arrays.asList(effectiveBlocks));
		blocks.add(Block.FURNACE);
		blocks.add(Block.FURNACE_LIT);
		blocks.add(Block.COBBLESTONE_STAIRS);
		blocks.add(Block.IRON_DOOR);
		blocks.add(Block.BUTTON);
		blocks.add(Block.STONE_PRESSURE_PLATE);
		blocks.add(Block.REDSTONE_ORE);
		blocks.add(Block.REDSTONE_ORE_LIT);
		blocks.add(VBEBlocks.STONE_SLAB_HALF);
		blocks.add(VBEBlocks.STONE_SLAB_FULL);
		blocks.add(VBEBlocks.COBBLESTONE_SLAB_HALF);
		blocks.add(VBEBlocks.COBBLESTONE_SLAB_FULL);
		blocks.add(VBEBlocks.SANDSTONE_SLAB_HALF);
		blocks.add(VBEBlocks.SANDSTONE_SLAB_FULL);
		effectiveBlocks = blocks.toArray(Block[]::new);
	}
}
