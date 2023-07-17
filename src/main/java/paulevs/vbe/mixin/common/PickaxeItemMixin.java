package paulevs.vbe.mixin.common;

import net.minecraft.block.BaseBlock;
import net.minecraft.item.PickaxeItem;
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
	@Shadow private static BaseBlock[] effectiveBlocks;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void vbe_onInit(CallbackInfo info) {
		List<BaseBlock> blocks = new ArrayList<>(Arrays.asList(effectiveBlocks));
		blocks.add(BaseBlock.FURNACE);
		blocks.add(BaseBlock.FURNACE_LIT);
		blocks.add(BaseBlock.COBBLESTONE_STAIRS);
		blocks.add(BaseBlock.IRON_DOOR);
		blocks.add(BaseBlock.BUTTON);
		blocks.add(BaseBlock.STONE_PRESSURE_PLATE);
		blocks.add(BaseBlock.REDSTONE_ORE);
		blocks.add(BaseBlock.REDSTONE_ORE_LIT);
		blocks.add(VBEBlocks.STONE_SLAB_HALF);
		blocks.add(VBEBlocks.STONE_SLAB_FULL);
		blocks.add(VBEBlocks.COBBLESTONE_SLAB_HALF);
		blocks.add(VBEBlocks.COBBLESTONE_SLAB_FULL);
		blocks.add(VBEBlocks.SANDSTONE_SLAB_HALF);
		blocks.add(VBEBlocks.SANDSTONE_SLAB_FULL);
		effectiveBlocks = blocks.toArray(BaseBlock[]::new);
	}
}
