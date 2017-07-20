/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.world.odorworld.entities;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.simbrain.util.propertyeditor.ComboBoxWrapper;
import org.simbrain.util.propertyeditor.DisplayOrder;
import org.simbrain.world.odorworld.OdorWorld;

/**
 * Represents an entity that can rotate.
 *
 * TODO: Currently rotating entities are "agents" that can accept sensors and
 * effectors. This concepts needs to be separated from that of a rotating
 * entity. In principle non-rotating entities should be able to have sensors,
 * for example.
 */
public class RotatingEntity extends OdorWorldEntity {

    /** Images for various angles. */
    private TreeMap<Double, Animation> imageMap;

    /** Current heading / orientation. */
    private double heading = DEFAULT_HEADING;

    /** Initial heading of agent. */
    private final static double DEFAULT_HEADING = 0;

    /** Default location for sensors relative to agent. */
    private static double WHISKER_ANGLE = Math.PI / 4;

    /** Amount to manually rotate. */
    private final double manualMotionTurnIncrement = 4;

    /** Amount to manually rotate. */
    private final double manualStraightMovementIncrement = 4;

    /** Default type. */
    private static final String DEFAULT_TYPE = "Mouse";

    // TODO: Poorly named. Image or image-set name.
    /** Type; used to load images. */
    private String entityType = DEFAULT_TYPE;

    /** Obvious... */
    private final static double DEGREES_IN_A_CIRCLE = 360;

    /**
     * Create a rotating entity using default map.
     *
     * @param world parent world
     */
    public RotatingEntity(final OdorWorld world) {
        super(world);
        initTreeMap();
        this.setAnimation(imageMap.get(imageMap.firstKey()));
    }

    /**
     * Initialize the tree map, which associates angles with images /
     * animations.
     */
    private void initTreeMap() {
        if (entityType == null) {
            entityType = "Mouse";
        }
        if (entityType.equalsIgnoreCase("Circle")) {
            imageMap = RotatingEntityManager.getCircle();
        } if (entityType.equalsIgnoreCase("Mouse")) {
            imageMap = RotatingEntityManager.getMouse();
        } else if (entityType.equalsIgnoreCase("Amy")) {
            imageMap = RotatingEntityManager.getRotatingTileset("amy", 20);
        } else if (entityType.equalsIgnoreCase("Arnold")) {
            imageMap = RotatingEntityManager.getRotatingTileset("arno", 20);
        } else if (entityType.equalsIgnoreCase("Boy")) {
            imageMap = RotatingEntityManager.getRotatingTileset("boy", 20);
        } else if (entityType.equalsIgnoreCase("Cow")) {
            imageMap = RotatingEntityManager.getRotatingTileset("cow", 25);
        } else if (entityType.equalsIgnoreCase("Girl")) {
            imageMap = RotatingEntityManager.getRotatingTileset("girl", 20);
        } else if (entityType.equalsIgnoreCase("Lion")) {
            imageMap = RotatingEntityManager.getRotatingTileset("lion", 15);
        } else if (entityType.equalsIgnoreCase("Susi")) {
            imageMap = RotatingEntityManager.getRotatingTileset("susi", 20);
        } else if (entityType.equalsIgnoreCase("Jake")) {
            imageMap = RotatingEntityManager.getRotatingTileset("jake", 20);
        } else if (entityType.equalsIgnoreCase("Steve")) {
            imageMap = RotatingEntityManager.getRotatingTileset("steve", 20);
        }
        update();
    }

    /**
     * Returns the heading in radians.
     *
     * @return orientation in degrees
     */
    public double getHeadingRadians() {
        return (heading * Math.PI) / 180;
    }

    /**
     * Set the orientation of the creature.
     *
     * @param d the orientation, in degrees
     */
    public void setHeading(final double d) {
        // System.out.println("setOrientation:" + d);
        heading = d;
        updateImageBasedOnHeading();
        getParentWorld().fireEntityChanged(this);
    }

