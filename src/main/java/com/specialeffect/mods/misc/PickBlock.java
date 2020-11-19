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


import com.specialeffect.mods.ChildMod;
import com.specialeffect.mods.utils.KeyWatcher;
import com.specialeffect.utils.CommonStrings;
import com.specialeffect.utils.ModUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import org.lwjgl.glfw.GLFW;



public class PickBlock extends ChildMod {
	public final String MODID = "pickblock";

	public static KeyBinding mPickBlockKB;

	public PickBlock() {
	    
        // Register key bindings
		mPickBlockKB = new KeyBinding("Pick block", GLFW.GLFW_KEY_KP_2, CommonStrings.EYEGAZE_COMMON);
		ClientRegistry.registerKeyBinding(mPickBlockKB);
    }
	
	@SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {   

	    if (KeyWatcher.f3Pressed) { return; }

		if (ModUtils.hasActiveGui()) { return; }	    
	    if (event.getAction() != GLFW.GLFW_PRESS) { return; }
		
		if (mPickBlockKB.getKey().getKeyCode() == event.getKey()) {
	        final Input pickBlockKey = Minecraft.getInstance().gameSettings.keyBindPickBlock.getKey();
			KeyBinding.onTick(pickBlockKey);
		}
    }

	@Override
	public void setup(FMLCommonSetupEvent event) {
	}	
}
