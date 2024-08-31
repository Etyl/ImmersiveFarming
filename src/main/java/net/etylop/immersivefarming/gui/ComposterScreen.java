/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package net.etylop.immersivefarming.gui;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonBoolean;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.client.gui.info.MultitankArea;
import blusunrize.immersiveengineering.client.gui.info.TooltipArea;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.network.MessageBlockEntitySync;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.etylop.immersivefarming.block.multiblocks.composter.ComposterBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.util.List;

public class ComposterScreen extends IEContainerScreen<ComposterContainer>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(ImmersiveFarming.MOD_ID, "textures/gui/composter.png");

	private final ComposterBlockEntity tile;

	public ComposterScreen(ComposterContainer container, Inventory inventoryPlayer, Component title)
	{
		super(container, inventoryPlayer, title, TEXTURE);
		this.tile = container.tile;
		this.imageHeight = 167;
		this.inventoryLabelY = this.imageHeight-91;
	}

	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		return ImmutableList.of(
				new EnergyInfoArea(leftPos+158, topPos+22, tile.energyStorage),
				new TooltipArea(
						new Rect2i(leftPos+106, topPos+61, 30, 16),
						() -> new TranslatableComponent(Lib.GUI_CONFIG+"mixer.output"+(tile.outputAll?"All": "Single"))
				),
				new MultitankArea(new Rect2i(leftPos+76, topPos+11, 58, 47), tile.tank)
		);
	}

	@Override
	public void init()
	{
		super.init();
		this.clearWidgets();
		this.addRenderableWidget(new GuiButtonBoolean(leftPos+106, topPos+61, 30, 16, "", tile.outputAll, TEXTURE, 176, 82, 1,
				btn -> {
					CompoundTag tag = new CompoundTag();
					tile.outputAll = !btn.getState();
					tag.putBoolean("outputAll", tile.outputAll);
					ImmersiveEngineering.packetHandler.sendToServer(new MessageBlockEntitySync(tile, tag));
					fullInit();
				}));
	}

	@Override
	protected void drawContainerBackgroundPre(@Nonnull PoseStack transform, float f, int mx, int my)
	{
		transform.pushPose();
		MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

		for(MultiblockProcess<ComposterRecipe> process : tile.processQueue)
			if(process instanceof MultiblockProcessInMachine<?> inMachine)
			{
				float mod = 1-(process.processTick/(float)process.getMaxTicks(tile.getLevel()));
				for(int slot : inMachine.getInputSlots())
				{
					int h = (int)Math.max(1, mod*16);
					this.blit(transform, leftPos+24+slot%2*21, topPos+7+slot/2*18+(16-h), 176, 16-h, 2, h);
				}
			}

		buffers.endBatch();
	}
}
