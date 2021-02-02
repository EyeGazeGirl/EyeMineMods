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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.specialeffect.mods.mousehandling.MouseHelperOwn;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;

/**
 * Manages a Inventory GUI Inventory.
 */
public class SurvivalInventoryManager {
	
	// Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

	private static SurvivalInventoryManager instance = null;

    private final int NUM_TABS = 5;
	private final int NUM_COLS = 9;
	private final int NUM_ROWS = 5;

	/**
	 * Creates a new Inventory Manager with the given container.
	 *
	 * @param /container The container from a crafting GUI
	 */
	private SurvivalInventoryManager() {
	}

	/**
	 * Returns a Inventory Manager Instance operating on the given container
	 * @param playerContainer 
	 *
	 * @param playerContainer A container from a GUI
	 * @return manager-singleton
	 */
	public static SurvivalInventoryManager getInstance(int left, int top, 
													   int xSize, int ySize, 
													   PlayerContainer playerContainer) {
		if (instance == null) {
			instance = new SurvivalInventoryManager();
		} 
		instance.updateCoordinates(left, top, xSize, ySize, playerContainer);
		return instance;
	}
		
	int left = 0;
	int top = 0;
	
	private int tabHeight = 0; // width between centres of consecutive tabs
	private int itemWidth = 0; // width between centres of consecutive items
	
	private int topTapYPos = 0;
	private int bottomRowYPos = 0;
	private int topItemYPos = 0;
	
	private int tabXPos = 0;
	private int leftItemXPos = 0;
	
	private float xScale = 1.0f;
	private float yScale = 1.0f;
	
	private int recipeX = 0;
	private int recipeY = 0;
	
	private int currTab;
	
	PlayerContainer playerContainer;
	
	private Slot outputSlot;		
		
	private int slotFirstX = 0;
	private int slotFirstY = 0;
	private int slotDelta = 0;
	 
	
	private void onTabChanged() {
		// reset to hovering over first item when changing tabs
		itemRow = -1;
		itemCol = -1;
	}
	
	private void updateCoordinates(int left, int top, int width, int height, PlayerContainer playerContainer) {
		int inventoryWidth = width;
		this.left = left;
		this.top = top;

		this.playerContainer = playerContainer;

		this.tabHeight = (int) (inventoryWidth/6.9);
		this.itemWidth = (int) (inventoryWidth/9.5);
		this.topTapYPos = top + tabHeight/2;
		this.tabXPos = left-inventoryWidth;

		this.topItemYPos = (int) (top + itemWidth*1.5);
		this.leftItemXPos = (int) (left + itemWidth*0.9);

		this.recipeX = (int) (inventoryWidth*0.65);
		this.recipeY = (int) (inventoryWidth*0.4);

		// Sizes need scaling before turning into click locations
		Minecraft mc = Minecraft.getInstance();
		this.xScale = (float) (mc.getMainWindow().getWidth())/(float)mc.getMainWindow().getScaledWidth();
		this.yScale = (float) (mc.getMainWindow().getHeight())/(float)mc.getMainWindow().getScaledHeight();


		List<Slot> slots = playerContainer.inventorySlots;
		int iSlotOutput = playerContainer.getOutputSlot();

		for (Slot slot : slots) {

			LOGGER.debug(slot.xPos+ ", "+ slot.yPos);
		}
		this.processSlots();

		int a = 2;

	}
	
