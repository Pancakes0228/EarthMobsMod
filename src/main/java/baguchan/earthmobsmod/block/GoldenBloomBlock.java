package baguchan.earthmobsmod.block;

import baguchan.earthmobsmod.client.particle.EarthParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class GoldenBloomBlock extends FlowerBlock {
	public GoldenBloomBlock(Effect effectIn, int duration, Block.Properties propertiesIn) {
		super(effectIn, duration, propertiesIn);
	}

	@OnlyIn(Dist.CLIENT)
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
				EarthParticles.FLOWER_POLLEN.spawn(worldIn, d0, d1, d2, d3, d4, d5);
			}

		}

	}

	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote) {
			if (entityIn instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity) entityIn;
				if (!((PlayerEntity) entityIn).isCreative()) {
					playerEntity.addPotionEffect(new EffectInstance(Effects.REGENERATION, 40));
				}
			} else if (entityIn instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity) entityIn;
				livingentity.addPotionEffect(new EffectInstance(Effects.REGENERATION, 40));
			}

		}
	}
}