    /**
     * Returns the current heading, in degrees.
     *
     * @return current heading.
     */
    @DisplayOrder(val = 30)
    public double getHeading() {
        return heading;
    }

    /**
     * Ensures that angle lies between 0 and 360.
     *
     * @param val the absolute angle
     * @return value's "absolute angle"
     */
    private double computeAngle(final double val) {

        // TODO: This will not work for vals greater or less than 360
        double retVal = val;
        if (val >= DEGREES_IN_A_CIRCLE) {
            retVal -= DEGREES_IN_A_CIRCLE;
        }
        if (val < 0) {
            retVal += DEGREES_IN_A_CIRCLE;
        }
        return retVal;
    }

    @Override
    public void update() {
        super.update();

        if (!isBlocked()) {
            heading = computeAngle(heading);
            // System.out.println("heading:" + heading);
            // TODO: only do this if heading has changed
            updateImageBasedOnHeading();
            getAnimation().update();
        }
    }

    /**
     * The method name says it all...
     */
    private void updateImageBasedOnHeading() {
        for (Entry<Double, Animation> entry : imageMap.entrySet()) {
            // System.out.println("" + heading + "-" + entry.getKey());
            if (heading < entry.getKey()) {
                setAnimation(entry.getValue());
                break;
            }
        }
    }

    /**
     * Initialize map animations using image location information.
     */
    public void postSerializationInit() {
        super.postSerializationInit();
        initTreeMap();
        Iterator<Double> i = imageMap.keySet().iterator();
        while (i.hasNext()) {
            Double key = i.next();
            imageMap.get(key).initializeImages();
        }
    }

    /**
     * @return the imageMap
     */
    public TreeMap<Double, Animation> getImageMap() {
        return imageMap;
    }

    /**
     * @param imageMap the imageMap to set
     */
    public void setImageMap(TreeMap<Double, Animation> imageMap) {
        this.imageMap = imageMap;
    }

    /**
     * @return the type
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param type the type to set
     */
    public void setEntityType(String type) {
        this.entityType = type;
        initTreeMap();
    }

    /**
     * Getter which returns data that can be used in a combo box (the property
     * editor dialog).
     *
     * @return the image data
     */
    public ComboBoxWrapper getType() {
        return new ComboBoxWrapper() {
            public Object getCurrentObject() {
                return getEntityType();
            }

            public Object[] getObjects() {
                return new Object[] { "Amy", "Arnold", "Boy", "Circle", "Cow",  "Girl", "Jake",
                        "Lion", "Mouse", "Susi", "Steve" };
            }
        };
    }

    /**
     * Setter which takes the data from a combo box as an argument.
     *
     * @param imageData the data from the combo box
     */
    public void setType(ComboBoxWrapper imageData) {
        setEntityType((String) imageData.getCurrentObject());
    }

    /**
     * Rotate left by the specified amount.
     *
     * @param amount amount to turn left. Assumes a positive number.
     */
    public void turnLeft(double amount) {
        turn(amount);
    }

    /**
     * Turn by the specified amount, positive or negative.
     *
     * @param amount
     */
    public void turn(double amount) {
        if (amount == 0) {
            return;
        }
        if (!isBlocked()) {
            heading += amount;
        }

    }

    /**
     * Rotate right by the specified amount.
     *
     * @param amount amount to turn right. Assumes a positive number.
     */
    public void turnRight(double amount) {
        if (amount == 0) {
            return;
        }
        if (!isBlocked()) {
            heading -= amount;
        }
    }

    /**
     * Move the entity in a straight line relative to its current heading.
     *
     * @param amount
     */
    public void goStraight(double amount) {
        if (amount == 0) {
            return;
        }
        if (!isBlocked()) {
            double radians = getHeadingRadians();
            setX(getX() + (float) (amount * Math.cos(radians)));
            setY(getY() - (float) (amount * Math.sin(radians)));
        }
    }


}
