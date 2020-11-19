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

import org.lwjgl.glfw.GLFW;

import com.specialeffect.mods.ChildMod;
import com.specialeffect.utils.CommonStrings;
import com.specialeffect.utils.ModUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class OpenChat extends ChildMod {	
	public final String MODID = "openchat";

	public void setup(final FMLCommonSetupEvent event) {
		
		// Register key bindings
		mOpenChatKB = new KeyBinding("Open chat", GLFW.GLFW_KEY_END, CommonStrings.EYEGAZE_EXTRA);
		ClientRegistry.registerKeyBinding(mOpenChatKB);

	}

	private static KeyBinding mOpenChatKB;

	@SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
    	if (ModUtils.hasActiveGui()) { return; }	    
	    if (event.getAction() != GLFW.GLFW_PRESS) { return; }
    	
		final Input chatKeyCode = Minecraft.getInstance().gameSettings.keyBindChat.getKey();
		if (mOpenChatKB.getKey().getKeyCode() == event.getKey()) {
			KeyBinding.onTick(chatKeyCode);
		}
	}
}
