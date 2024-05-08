package paulevs.vbe.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;

public class VBELogBlock extends TemplateBlock {
	public VBELogBlock(Identifier id) {
		this(id, Material.WOOD);
	}
	
	public VBELogBlock(Identifier id, Material material) {
		super(id, material);
		setTranslationKey(id);
		setHardness(LOG.getHardness());
		setSounds(WOOD_SOUNDS);
		setDefaultState(getDefaultState().with(VBEBlockProperties.AXIS, Axis.Y));
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(VBEBlockProperties.AXIS);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Axis axis = context.getSide().getAxis();
		return getDefaultState().with(VBEBlockProperties.AXIS, axis);
	}
}
