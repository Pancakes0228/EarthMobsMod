package baguchan.earthmobsmod.block;

import baguchan.earthmobsmod.init.EarthParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.potion.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class ButtercupBlock extends FlowerBlock {
	public ButtercupBlock(Effect effectIn, int duration, Block.Properties propertiesIn) {
		super(effectIn, duration, propertiesIn);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for (int i2 = 0; i2 < 3; ++i2) {
			int j = rand.nextInt(2) * 2 - 1;
			int k = rand.nextInt(2) * 2 - 1;
			double d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
			double d1 = (double) ((float) pos.getY() + rand.nextFloat());
			double d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
			double d3 = (double) (rand.nextFloat() * (float) j);
			double d4 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
			double d5 = (double) (rand.nextFloat() * (float) k);
			if (rand.nextBoolean()) {
                worldIn.addParticle(EarthParticles.FLOWER_POLLEN, d0, d1, d2, d3, d4, d5);
            }
		}
	}
}