	private void processSlots() {
		// parse the container to get positions and sizes of slots 
	
	    /* slot order for survival inventory is:
	     * - crafting output slot (0)
	     * - 4 crafting input slots (1..4)
	     * - 4 player armour slots (5..8)
	     * - all the other slots (...)
	     * - player shield slot
	     */
		
		List<Slot> slots = playerContainer.inventorySlots;

		this.outputSlot = slots.get(playerContainer.getOutputSlot());		
					
		this.slotFirstX = slots.get(10).xPos;
		this.slotFirstY = slots.get(10).yPos;
		this.slotDelta = slots.get(10).xPos - slots.get(9).xPos;
		
		// offset by half a slot
		this.slotFirstX += this.slotDelta/2;
		this.slotFirstY += this.slotDelta/2;
	} 
	    /*
		
		for (Slot slot : slots) {	
			LOGGER.debug(slot.xPos+ ", "+ slot.yPos);
		}
	  
		// Parse the list of slots to work out the location of things
		List<Slot> slots = playerContainer.inventorySlots;
		int iSlotOutput = playerContainer.getOutputSlot();
		for (Slot slot : slots) {
//			if (slot instanceof )
			LOGGER.debug(slot.xPos+ ", "+ slot.yPos);
		}
	}*/
	
	public void clickRecipeBook() {
		LOGGER.debug("Recipe book");		
		int xPos = left + recipeX; 
		int yPos = top + recipeY;
		MouseHelperOwn helper = (MouseHelperOwn)Minecraft.getInstance().mouseHelper;		
		helper.leftMouseClickAtPosition(xPos*this.xScale, yPos*this.yScale);		
	}
	
	public void hoverOutput() {
		LOGGER.debug("output");		

		int xPos = left + recipeX; 
		int yPos = top + recipeY;
		xPos = xPos + itemWidth*3;
		yPos = (int) (yPos - itemWidth*2.2);
		
		MouseHelperOwn helper = (MouseHelperOwn)Minecraft.getInstance().mouseHelper;				
		helper.moveCursor(xPos*this.xScale, yPos*this.yScale);
		
		
		playerContainer.transferStackInSlot(Minecraft.getInstance().player, playerContainer.getOutputSlot());
		
	}
	
	public void changePage(boolean forward) {
		LOGGER.debug("Page "+forward);
		
		int yPos = (int) (top + 5.5*tabHeight);
		int xPos = forward ? xPos = left - 100 : left - 50;		
		 		
		MouseHelperOwn helper = (MouseHelperOwn)Minecraft.getInstance().mouseHelper;	
		helper.moveCursor(xPos*this.xScale, yPos*this.yScale);
		helper.leftMouseClickAtPosition(xPos*this.xScale, yPos*this.yScale);		
	}
	
	public void toggleCraftable() {
		LOGGER.debug("craftable");
		
		int yPos = (int) (top + tabHeight/2);
		int xPos = 	left - 20;
		 		
		MouseHelperOwn helper = (MouseHelperOwn)Minecraft.getInstance().mouseHelper;	
		helper.moveCursor(xPos*this.xScale, yPos*this.yScale);
		helper.leftMouseClickAtPosition(xPos*this.xScale, yPos*this.yScale);		
	}
	
	public void clickItem() {
		int yPos = topItemYPos;
		int xPos = leftItemXPos;
		
		MouseHelperOwn helper = (MouseHelperOwn)Minecraft.getInstance().mouseHelper;
		helper.leftMouseClickAtPosition(xPos*this.xScale, yPos*this.yScale);
		
		//GLFW.glfwSetCursorPos(Minecraft.getInstance().mainWindow.getHandle(), xPos*this.xScale, yPos*this.yScale);		
	}
	
	public void shiftClickItem() {
		int yPos = topItemYPos;
		int xPos = leftItemXPos;
		
		MouseHelperOwn helper = (MouseHelperOwn)Minecraft.getInstance().mouseHelper;
		helper.leftShiftMouseClickAtPosition(xPos*this.xScale, yPos*this.yScale);
		
	}

