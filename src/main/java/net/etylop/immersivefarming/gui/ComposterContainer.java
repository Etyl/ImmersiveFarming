/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package net.etylop.immersivefarming.gui;

import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.IESlot.ICallbackContainer;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.etylop.immersivefarming.block.multiblocks.composter.ComposterBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

;

//TODO custom subclass of ItemStackHandler for markDirty etc
public class ComposterContainer extends IEBaseContainer<ComposterBlockEntity> implements ICallbackContainer
{
	public ComposterContainer(MenuType<?> type, int id, Inventory inventoryPlayer, ComposterBlockEntity tile)
	{
		super(type, tile, id);

		IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
				.orElseThrow(RuntimeException::new);
		for(int i = 0; i < 8; i++)
			this.addSlot(new IESlot.ContainerCallback(this, inv, i, 7+(i%2)*21, 7+(i/2)*18));
		slotCount = 8;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, 8+j*18, 86+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, 8+i*18, 144));
		addGenericData(GenericContainerData.energy(tile.energyStorage));
	}

	@Override
	public boolean canInsert(ItemStack stack, int slotNumer, Slot slotObject)
	{
		for(MultiblockProcess<ComposterRecipe> process : tile.processQueue)
			if(process instanceof MultiblockProcessInMachine)
				for(int s : ((MultiblockProcessInMachine)process).getInputSlots())
					if(s==slotNumer)
						return false;
		return true;
	}

	@Override
	public boolean canTake(ItemStack stack, int slotNumber, Slot slotObject)
	{
		for(MultiblockProcess<ComposterRecipe> process : tile.processQueue)
			if(process instanceof MultiblockProcessInMachine)
				for(int s : ((MultiblockProcessInMachine)process).getInputSlots())
					if(s==slotNumber)
						return false;
		return true;
	}
}