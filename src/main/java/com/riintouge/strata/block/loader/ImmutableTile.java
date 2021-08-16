package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IFacingTextureMap;
import com.riintouge.strata.block.IModelRetexturizerMap;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class ImmutableTile implements IGeoTileInfo
{
    private String tileSetName;
    private ResourceLocation registryName;
    private int meta;
    private TileType type;
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private int burnTime;
    private MetaResourceLocation equivalentItemResourceLocation;
    private ItemStack equivalentItemStack;
    private LayeredTextureLayer[] fragmentTextureLayers;
    private MetaResourceLocation equivalentFragmentItemResourceLocation;
    private ItemStack equivalentFragmentItemStack;
    private ArrayList< EnumPlantType > sustainedPlantTypes;
    private ArrayList< MetaResourceLocation > sustainsPlantsSustainedByRaw;
    private ArrayList< IBlockState > sustainsPlantsSustainedBy;
    private GenericCubeTextureMap genericCubeTextureMap;
    private Integer particleFallingColor = null;

    public ImmutableTile(
        String tileSetName,
        int meta,
        TileType type,
        Material material,
        SoundType soundType,
        String harvestTool,
        int harvestLevel,
        float hardness,
        float explosionResistance,
        int burnTime,
        GenericCubeTextureMap textureMap,
        MetaResourceLocation equivalentItem,
        List< LayeredTextureLayer > fragmentTextureLayers,
        MetaResourceLocation equivalentFragmentItem,
        ArrayList< EnumPlantType > sustainedPlantTypes,
        ArrayList< MetaResourceLocation > sustainsPlantsSustainedBy )
    {
        this.tileSetName = tileSetName;
        this.registryName = type.registryName( tileSetName );
        this.meta = meta;
        this.type = type;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.burnTime = burnTime;
        this.genericCubeTextureMap = textureMap;
        this.equivalentItemResourceLocation = equivalentItem;
        if( fragmentTextureLayers != null )
            this.fragmentTextureLayers = fragmentTextureLayers.toArray( new LayeredTextureLayer[ fragmentTextureLayers.size() ] );
        this.equivalentFragmentItemResourceLocation = equivalentFragmentItem;
        this.sustainedPlantTypes = sustainedPlantTypes != null ? sustainedPlantTypes : new ArrayList<>();
        this.sustainsPlantsSustainedByRaw = sustainsPlantsSustainedBy != null ? sustainsPlantsSustainedBy : new ArrayList<>();
    }

    // IGeoTileInfo overrides

    @Override
    public String tileSetName()
    {
        return tileSetName;
    }

    @Override
    public ResourceLocation registryName()
    {
        return registryName;
    }

    @Override
    public int meta()
    {
        return meta;
    }

    @Override
    public TileType type()
    {
        return type;
    }

    @Override
    public ItemStack equivalentItem()
    {
        // Deferred resolution until reasonably sure the item has been created
        if( equivalentItemResourceLocation != null )
        {
            Item equivalentItem = Item.REGISTRY.getObject( equivalentItemResourceLocation.resourceLocation );
            if( equivalentItem != null )
                equivalentItemStack = new ItemStack( equivalentItem , 1 , equivalentItemResourceLocation.meta );
            equivalentItemResourceLocation = null;
        }

        return equivalentItemStack;
    }

    @Override
    public Boolean hasFragment()
    {
        return fragmentTextureLayers != null;
    }

    @Override
    public LayeredTextureLayer[] fragmentTextureLayers()
    {
        return fragmentTextureLayers;
    }

    @Override
    public ItemStack equivalentFragmentItem()
    {
        // Deferred resolution until reasonably sure the item has been created
        if( equivalentFragmentItemResourceLocation != null )
        {
            if( hasFragment() )
            {
                Item equivalentFragmentItem = Item.REGISTRY.getObject( equivalentFragmentItemResourceLocation.resourceLocation );
                if( equivalentFragmentItem != null )
                    equivalentFragmentItemStack = new ItemStack( equivalentFragmentItem , 1 , equivalentFragmentItemResourceLocation.meta );
            }

            equivalentFragmentItemResourceLocation = null;
        }

        return equivalentFragmentItemStack;
    }

    @Override
    public ArrayList< EnumPlantType > sustainedPlantTypes()
    {
        return sustainedPlantTypes;
    }

    @Override
    public ArrayList< IBlockState > sustainsPlantsSustainedBy()
    {
        // Deferred resolution until reasonably sure the block(s) have been created
        if( sustainsPlantsSustainedByRaw != null )
        {
            sustainsPlantsSustainedBy = new ArrayList<>();
            for( MetaResourceLocation metaResourceLocation : sustainsPlantsSustainedByRaw )
            {
                Block otherBlock = ForgeRegistries.BLOCKS.getValue( metaResourceLocation.resourceLocation );
                sustainsPlantsSustainedBy.add( otherBlock.getStateFromMeta( metaResourceLocation.meta ) );
            }
            sustainsPlantsSustainedByRaw = null;
        }

        return sustainsPlantsSustainedBy;
    }

    // IHostInfo overrides

    @Override
    public IFacingTextureMap facingTextureMap()
    {
        return genericCubeTextureMap;
    }

    // IGenericBlockProperties overrides

    @Override
    public Material material()
    {
        return material;
    }

    @Override
    public SoundType soundType()
    {
        return soundType;
    }

    @Override
    public String harvestTool()
    {
        return harvestTool;
    }

    @Override
    public int harvestLevel()
    {
        return harvestLevel;
    }

    @Override
    public float hardness()
    {
        return hardness;
    }

    @Override
    public float explosionResistance()
    {
        return explosionResistance;
    }

    @Override
    public int burnTime()
    {
        return burnTime;
    }

    @Override
    public IModelRetexturizerMap modelTextureMap()
    {
        return genericCubeTextureMap;
    }

    @Override
    public int particleFallingColor()
    {
        return particleFallingColor != null
            ? particleFallingColor
            : ( particleFallingColor = HostRegistry.getParticleFallingColor( this ) );
    }

    // IForgeRegistrable overrides

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        genericCubeTextureMap.stitchTextures( textureMap );
    }
}
