package paulevs.vbe.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.world.BlockStateView;
import net.modificationstation.stationapi.mixin.arsenic.client.BlockRendererAccessor;
import paulevs.vbe.block.StairsShape;

@Environment(EnvType.CLIENT)
public class VBEBlockRenderer {
	private static BlockViewWrapper wrapper = new BlockViewWrapper();
	private static BaseBlock block;
	
	public static void startSelectionRendering(Level level, BaseBlock block, HitResult hit) {
		if (!(block instanceof CustomBreakingRender render)) return;
		BlockState state = level.getBlockState(hit.x, hit.y, hit.z);
		float dx = (float) (hit.pos.x - hit.x);
		float dy = (float) (hit.pos.y - hit.y);
		float dz = (float) (hit.pos.z - hit.z);
		render.vbe_setSelection(state, dx, dy, dz);
		VBEBlockRenderer.block = block;
	}
	
	public static void endSelectionRendering() {
		if (block == null) return;
		block.setBoundingBox(0, 0, 0, 1, 1, 1);
		block = null;
	}
	
	public static BlockState getBreakingState(BlockState state, BlockRendererAccessor blockRendererAccessor) {
		if (blockRendererAccessor.getTextureOverride() == -1) return state;
		if (!(state.getBlock() instanceof CustomBreakingRender render)) return state;
		return render.vbe_getBreakingState(state);
	}
	
	public static void renderStairs(StairsShape stairs, BlockState state, int x, int y, int z, BlockRenderer renderer) {
		stairs.vbe_getStairsShape(state).forEach(shape -> {
			block.minX = shape.minX;
			block.minY = shape.minY;
			block.minZ = shape.minZ;
			block.maxX = shape.maxX;
			block.maxY = shape.maxY;
			block.maxZ = shape.maxZ;
			renderer.renderFullCube(block, x, y, z);
		});
	}
	
	public static BlockView getBreakView(BlockView view, int x, int y, int z) {
		BlockState state = ((BlockStateView) view).getBlockState(x, y, z);
		if (state.getBlock() instanceof CustomBreakingRender render) {
			wrapper.setData(view, render.vbe_getBreakingState(state), x, y, z);
			return wrapper;
		}
		return view;
	}
}
