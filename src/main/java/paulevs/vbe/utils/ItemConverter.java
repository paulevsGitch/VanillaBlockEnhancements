package paulevs.vbe.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import paulevs.vbe.VBE;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.item.VBEItems;

public class ItemConverter {
	public static int getID(int id, int damage) {
		Item item = null;
		if (id == Block.STONE_SLAB.id && VBE.ENHANCED_SLABS.getValue()) item = VBEBlocks.getHalfSlabByMeta(damage).asItem();
		if (id == Block.LOG.id) item = VBEBlocks.getLogByMeta(damage).asItem();
		if (id == Block.LEAVES.id) item = VBEBlocks.getLeavesByMeta(damage).asItem();
		if (id == Item.woodDoor.id) item = VBEItems.OAK_DOOR.asItem();
		if (id == Item.ironDoor.id) item = VBEItems.IRON_DOOR.asItem();
		return item == null ? -1 : item.id;
	}
	
	public static int getDamage(int id, int damage) {
		return id == Block.STONE_SLAB.id && VBE.ENHANCED_SLABS.getValue() ? damage : 0;
	}
}
