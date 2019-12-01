package com.riintouge.strata.item;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IItemMeshDefinitionProvider
{
    @SideOnly( Side.CLIENT )
    ItemMeshDefinition getItemMeshDefinition();
}
