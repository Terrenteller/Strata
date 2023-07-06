package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.misc.IForgeRegistrable;
import com.riintouge.strata.item.ItemHelper;
import com.riintouge.strata.misc.MetaResourceLocation;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.geo.TileType;
import com.riintouge.strata.block.ore.IOreInfo;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.IDropFormula;
import com.riintouge.strata.item.LocalizationRegistry;
import com.riintouge.strata.item.WeightedDropCollections;
import com.riintouge.strata.sound.SoundEventTuple;
import com.riintouge.strata.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class ImmutableOre implements IOreInfo , IForgeRegistrable
{
    // IOreInfo
    private String oreName;
    private TileType tileType;
    private String blockOreDictionaryName;
    private String itemOreDictionaryName;
    private ProtoBlockTextureMap modelTextureMap;
    private Integer particleFallingColor = null; // Lazily evaluated
    private ResourceLocation blockStateResourceLocation;
    private LayeredTextureLayer[] oreItemTextureLayers;
    private MetaResourceLocation equivalentItemResourceLocation;
    private MetaResourceLocation furnaceResult;
    private Float furnaceExperience;
    private MetaResourceLocation proxyBlockResourceLocation;
    private Block proxyBlock = null; // Lazily evaluated
    private IBlockState proxyBlockState = null; // Lazily evaluated
    private MetaResourceLocation forcedHost;
    private List< MetaResourceLocation > hostAffinities;
    private MetaResourceLocation breaksIntoResourceLocation;
    private WeightedDropCollections weightedDropCollections;
    private IDropFormula experienceDropFormula;
    private SoundEventTuple ambientSound;

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

    public ImmutableOre( TileData tileData ) throws IllegalArgumentException
    {
        Util.argumentNullCheck( tileData , "tileData" );

        // IOreInfo
        this.oreName = Util.argumentNullCheck( tileData.oreName , "oreName" );
        this.tileType = Util.argumentNullCheck( tileData.tileType , "tileType" );
        this.blockOreDictionaryName = tileData.blockOreDictionaryName;
        this.itemOreDictionaryName = tileData.itemOreDictionaryName;
        this.modelTextureMap = Util.argumentNullCheck( tileData.textureMap , "texture" );
        this.blockStateResourceLocation = Util.coalesce( tileData.blockStateResource , Strata.resource( "proto_cube_gimbal_overlay" ) );
        if( tileData.oreItemTextureLayers != null && tileData.oreItemTextureLayers.size() > 0 )
            this.oreItemTextureLayers = tileData.oreItemTextureLayers.toArray( new LayeredTextureLayer[ tileData.oreItemTextureLayers.size() ] );
        this.equivalentItemResourceLocation = tileData.equivalentItemResourceLocation;
        this.furnaceResult = tileData.furnaceResult;
        this.furnaceExperience = tileData.furnaceExperience;
        this.proxyBlockResourceLocation = tileData.proxyOreResourceLocation;
        this.forcedHost = tileData.forcedHost;
        this.hostAffinities = tileData.hostAffinities;
        this.breaksIntoResourceLocation = tileData.breaksIntoResourceLocation;
        this.weightedDropCollections = tileData.weightedDropCollections;
        this.experienceDropFormula = tileData.experienceDropFormula;
        this.ambientSound = tileData.ambientSound;

        // ICommonBlockProperties
        this.material = Util.argumentNullCheck( tileData.material , "material" );
        this.soundType = Util.argumentNullCheck( tileData.soundType , "soundType" );
        this.harvestTool = Util.argumentNullCheck( tileData.harvestTool , "harvestTool" );
        this.harvestLevel = Util.coalesce( tileData.harvestLevel , 0 );
        this.hardness = Util.coalesce( tileData.hardness , 3.0f );
        this.explosionResistance = Util.coalesce( tileData.explosionResistance , 5.0f );
        this.lightLevel = Util.coalesce( tileData.lightLevel , 0 );
        this.burnTime = Util.coalesce( tileData.burnTime , 0 );
        this.specialBlockPropertyFlags = Util.coalesce( tileData.specialBlockPropertyFlags , 0L );

        LocalizationRegistry.INSTANCE.register(
            Strata.resource( oreName ).toString(),
            Util.lazyCoalesce( tileData.languageMap , HashMap::new ) );
        LocalizationRegistry.INSTANCE.register(
            Strata.resource( oreName ).toString() + ".name",
            Util.lazyCoalesce( tileData.languageMap , HashMap::new ) );
        LocalizationRegistry.INSTANCE.register(
            Strata.resource( oreName ).toString() + ".tooltip",
            Util.lazyCoalesce( tileData.tooltipMap , HashMap::new ) );
    }

    private void resolveProxyMembersIfNecessary()
    {
        if( proxyBlockResourceLocation == null || proxyBlock != null )
            return;

        proxyBlock = Block.REGISTRY.getObject( proxyBlockResourceLocation.resourceLocation );
        if( proxyBlock == null || proxyBlock.equals( Blocks.AIR ) )
        {
            proxyBlock = null;
            proxyBlockResourceLocation = null;
            return;
        }

        if( proxyBlockState == null )
            proxyBlockState = proxyBlock.getStateFromMeta( proxyBlockResourceLocation.meta );
    }

    // IOreInfo overrides

    @Nonnull
    @Override
    public String oreName()
    {
        return oreName;
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
    public String itemOreDictionaryName()
    {
        return itemOreDictionaryName;
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

    @Nonnull
    @Override
    public ResourceLocation blockStateResourceLocation()
    {
        return blockStateResourceLocation;
    }

    @Nullable
    @Override
    @SideOnly( Side.CLIENT )
    public LayeredTextureLayer[] oreItemTextureLayers()
    {
        return oreItemTextureLayers;
    }

    @Nullable
    @Override
    public ItemStack equivalentItemStack()
    {
        if( equivalentItemResourceLocation == null )
            return null;

        // Deferred resolution until reasonably sure the item has been created
        ItemStack equivalentItemStack = ItemHelper.metaResourceLocationToItemStack( equivalentItemResourceLocation );
        return ItemHelper.isNullOrAirOrEmpty( equivalentItemStack ) ? null : equivalentItemStack;
    }

    @Nullable
    @Override
    public ItemStack furnaceResult()
    {
        ItemStack equivalentItemStack = equivalentItemStack();
        if( equivalentItemStack != null )
        {
            ItemStack equivalentSmeltingResult = FurnaceRecipes.instance().getSmeltingResult( equivalentItemStack );
            if( equivalentSmeltingResult != null && !equivalentSmeltingResult.isEmpty() )
                return equivalentSmeltingResult;
        }

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

    @Nullable
    @Override
    public IBlockState proxyBlockState()
    {
        resolveProxyMembersIfNecessary();

        return proxyBlockState;
    }

    @Nullable
    @Override
    public ItemStack proxyBlockItemStack()
    {
        resolveProxyMembersIfNecessary();

        return proxyBlock != null ? new ItemStack( Item.getItemFromBlock( proxyBlock ) , 1 , proxyBlockResourceLocation.meta ) : null;
    }

    @Nullable
    @Override
    public MetaResourceLocation forcedHost()
    {
        return forcedHost;
    }

    @Nullable
    @Override
    public List< MetaResourceLocation > hostAffinities()
    {
        return hostAffinities;
    }

    @Nullable
    @Override
    public MetaResourceLocation breaksInto()
    {
        return breaksIntoResourceLocation;
    }

    @Nullable
    @Override
    public WeightedDropCollections weightedDropGroups()
    {
        return weightedDropCollections;
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
    public String localizedName()
    {
        return LocalizationRegistry.INSTANCE.get( Strata.resource( oreName ).toString() );
    }

    @Nullable
    @Override
    public List< String > localizedTooltip()
    {
        String tooltip = LocalizationRegistry.INSTANCE.get( Strata.resource( oreName ).toString() + ".tooltip" );
        return tooltip != null ? Arrays.asList( tooltip.split( "\\\\n" ) ) : null;
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

    // IForgeRegistrable overrides

    @Override
    @SideOnly( Side.CLIENT )
    public void stitchTextures( TextureMap textureMap , boolean pre )
    {
        modelTextureMap.stitchTextures( textureMap , pre );
    }
}
