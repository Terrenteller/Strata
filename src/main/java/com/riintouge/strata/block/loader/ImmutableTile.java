package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.geo.HostRegistry;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.LocalizationRegistry;
import com.riintouge.strata.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ImmutableTile implements IGeoTileInfo
{
    private Map< String , String > languageMap;

    // IGeoTileInfo
    private String tileSetName;
    private TileType type;
    private String blockOreDictionaryName;
    private String fragmentItemOreDictionaryName;
    private MetaResourceLocation equivalentItemResourceLocation;
    private MetaResourceLocation furnaceResult;
    private Float furnaceExp;
    private ItemStack equivalentItemStack = null; // Lazily evaluated
    private LayeredTextureLayer[] fragmentTextureLayers;
    private MetaResourceLocation equivalentFragmentItemResourceLocation;
    private ItemStack equivalentFragmentItemStack = null; // Lazily evaluated
    private MetaResourceLocation fragmentFurnaceResult;
    private Float fragmentFurnaceExp;
    private ArrayList< EnumPlantType > sustainedPlantTypes;
    private ArrayList< MetaResourceLocation > sustainsPlantsSustainedByRaw;
    private ArrayList< IBlockState > sustainsPlantsSustainedBy = null; // Lazily evaluated
    private GenericCubeTextureMap modelTextureMap;
    private ResourceLocation blockstateResourceLocation;

    // IHostInfo
    private ResourceLocation registryName;
    private Integer particleFallingColor = null; // Lazily evaluated

    // IGenericBlockProperties
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private int burnTime;

    public ImmutableTile( TileData tileData ) throws IllegalArgumentException
    {
        this.languageMap = Util.lazyCoalesce( tileData.languageMap , HashMap::new );

        // IGeoTileInfo
        this.tileSetName = Util.argumentNullCheck( tileData.tileSetName , "tileSetName" );
        this.type = Util.argumentNullCheck( tileData.type , "type" );
        this.blockOreDictionaryName = tileData.blockOreDictionaryName;
        this.fragmentItemOreDictionaryName = tileData.fragmentItemOreDictionaryName;
        this.equivalentItemResourceLocation = tileData.equivalentItemResourceLocation;
        this.furnaceResult = tileData.furnaceResult;
        this.furnaceExp = tileData.furnaceExp;
        if( tileData.fragmentTextureLayers != null && tileData.fragmentTextureLayers.size() > 0 )
            this.fragmentTextureLayers = tileData.fragmentTextureLayers.toArray( new LayeredTextureLayer[ tileData.fragmentTextureLayers.size() ] );
        this.equivalentFragmentItemResourceLocation = tileData.equivalentFragmentItemResourceLocation;
        this.fragmentFurnaceResult = tileData.fragmentFurnaceResult;
        this.fragmentFurnaceExp = tileData.fragmentFurnaceExp;
        this.sustainedPlantTypes = Util.lazyCoalesce( tileData.sustainedPlantTypes , ArrayList::new );
        this.sustainsPlantsSustainedByRaw = Util.lazyCoalesce( tileData.sustainsPlantsSustainedByRaw , ArrayList::new );
        this.modelTextureMap = Util.argumentNullCheck( tileData.textureMap , "textureMap" );
        this.blockstateResourceLocation = Util.coalesce( tileData.blockstateResourceLocation , this.type.blockstate );

        // IHostInfo
        this.registryName = tileData.type.registryName( this.tileSetName );

        // IGenericBlockProperties
        this.material = Util.argumentNullCheck( tileData.material , "material" );
        this.soundType = Util.argumentNullCheck( tileData.soundType , "soundType" );
        this.harvestTool = Util.argumentNullCheck( tileData.harvestTool , "harvestTool" );
        this.harvestLevel = Util.coalesce( tileData.harvestLevel , 0 );
        this.hardness = Util.coalesce( tileData.hardness , 1.0f );
        this.explosionResistance = Util.coalesce( tileData.explosionResistance , 5.0f * this.hardness );
        this.burnTime = Util.coalesce( tileData.burnTime , 0 );

        LocalizationRegistry.INSTANCE.register( this , registryName.toString() + ".name" , this.languageMap );
    }

    // IGeoTileInfo overrides

    @Override
    public String tileSetName()
    {
        return tileSetName;
    }

    @Override
    public TileType type()
    {
        return type;
    }

    @Override
    public String blockOreDictionaryName()
    {
        return blockOreDictionaryName;
    }

    @Override
    public String fragmentItemOreDictionaryName()
    {
        return fragmentItemOreDictionaryName;
    }

    @Override
    public ItemStack equivalentItemStack()
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

    @Nullable
    @Override
    public MetaResourceLocation furnaceResult()
    {
        return furnaceResult;
    }

    @Override
    public Float furnaceExp()
    {
        return furnaceExp;
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
    public ItemStack equivalentFragmentItemStack()
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

    @Nullable
    @Override
    public MetaResourceLocation fragmentFurnaceResult()
    {
        return fragmentFurnaceResult;
    }

    @Override
    public Float fragmentFurnaceExp()
    {
        return fragmentFurnaceExp;
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

    @Override
    public GenericCubeTextureMap modelTextureMap()
    {
        return modelTextureMap;
    }

    @Override
    public ResourceLocation blockstateResourceLocation()
    {
        return blockstateResourceLocation;
    }

    @Override
    public String localizedName()
    {
        return LocalizationRegistry.INSTANCE.get( this );
    }

    // IHostInfo overrides

    @Override
    public ResourceLocation registryName()
    {
        return registryName;
    }

    @Override
    public int meta()
    {
        return 0;
    }

    @Override
    public int particleFallingColor()
    {
        return particleFallingColor != null
            ? particleFallingColor
            : ( particleFallingColor = HostRegistry.getParticleFallingColor( this ) );
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
}
