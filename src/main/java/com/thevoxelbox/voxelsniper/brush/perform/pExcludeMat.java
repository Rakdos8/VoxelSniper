/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.VoxelList;

/**
 * @author Voxel
 */
public class pExcludeMat extends vPerformer
{

    private VoxelList excludeList;
    private int id;

    public pExcludeMat()
    {
        name = "Exclude Material";
    }

    @Override
    public void info(Message vm)
    {
        vm.performerName(name);
        vm.voxelList();
        vm.voxel();
    }

    @Override
    public void init(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        w = v.getWorld();
        id = v.getVoxelId();
        excludeList = v.getVoxelList();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform(Block b)
    {
        if (!excludeList.contains(new int[] {b.getTypeId(), b.getData()}))
        {
            h.put(b);
            b.setTypeId(id);
        }
    }
}
