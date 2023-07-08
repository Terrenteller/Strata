package com.riintouge.strata.misc;

import java.util.ArrayList;
import java.util.List;

public class TreeIndenter
{
    public static char CHILD_GLYPH = '├';
    public static char CONT_GLYPH = '│';
    public static char LAST_CHILD_GLYPH = '└';
    public static char BLANK_GLYPH = ' ';
    public static char DASH_GLYPH = '─';

    public enum Indent
    {
        CHILD,
        CONT,
        LAST_CHILD,
        BLANK
    }

    protected final String childIndent;
    protected final String contIndent;
    protected final String lastChildIndent;
    protected final String blankIndent;

    protected List< Indent > indents = new ArrayList<>();
    protected String existingIndent;

    public TreeIndenter( String indentTemplate )
    {
        this.childIndent = String.format( indentTemplate , CHILD_GLYPH ).replace( '-' , DASH_GLYPH );
        this.contIndent = String.format( indentTemplate , CONT_GLYPH ).replace( '-' , BLANK_GLYPH );
        this.lastChildIndent = String.format( indentTemplate , LAST_CHILD_GLYPH ).replace( '-' , DASH_GLYPH );
        this.blankIndent = String.format( indentTemplate , BLANK_GLYPH ).replace( '-' , BLANK_GLYPH );
    }

    public void indent( Indent indent )
    {
        indents.add( indent );
        existingIndent = null;
    }

    public void reindent( Indent indent )
    {
        if( indents.size() != 0 )
        {
            indents.set( indents.size() - 1 , indent );
            existingIndent = null;
        }
    }

    public void unindent()
    {
        if( indents.size() > 0 )
            indents.remove( indents.size() - 1 );

        existingIndent = null;
    }

    // Object overrides

    @Override
    public String toString()
    {
        if( existingIndent != null )
            return existingIndent;

        StringBuilder indentBuilder = new StringBuilder();
        for( Indent indent : indents )
        {
            switch( indent )
            {
                case CHILD:
                    indentBuilder.append( childIndent );
                    break;
                case LAST_CHILD:
                    indentBuilder.append( lastChildIndent );
                    break;
                case CONT:
                    indentBuilder.append( contIndent );
                    break;
                default:
                    indentBuilder.append( blankIndent );
            }
        }

        existingIndent = indentBuilder.toString();
        return existingIndent;
    }
}
