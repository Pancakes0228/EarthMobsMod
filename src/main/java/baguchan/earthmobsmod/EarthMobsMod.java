package baguchan.earthmobsmod;

import baguchan.earthmobsmod.client.ClientRegistrar;
import baguchan.earthmobsmod.entity.MooBloomEntity;
import baguchan.earthmobsmod.entity.MuddyPigEntity;
import baguchan.earthmobsmod.entity.RainbowSheepEntity;
import baguchan.earthmobsmod.entity.ai.GoToMudGoal;
import baguchan.earthmobsmod.init.*;
import net.minecraft.block.Block;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("earthmobsmod")
public class EarthMobsMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "earthmobsmod";

    public static  EarthMobsMod instance;

    public EarthMobsMod() {
        instance = this;
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::onBlockRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::onItemRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::onEntityRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Fluid.class, this::onFluidRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, this::onFeatureRegistry);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientRegistrar::setup));
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EarthConfig.COMMON_SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        EarthFeatures.addFeature();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        World world = event.getEntityLiving().world;

        LivingEntity livingEntity = event.getEntityLiving();


        if (livingEntity.handleFluidAcceleration(EarthTags.Fluids.MUD_WATER, 0.014D)) {
            if (livingEntity.getMotion().getY() < 0.0F) {
                livingEntity.setMotion(livingEntity.getMotion().mul(0.8F, 0.5F, 0.8F));
            } else {
                livingEntity.setMotion(livingEntity.getMotion().scale(0.9F));
            }

            if (livingEntity.isJumping) {
                livingEntity.setMotion(livingEntity.getMotion().add(0.0D, (double) 0.08F * livingEntity.getAttribute(ForgeMod.SWIM_SPEED.get()).getValue(), 0.0D));
            }

            livingEntity.fallDistance = 0;

        }

        if (event.getEntityLiving().getType() == EntityType.PIG && event.getEntityLiving().ticksExisted % 5 == 0 && livingEntity.handleFluidAcceleration(EarthTags.Fluids.MUD_WATER, 0.014D)) {
            if (!world.isRemote()) {
                MuddyPigEntity pigEntity = EarthEntitys.MUDDYPIG.create(world);
                pigEntity.copyDataFromOld(event.getEntityLiving());

                pigEntity.setNoAI(((PigEntity) livingEntity).isAIDisabled());
                if (livingEntity.hasCustomName()) {
                    pigEntity.setCustomName(livingEntity.getCustomName());
                    pigEntity.setCustomNameVisible(livingEntity.isCustomNameVisible());
                }

                if (((PigEntity) livingEntity).isHorseSaddled()) {
                    pigEntity.func_230264_L__();
                }

                if (livingEntity.isChild()) {
                    pigEntity.setGrowingAge(((PigEntity) livingEntity).getGrowingAge());
                }

                pigEntity.setFlowerColor(DyeColor.byId(world.getRandom().nextInt(DyeColor.values().length)));
                pigEntity.setHasFlower(false);
                pigEntity.setDry(true);

                livingEntity.world.getServer().getWorld(livingEntity.getEntityWorld().getDimensionKey()).removeEntityComplete(livingEntity, false);
                livingEntity.world.getServer().getWorld(livingEntity.getEntityWorld().getDimensionKey()).addEntity(pigEntity);

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        World world = event.getWorld();
        if (event.getTarget().getType() == EntityType.COW && !(event.getTarget() instanceof MooBloomEntity)) {
            if (stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                MooBloomEntity cowBloomEntity = EarthEntitys.MOOBLOOM.create(world);
                cowBloomEntity.setLocationAndAngles(event.getTarget().getPosX(), event.getTarget().getPosY(), event.getTarget().getPosZ(), event.getTarget().rotationYaw, event.getTarget().rotationPitch);
                cowBloomEntity.setNoAI(((AnimalEntity) event.getTarget()).isAIDisabled());
                if (event.getTarget().hasCustomName()) {
                    cowBloomEntity.setCustomName(event.getTarget().getCustomName());
                    cowBloomEntity.setCustomNameVisible(event.getTarget().isCustomNameVisible());
                }

                if (((AnimalEntity) event.getTarget()).isChild()) {
                    cowBloomEntity.setGrowingAge(((AnimalEntity) event.getTarget()).getGrowingAge());
                }

                if (!event.getPlayer().isCreative()) {
                    stack.shrink(1);
                }

                event.getWorld().addEntity(cowBloomEntity);

                event.getWorld().playSound(null, cowBloomEntity.getPosition(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 1.5F, 1.0F);
                event.getTarget().remove();
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        World world = event.getWorld();

        if (event.getEntity() instanceof MobEntity) {
            MobEntity livingEntity = (MobEntity) event.getEntity();
            if (livingEntity.getCreatureAttribute() != CreatureAttribute.UNDEAD) {
                //livingEntity.goalSelector.addGoal(0, new SwimMudGoal(livingEntity));
            }
        }

        if (event.getEntity().getType() == EntityType.PIG) {
            PigEntity pig = (PigEntity) event.getEntity();
            pig.goalSelector.addGoal(0, new GoToMudGoal(pig, 1.0D));
        }
    }

    @SubscribeEvent
    public void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity().getType() == EntityType.SHEEP) {
            SheepEntity oldEntity = (SheepEntity) event.getEntity();

            RainbowSheepEntity rainbowsheep = EarthEntitys.RAINBOW_SHEEP.create(oldEntity.world);
            rainbowsheep.setLocationAndAngles(oldEntity.getPosX(), oldEntity.getPosY(), oldEntity.getPosZ(), oldEntity.rotationYaw, oldEntity.rotationPitch);
            rainbowsheep.setNoAI(oldEntity.isAIDisabled());
            if (oldEntity.hasCustomName()) {
                rainbowsheep.setCustomName(oldEntity.getCustomName());
                rainbowsheep.setCustomNameVisible(oldEntity.isCustomNameVisible());
            }

            if (oldEntity.isChild()) {
                rainbowsheep.setGrowingAge(oldEntity.getGrowingAge());
            }

            oldEntity.world.addEntity(rainbowsheep);
            oldEntity.remove();
        }

    }

    public void onBlockRegistry(final RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        EarthBlocks.registerBlocks(registry);
    }

    public void onItemRegistry(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        EarthBlocks.registerItemBlocks(registry);
        EarthItems.registerItems(registry);
    }

    public void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();

        EarthEntitys.registerEntity(registry);
    }

    public void onFeatureRegistry(final RegistryEvent.Register<Feature<?>> event) {
        IForgeRegistry<Feature<?>> registry = event.getRegistry();

        EarthFeatures.register(registry);
    }

    public void onFluidRegistry(final RegistryEvent.Register<Fluid> event) {
        IForgeRegistry<Fluid> registry = event.getRegistry();

        EarthFluids.register(registry);
    }
}
