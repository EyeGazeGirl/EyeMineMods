/**
 * Copyright (C) 2016 Kirsty McNaught, SpecialEffect
 * www.specialeffect.org.uk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.specialeffect.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MovePlayerMessage implements IMessage {
    
    private float moveAmount;
    private float moveAngle;

    public MovePlayerMessage() { }

    public MovePlayerMessage(float moveAmount,
    						 float moveAngle) {
    	this.moveAmount = moveAmount;
    	this.moveAngle = moveAngle;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	moveAmount = buf.readFloat();
    	moveAngle = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeFloat(moveAmount);
    	buf.writeFloat(moveAngle);
    }

    public static class Handler implements IMessageHandler<MovePlayerMessage, IMessage> {        
    	@Override
        public IMessage onMessage(final MovePlayerMessage message,final MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = ctx.getServerHandler().playerEntity;
                    if (player.isRiding()) {
                    	player.moveForward = 1.0f;
                    	Entity riddenEntity = player.ridingEntity;
						if (null != riddenEntity) {
							// Minecarts can only be moved forward/backward
							if (riddenEntity instanceof EntityMinecart) {
								EntityMinecart minecart = (EntityMinecart)riddenEntity;
								Vec3 lookVec = player.getLookVec();
								int lookYaw = (int)player.rotationYaw;
								int yawDiff = ((int)riddenEntity.rotationYaw - lookYaw) % 360;
								if (yawDiff < 90 || yawDiff > 270) {
									message.moveAngle = 0.0f;
								}
								else {
									message.moveAngle = (float) Math.PI;
								}
								// slower for minecarts
								message.moveAmount /= 2.0f;
							}
							
							double yaw = Math.toRadians(riddenEntity.rotationYaw);
							
							float xDiff =  -(float)(message.moveAmount*Math.sin(yaw+message.moveAngle));
							float yDiff = (float)(message.moveAmount*Math.cos(yaw+message.moveAngle));

							riddenEntity.moveEntity(xDiff, 0, 
													yDiff);
						}
                    }
                }
            });
            return null; // no response in this case
        }
    }
}
