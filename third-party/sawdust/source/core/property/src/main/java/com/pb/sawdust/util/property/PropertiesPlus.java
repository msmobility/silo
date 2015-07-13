package com.pb.sawdust.util.property;

import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

/**
 * The {@code PropertiesPlus} class provides a simple augmentation of the standard {@code Properties} class. It provides
 * methods for getting properties as certain types, as well as throwing an exception when a property does not exist
 * (as opposed to returning {@code null}).
 * <p>
 * Usually it is preferable to use {@link com.pb.sawdust.util.property.PropertyDeluxe} instead of this class, but if
 * the circumstances find that class to be problematic or overly complex, then this one can provide additional functionality
 * with little effort. 
 *
 * @author crf <br/>
 *         Started Sep 21, 2010 3:16:30 PM
 */
public class PropertiesPlus extends Properties {
    private static final long serialVersionUID = -731034912617358117L;

    /**
     * Constructor.
     */
    public PropertiesPlus() {
        super();
    }

    /**
     * Constructor specifying resources to initialize this class with.
     * 
     * @param resource
     *        The name first resource.
     * 
     * @param additionalResources
     *        The names of the additional resources.
     * 
     * @throws RuntimeIOException if any of the resources is not found, or if an i/o error occurs while loading them.
     */
    public PropertiesPlus(String resource, String ... additionalResources) {
        super();
        loadFromResource(resource);
        for (String rc : additionalResources)
            loadFromResource(rc);
    }

    /**
     * Load properties from a specified resource.
     * 
     * @param resourceName
     *        The name of the resource.      
     * 
     * @throws RuntimeIOException if the resource is not found, or if an i/o error occurs while loading it.
     */
    public void loadFromResource(String resourceName) {
        try {
            load(new FileReader(getFileFromResource(resourceName)));
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Load properties from a string.
     * 
     * @param properties
     *        The properties.
     */
    public void loadFromString(String properties) {
        try {
            load(new StringReader(properties));
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private File getFileFromResource(String resource) {
        URL url = ClassLoader.getSystemResource(resource);
        return new File( url == null ? resource : url.getFile());
    }

    /**
     * {@inheritDoc}
     * 
     * This method will throw an exception as opposed to returning {@code null} if the property is not found.
     * 
     * @throws IllegalArgumentException if {@code propery} is not contained in this instance.
     */
    public String getProperty(String property) {
        if (containsKey(property))
            return super.getProperty(property);
        throw new IllegalArgumentException("Property not found: " + property);
    }

    public String getProperty(String key, String defaultValue) {
        return contains(key) ? super.getProperty(key) : defaultValue;
    }

    /**
     * Get a property value as an {@code int}.
     * 
     * @param property
     *        The property key.
     * 
     * @return the value for {@code property} as an {@code int}.
     * 
     * @throws IllegalArgumentException if {@code property} is not found, or if the value cannot be parsed to an {@code int}.
     */
    public int getInt(String property) {
        return Integer.parseInt(getProperty(property));
    }

    /**
     * Get a property value as an {@code long}.
     * 
     * @param property
     *        The property key.
     * 
     * @return the value for {@code property} as an {@code long}.
     * 
     * @throws IllegalArgumentException if {@code property} is not found, or if the value cannot be parsed to an {@code long}.
     */
    public long getLong(String property) {
        return Long.parseLong(getProperty(property));
    }

    /**
     * Get a property value as a {@code float}.
     * 
     * @param property
     *        The property key.
     * 
     * @return the value for {@code property} as a {@code float}.
     * 
     * @throws IllegalArgumentException if {@code property} is not found, or if the value cannot be parsed to a {@code float}.
     */
    public float getFloat(String property) {
        return Float.parseFloat(getProperty(property));
    }

    /**
     * Get a property value as a {@code double}.
     * 
     * @param property
     *        The property key.
     * 
     * @return the value for {@code property} as a {@code double}.
     * 
     * @throws IllegalArgumentException if {@code property} is not found, or if the value cannot be parsed to a {@code double}.
     */
    public double getDouble(String property) {
        return Double.parseDouble(getProperty(property));
    }

    /**
     * Get a property value as a {@code boolean}.
     *
     * @param property
     *        The property key.
     *
     * @return the value for {@code property} as a {@code boolean}.
     *
     * @throws IllegalArgumentException if {@code property} is not found.
     */
    public boolean getBoolean(String property) {
        return Boolean.parseBoolean(property);
    }

}
