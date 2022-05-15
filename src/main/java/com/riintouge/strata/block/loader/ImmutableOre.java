package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.ore.IOreInfo;
import com.riintouge.strata.image.LayeredTextureLayer;
import com.riintouge.strata.item.IDropFormula;
import com.riintouge.strata.item.LocalizationRegistry;
import com.riintouge.strata.item.RPNDropFormula;
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
    private String blockOreDictionaryName;
    private String itemOreDictionaryName;
    private ProtoBlockTextureMap modelTextureMap;
    private ResourceLocation blockstateResourceLocation;
    private List< LayeredTextureLayer > oreItemTextureLayers;
    private MetaResourceLocation equivalentItemResourceLocation;
    private ItemStack equivalentItemStack = null; // Lazily evaluated
    private MetaResourceLocation furnaceResult;
    private Float furnaceExp;
    private MetaResourceLocation proxyBlockResourceLocation;
    private IBlockState proxyBlockState = null; // Lazily evaluated
    private ItemStack proxyBlockItemStack = null; // Lazily evaluated
    private WeightedDropCollections weightedDropCollections;
    private MetaResourceLocation forcedHost;
    private List< MetaResourceLocation > hostAffinities;
    private IDropFormula expDropFormula;

    // ICommonBlockProperties
    private Material material;
    private SoundType soundType;
    private SoundEventTuple ambientSound;
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
        this.blockOreDictionaryName = tileData.blockOreDictionaryName;
        this.itemOreDictionaryName = tileData.itemOreDictionaryName;
        this.modelTextureMap = Util.argumentNullCheck( tileData.textureMap , "texture" );
        this.blockstateResourceLocation = Util.coalesce( tileData.blockstateResourceLocation , Strata.resource( "proto_cube_gimbal_overlay" ) );
        this.oreItemTextureLayers = tileData.oreItemTextureLayers;
        this.equivalentItemResourceLocation = tileData.equivalentItemResourceLocation;
        this.furnaceResult = tileData.furnaceResult;
        this.furnaceExp = tileData.furnaceExp;
        this.proxyBlockResourceLocation = tileData.proxyOreResourceLocation;
        this.ambientSound = tileData.ambientSound;
        this.weightedDropCollections = tileData.weightedDropCollections;
        this.forcedHost = tileData.forcedHost;
        this.hostAffinities = tileData.hostAffinities;
        this.expDropFormula = tileData.expDropFormula;

        // ICommonBlockProperties
        this.material = Util.argumentNullCheck( tileData.material , "material" );
        this.soundType = Util.argumentNullCheck( tileData.soundType , "soundType" );
        this.harvestTool = Util.argumentNullCheck( tileData.harvestTool , "harvestTool" );
        this.harvestLevel = Util.coalesce( tileData.harvestLevel , 0 );
        this.hardness = Util.coalesce( tileData.hardness , 1.0f );
        this.explosionResistance = Util.coalesce( tileData.explosionResistance , 1.7f * this.hardness );
        this.lightLevel = Util.coalesce( tileData.lightLevel , 0 );
        this.burnTime = Util.coalesce( tileData.burnTime , 0 );
        this.specialBlockPropertyFlags = Util.coalesce( tileData.specialBlockPropertyFlags , 0L );

        LocalizationRegistry.INSTANCE.register(
            Strata.resource( oreName ).toString(),
            Util.lazyCoalesce( tileData.languageMap , HashMap::new ) );
        LocalizationRegistry.INSTANCE.register(
            Strata.resource( oreName ).toString() + ".tooltip",
            Util.lazyCoalesce( tileData.tooltipMap , HashMap::new ) );
    }

    private void resolveProxyMembers()
    {
        if( proxyBlockResourceLocation == null )
            return;

        Block proxyBlock = Block.REGISTRY.getObject( proxyBlockResourceLocation.resourceLocation );
        proxyBlockState = proxyBlock.getStateFromMeta( proxyBlockResourceLocation.meta );

        Item proxyItemBlock = Item.getItemFromBlock( proxyBlock );
        proxyBlockItemStack = new ItemStack( proxyItemBlock , 1 , proxyBlockResourceLocation.meta );

        proxyBlockResourceLocation = null;
    }

    // IOreInfo overrides

    @Nonnull
    @Override
    public String oreName()
    {
        return oreName;
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

    @Nonnull
    @Override
    public ResourceLocation blockstateResourceLocation()
    {
        return blockstateResourceLocation;
    }

    @Nullable
    @Override
    @SideOnly( Side.CLIENT )
    public List< LayeredTextureLayer > oreItemTextureLayers()
    {
        return oreItemTextureLayers;
    }

    @Override
    public ItemStack equivalentItemStack()
    {
        // Deferred resolution until reasonably sure the item has been created
        if( equivalentItemResourceLocation != null )
        {
            if( !equivalentItemResourceLocation.resourceLocation.equals( Blocks.AIR.getRegistryName() ) )
            {
                Item equivalentItem = Item.REGISTRY.getObject( equivalentItemResourceLocation.resourceLocation );
                if( equivalentItem != null )
                    equivalentItemStack = new ItemStack( equivalentItem , 1 , equivalentItemResourceLocation.meta );
            }

            equivalentItemResourceLocation = null;
        }

        return equivalentItemStack;
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

    @Nullable
    @Override
    public IBlockState proxyBlockState()
    {
        resolveProxyMembers();

        return proxyBlockState;
    }

    @Nullable
    @Override
    public ItemStack proxyBlockItemStack()
    {
        resolveProxyMembers();

        return proxyBlockItemStack;
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

    @Nullable
    @Override
    public WeightedDropCollections weightedDropGroups()
    {
        return weightedDropCollections;
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
    public IDropFormula expDropFormula()
    {
        return expDropFormula;
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
    public void stitchTextures( TextureMap textureMap )
    {
        modelTextureMap.stitchTextures( textureMap );
    }
}
