/*
   Copyright (c) 2010 ESRI

   All rights reserved under the copyright laws of the United States
   and applicable international laws, treaties, and conventions.

   You may freely redistribute and use this sample code, with or
   without modification, provided you include the original copyright
   notice and use restrictions.

   See use restrictions in use_restrictions.txt.
 */
package com.esri.ags.skins.supportClasses
{

import mx.core.ILayoutElement;

import spark.components.supportClasses.GroupBase;
import spark.layouts.supportClasses.LayoutBase;

/**
 * FlowLayout for editor toolbar
 * @private
 */
public class FlowLayout extends LayoutBase
{
    override public function updateDisplayList(containerWidth:Number, containerHeight:Number):void
    {
        // The position for the current element
        var x:Number = 0;
        var y:Number = 0;

        // loop through the elements
        var layoutTarget:GroupBase = target;
        var count:int = layoutTarget.numElements;
        for (var i:int = 0; i < count; i++)
        {
            // get the current element, work with the ILayoutElement interface
            var element:ILayoutElement = layoutTarget.getElementAt(i);

            if (element.includeInLayout) // always check this, so that element is not included in the layout
            {
                // Resize the element to its preferred size by passing
                // NaN for the width and height constraints
                element.setLayoutBoundsSize(NaN, NaN);

                // Find out the element's dimensions sizes.
                // Do this after the element has been already resized to its preferred size.
                var elementWidth:Number = element.getLayoutBoundsWidth();
                var elementHeight:Number = element.getLayoutBoundsHeight();

                // Would the element fit on this line, or should move to the next line?
                if (x + elementWidth > containerWidth)
                {
                    // Start from the left side
                    x = 0;

                    // Move down by elementHeight+5,assuming all elements are of equal height
                    y += elementHeight + 5;
                }

                // Position the element
                element.setLayoutBoundsPosition(x, y);

                // Update the current position, add a gap of 5
                x += elementWidth + 5;
            }
        }
    }
}

}
