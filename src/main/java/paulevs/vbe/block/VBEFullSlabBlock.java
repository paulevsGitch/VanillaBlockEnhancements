package paulevs.vbe.block;

import net.minecraft.block.BaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlockBase;

import java.util.Collections;
import java.util.List;

public class VBEFullSlabBlock extends TemplateBlockBase {
	private BaseBlock halfBlock;
	
	public VBEFullSlabBlock(Identifier id, Material material) {
		super(id, material);
		setTranslationKey(id.toString());
	}
	
	public VBEFullSlabBlock(Identifier id, BaseBlock source) {
		this(id, source.material);
		setTranslationKey(id.toString());
		BaseBlock.EMITTANCE[this.id] = BaseBlock.EMITTANCE[source.id];
		setHardness(source.getHardness());
		setSounds(source.sounds);
	}
	
	@Override
	public void appendProperties(Builder<BaseBlock, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(VBEBlockProperties.AXIS);
	}
	
	public void setHalfBlock(BaseBlock halfBlock) {
		this.halfBlock = halfBlock;
	}
	
	@Override
	public List<ItemStack> getDropList(Level level, int x, int y, int z, BlockState state, int meta) {
		return Collections.singletonList(new ItemStack(halfBlock, 2));
	}
}
