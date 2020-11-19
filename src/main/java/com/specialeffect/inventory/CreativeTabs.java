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

package com.specialeffect.inventory;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.specialeffect.mods.utils.KeyWatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(CreativeTabs.MODID)
public class CreativeTabs 
{
	// Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

	public static final String MODID = "creativetabs";
	
	public CreativeTabs() {
		// Register ourselves for server and other game events we are interested in

		// Config setup
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, InventoryConfig.CLIENT_CONFIG);

		InventoryConfig.loadConfig(InventoryConfig.CLIENT_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve("eyemine-inventory-config.toml"));
				
		MinecraftForge.EVENT_BUS.register(this);					
	}


    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) { 
	    if (KeyWatcher.f3Pressed) { return; }

        if (event.getAction() == GLFW.GLFW_RELEASE) {
	    	int key = event.getKey();
	        LOGGER.debug(key);
	        Screen currentScreen = Minecraft.getInstance().currentScreen;
			if (currentScreen != null) {				
				if (currentScreen instanceof CreativeScreen) {
					CreativeScreen gui = (CreativeScreen)currentScreen;
					CreativeInventoryManager con = CreativeInventoryManager.getInstance(
							gui.getGuiLeft(), gui.getGuiTop(), 
							gui.getXSize(), gui.getYSize(),
							gui.getSelectedTabIndex(),
							gui.getContainer());            	
					boolean handled = con.acceptKey(key);
					if (handled && event.isCancelable()) {
						event.setCanceled(true);
					}
				}
				else if (currentScreen instanceof ChestScreen)
				{
					ChestScreen gui = (ChestScreen)currentScreen;
					ChestInventoryManager con = ChestInventoryManager.getInstance(
							gui.getGuiLeft(), gui.getGuiTop(), 
							gui.getXSize(), gui.getYSize(),
							gui.getContainer());            	
					con.acceptKey(key);					
				}
				else if (currentScreen instanceof InventoryScreen)
				{
					InventoryScreen gui = (InventoryScreen)currentScreen;
					
					SurvivalInventoryManager con = SurvivalInventoryManager.getInstance(
							gui.getGuiLeft(), gui.getGuiTop(), 
							gui.getXSize(), gui.getYSize(), gui.getContainer());					
					
					con.acceptKey(key);					
				}
			}
			
        }
		
    }
}
