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

import flash.geom.Matrix3D;
import flash.geom.Point;
import flash.geom.Rectangle;

import mx.core.ILayoutElement;

import spark.layouts.supportClasses.LayoutBase;

/**
 * AttachmentLayout
 * @private
 */
public class AttachmentLayout extends LayoutBase
{
    private var m_distance:Number = 80;

    private var m_index:Number = 0;

    private var m_layoutWidth:Number;

    private var m_offsetZ:Number = 200.0;

    private var m_totalWidth:Number;

    public function AttachmentLayout()
    {
        super();
    }

    /**
     * Distance between each item
     */
    public function get distance():Number
    {
        return m_distance;
    }

    /**
     * @private
     */
    public function set distance(value:Number):void
    {
        if (m_distance != value)
        {
            m_distance = value;
            invalidateTarget();
        }
    }

    /**
     * @private
     */
    override public function getScrollPositionDeltaToElement(index:int):Point
    {
        const scrollPos:int = ((m_totalWidth - m_layoutWidth) / (target.numElements - 1)) * index;
        return new Point(scrollPos, 0);
    }

    /**
     * Index of centered item
     */
    [Bindable]
    public function get index():Number
    {
        return m_index;
    }

    public function set index(value:Number):void
    {
        if (m_index != value)
        {
            m_index = value;
            invalidateTarget();
        }
    }

    /**
     * The Z offset.
     */
    public function get offsetZ():Number
    {
        return m_offsetZ;
    }

    /**
     * @private
     */
    public function set offsetZ(value:Number):void
    {
        if (m_offsetZ !== value)
        {
            m_offsetZ = value;
            invalidateTarget();
        }
    }

    /**
     * @private
     */
    override public function updateDisplayList(width:Number, height:Number):void
    {
        if (target && target.numElements > 0)
        {
            const numElements:int = target.numElements;
            const selectedIndex:int = Math.max(index, 0);

            m_totalWidth = width + (numElements - 1) * distance;
            m_layoutWidth = width;

            target.setContentSize(m_totalWidth, height);

            for (var i:int = 0; i < numElements; i++)
            {
                const element:ILayoutElement = useVirtualLayout ? target.getVirtualElementAt(i) : target.getElementAt(i);

                element.setLayoutBoundsSize(NaN, NaN, false);

                const elemWidth2:Number = element.getPreferredBoundsWidth() * 0.5;
                const elemHeight2:Number = element.getPreferredBoundsHeight() * 0.5;

                var posZ:Number = 0.0;
                const posX:Number = distance * i - target.horizontalScrollPosition;
                /*
                   if (posX < 0)
                   {
                   IVisualElement(element).depth = posX;
                   if (posX > -elemWidth2)
                   {
                   posZ = -m_offsetZ - m_offsetZ * posX / elemWidth2;
                   }
                   }
                   else if (posX > 0)
                   {
                   IVisualElement(element).depth = -posX;
                   if (posX < elemHeight2)
                   {
                   posZ = -m_offsetZ + m_offsetZ * posX / elemHeight2;
                   }
                   }
                   else
                   {
                   IVisualElement(element).depth = 0.0;
                   posZ = -m_offsetZ;
                   }
                 */
                const matrix:Matrix3D = new Matrix3D();
                matrix.appendTranslation(posX + width * 0.5 - elemWidth2, height * 0.5 - elemHeight2, posZ);
                element.setLayoutMatrix3D(matrix, false);
            }
        }
    }

    /**
     * @private
     */
    override public function updateScrollRect(w:Number, h:Number):void
    {
        if (target)
        {
            if (clipAndEnableScrolling)
            {
                target.scrollRect = new Rectangle(0, 0, w, h);
            }
            else
            {
                target.scrollRect = null;
            }
        }
    }

    /**
     * @private
     */
    protected function invalidateTarget():void
    {
        if (target)
        {
            target.invalidateSize();
            target.invalidateDisplayList();
        }
    }

    /**
     * @private
     */
    override protected function scrollPositionChanged():void
    {
        if (target)
        {
            target.invalidateDisplayList();
        }
    }
}

}
