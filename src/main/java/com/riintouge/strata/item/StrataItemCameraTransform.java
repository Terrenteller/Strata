package com.riintouge.strata.item;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Optional;

// Item transforms for dynamically created items are very broken.
// Just hardcode the important numbers here and don't bother with a template item model JSON.
// These values were very unscientifically taken from the result of model.getAllTransforms()
// in ModelLoader.VanillaModelWrapper.bakeImpl() for "minecraft:models/item/clay_ball".
@SideOnly( Side.CLIENT )
public enum StrataItemCameraTransform
{
    THIRD_PERSON_LEFT_HAND(
        new Vector3f( 0.0f , 0.0f , 0.0f ),
        new Vector3f( 0.0f , 0.1875f , 0.0625f ),
        new Vector3f( 0.55f , 0.55f , 0.55f ) ),

    THIRD_PERSON_RIGHT_HAND(
        new Vector3f( 0.0f , 0.0f , 0.0f ),
        new Vector3f( 0.0f , 0.1875f , 0.0625f ),
        new Vector3f( 0.55f , 0.55f , 0.55f ) ),

    FIRST_PERSON_LEFT_HAND(
        new Vector3f( 0.0f , -90.0f , 25.0f ),
        new Vector3f( 0.070625f , 0.2f , 0.070625f ),
        new Vector3f( 0.68f , 0.68f , 0.68f ) ),

    FIRST_PERSON_RIGHT_HAND(
        new Vector3f( 0.0f , -90.0f , 25.0f ),
        new Vector3f( 0.070625f , 0.2f , 0.070625f ),
        new Vector3f( 0.68f , 0.68f , 0.68f ) ),

    HEAD(
        new Vector3f( 0.0f , 180.0f , 0.0f ),
        new Vector3f( 0.0f , 0.8125f , 0.4375f ),
        new Vector3f( 1.0f , 1.0f , 1.0f ) ),

    GUI(
        new Vector3f( 0.0f , 0.0f , 0.0f ),
        new Vector3f( 0.0f , 0.0f , 0.0f ),
        new Vector3f( 1.0f , 1.0f , 1.0f ) ),

    GROUND(
        new Vector3f( 0.0f , 0.0f , 0.0f ),
        new Vector3f( 0.0f , 0.125f , 0.0f ),
        new Vector3f( 0.5f , 0.5f , 0.5f ) ),

    FIXED(
        new Vector3f( 0.0f , 180.0f , 0.0f ),
        new Vector3f( 0.0f , 0.0f , 0.0f ),
        new Vector3f( 1.0f , 1.0f , 1.0f ) );

    public final TRSRTransformation TRSR;

    StrataItemCameraTransform( Vector3f rotation , Vector3f translation , Vector3f scale )
    {
        TRSR = TRSRTransformation.blockCenterToCorner(
            TRSRTransformation.from(
                new ItemTransformVec3f(
                    rotation,
                    translation,
                    scale ) ) );
    }

    // Statics

    @Nonnull
    public static ImmutableMap< ItemCameraTransforms.TransformType , TRSRTransformation > getTransforms( IModelState state )
    {
        EnumMap< ItemCameraTransforms.TransformType , TRSRTransformation > transforms = new EnumMap<>( ItemCameraTransforms.TransformType.class );

        for( ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values() )
        {
            Optional< TRSRTransformation > optionalTransform = state.apply( Optional.of( type ) );
            if( optionalTransform.isPresent() )
            {
                transforms.put( type , optionalTransform.get() );
            }
            else
            {
                switch( type )
                {
                    case THIRD_PERSON_LEFT_HAND:
                        transforms.put( type , THIRD_PERSON_LEFT_HAND.TRSR );
                        break;
                    case THIRD_PERSON_RIGHT_HAND:
                        transforms.put( type , THIRD_PERSON_RIGHT_HAND.TRSR );
                        break;
                    case FIRST_PERSON_LEFT_HAND:
                        transforms.put( type , FIRST_PERSON_LEFT_HAND.TRSR );
                        break;
                    case FIRST_PERSON_RIGHT_HAND:
                        transforms.put( type , FIRST_PERSON_RIGHT_HAND.TRSR );
                        break;
                    case HEAD:
                        transforms.put( type , HEAD.TRSR );
                        break;
                    case GUI:
                        transforms.put( type , GUI.TRSR );
                        break;
                    case GROUND:
                        transforms.put( type , GROUND.TRSR );
                        break;
                    case FIXED:
                        transforms.put( type , FIXED.TRSR );
                        break;
                }
            }
        }

        return ImmutableMap.copyOf( transforms );
    }
}
