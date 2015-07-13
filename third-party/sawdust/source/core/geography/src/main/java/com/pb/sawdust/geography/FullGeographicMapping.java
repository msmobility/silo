package com.pb.sawdust.geography;

import com.pb.sawdust.geography.tensor.GeographicBooleanMatrix;
import com.pb.sawdust.geography.tensor.GeographicDoubleMatrix;
import com.pb.sawdust.tensor.SparseTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;

/**
 * The {@code FullGeographicMapping} is a straightforward {@code GeographicMapping} implementation.
 *
 * @param <F>
 *        The type of the "from" geography.
 *
 * @param <T>
 *        The type of the "to" geography.
 *
 * @author crf
 *         Started 10/17/11 4:44 PM
 */
public class FullGeographicMapping<F extends GeographyElement<?>,T extends GeographyElement<?>> implements GeographicMapping<F,T> {
    private final GeographicDoubleMatrix<F,T> overlay;

    /**
     * Constructor specifying the overlay matrix defining the mapping. The overlay matrix will be used directly in this
     * class (a copy will not be made), so care must be taken that it is not allowed to be modified externally.
     *
     * @param overlay
     *        The overlay matrix defining this mapping.
     */
    public FullGeographicMapping(GeographicDoubleMatrix<F,T> overlay) {
        this.overlay = new GeographicDoubleMatrix<>(overlay.getFromGeography(),overlay.getToGeography(),(DoubleMatrix) TensorUtil.unmodifiableTensor(overlay));
    }

    @Override
    public Geography<?,F> getFromGeography() {
        return overlay.getFromGeography();
    }

    @Override
    public Geography<?,T> getToGeography() {
        return overlay.getToGeography();
    }

    @Override
    public GeographicDoubleMatrix<F,T> getOverlay() {
        return overlay;
    }

    @Override
    public GeographicBooleanMatrix<F,T> getUsageOverlay() {
        return new GeographicBooleanMatrix<F,T>(overlay.getFromGeography(),overlay.getToGeography(),SparseTensor.getFactory()) {
            @Override
            public boolean getCellById(GeographyElement<?> d0id, GeographyElement<?> d1id) {
                return overlay.getCellById(d0id,d1id) > 0.0;
            }

            @Override
            public boolean getCell(int d0, int d1) {
                return overlay.getCell(d0,d1) > 0.0;
            }
        };
    }
}
