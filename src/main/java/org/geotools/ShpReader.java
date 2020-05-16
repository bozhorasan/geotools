package org.geotools;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;


/**
 * Hello world!
 */
public class ShpReader {
//    String fileName = "location.shp";
    String fileName = "contour.shp";

    public static void main(String[] args) {
        ShpReader shpReader = new ShpReader();
        try {
//            shpReader.Method1();
            shpReader.Method2();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Method2() throws IOException {
        FileDataStore fileDataStore = FileDataStoreFinder.getDataStore(new File(fileName));
        SimpleFeatureSource simpleFeatureSource = fileDataStore.getFeatureSource();
        SimpleFeatureCollection simpleFeatureCollection = simpleFeatureSource.getFeatures();
        int featureCounter = 0;
        for (SimpleFeatureIterator simpleFeatureIterator = simpleFeatureCollection.features(); simpleFeatureIterator.hasNext(); ) {
            SimpleFeature simpleFeature = simpleFeatureIterator.next();
            //PointFeature
            Object geometry = simpleFeature.getDefaultGeometry();
            if (featureCounter % 100 == 0) {
                System.out.println("Geometry at[" + featureCounter + "] :" + geometry);
                if (geometry instanceof Point) {
                    System.out.println("Point...");
                    Point point = (Point) geometry;
                    double x = point.getCoordinate().getX();
                    double y = point.getCoordinate().getY();
                    System.out.println("Point X Y: " + x + " - " + y);
                } else if (geometry instanceof MultiLineString) {
                    System.out.println("MultilineString...");
                    MultiLineString multiLineString = (MultiLineString) geometry;
                    Coordinate[] coordinates = multiLineString.getCoordinates();
                    int i;
                    for (i= 0; i < coordinates.length; i++) {
                        if (i % 100 == 0) {
                            Coordinate coordinate = coordinates[i];
                            double x = coordinate.getX();
                            double y = coordinate.getY();
                            System.out.println("Point X Y: " + x + " - " + y);
                        }
                    }
                    System.out.println("Number of coordinates in multilinestring:"+i);
                } else {
                    System.out.println("Unhandled type");
                }
            }
            featureCounter++;
        }
        System.out.println("Feature Counter:" + featureCounter);
    }

    private void Method1() throws IOException {
        ShpFiles shapeFiles;
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        System.out.println("Shape File Reader App");
        shapeFiles = new ShpFiles(fileName);
        ShapefileReader shapefileReader = new ShapefileReader(shapeFiles, true, true, geometryFactory);
        while (shapefileReader.hasNext()) {
            ShapefileReader.Record record = shapefileReader.nextRecord();
            System.out.println("type:" + record.type.name);
            System.out.println("no:" + record.number);
        }
    }
}
