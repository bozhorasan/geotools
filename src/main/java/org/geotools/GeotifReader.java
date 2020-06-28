package org.geotools;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.util.factory.Hints;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;


/**
 * Hello world!
 */
public class GeotifReader {
    String fileName = "GRAY_LR_SR.tif";

    public static void main(String[] args) {
        GeotifReader tifReader = new GeotifReader();
        try {
//            tifReader.method1();
            tifReader.method2();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void method1() throws IOException {
        File file = new File(fileName);
        AbstractGridFormat format = GridFormatFinder.findFormat( file );
        GridCoverage2DReader reader = format.getReader( file );
        System.out.println("M1");
    }

    private void method2() throws IOException {
        File file = new File(fileName);
        GeoTiffReader reader = new GeoTiffReader(file, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));

        GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
        CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();
        Envelope env = coverage.getEnvelope();
        RenderedImage image = coverage.getRenderedImage();

        System.out.println("M2");
    }
}