	public void acceptKey(int key) {

		// Handle key press
		// 
		LOGGER.debug(InventoryConfig.keySurvRecipes.get());
		LOGGER.debug(key);
		if (key == InventoryConfig.keySurvRecipes.get()) {
			this.clickRecipeBook();			
		} 
		else if (key == InventoryConfig.keySurvPrevTab.get()) {
			this.switchToTab(validateTabIdx(currTab - 1));			
		}
		else if (key == InventoryConfig.keySurvNextTab.get()) {
			this.switchToTab(validateTabIdx(currTab + 1));			
		}		
		else if (key == InventoryConfig.keySurvPrevPage.get()) {
			this.changePage(true);			
		}
		else if (key == InventoryConfig.keySurvNextPage.get()) {
			this.changePage(false);			
		}
		else if (key == InventoryConfig.keySurvCraftable.get()) {
			this.toggleCraftable();
		} 
		else if (key == InventoryConfig.keySurvOutput.get()) {
			this.hoverOutput();
		}
	}
	
	private int itemRow = -1;
	private int itemCol = -1;
	
	private void scrollDown(int amount) {		
		MouseHelperOwn helper = (MouseHelperOwn)Minecraft.getInstance().mouseHelper;
		helper.scroll(amount);				
	}
	
	private void hoverItem() {		
		int yPos = topItemYPos + itemRow*itemWidth;
		int xPos = leftItemXPos + itemCol*itemWidth;
		
		GLFW.glfwSetCursorPos(Minecraft.getInstance().getMainWindow().getHandle(), xPos*this.xScale, yPos*this.yScale);
	}
	
	private void switchToTab(int iTab) {
		LOGGER.debug("switch tab " + iTab);
		// Set up (x, y) for specified tab 
		int xPos = -1;
		int yPos = -1;
		switch(iTab) {
		case -1:
			// this is proxy for "drop by clicking outside inventory"
			xPos = tabXPos - tabHeight;
			yPos = topTapYPos;
			break;
		case 0:
			xPos = tabXPos;
			yPos = topTapYPos;
			break;
		case 1:
			xPos = tabXPos;
			yPos = topTapYPos+tabHeight;
			break;
		case 2:
			xPos = tabXPos;
			yPos = topTapYPos+2*tabHeight;
			break;
		case 3:
			xPos = tabXPos;
			yPos = topTapYPos+3*tabHeight;
			break;
		case 4:
			xPos = tabXPos;
			yPos = topTapYPos+4*tabHeight;
			break;
		case 5: 
			xPos = tabXPos;
			yPos = topTapYPos+6*tabHeight;
			break;
		case 6:
			xPos = tabXPos;
			yPos = bottomRowYPos;
			break;
		case 7:
			xPos = tabXPos+tabHeight;;
			yPos = bottomRowYPos;
			break;
		case 8:
			xPos = tabXPos+2*tabHeight;;
			yPos = bottomRowYPos;
			break;
		case 9:
			xPos = tabXPos+3*tabHeight;;
			yPos = bottomRowYPos;
			break;
		case 10:
			xPos = tabXPos+4*tabHeight;
			yPos = bottomRowYPos;
			break;			
		case 11:
			xPos = tabXPos+6*tabHeight;
			yPos = bottomRowYPos;
			break;
		default:
			LOGGER.debug("Unknown tab requested");
			break;
		}
		
		// Select the tab via a mouse action
		if (xPos > -1) {			
			//FIXME: test with eye-gaze mouse emulation, no stray cursor movements interfere
			// Do we need synchronisation in the mouse helper??
			MouseHelperOwn helper = (MouseHelperOwn)Minecraft.getInstance().mouseHelper;
			helper.moveCursor(xPos*this.xScale, yPos*this.yScale);
			helper.leftMouseClickAtPosition(xPos*this.xScale, yPos*this.yScale);
			
			// we want to trigger 'tabChanged' if user has explicitly selected
			// the same tab again (otherwise this gets missed)
			this.onTabChanged();
			
			currTab = iTab;
		}	
	}
		
	// Ensure index in range, wrap if necessary
	private int validateTabIdx(int idx) {
		idx += NUM_TABS; // ensure positive
		idx %= NUM_TABS; // modulo into range	
		return idx;
	}
	
}