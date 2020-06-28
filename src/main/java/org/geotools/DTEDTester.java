/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2007-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package org.geotools;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.swing.*;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverageio.gdal.BaseGDALGridCoverage2DReader;
import org.geotools.coverageio.gdal.dted.DTEDFormatFactory;
import org.geotools.coverageio.gdal.dted.DTEDReader;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.util.factory.Hints;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**    Dikkat: Calismiyor!!!
 * @author Daniele Romagnoli, GeoSolutions
 * @author Simone Giannecchini (simboss), GeoSolutions
 */
public final class DTEDTester {

    /**
     * file name of a valid DTED sample data to be used for tests.
     */
//    private static final String fileName = "n43.dt0";
            //Dikkat: Calismiyor!!!
    private static final String fileName = "d3439_(i33a1).dt2";
    DTEDFormatFactory dtedFormatFactory = new DTEDFormatFactory();

    public DTEDTester() {
    }

    public static void main(String[] args) {
        DTEDTester dtedTester = new DTEDTester();
        try {
            dtedTester.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test() throws Exception {
        // Preparing an useful layout in case the image is striped.
        final ImageLayout l = new ImageLayout();
        l.setTileGridXOffset(0).setTileGridYOffset(0).setTileHeight(512).setTileWidth(512);

        Hints hints = new Hints();
        hints.add(new RenderingHints(JAI.KEY_IMAGE_LAYOUT, l));

        // get a reader
        final File file = new File(fileName);
        final BaseGDALGridCoverage2DReader reader = new DTEDReader(file, hints);

        // read once
        GridCoverage2D gc = (GridCoverage2D) reader.read(null);
        forceDataLoading(gc);

        // log band names (check they are not all UNKNOWN)
        System.out.println(Arrays.toString(gc.getSampleDimensions()));

        // read again with subsampling and crop
        final double cropFactor = 2.0;
        final int oldW = gc.getRenderedImage().getWidth();
        final int oldH = gc.getRenderedImage().getHeight();
        final Rectangle range = ((GridEnvelope2D) reader.getOriginalGridRange());
        final GeneralEnvelope oldEnvelope = reader.getOriginalEnvelope();
        final GeneralEnvelope cropEnvelope =
                new GeneralEnvelope(
                        new double[]{
                                oldEnvelope.getLowerCorner().getOrdinate(0)
                                        + (oldEnvelope.getSpan(0) / cropFactor),
                                oldEnvelope.getLowerCorner().getOrdinate(1)
                                        + (oldEnvelope.getSpan(1) / cropFactor)
                        },
                        new double[]{
                                oldEnvelope.getUpperCorner().getOrdinate(0),
                                oldEnvelope.getUpperCorner().getOrdinate(1)
                        });
        cropEnvelope.setCoordinateReferenceSystem(reader.getCoordinateReferenceSystem());

        final ParameterValue gg =
                (ParameterValue)
                        ((AbstractGridFormat) reader.getFormat()).READ_GRIDGEOMETRY2D.createValue();
        gg.setValue(
                new GridGeometry2D(
                        new GridEnvelope2D(
                                new Rectangle(
                                        0,
                                        0,
                                        (int) (range.width / 2.0 / cropFactor),
                                        (int) (range.height / 2.0 / cropFactor))),
                        cropEnvelope));
        gc = (GridCoverage2D) reader.read(new GeneralParameterValue[]{gg});
        forceDataLoading(gc);
    }

    public void testService() throws NoSuchAuthorityCodeException, FactoryException {
        GridFormatFinder.scanForPlugins();

        Iterator<GridFormatFactorySpi> list = GridFormatFinder.getAvailableFormats().iterator();
        boolean found = false;
        GridFormatFactorySpi fac = null;

        while (list.hasNext()) {
            fac = (GridFormatFactorySpi) list.next();

            if (fac instanceof DTEDFormatFactory) {
                found = true;

                break;
            }
        }

//    Assert.assertTrue("DTEDFormatFactory not registered", found);
//    Assert.assertTrue("DTEDFormatFactory not available", fac.isAvailable());
//    Assert.assertNotNull(new DTEDFormatFactory().createFormat());
    }

    /*Copied from GDALTestCase*/
    protected static void forceDataLoading(final GridCoverage2D gc) {
//    Assert.assertNotNull(gc);

        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane()
                .add(
                        new javax.media.jai.widget.ScrollingImagePanel(
                                gc.getRenderedImage(), 800, 800));
        frame.pack();
        SwingUtilities.invokeLater(
                new Runnable() {

                    public void run() {
                        frame.setVisible(true);
                    }
                });

//      PlanarImage.wrapRenderedImage(gc.getRenderedImage()).getTiles();
    }
}