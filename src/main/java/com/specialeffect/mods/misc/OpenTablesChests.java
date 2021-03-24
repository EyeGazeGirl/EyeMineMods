/**
 * Copyright (C) 2016-2020 Kirsty McNaught
 * 
 * Developed for SpecialEffect, www.specialeffect.org.uk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.specialeffect.mods.misc;

import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3i;
import org.lwjgl.glfw.GLFW;

import com.specialeffect.messages.ActivateBlockAtPosition;
import com.specialeffect.mods.ChildMod;
import com.specialeffect.mods.utils.KeyWatcher;
import com.specialeffect.utils.ChildModWithConfig;
import com.specialeffect.utils.CommonStrings;
import com.specialeffect.utils.ModUtils;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;


public class OpenTablesChests 

extends ChildMod implements ChildModWithConfig
{

	public final String MODID = "opentableschests";

	private static KeyBinding mOpenChestKB;
	private static KeyBinding mOpenCraftingTableKB;	
	
    private static int mRadius = 5;
    

    public void setup(final FMLCommonSetupEvent event)
    {
		
		this.setupChannel(MODID, 1);
        
        int id = 0;        
        channel.registerMessage(id++, ActivateBlockAtPosition.class, ActivateBlockAtPosition::encode, 
        		ActivateBlockAtPosition::decode, ActivateBlockAtPosition.Handler::handle);

				
		// Register key bindings	
		mOpenChestKB = new KeyBinding("Open chest", GLFW.GLFW_KEY_LEFT_BRACKET, CommonStrings.EYEGAZE_EXTRA);
		mOpenCraftingTableKB = new KeyBinding("Open crafting table", GLFW.GLFW_KEY_RIGHT_BRACKET, CommonStrings.EYEGAZE_EXTRA);
		ClientRegistry.registerKeyBinding(mOpenChestKB);
		ClientRegistry.registerKeyBinding(mOpenCraftingTableKB);
	}

	public void syncConfig() {
        
	}

	// Search for closest block of a certain class, within maximum radius
	private static BlockPos findClosestBlockOfType(String className, PlayerEntity player, World world, int radius) {
		BlockPos playerPos = player.getPosition();		
	    Class<?> classType;
    	BlockPos closestBlockPos = null;

		try {
			classType = Class.forName(className);
			
	    	// Find closest chest (within radius)
	    	double closestDistanceSq = Double.MAX_VALUE;
	    	for (int x = -radius; x <= radius; x++) {
	    		for (int z = -radius; z <= radius; z++) {
	    			for (int y = -radius; y <= radius; y++) { 

	    				BlockPos blockPos = playerPos.add(x, y, z);

	    				// Check if block is appropriate class
	    				Block block = world.getBlockState(blockPos).getBlock();
	    				if (classType.isInstance(block)) {
	    					double distSq = playerPos.distanceSq(new Vector3i(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
	    					if (distSq < closestDistanceSq) {
	    						closestBlockPos = blockPos;
	    						closestDistanceSq = distSq;
	    					}
	    				}
	    			}
	    		}
	    	}
		} catch (ClassNotFoundException e) {
			LOGGER.debug("Could not find class: " + className);
		}
	    return closestBlockPos;
	}


    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) { 
    	if (ModUtils.hasActiveGui()) { return; }	    
	    if (event.getAction() != GLFW.GLFW_PRESS) { return; }

	    if (KeyWatcher.f3Pressed) { return; }

		if(mOpenChestKB.getKey().getKeyCode() == event.getKey()) {
			PlayerEntity player = Minecraft.getInstance().player;
			World world = Minecraft.getInstance().world;

			BlockPos closestBlockPos = OpenTablesChests.findClosestBlockOfType(
					ChestBlock.class.getName(), player, world, mRadius);
			
			// Ask server to open 
			if (null == closestBlockPos) {
				player.sendMessage(new StringTextComponent(
						"No chests found in range"), Util.DUMMY_UUID);
			}
			else {
                channel.sendToServer(new ActivateBlockAtPosition(closestBlockPos));
			}
		}
		else if(mOpenCraftingTableKB.getKey().getKeyCode() == event.getKey()) {
			PlayerEntity player = Minecraft.getInstance().player;
			World world = Minecraft.getInstance().world;

			BlockPos closestBlockPos = OpenTablesChests.findClosestBlockOfType(
					CraftingTableBlock.class.getName(), player, world, mRadius);

			// Ask server to open 
			if (null == closestBlockPos) {
				player.sendMessage(new StringTextComponent(
						"No crafting tables found in range"), Util.DUMMY_UUID);
			}
			else {
				channel.sendToServer(new ActivateBlockAtPosition(closestBlockPos));
			}
		}
	}

}
