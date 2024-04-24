package paulevs.vbe.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;

public class ItemConverter {
	public static int getID(int id, int damage) {
		if (id == Block.STONE_SLAB.id && VBE.ENHANCED_SLABS.getValue()) return VBEBlocks.getHalfSlabByMeta(damage).asItem().id;
		if (id == Block.LOG.id) return VBEBlocks.getLogByMeta(damage).asItem().id;
		if (id == Block.LEAVES.id) return VBEBlocks.getLeavesByMeta(damage).asItem().id;
		if (id == Item.woodDoor.id) return VBEItems.OAK_DOOR.asItem().id;
		if (id == Item.ironDoor.id) return VBEItems.IRON_DOOR.asItem().id;
		return -1;
	}
}
