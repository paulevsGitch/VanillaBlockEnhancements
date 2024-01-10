package paulevs.vbe.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.living.player.PlayerEntity;
import paulevs.bhcreative.api.BlockSelectAPI;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;

public class CreativeUtil {
	private static final boolean NOT_INSTALLED = !FabricLoader.getInstance().isModLoaded("bhcreative");
	
	public static boolean isCreative(PlayerEntity player) {
		if (NOT_INSTALLED) return false;
		return player.creative_isCreative();
	}
	
	public static void registerBlockConverters() {
		if (NOT_INSTALLED) return;
		if (VBE.ENHANCED_SLABS.getValue()) {
			BlockSelectAPI.registerConverter(VBEBlocks.STONE_SLAB_FULL, VBEBlocks.STONE_SLAB_HALF.asItem());
			BlockSelectAPI.registerConverter(VBEBlocks.COBBLESTONE_SLAB_FULL, VBEBlocks.COBBLESTONE_SLAB_HALF.asItem());
			BlockSelectAPI.registerConverter(VBEBlocks.OAK_SLAB_FULL, VBEBlocks.OAK_SLAB_HALF.asItem());
			BlockSelectAPI.registerConverter(VBEBlocks.SANDSTONE_SLAB_FULL, VBEBlocks.SANDSTONE_SLAB_HALF.asItem());
		}
		BlockSelectAPI.registerConverter(Block.WOOD_DOOR, VBEItems.OAK_DOOR);
		BlockSelectAPI.registerConverter(Block.IRON_DOOR, VBEItems.IRON_DOOR);
	}
}
