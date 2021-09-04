package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.ore.IOreInfo;
import com.riintouge.strata.item.LocalizationRegistry;
import com.riintouge.strata.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class ImmutableOre implements IOreInfo , IForgeRegistrable
{
    private Map< String , String > languageMap;

    // IOreInfo
    private String oreName;
    private String blockOreDictionaryName;
    private String itemOreDictionaryName;
    private GenericCubeTextureMap modelTextureMap;
    private ResourceLocation oreItemTextureResource;
    private MetaResourceLocation equivalentItemResourceLocation;
    private ItemStack equivalentItemStack = null; // Lazily evaluated
    private MetaResourceLocation proxyBlockResourceLocation;
    private IBlockState proxyBlockState = null; // Lazily evaluated
    private int baseDropAmount;
    private String bonusDropExpr;
    private int baseExp;
    private String bonusExpExpr;

    // IGenericBlockProperties
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private int burnTime;

    public ImmutableOre( TileData tileData ) throws IllegalArgumentException
    {
        this.languageMap = Util.lazyCoalesce( tileData.languageMap , HashMap::new );

        // IOreInfo
        this.oreName = Util.argumentNullCheck( tileData.oreName , "oreName" );
        this.blockOreDictionaryName = tileData.blockOreDictionaryName;
        this.itemOreDictionaryName = tileData.itemOreDictionaryName;
        this.modelTextureMap = Util.argumentNullCheck( tileData.textureMap , "textureMap" );
        this.oreItemTextureResource = this.modelTextureMap.get( (EnumFacing)null );
        this.equivalentItemResourceLocation = tileData.equivalentItemResourceLocation;
        this.proxyBlockResourceLocation = tileData.proxyOreResourceLocation;
        this.baseDropAmount = Util.coalesce( tileData.baseDropAmount , 1 );
        this.bonusDropExpr = tileData.bonusDropExpr;
        this.baseExp = Util.coalesce( tileData.baseExp , 0 );
        this.bonusExpExpr = tileData.bonusExpExpr;

        // IGenericBlockProperties
        this.material = Util.argumentNullCheck( tileData.material , "material" );
        this.soundType = Util.argumentNullCheck( tileData.soundType , "soundType" );
        this.harvestTool = Util.argumentNullCheck( tileData.harvestTool , "harvestTool" );
        this.harvestLevel = Util.coalesce( tileData.harvestLevel , 0 );
        this.hardness = Util.coalesce( tileData.hardness , 1.0f );
        this.explosionResistance = Util.coalesce( tileData.explosionResistance , 1.7f * this.hardness );
        this.burnTime = Util.coalesce( tileData.burnTime , 0 );

        LocalizationRegistry.INSTANCE.register( this , Strata.resource( oreName ).toString() + ".name" , this.languageMap );
    }

    // IOreInfo overrides

    @Override
    public String oreName()
    {
        return oreName;
    }

    @Override
    public String blockOreDictionaryName()
    {
        return blockOreDictionaryName;
    }

    @Override
    public String itemOreDictionaryName()
    {
        return itemOreDictionaryName;
    }

    @Override
    public GenericCubeTextureMap modelTextureMap()
    {
        return modelTextureMap;
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return oreItemTextureResource;
    }

    @Override
    public ItemStack equivalentItemStack()
    {
        // Deferred resolution until reasonably sure the item has been created
        if( equivalentItemResourceLocation != null )
        {
            Item equivalentItem = Item.REGISTRY.getObject( equivalentItemResourceLocation.resourceLocation );
            equivalentItemStack = new ItemStack( equivalentItem , 1 , equivalentItemResourceLocation.meta );
            equivalentItemResourceLocation = null;
        }

        return equivalentItemStack;
    }

    @Override
    public IBlockState proxyBlockState()
    {
        // Deferred resolution until reasonably sure the block has been created
        if( proxyBlockResourceLocation != null )
        {
            Block proxyBlock = Block.REGISTRY.getObject( proxyBlockResourceLocation.resourceLocation );
            proxyBlockState = proxyBlock.getStateFromMeta( proxyBlockResourceLocation.meta );
            proxyBlockResourceLocation = null;
        }

        return proxyBlockState;
    }

    @Override
    public String localizedName()
    {
        return LocalizationRegistry.INSTANCE.get( this );
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
    public int baseDropAmount()
    {
        return baseDropAmount;
    }

    @Override
    public String bonusDropExpr()
    {
        return bonusDropExpr;
    }

    @Override
    public int baseExp()
    {
        return baseExp;
    }

    @Override
    public String bonusExpExpr()
    {
        return bonusExpExpr;
    }

    // IForgeRegistrable overrides

    @Override
    public void stitchTextures( TextureMap textureMap )
    {
        modelTextureMap.stitchTextures( textureMap );
    }
}
