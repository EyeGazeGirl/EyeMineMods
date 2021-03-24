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

package com.specialeffect.messages;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class DismountPlayerMessage {
    
    public DismountPlayerMessage() { }
    
	public static DismountPlayerMessage decode(PacketBuffer buf) {    	
        return new DismountPlayerMessage();
    }

    public static void encode(DismountPlayerMessage pkt, PacketBuffer buf) {
    }    

    public static class Handler {
		public static void handle(final DismountPlayerMessage pkt, Supplier<NetworkEvent.Context> ctx) {
			PlayerEntity player = ctx.get().getSender();
	        if (player == null) {
	            return;
	        }       

	        if (player.isPassenger()) {
				Entity riddenEntity = player.getRidingEntity();
				riddenEntity.getEntityId();
				
				if (null != riddenEntity) {
					player.stopRiding();
					player.func_233628_a_(riddenEntity); //dismountEntity became the private func_233628_a_? AT-ed it public
					player.jump();
				}
			}
		}
	}       
}
