package com.riintouge.strata.block.loader;

import com.riintouge.strata.item.ItemHelper;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.geo.IGeoTileInfo;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.IDropFormula;
import com.riintouge.strata.item.LocalizationRegistry;
import com.riintouge.strata.item.StaticDropFormula;
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
    private ItemStack equivalentItemStack = null; // Lazily evaluated
    private MetaResourceLocation furnaceResult;
    private Float furnaceExperience;
    private LayeredTextureLayer[] fragmentTextureLayers;
    private IDropFormula fragmentDropFormula;
    private MetaResourceLocation equivalentFragmentItemResourceLocation;
    private ItemStack equivalentFragmentItemStack = null; // Lazily evaluated
    private MetaResourceLocation fragmentFurnaceResult;
    private Float fragmentFurnaceExperience;
    private int fragmentBurnTime;
    private MetaResourceLocation breaksIntoResourceLocation;
    private ArrayList< EnumPlantType > sustainedPlantTypes;
    private ArrayList< MetaResourceLocation > sustainsPlantsSustainedByRaw;
    private ArrayList< IBlockState > sustainsPlantsSustainedBy = new ArrayList<>(); // Lazily populated
    private ResourceLocation blockStateResource;
    private IDropFormula experienceDropFormula;
    private SoundEventTuple ambientSound;
    private Integer lightOpacity;

    // IHostInfo
    private ResourceLocation registryName;
    private Float slipperiness;
    private Integer meltsAt;
    private MetaResourceLocation meltsInto;
    private Integer sublimatesAt;
    private MetaResourceLocation sublimatesInto;
    private ProtoBlockTextureMap modelTextureMap;
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

    public ImmutableTile( TileData tileData ) throws NullPointerException
    {
        Util.argumentNullCheck( tileData , "tileData" );

        // IGeoTileInfo
        this.tileSetName = Util.argumentNullCheck( tileData.tileSetName , "tileSetName" );
        this.tileType = Util.argumentNullCheck( tileData.tileType , "tileType" );
        this.blockOreDictionaryName = tileData.blockOreDictionaryName;
        this.fragmentItemOreDictionaryName = tileData.fragmentItemOreDictionaryName;
        this.equivalentItemResourceLocation = tileData.equivalentItemResourceLocation;
        this.furnaceResult = tileData.furnaceResult;
        this.furnaceExperience = tileData.furnaceExperience;
        if( tileType.isPrimary && tileData.fragmentTextureLayers != null && tileData.fragmentTextureLayers.size() > 0 )
            this.fragmentTextureLayers = tileData.fragmentTextureLayers.toArray( new LayeredTextureLayer[ tileData.fragmentTextureLayers.size() ] );
        this.fragmentDropFormula = Util.lazyCoalesce( tileData.fragmentDropFormula , () -> new StaticDropFormula( StaticDropFormula.STANDARD_FRAGMENT_COUNT ) );
        this.equivalentFragmentItemResourceLocation = tileData.equivalentFragmentItemResourceLocation;
        this.fragmentFurnaceResult = tileData.fragmentFurnaceResult;
        this.fragmentFurnaceExperience = tileData.fragmentFurnaceExperience;
        this.fragmentBurnTime = Util.coalesce( tileData.fragmentBurnTime , 0 );
        this.breaksIntoResourceLocation = tileData.breaksIntoResourceLocation;
        this.sustainedPlantTypes = Util.lazyCoalesce( tileData.sustainedPlantTypes , ArrayList::new );
        this.blockStateResource = Util.coalesce( tileData.blockStateResource , this.tileType.blockStateResource );
        this.experienceDropFormula = tileData.experienceDropFormula;
        this.ambientSound = tileData.ambientSound;
        this.lightOpacity = tileData.lightOpacity;

        // IHostInfo
        this.registryName = tileData.tileType.registryName( this.tileSetName );
        this.slipperiness = tileData.slipperiness;
        if( tileType.isPrimary )
        {
            this.meltsAt = tileData.meltsAt;
            this.meltsInto = tileData.meltsInto;
            this.sublimatesAt = tileData.sublimatesAt;
            this.sublimatesInto = tileData.sublimatesInto;
        }
        this.modelTextureMap = Util.argumentNullCheck( tileData.textureMap , "texture" );

        // ICommonBlockProperties
        this.material = Util.argumentNullCheck( tileData.material , "material" );
        this.soundType = Util.argumentNullCheck( tileData.soundType , "soundType" );
        this.harvestTool = Util.argumentNullCheck( tileData.harvestTool , "harvestTool" );
        this.harvestLevel = Util.coalesce( tileData.harvestLevel , 0 );
        this.hardness = Util.coalesce( tileData.hardness , 1.5f );
        this.explosionResistance = Util.coalesce( tileData.explosionResistance , 10.0f );
        this.lightLevel = Util.coalesce( tileData.lightLevel , 0 );
        this.burnTime = Util.coalesce( tileData.burnTime , 0 );
        this.specialBlockPropertyFlags = Util.coalesce( tileData.specialBlockPropertyFlags , 0L );

        LocalizationRegistry.INSTANCE.register(
            registryName.toString(),
            Util.lazyCoalesce( tileData.languageMap , HashMap::new ) );
        LocalizationRegistry.INSTANCE.register(
            registryName.toString() + ".name",
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
    public TileType tileType()
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
        return furnaceResult != null ? ItemHelper.metaResourceLocationToItemStack( furnaceResult ) : null;
    }

    @Override
    public float furnaceExperience()
    {
        if( furnaceExperience == null )
        {
            ItemStack furnaceResult = furnaceResult();
            furnaceExperience = furnaceResult != null ? FurnaceRecipes.instance().getSmeltingExperience( furnaceResult ) : 0.0f;
        }

        return furnaceExperience;
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
    public IDropFormula fragmentDropFormula()
    {
        return fragmentDropFormula;
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
        return fragmentFurnaceResult != null ? ItemHelper.metaResourceLocationToItemStack( fragmentFurnaceResult ) : null;
    }

    @Override
    public float fragmentFurnaceExperience()
    {
        if( fragmentFurnaceExperience == null )
        {
            ItemStack fragmentFurnaceResult = fragmentFurnaceResult();
            fragmentFurnaceExperience = fragmentFurnaceResult != null ? FurnaceRecipes.instance().getSmeltingExperience( fragmentFurnaceResult ) : 0.0f;
        }

        return fragmentFurnaceExperience;
    }

    @Override
    public int fragmentBurnTime()
    {
        return fragmentBurnTime;
    }

    @Nullable
    @Override
    public MetaResourceLocation breaksInto()
    {
        return breaksIntoResourceLocation;
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

    @Nonnull
    @Override
    public ResourceLocation blockStateResource()
    {
        return blockStateResource;
    }

    @Nullable
    public IDropFormula experienceDropFormula()
    {
        return experienceDropFormula;
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

    @Nullable
    @Override
    public Float slipperiness()
    {
        return slipperiness;
    }

    @Nullable
    @Override
    public Integer meltsAt()
    {
        return meltsAt;
    }

    @Nullable
    @Override
    public MetaResourceLocation meltsInto()
    {
        return meltsInto;
    }

    @Nullable
    @Override
    public Integer sublimatesAt()
    {
        return sublimatesAt;
    }

    @Nullable
    @Override
    public MetaResourceLocation sublimatesInto()
    {
        return sublimatesInto;
    }

    @Override
    public boolean ticksRandomly()
    {
        return meltsAt != null || sublimatesAt != null;
    }

    @Nonnull
    @Override
    @SideOnly( Side.CLIENT )
    public ProtoBlockTextureMap modelTextureMap()
    {
        return modelTextureMap;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public int particleFallingColor()
    {
        if( particleFallingColor == null )
            particleFallingColor = ParticleHelper.getParticleFallingColor( modelTextureMap().get( EnumFacing.DOWN ) );

        return particleFallingColor;
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
