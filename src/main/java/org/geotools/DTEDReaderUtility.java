package org.geotools;

import org.geotools.coverage.grid.GeneralGridEnvelope;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.geotools.coverage.grid.io.footprint.FootprintBehavior;
import org.geotools.coverageio.gdal.BaseGDALGridFormat;
import org.geotools.coverageio.gdal.dted.DTEDReader;
import org.geotools.coverageio.gdal.mrsid.MrSIDReader;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.geometry.GeneralEnvelope;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.io.File;
import java.io.IOException;


/**
 * Hello world!
 */
public class DTEDReaderUtility {
    String fileName = "contour.shp";

    public static void main(String[] args) {
        DTEDReaderUtility dtedReaderUtility = new DTEDReaderUtility();
        try {
            dtedReaderUtility.parseDtedFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseDtedFile() throws IOException {
        final File file = new File("C:\\source\\geotools\\tutorial\\d3439_(i33a1).dt2");
        final DTEDReader reader = new DTEDReader(file);

        // Setting GridGeometry for reading half coverage.
//        todo Dikkat: Original line final Rectangle range = reader.getOriginalGridRange().toRectangle();
//        todo https://docs.geotools.org/maintenance/userguide/welcome/upgrade.html Geotools 2.6 GridRange Removed
        final Rectangle range = (Rectangle)reader.getOriginalGridRange();
        final GeneralEnvelope originalEnvelope = reader.getOriginalEnvelope();
        final GeneralEnvelope reducedEnvelope = new GeneralEnvelope(new double[]{
                originalEnvelope.getLowerCorner().getOrdinate(0),
                originalEnvelope.getLowerCorner().getOrdinate(1)},
                new double[]{originalEnvelope.getMedian().getOrdinate(0),
                        originalEnvelope.getMedian().getOrdinate(1)});
        reducedEnvelope.setCoordinateReferenceSystem(reader.getCoordinateReferenceSystem());

        final ParameterValue gg = (ParameterValue) ((AbstractGridFormat) reader.getFormat()).READ_GRIDGEOMETRY2D.createValue();

        gg.setValue(new GridGeometry2D(new GeneralGridEnvelope(new Rectangle(0, 0,
                (int) (range.width / 2.0),
                (int) (range.height / 2.0))), originalEnvelope/*reducedEnvelope*/));

        // Read ignoring overviews with subsampling and crop, using Jai,
        // multithreading and customized tilesize
        final ParameterValue policy = (ParameterValue) ((AbstractGridFormat) reader.getFormat()).OVERVIEW_POLICY.createValue();
        policy.setValue(OverviewPolicy.IGNORE);

        // Enable multithreading read
        final ParameterValue mt = (ParameterValue) ((BaseGDALGridFormat) reader.getFormat()).USE_MULTITHREADING.createValue();
        mt.setValue(true);

        // Customizing Tile Size
        final ParameterValue tilesize = (ParameterValue) ((BaseGDALGridFormat) reader.getFormat()).SUGGESTED_TILE_SIZE.createValue();
        tilesize.setValue("512,512");

        // Setting read type: use JAI ImageRead
        final ParameterValue useJaiRead = (ParameterValue) ((BaseGDALGridFormat) reader.getFormat()).USE_JAI_IMAGEREAD.createValue();
        useJaiRead.setValue(true);

        // Setting the footprint behavior
        // For this example, there should be a C:/testdata/sampledata.wkt file containing
        // the footprint, so that the masking can occur
        final ParameterValue<String> footprint = AbstractGridFormat.FOOTPRINT_BEHAVIOR.createValue();
        footprint.setValue(FootprintBehavior.Transparent.toString());

        GridCoverage gc = (GridCoverage2D) reader.read(new GeneralParameterValue[]{gg,
                policy, mt, tilesize, useJaiRead, footprint});
        double x =34.5934; //lon QGISde solda
        double y =39.9477; //lat QGISde sağda
        //value Bant1 1400
        DirectPosition directPosition = new GeneralDirectPosition(x,y);
        Object object = gc.evaluate(directPosition);
        System.out.println("gc read..."+gc.toString());
    }
}
