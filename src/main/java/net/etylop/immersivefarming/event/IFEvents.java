package net.etylop.immersivefarming.event;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.IFBlocks;
import net.etylop.immersivefarming.block.custom.Soil;
import net.etylop.immersivefarming.utils.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IFEvents {
    @Mod.EventBusSubscriber(modid = ImmersiveFarming.MOD_ID)
    public static class ForgeEvents {
        private static final Map<ChunkPos, List<BlockPos>> chunkCrops = new HashMap<>();
        private static final Map<BlockPos, Long> cropDate = new HashMap<>();

        /*
        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            Block target = event.getWorld().getBlockState(event.getPos()).getBlock();
            if (Registry.BLOCK.getHolderOrThrow(Registry.BLOCK.getResourceKey(target).get()).is(ModTags.Blocks.TILLABLE_BLOCK) &&
                    !event.getWorld().isClientSide() &&
                    event.getPlayer().getMainHandItem().getItem() instanceof HoeItem) {

                event.getWorld().setBlock(event.getPos(), IFBlocks.SOIL.get().defaultBlockState(), 3);
                event.getPlayer().getMainHandItem().hurtAndBreak(1, event.getPlayer(), (val) -> {
                    val.broadcastBreakEvent(event.getPlayer().getUsedItemHand());
                });
            }
        }*/

        @SubscribeEvent
        public static void onHoeUse(UseHoeEvent event) {
            Level level = event.getContext().getLevel();
            Block target = level.getBlockState(event.getContext().getClickedPos()).getBlock();
            if (Registry.BLOCK.getHolderOrThrow(Registry.BLOCK.getResourceKey(target).get()).is(ModTags.Blocks.TILLABLE_BLOCK) &&
                    !level.isClientSide()) {

                level.setBlock(event.getContext().getClickedPos(), IFBlocks.SOIL.get().defaultBlockState(), 3);
                event.getPlayer().getMainHandItem().hurtAndBreak(1, event.getPlayer(), (val) -> {
                    val.broadcastBreakEvent(event.getPlayer().getUsedItemHand());
                });
            }
        }

        @SubscribeEvent
        public static void onBonemeal(BonemealEvent event) {
            if (event.getBlock().getBlock() instanceof GrassBlock) {
                return;
            }
            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onTrampleFarmland(BlockEvent.FarmlandTrampleEvent event) {
            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onCropGrowth(BlockEvent.CropGrowEvent.Pre event) {
            LevelAccessor level = event.getWorld();
            if (level.getBiome(event.getPos()).containsTag(Tags.Biomes.IS_COLD)) {
                event.setResult(Event.Result.DENY);
                return;
            }


            if (ModList.get().isLoaded("sereneseasons")) {
                if (SeasonHelper.getSeasonState((Level) level).getSeason() == Season.WINTER) {
                    System.out.println("winter !");
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }

            if (canCropGrow((Level) event.getWorld(), event.getPos())) {
                int fertilization = event.getWorld().getBlockState(event.getPos().below()).getValue(Soil.FERTILITY);
                if (fertilization == 0 && Math.random() < 0.75 * 0.5)
                    event.setResult(Event.Result.DENY);
                else if (fertilization == 1 && Math.random() < 0.5)
                    event.setResult(Event.Result.DENY);
                else
                    event.setResult(Event.Result.DEFAULT);
                return;
            }

            event.setResult(Event.Result.DENY);
        }

        private static boolean canCropGrow(Level level, BlockPos pos) {
            BlockState soil = level.getBlockState(pos.below());
            if ((soil.getBlock() instanceof Soil) && soil.getBlock().isFertile(soil, level, pos) && level.canSeeSky(pos))
                return true;
            else
                return false;
        }

        @SubscribeEvent
        public static void onHarvestCrop(BlockEvent.BreakEvent event) {
            BlockState block = event.getState();
            if (!(block.getBlock() instanceof CropBlock))
                return;

            BlockState soil = event.getWorld().getBlockState(event.getPos().below());
            if (!(soil.getBlock() instanceof Soil))
                return;

            soil.setValue(Soil.FERTILITY, 0);
        }
    }
}
