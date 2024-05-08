package paulevs.vbe.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitType;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BeforeBlockRemoved;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.util.math.Direction.AxisDirection;
import paulevs.vbe.render.CustomBreakingRender;
import paulevs.vbe.utils.LevelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class VBEFullSlabBlock extends TemplateBlock implements BeforeBlockRemoved, CustomBreakingRender {
	private final Function<Integer, Integer> textureGetter;
	private static BlockState blockState;
	public static PlayerEntity player;
	public static HitResult hit;
	private Block halfBlock;
	
	public VBEFullSlabBlock(Identifier id, Material material) {
		super(id, material);
		setTranslationKey(id);
		this.textureGetter = side -> this.texture;
	}
	
	public VBEFullSlabBlock(Identifier id, Block source) {
		super(id, source.material);
		setTranslationKey(id);
		Block.EMITTANCE[this.id] = Block.EMITTANCE[source.id];
		this.resistance = source.getHardness() * 5F;
		this.hardness = source.getHardness() * 0.5F;
		setSounds(source.sounds);
		this.textureGetter = source::getTexture;
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(VBEBlockProperties.AXIS);
	}
	
	public void setHalfBlock(Block halfBlock) {
		this.halfBlock = halfBlock;
	}
	
	@Override
	public void onBlockRemoved(Level level, int x, int y, int z) {
		if (!blockState.isOf(this) || hit == null || hit.type != HitType.BLOCK) return;
		Axis axis = blockState.get(VBEBlockProperties.AXIS);
		float delta = 0;
		switch (axis) {
			case X -> delta = (float) (hit.pos.x - hit.x);
			case Y -> delta = (float) (hit.pos.y - hit.y);
			case Z -> delta = (float) (hit.pos.z - hit.z);
		}
		Direction facing = Direction.from(axis, delta > 0.5F ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
		level.setBlockState(x, y, z, halfBlock.getDefaultState().with(VBEBlockProperties.DIRECTION, facing));
	}
	
	@Override
	public List<ItemStack> getDropList(Level level, int x, int y, int z, BlockState state, int meta) {
		if (blockState.isOf(this) && player != null) {
			return Collections.singletonList(new ItemStack(halfBlock));
		}
		return Collections.singletonList(new ItemStack(halfBlock, 2));
	}
	
	@Override
	public void doesBoxCollide(Level level, int x, int y, int z, Box box, ArrayList list) {
		this.setBoundingBox(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
		super.doesBoxCollide(level, x, y, z, box, list);
	}
	
	@Override
	public void beforeBlockRemoved(Level level, int x, int y, int z) {
		blockState = level.getBlockState(x, y, z);
		if (player == null || !blockState.isOf(this)) return;
		hit = LevelUtil.raycast(level, player);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void vbe_setSelection(BlockState state, float dx, float dy, float dz) {
		Axis axis = state.get(VBEBlockProperties.AXIS);
		switch (axis) {
			case X -> this.setBoundingBox(dx > 0.5F ? 0.5F : 0.0F, 0.0F, 0.0F, dx > 0.5F ? 1.0F : 0.5F, 1.0F, 1.0F);
			case Y -> this.setBoundingBox(0.0F, dy > 0.5F ? 0.5F : 0.0F, 0.0F, 1.0F, dy > 0.5F ? 1.0F : 0.5F, 1.0F);
			case Z -> this.setBoundingBox(0.0F, 0.0F, dz > 0.5F ? 0.5F : 0.0F, 1.0F, 1.0F, dz > 0.5F ? 1.0F : 0.5F);
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public BlockState vbe_getBreakingState(BlockState state) {
		@SuppressWarnings("deprecation")
		Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
		HitResult hit = LevelUtil.raycast(minecraft.level, minecraft.player);
		if (hit == null || hit.type != HitType.BLOCK) return state;
		Axis axis = state.get(VBEBlockProperties.AXIS);
		state = halfBlock.getDefaultState();
		float delta = 0;
		switch (axis) {
			case X -> delta = (float) (hit.pos.x - hit.x);
			case Y -> delta = (float) (hit.pos.y - hit.y);
			case Z -> delta = (float) (hit.pos.z - hit.z);
		}
		Direction facing = Direction.from(axis, delta < 0.5F ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
		return state.with(VBEBlockProperties.DIRECTION, facing);
	}
	
	@Override
	public int getTexture(int side) {
		return textureGetter.apply(side);
	}
}
