package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.LocalizationRegistry;
import com.riintouge.strata.sound.SoundEventTuple;
import com.riintouge.strata.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class ImmutableTile implements IGeoTileInfo
{
    // IGeoTileInfo
    private String tileSetName;
    private TileType tileType;
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
    private ArrayList< IBlockState > sustainsPlantsSustainedBy = new ArrayList<>(); // Lazily populated
    private ProtoBlockTextureMap modelTextureMap;
    private ResourceLocation blockstateResourceLocation;
    private SoundEventTuple ambientSound;
    private Integer lightOpacity;
    private Float slipperiness;

    // IHostInfo
    private ResourceLocation registryName;
    private Integer particleFallingColor = null; // Lazily evaluated

    // ICommonBlockProperties
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private int lightLevel;
    private int burnTime;
    private long specialBlockPropertyFlags;

    public ImmutableTile( TileData tileData ) throws IllegalArgumentException
    {
        Util.argumentNullCheck( tileData , "tileData" );

        // IGeoTileInfo
        this.tileSetName = Util.argumentNullCheck( tileData.tileSetName , "tileSetName" );
        this.tileType = Util.argumentNullCheck( tileData.tileType , "type" );
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
        this.modelTextureMap = Util.argumentNullCheck( tileData.textureMap , "texture" );
        this.blockstateResourceLocation = Util.coalesce( tileData.blockstateResourceLocation , this.tileType.blockstate );
        this.ambientSound = tileData.ambientSound;
        this.lightOpacity = tileData.lightOpacity;
        this.slipperiness = tileData.slipperiness;

        // IHostInfo
        this.registryName = tileData.tileType.registryName( this.tileSetName );

        // ICommonBlockProperties
        this.material = Util.argumentNullCheck( tileData.material , "material" );
        this.soundType = Util.argumentNullCheck( tileData.soundType , "soundType" );
        this.harvestTool = Util.argumentNullCheck( tileData.harvestTool , "harvestTool" );
        this.harvestLevel = Util.coalesce( tileData.harvestLevel , 0 );
        this.hardness = Util.coalesce( tileData.hardness , 1.0f );
        this.explosionResistance = Util.coalesce( tileData.explosionResistance , 5.0f * this.hardness );
        this.lightLevel = Util.coalesce( tileData.lightLevel , 0 );
        this.burnTime = Util.coalesce( tileData.burnTime , 0 );
        this.specialBlockPropertyFlags = Util.coalesce( tileData.specialBlockPropertyFlags , 0L );

        LocalizationRegistry.INSTANCE.register(
            registryName.toString(),
            Util.lazyCoalesce( tileData.languageMap , HashMap::new ) );
        LocalizationRegistry.INSTANCE.register(
            registryName.toString() + ".tooltip",
            Util.lazyCoalesce( tileData.tooltipMap , HashMap::new ) );
    }

    // IGeoTileInfo overrides

    @Nonnull
    @Override
    public String tileSetName()
    {
        return tileSetName;
    }

    @Nonnull
    @Override
    public TileType type()
    {
        return tileType;
    }

    @Nullable
    @Override
    public String blockOreDictionaryName()
    {
        return blockOreDictionaryName;
    }

    @Nullable
    @Override
    public String fragmentItemOreDictionaryName()
    {
        return fragmentItemOreDictionaryName;
    }

    @Nullable
    @Override
    public ItemStack equivalentItemStack()
    {
        if( equivalentItemResourceLocation == null )
            return tileType.vanillaItemStack;

        // Deferred resolution until reasonably sure the item has been created
        if( equivalentItemStack == null )
        {
            if( equivalentItemResourceLocation.resourceLocation.equals( Blocks.AIR.getRegistryName() ) )
            {
                equivalentItemStack = ItemStack.EMPTY;
            }
            else
            {
                Item equivalentItem = Item.REGISTRY.getObject( equivalentItemResourceLocation.resourceLocation );
                if( equivalentItem != null )
                    equivalentItemStack = new ItemStack( equivalentItem , 1 , equivalentItemResourceLocation.meta );
            }
        }

        return equivalentItemStack;
    }

    @Nullable
    @Override
    public ItemStack furnaceResult()
    {
        return furnaceResult != null ? furnaceResult.toItemStack() : null;
    }

    @Override
    public float furnaceExp()
    {
        if( furnaceExp != null )
            return furnaceExp;

        ItemStack furnaceResult = furnaceResult();
        return furnaceResult != null ? FurnaceRecipes.instance().getSmeltingExperience( furnaceResult ) : 0.0f;
    }

    @Override
    public boolean hasFragment()
    {
        return fragmentTextureLayers != null;
    }

    @Nullable
    @Override
    public LayeredTextureLayer[] fragmentTextureLayers()
    {
        return fragmentTextureLayers;
    }

    @Nullable
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
    public ItemStack fragmentFurnaceResult()
    {
        return fragmentFurnaceResult != null ? fragmentFurnaceResult.toItemStack() : null;
    }

    @Override
    public float fragmentFurnaceExp()
    {
        if( fragmentFurnaceExp != null )
            return fragmentFurnaceExp;

        ItemStack fragmentFurnaceResult = fragmentFurnaceResult();
        return fragmentFurnaceResult != null ? FurnaceRecipes.instance().getSmeltingExperience( fragmentFurnaceResult ) : 0.0f;
    }

    @Nonnull
    @Override
    public ArrayList< EnumPlantType > sustainedPlantTypes()
    {
        return sustainedPlantTypes;
    }

    @Nonnull
    @Override
    public ArrayList< IBlockState > sustainsPlantsSustainedBy()
    {
        // Deferred resolution until reasonably sure the block(s) have been created
        if( sustainsPlantsSustainedByRaw != null )
        {
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
    @SideOnly( Side.CLIENT )
    public ProtoBlockTextureMap modelTextureMap()
    {
        return modelTextureMap;
    }

    @Nonnull
    @Override
    public ResourceLocation blockstateResourceLocation()
    {
        return blockstateResourceLocation;
    }

    @Override
    public SoundEventTuple ambientSound()
    {
        return ambientSound;
    }

    @Nullable
    @Override
    public Integer lightOpacity()
    {
        return lightOpacity;
    }

    @Nullable
    @Override
    public Float slipperiness()
    {
        return slipperiness;
    }

    @Nullable
    @Override
    public String localizedName()
    {
        return LocalizationRegistry.INSTANCE.get( registryName.toString() );
    }

    @Nullable
    @Override
    public List< String > localizedTooltip()
    {
        String tooltip = LocalizationRegistry.INSTANCE.get( registryName.toString() + ".tooltip" );
        return tooltip != null ? Arrays.asList( tooltip.split( "\\\\n" ) ) : null;
    }

    // IHostInfo overrides

    @Nonnull
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
    @SideOnly( Side.CLIENT )
    public int particleFallingColor()
    {
        return particleFallingColor != null
            ? particleFallingColor
            : ( particleFallingColor = ParticleHelper.getParticleFallingColor( modelTextureMap().get( EnumFacing.DOWN ) ) );
    }

    // ICommonBlockProperties overrides

    @Nonnull
    @Override
    public Material material()
    {
        return material;
    }

    @Nonnull
    @Override
    public SoundType soundType()
    {
        return soundType;
    }

    @Nonnull
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
    public int lightLevel()
    {
        return lightLevel;
    }

    @Override
    public int burnTime()
    {
        return burnTime;
    }

    @Override
    public long specialBlockPropertyFlags()
    {
        return specialBlockPropertyFlags;
    }
}
