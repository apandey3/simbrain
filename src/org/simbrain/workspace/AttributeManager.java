/*
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
package org.simbrain.workspace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A raft of methods for creating attributes and potential attributes.
 *
 * For attributes (Producer and Consumer) the main creation method, using
 * reflection is here. You can create an attribute without specifying a
 * description, in which case a standard description is created.
 *
 * For potential attributes (PotentialProducer and PotentialConsumer), there are
 * two choices, so that there are four creation methods. As with attributes, you
 * can specify a custom description or not. You can also use an attribute type
 * when creating the potential attribute.
 *
 * @author jyoshimi
 *
 */
public class AttributeManager {

    /** Reference to parent component. */
    private WorkspaceComponent parentComponent;

    /**
     * @param parentWorkspace
     */
    public AttributeManager(WorkspaceComponent parentComponent) {
        this.parentComponent = parentComponent;
    }


    /**
     * Create a producer. This version of the method does the real work; others
     * forward to it.
     *
     * @param parentObject parent object
     * @param methodBaseName name of method
     * @param dataType type of data
     * @param description description
     * @return the resulting producer
     */
    public Producer<?> createProducer(
            final Object parentObject,
            final String methodBaseName,
            final Class<?> dataType,
            final String description) {

        Producer<?> producer = new Producer() {

            private Method theMethod;

            // Static initializer
            {
                try {
                    theMethod = parentObject.getClass().getMethod(
                            "get" + methodBaseName, null);
                } catch (SecurityException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    System.err.println("Could not find method "
                            + methodBaseName + " with return type of "
                            + dataType.getCanonicalName());
                    e1.printStackTrace();
                }

            }

            /**
             * {@inheritDoc}
             */
            public Object getValue() {
                try {
                    return theMethod.invoke(parentObject, null);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            /**
             * {@inheritDoc}
             */
            public WorkspaceComponent getParentComponent() {
                return parentComponent;
            }

            /**
             * {@inheritDoc}
             */
            public Object getBaseObject() {
                return parentObject;
            }

            /**
             * {@inheritDoc}
             */
            public String getMethodBaseName() {
                return methodBaseName;
            }

            /**
             * {@inheritDoc}
             */
            public Class<?> getDataType() {
                return dataType;
            }

            /**
             * {@inheritDoc}
             */
            public String getDescription() {
                return description;
            }

        };
        return producer;

    }


    /**
     * Create a producer without specifying a custom description (the
     * description is created automatically).
     *
     * @param baseObject
     *            base object
     * @param methodBaseName
     *            method name
     * @param dataType
     *            data type
     * @return created producer
     */
    public Producer<?> createProducer(final Object baseObject,
            final String methodBaseName, final Class<?> dataType) {
        String description = getDescriptionString(baseObject, methodBaseName, dataType);
        return createProducer(baseObject, methodBaseName, dataType, description);
    }

    /**
     * Create an actual producer from a potential producer.
     *
     * @param potentialAttribute the potential attribute to actualize
     * @return the resulting producer
     */
    public Producer<?> createProducer(final PotentialAttribute potentialAttribute) {
        return createProducer(potentialAttribute.getBaseObject(), potentialAttribute
                .getMethodBaseName(), potentialAttribute.getDataType(),
                potentialAttribute.getDescription());
    }

    /**
     * Create a consumer. This version of the method does the real work; others
     * forward to it.
     *
     * @param parentObject parent object
     * @param methodBaseName name of method
     * @param dataType type of data
     * @param description description
     * @return the resulting consumer
     */
    public Consumer<?> createConsumer(final Object parentObject,
            final String methodBaseName, final Class<?> dataType,
            final String description) {

        Consumer<?> consumer = new Consumer() {

            Method theMethod;

            // Static initializer
            {
                try {
                    theMethod = parentObject.getClass().getMethod(
                            "set" + methodBaseName, new Class[] { dataType });
                } catch (SecurityException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    System.err.println("Could not find method "
                            + methodBaseName + " with argument of type of "
                            + dataType.getCanonicalName());
                    e1.printStackTrace();
                }
            }

            /**
             * {@inheritDoc}
             */
            public void setValue(Object value) {
                try {
                    theMethod.invoke(parentObject, new Object[] { value });
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            /**
             * {@inheritDoc}
             */
            public WorkspaceComponent getParentComponent() {
                return parentComponent;
            }

            /**
             * {@inheritDoc}
             */
            public Object getBaseObject() {
                return parentObject;
            }

            /**
             * {@inheritDoc}
             */
            public String getMethodBaseName() {
                return methodBaseName;
            }

            /**
             * {@inheritDoc}
             */
            public Class<?> getDataType() {
                return dataType;
            }

            /**
             * {@inheritDoc}
             */
            public String getDescription() {
                return description;
            }

        };
        return consumer;

    }

    /**
     * Create a consumer using
     *  1) Parent Object
     *  2) Method name
     *  3) Data type
     *  Description is automatically created.
     */
    public Consumer<?> createConsumer(final Object baseObject,
            final String methodBaseName, final Class<?> dataType) {
        String description = getDescriptionString(baseObject, methodBaseName, dataType);
        return createConsumer(baseObject, methodBaseName, dataType, description);
    }

    /**
     * Create an actual consumer from a potential consumer.
     *
     * @param potentialAttribute the potential attribute to actualize
     * @return the resulting consumer
     */
    public Consumer<?> createConsumer(final PotentialAttribute potentialAttribute) {
        return createConsumer(potentialAttribute.getBaseObject(), potentialAttribute
                .getMethodBaseName(), potentialAttribute.getDataType(),
                potentialAttribute.getDescription());
    }

    /**
     * Create a potential producer.  All information is provided.
     *
     * @param parentObject parent object
     * @param methodBaseName name of method
     * @param dataType type of data
     * @param description custom description
     * @return the resulting producer
     */
    public PotentialProducer createPotentialProducer(
            final Object parentObject,
            final String methodBaseName,
            final Class<?> dataType,
            final String description) {
        return new PotentialProducer(parentComponent, parentObject,  methodBaseName,
                 dataType, description);
    }

    /**
     * Create a potential producer. The description is automatically generated.
     *
     * @param baseObject parent object
     * @param methodBaseName name of method
     * @param dataType type of data
     * @return the resulting producer
     */
    public PotentialProducer createPotentialProducer(final Object baseObject, final String methodBaseName, final Class<?> dataType) {
        return createPotentialProducer(baseObject, methodBaseName, dataType,
                getDescriptionString(baseObject, methodBaseName, dataType));
    }

    /**
     * Returns a potential producer with a base object and attribute type.  The
     * description string is automatically generated.
     *
     * @param baseObject the base object
     * @param type the attribute type
     * @return the potential producer
     */
    public PotentialProducer createPotentialProducer(final Object baseObject, final AttributeType type) {
        return createPotentialProducer(baseObject, type, type.getBaseDescription());
    }

    /**
     * Returns a potential producer with a base object and attribute type. A
     * custom description is provided
     *
     * @param baseObject the base object
     * @param type the attribute type
     * @param description the custom description
     * @return the potential producer
     */
    public PotentialProducer createPotentialProducer(final Object baseObject, final AttributeType type,  final String description) {
        String methodName = type.getMethodBaseName();
        Class<?> dataType = type.getDataType();
        return createPotentialProducer(baseObject, methodName, dataType,
                description);
    }

    /**
     * Create a potential consumer.  All information is provided.
     *
     * @param parentObject parent object
     * @param methodBaseName name of method
     * @param dataType type of data
     * @param description custom description
     * @return the resulting consumer
     */
    public PotentialConsumer createPotentialConsumer(
            final Object parentObject,
            final String methodBaseName,
            final Class<?> dataType,
            final String description) {
        return new PotentialConsumer(parentComponent, parentObject,  methodBaseName,
                 dataType, description);
    }

    /**
     * Create a potential consumer. The description is automatically generated.
     *
     * @param baseObject parent object
     * @param methodBaseName name of method
     * @param dataType type of data
     * @return the resulting consumer
     */
    public PotentialConsumer createPotentialConsumer(final Object baseObject, final String methodBaseName, final Class<?> dataType) {
        return createPotentialConsumer(baseObject, methodBaseName, dataType,
                getDescriptionString(baseObject, methodBaseName, dataType));
    }

    /**
     * Returns a potential consumer with a base object and attribute type.  The
     * description string is automatically generated.
     *
     * @param baseObject the base object
     * @param type the attribute type
     * @return the potential consumer
     */
    public PotentialConsumer createPotentialConsumer(final Object baseObject, final AttributeType type) {
        return createPotentialConsumer(baseObject, type, type.getBaseDescription());
    }

    /**
     * Returns a potential consumer with a base object and attribute type. A
     * custom description is provided
     *
     * @param baseObject the base object
     * @param type the attribute type
     * @param description the custom description
     * @return the potential consumer
     */
    public PotentialConsumer createPotentialConsumer(final Object baseObject, final AttributeType type,  final String description) {
        String methodName = type.getMethodBaseName();
        Class<?> dataType = type.getDataType();
        return createPotentialConsumer(baseObject, methodName, dataType,
                description);
    }


    /**
     * Returns a formatted description string.  E.g "Neuron:activatoin<double>".
     *
     * @param baseObject base object
     * @param methodBaseName base name of method
     * @param dataType class of data
     * @return formatted string
     */
    private String getDescriptionString(Object baseObject, String methodBaseName, Class<?> dataType) {
        return baseObject.getClass().getSimpleName() + ":" + methodBaseName
                + "<" + dataType.getSimpleName() + ">";
    }
}