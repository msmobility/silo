package com.pb.sawdust.util.property;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.io.IterableFileReader;
import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code PropertyDeluxe} class provides a means to hold and retrieve properties in a more flexible manner than
 * {@code java.util.Properties}.  It provides the ability to load (and access) standard properties files, while
 * augmenting them with namespaces, typing, and tokenizing.
 * <p>
 * The namespace functionality allows properties to be nested within declared namespaces.  Within a properties file, the
 * namespaces are declared to start by including a line "+namespace_name", where "namespace_name" is the name of the
 * namespace.  A namespace ends by including a line "-namespace_name".  If a namespace is never declared to end in a
 * properties file, it is assumed to be valid from its start point through the end of the file. Namespace declarations do
 * not need to be properly nested. Namespaces are agnostic about namespace ordering, and inherit the properties from the
 * namespaces they contain. Property values set "further" within an namespace override the same property values set
 * previously in the namespace hierarchy. In the following example,
 * <pre><code>
 *     +a
 *     p1 = 1
 *     +b
 *     p2 = 2
 *     -a
 *     p2 = 3
 * </code></pre>
 * property <code>p1</code> is in namespace <code>(a)</code>, and property <code>p2</code> is in namespace <code>(a,b)</code>.
 * The following table summarizes the property values for various namespace combinations (<code>N/A</code> indicates the
 * property is not present in the namespace):
 * <p>
 * <table border="1" cellpadding="3" style="border-collapse: collapse;">
 *     <tr>
 *         <th>Namespace</td>
 *         <th><code>p1</code></td>
 *         <th><code>p2</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>(a)</code></td>
 *         <td><code>1</code></td>
 *         <td><code>-</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>(b)</code></td>
 *         <td><code>-</code></td>
 *         <td><code>3</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>(a,b)</code></td>
 *         <td><code>-</code></td>
 *         <td><code>2</code></td>
 *     </tr>
 * </table>
 * <p>
 * Although properties in {@code PropertyDeluxe} instances may be added/changed, the namespaces are fixed by the property
 * resources read at construction time.  Thus, if namespaces are required which do not have properties from the resources
 * used at construction-time, they must be at least declared in the resources.
 * <p>
 * The typing system provides the ability to specify the type of the value for a given property key.  Any key with the
 * form:
 * <pre><code>
 *     key(type) = value
 * </code></pre>
 * is assumed to be a typed key-value pair, where <code>type</code> specifies the type. The allowed type codes are:
 * <p>
 * <table border="1" cellpadding="3" style="border-collapse: collapse;">
 *     <tr>
 *         <th>Type Code</td>
 *         <th>Type</td>
 *     </tr>
 *     <tr>
 *         <td><code>I</code></td>
 *         <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>D</code></td>
 *         <td><code>double</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>B</code></td>
 *         <td><code>boolean</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>S</code></td>
 *         <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>L</code></td>
 *         <td><code>List</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>M</code></td>
 *         <td><code>Map</code></td>
 *     </tr>
 * </table>
 * <p>
 * The list and map type codes can be augmented with subtypes specifying their element types or key-value types, respectively.
 * (Note: to avoid ambiguity, maps cannot use lists or maps as their key types.) For example, the type <code>LI</code>
 * indicates a type of {@code List<Integer>}, and the type <code>MBLLS</code> indicates a type of
 * {@code Map<Boolean,List<List<String>>>}.
 * <p>
 * For the primitive types, the object wrapper's {@code parse} method is used to type-check and create the value (<i>e.g.</i>
 * {@code Integer.parseInt(String)} for {@code int}). A string type will always pass type-checking, since properties are
 * inherently strings.  For list types, the syntax for values is a bracketed, comma-separated list:
 * <pre><code>
 *     [element1,element2,...]
 * </code></pre>
 * For map types, the syntax for values is a braced, comma-separated list of key-value pairs separated by a colon:
 * <pre><code>
 *     {key1:value1,key2:value2,...}
 * </code></pre>
 * For both lists and maps, whitespace within the elements/keys/values is retained in the resulting value elements (that
 * is, whitespace around the commas is not stripped out). There is currently no way to escape commas for lists or maps.
 * <p>
 * In addition to the type checking, this class provides methods to retrieve the property values as one of the above
 * specified types. These methods do not require that the type be specified in the key, though this is recommended practice
 * for fail-fast behavior (if the type is declared in the property file, and incorrectly specfied value will cause an
 * excpetion to be thrown at construction time, as opposed to when the "<code>getType</code>" method is called).
 * <p>
 * The tokenizing system allows token strings and replacement strings to specified in the properties, which are then used
 * to detokenize the values in the properties.  All tokens are of the form <code>[token_specifier]token</code>, where
 * the token specifier is set at contruction (the default value is <code>@@</code>).  For example, the following (default)
 * token declaration in a property file:
 * <pre><code>
 *     &#064;&#064;my_token = hello
 * </code></pre>
 * when applied to the following property:
 * <pre><code>
 *     my.property = I said my_token
 * </code></pre>
 * will result in the property <code>my.property</code> having the value "<code>I said hello</code>".  Tokens follow
 * the same namespace nesting and overriding rules as properties; they will only apply within their namespaces and will
 * override any previously declared tokens.
 * <p>
 * A token is also available (within its namespace) as a regular property, where access to it is through its token name
 * (sans the token specifier). It is also noted that tokens may be nested, but that the detokenizing process is currently
 * not set up to definitely replace all nested tokens.
 *
 * @author crf <br/>
 *         Started Sep 1, 2010 7:51:50 PM
 */
public class PropertyDeluxe {
    /**
     * Default token key specifier.
     */
    public static final String DEFAULT_TOKEN_SPECIFIER = "@@";
    private static final Pattern TYPE_PATTERN = Pattern.compile("(\\S+)\\((\\S+)\\)");

    private final Map<PropertyType,Object> propertyMap;
    private final Set<Set<String>> namespacesSet;
    private final String tokenSpecifier;

    private PropertyDeluxe(Map<PropertyType,Object> propertyMap, Set<Set<String>> baseNamespacesSet, List<String> inNamespaces, String tokenSpecifier) {
        this.propertyMap = propertyMap;
        namespacesSet = new HashSet<Set<String>>();
        //pull out unconnected namespaces and trim off namespace ancestry
        for (Set<String> baseNamespace : baseNamespacesSet) {
            if (baseNamespace.containsAll(inNamespaces)) {
                Set<String> namespace = new HashSet<String>(baseNamespace);
                namespace.removeAll(inNamespaces);
                namespacesSet.add(namespace);
            }
        }
        this.tokenSpecifier = tokenSpecifier;
    }

    /**
     * Constructor specifying the token key specifier, as well as the resources to load into this property file.
     *
     * @param setTokenSpecifier
     *        Dummy (ignored) parameter indicating this constructor should be used.
     *
     * @param tokenSpecifier
     *        The specifier for token keys.
     *
     * @param firstResource
     *        The first resource to load.
     *
     * @param additionalResources
     *        Any additional resources to load.
     */
    //todo: end open namespaces when done reading one file - I think it already does this
    @SuppressWarnings("unchecked") //ok - these generics are internally consistent
    public PropertyDeluxe(boolean setTokenSpecifier, String tokenSpecifier, String firstResource, String ... additionalResources) {
        this.tokenSpecifier = tokenSpecifier;
        Map<Set<String>,StringBuilder> namespaces = pullNamespaces(firstResource);
        for (String resource : additionalResources)
            joinNamespacesTo(namespaces,pullNamespaces(resource));
        //switch to properties
        Map<Set<String>,Properties> namespaceProperties = new HashMap<Set<String>,Properties>();
        //and order tokens by namespace size
        Map<Set<String>,Map<String,String>> tokens = new TreeMap<Set<String>,Map<String, String>>(new Comparator<Set<String>>() {
            public int compare(Set<String> o1, Set<String> o2) {
                return o1.size() - o2.size();
            }
        });
        for (Set<String> key : namespaces.keySet()) {
            Properties p = getPropertiesFromStringBuilder(namespaces.get(key));
            tokens.put(key,pullTokens(p));
            namespaceProperties.put(key,p);
        }

        //detokenize into map and type check
        Map<Set<String>,Map<String,Object>> detokenizedProperties = null;
        int maxDetokenizeLoops = 10;
        for (int i = 0; i < maxDetokenizeLoops; i++) { //do it 5 times
            detokenizedProperties = new HashMap<Set<String>,Map<String,Object>>();
            for (Set<String> key : namespaceProperties.keySet()) {
                Map<String,String> matchingTokens = new HashMap<String,String>();
                for (Set<String> tokenKey : tokens.keySet())
                    if (isContainedIn(key,tokenKey))
                        matchingTokens.putAll(tokens.get(tokenKey));
                if (i == maxDetokenizeLoops-1)
                    detokenizedProperties.put(key,typeCheckProperties(detokenizeProperties(namespaceProperties.get(key),matchingTokens)));
                 else
                    detokenizedProperties.put(key,dontTypeCheckProperties(detokenizeProperties(namespaceProperties.get(key),matchingTokens)));

                Properties p = new Properties();
                Map<Object,Object> oldProps = namespaceProperties.get(key);
                p.putAll(detokenizedProperties.get(key));
                //need to add in all non-string valued properties
                for (Object k : oldProps.keySet())
                    if (!p.containsKey(k))
                        p.put(k,oldProps.get(k));
                namespaceProperties.put(key,p);
            }
        }

        //create final mapping
        propertyMap = new EnumMap<PropertyType,Object>(PropertyType.class);
        //generics don't mean anything here, just documentation
        propertyMap.put(PropertyType.NAMESPACE,new HashMap<String,PropertyDeluxe>());
        propertyMap.put(PropertyType.PROPERTIES,new HashMap<String,Object>());
        propertyMap.put(PropertyType.PREVIOUS,null);

        namespacesSet = detokenizedProperties.keySet();
        for (Set<String> key : namespacesSet) {
            for (List<String> lst : getNamespacePermutations(key)) {
                PropertyDeluxe currentProperties = this;
                Map<String,PropertyDeluxe> currentNamespaceMap = (Map<String,PropertyDeluxe>) propertyMap.get(PropertyType.NAMESPACE);
                for (String nm : lst) {
                    if (!currentNamespaceMap.containsKey(nm)) {
                        //new map for sub PropertyDeluxe
                        Map<PropertyType,Object> pdm = new EnumMap<PropertyType,Object>(PropertyType.class);
                        pdm.put(PropertyType.NAMESPACE,new HashMap<String,PropertyDeluxe>());
                        pdm.put(PropertyType.PROPERTIES,new HashMap<String,Object>()); //empty as default, if not used
                        pdm.put(PropertyType.PREVIOUS,currentProperties);
                        currentNamespaceMap.put(nm,new PropertyDeluxe(pdm,namespacesSet,lst,tokenSpecifier));
                    }
                    currentProperties = currentNamespaceMap.get(nm);
                    currentNamespaceMap = (Map<String,PropertyDeluxe>) currentProperties.propertyMap.get(PropertyType.NAMESPACE);
                }
                currentProperties.propertyMap.put(PropertyType.PROPERTIES,detokenizedProperties.get(key));
            }
        }

    }

    /**
     * Constructor specifying the resources to load into this property file.  {@link #DEFAULT_TOKEN_SPECIFIER} will be used
     * as the token key specifier.
     *
     * @param firstResource
     *        The first resource to load.
     *
     * @param additionalResources
     *        Any additional resources to load.
     */
    public PropertyDeluxe(String firstResource, String ... additionalResources) {
        this(true,DEFAULT_TOKEN_SPECIFIER,firstResource,additionalResources);
    }


    @SuppressWarnings("unchecked") //ok - these generics are internally consistent
    private PropertyDeluxe getPropertiesOneStep(String namespace) {
        return ((Map<String,PropertyDeluxe>) propertyMap.get(PropertyType.NAMESPACE)).get(namespace);
    }

    /**
     * Get the {@code PropertyDeluxe} instance for the specified namespace.  The returned instance contain all of the
     * properties for the specified namespace, and will neither need nor allow the use of any of the namespace names in
     * {@code namespaces}.
     *
     * @param namespaces
     *        The namespace.
     *
     * @return the properties for the namespace specified by {@code namespaces}.
     *
     * @throws IllegalArgumentException if the specified namespace is not found.
     */
    public PropertyDeluxe getProperties(String ... namespaces) {
        PropertyDeluxe pdx = this;
        for (String namespace : namespaces)
            if ((pdx = pdx.getPropertiesOneStep(namespace)) == null)
                throw new IllegalArgumentException("Namespace not found: " + Arrays.toString(namespaces));
        return pdx;
    }

    /**
     * Get the property value for a given key and namespace.  The returned value will be a {@code String} unless its
     * type was specified when the property resources were loaded, or it was set to a non-{@code String} value via
     * {@link #setProperty(String, Object, String...)}.
     *
     * @param key
     *        The property key.
     *
     * @param namespaces
     *        The namespace.
     *
     * @return the property for {@code key} in the namespace specified by {@code namespaces}.
     *
     * @throws IllegalArgumentException if the specified namespace or key is not found.
     */
    public Object getProperty(String key, String ... namespaces) {
        return getProperties(namespaces).getPropertyFromNamespaceBranch(key);
    }

    private Object getPropertyFromNamespaceBranch(String key) {
        PropertyDeluxe pdx = this;
        while (pdx != null) {
            @SuppressWarnings("unchecked") //ok - these generics are internally consistent
            Map<String,Object> props = (Map<String,Object>) pdx.propertyMap.get(PropertyType.PROPERTIES);
            if (props.containsKey(key))
                return props.get(key);
            pdx = (PropertyDeluxe) pdx.propertyMap.get(PropertyType.PREVIOUS);
        }
        throw new IllegalArgumentException("Property key not found: " + key);
    }

    /**
     * Get the property value for a given key and namespace as a string. This method returns {@code getProperty(key,namespaces).toString()},
     * which may be different from the value in the property resource, if that value was typed.
     *
     * @param key
     *        The property key.
     *
     * @param namespaces
     *        The namespace.
     *
     * @return the property for {@code key} in the namespace specified by {@code namespaces}.
     *
     * @throws IllegalArgumentException if the specified namespace or key is not found.
     */
    public String getString(String key, String ... namespaces) {
        return getProperty(key,namespaces).toString();
    }

    /**
     * Get the property value for a given key and namespace as a path. There is no guarantee (from this method) that the
     * returned path exists or is viable.
     *
     * @param key
     *        The property key.
     *
     * @param namespaces
     *        The namespace.
     *
     * @return the property for {@code key} in the namespace specified by {@code namespaces}.
     *
     * @throws IllegalArgumentException if the specified namespace or key is not found.
     */
    public Path getPath(String key, String ... namespaces) {
        return Paths.get(getString(key,namespaces));
    }

    /**
     * Get the property value for a given key and namespace as an integer. If the value was not typed at construction time,
     * then the {@code Integer.parseInt(String)} method is used to coerce the value.
     *
     * @param key
     *        The property key.
     *
     * @param namespaces
     *        The namespace.
     *
     * @return the property for {@code key} in the namespace specified by {@code namespaces}.
     *
     * @throws IllegalArgumentException if the specified namespace or key is not found, or if the value cannot be coerced
     *                                  into an integer value.
     */
    public int getInt(String key, String ... namespaces) {
        Object p = getProperty(key,namespaces);
        if (p instanceof Number)
            return ((Number) p).intValue();
        else if (p instanceof String)
            return Integer.parseInt((String) p);
        throw new IllegalArgumentException("Cannot coerce value for key " + key + " (" + Arrays.toString(namespaces) + ") to int: " + p);
    }

    /**
     * Get the property value for a given key and namespace as a double. If the value was not typed at construction time,
     * then the {@code Double.parseDouble(String)} method is used to coerce the value.
     *
     * @param key
     *        The property key.
     *
     * @param namespaces
     *        The namespace.
     *
     * @return the property for {@code key} in the namespace specified by {@code namespaces}.
     *
     * @throws IllegalArgumentException if the specified namespace or key is not found, or if the value cannot be coerced
     *                                  into a double value.
     */
    public double getDouble(String key, String ... namespaces) {
        Object p = getProperty(key,namespaces);
        if (p instanceof Number)
            return ((Number) p).doubleValue();
        else if (p instanceof String)
            return Double.parseDouble((String) p);
        throw new IllegalArgumentException("Cannot coerce value for key " + key + " (" + Arrays.toString(namespaces) + ") to float: " + p);
    }

    /**
     * Get the property value for a given key and namespace as a boolean. If the value was not typed at construction time,
     * then the {@code Boolean.parseBoolean(String)} method is used to coerce the value.
     *
     * @param key
     *        The property key.
     *
     * @param namespaces
     *        The namespace.
     *
     * @return the property for {@code key} in the namespace specified by {@code namespaces}.
     *
     * @throws IllegalArgumentException if the specified namespace or key is not found, or if the value cannot be coerced
     *                                  into an boolean value.
     */
    public boolean getBoolean(String key, String ... namespaces) {
        Object p = getProperty(key,namespaces);
        if (p instanceof Boolean)
            return (Boolean) p;
        else if (p instanceof String)
            return Boolean.parseBoolean((String) p);
        throw new IllegalArgumentException("Cannot coerce value for key " + key + " (" + Arrays.toString(namespaces) + ") to boolean: " + p);
    }

    /**
     * Get the property value for a given key and namespace as a list. If the value was not typed at construction time,
     * then the value is coerced into a list using the syntax specified in this class' description (sub-typing in this
     * case is not performed).
     *
     * @param key
     *        The property key.
     *
     * @param namespaces
     *        The namespace.
     *
     * @param <E>
     *        The type of the elements in the returned list.
     *
     * @return the property for {@code key} in the namespace specified by {@code namespaces}.
     *
     * @throws IllegalArgumentException if the specified namespace or key is not found, or if the value cannot be coerced
     *                                  into an list.
     */
    @SuppressWarnings("unchecked") //cannot ensure List will hold Es, but if not it is a user error, so suppressing
    public <E> List<E> getList(String key, String ... namespaces) {
        Object p = getProperty(key,namespaces);
        if (p instanceof List)
            return (List<E>) p;
        else if (p instanceof String)
            try {
                return (List<E>) parseList((String) p,"");
            } catch (IllegalArgumentException ignored) {
                //cannot be parsed to list
            }
        throw new IllegalArgumentException("Value for key " + key + " (" + Arrays.toString(namespaces) + ") not a list: " + p);
    }

    /**
     * Get the property value for a given key and namespace as a map. If the value was not typed at construction time,
     * then the value is coerced into a map using the syntax specified in this class' description (sub-typing in this
     * case is not performed).
     *
     * @param key
     *        The property key.
     *
     * @param namespaces
     *        The namespace.
     *
     * @param <K>
     *        The type of the keys for the returned map.
     *
     * @param <V>
     *        The type of the values for the returned map.
     *
     * @return the property for {@code key} in the namespace specified by {@code namespaces}.
     *
     * @throws IllegalArgumentException if the specified namespace or key is not found, or if the value cannot be coerced
     *                                  into an map.
     */
    @SuppressWarnings("unchecked") //cannot ensure Map will hold K/Vs, but if not it is a user error, so suppressing
    public <K,V> Map<K,V> getMap(String key, String ... namespaces) {
        Object p = getProperty(key,namespaces);
        if (p instanceof Map)
            return (Map<K,V>) p;
        else if (p instanceof String)
            try {
                return (Map<K,V>) parseMap((String) p,"");
            } catch (IllegalArgumentException ignored) {
                //cannot be parsed to map
            }
        throw new IllegalArgumentException("Value for key " + key + " (" + Arrays.toString(namespaces) + ") not a map: " + p);
    }

    /**
     * Determine whether a specified property key exists in a namespace.
     *
     * @param key
     *        The property key.
     *
     * @param namespaces
     *        The namespace.
     *
     * @return {@code true} if the key exists in the namespace, {@code false} if not.
     */
    public boolean hasKey(String key, String ... namespaces) {
        PropertyDeluxe pdx = getProperties(namespaces);
        while (pdx != null) {
            @SuppressWarnings("unchecked") //ok - these generics are internally consistent
            Map<String,Object> props = (Map<String,Object>) pdx.propertyMap.get(PropertyType.PROPERTIES);
            if (props.containsKey(key))
                return true;
            pdx = (PropertyDeluxe) pdx.propertyMap.get(PropertyType.PREVIOUS);
        }
        return false;
    }

    /**
     * Determine whether a specified namespace exists in this instance.
     *
     * @param namespaces
     *        The namespace.
     *
     * @return {@code true} if the namespace exists, {@code false} if not.
     */
    public boolean hasNamespace(String ... namespaces) {
        PropertyDeluxe pdx = this;
        for (String namespace : namespaces)
            if ((pdx = pdx.getPropertiesOneStep(namespace)) == null)
                return false;
        return true;
    }

    /**
     * Get an {@code Iterable} which will iterate over all keys in this instance.
     *
     * @return an object which can be used to iterate over all of the keys in this {@code PropertyDeluxe}.
     */
    @SuppressWarnings("unchecked") //these generics are internally consistent
    public Iterable<String> getKeys() {
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    private Iterator<String> currentIterator = ((Map<String,Object>) propertyMap.get(PropertyType.PROPERTIES)).keySet().iterator();
                    private PropertyDeluxe pdx = PropertyDeluxe.this;

                    @Override
                    public boolean hasNext() {
                        if (currentIterator == null)
                            return false;
                        if (currentIterator.hasNext())
                            return true;
                        pdx = (PropertyDeluxe) propertyMap.get(PropertyType.PREVIOUS);
                        currentIterator = pdx == null ? null : ((Map<String,Object>) propertyMap.get(PropertyType.PROPERTIES)).keySet().iterator();
                        return hasNext();
                    }

                    @Override
                    public String next() {
                        if (hasNext()) //increment iterator if not already done so
                            return currentIterator.next();
                        throw new NoSuchElementException();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Get a set of all namespaces contained in this instance. The individual namespaces are specified as a {@code Set}
     * of namespace names.
     *
     * @return all of the namespaces contained in this {@code PropertyDeluxe} instance.
     */
    public Set<Set<String>> getNamespaces() {
        return namespacesSet;
    }

    /**
     * Set a property value. Previously defined properties may be replaced, and any type declarations that were made
     * when the property resources were loaded are not enforced.
     *
     * @param key
     *        The property key.
     *
     * @param value
     *        The property value.
     *
     * @param namespaces
     *        The namespace.
     *
     * @throws IllegalArgumentException if the specified namespace does not exist.
     */
    @SuppressWarnings("unchecked") //these generics are internally consistent
    public void setProperty(String key, Object value, String ... namespaces) {
        PropertyDeluxe pdx = getProperties(namespaces);
        ((Map<String,Object>) pdx.propertyMap.get(PropertyType.PROPERTIES)).put(key,value);
    }

    private String buildType(Object o) {
        Class<?> oClass = o.getClass();
        StringBuilder sb = new StringBuilder();
        if (oClass == String.class)
            sb.append('S');
        else if (oClass == Boolean.class)
            sb.append('B');
        else if (oClass == Integer.class)
            sb.append('I');
        else if (oClass == Long.class)
            sb.append('N');
        else if (oClass == Float.class)
            sb.append('F');
        else if (oClass == Double.class)
            sb.append('D');
        else if (List.class.isAssignableFrom(oClass))
            sb.append('L').append(((List) o).size() > 0 ? buildType(((List) o).iterator().next()) : 'S'); //string list if it is empty...
        else if (Map.class.isAssignableFrom(oClass))
            sb.append('M').append(((Map) o).size() > 0 ? buildType(((Map) o).keySet().iterator().next()) : 'S')
                          .append(((Map) o).size() > 0 ? buildType(((Map) o).values().iterator().next()) : 'S'); //string to string if it is empty
        return sb.toString();
    }

    private String buildObject(Object o) {
        Class<?> oClass = o.getClass();
        if (Map.class.isAssignableFrom(oClass))
            return buildMapProperty((Map<?,?>) o);
        else if (List.class.isAssignableFrom(oClass))
            return buildListProperty((List<?>) o);
        else
            return o.toString();
    }

    private String buildMapProperty(Map<?,?> map) {
        boolean first = true;
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (first)
                first = false;
            else
                sb.append(" , ");
            sb.append(buildObject(entry.getKey()));
            sb.append(" : ");
            sb.append(buildObject(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    private String buildListProperty(List<?> list) {
        boolean first = true;
        StringBuilder sb = new StringBuilder("[");
        for (Object o : list) {
            if (first)
                first = false;
            else
                sb.append(" , ");
            sb.append(buildObject(o));
        }
        sb.append("]");
        return sb.toString();
    }

    public void writeProperties(Path outputPath) {
        Set<Set<String>> namespaces = getNamespaces();
        try (PrintWriter writer = new PrintWriter(outputPath.toFile())) {
            for (Set<String> namespace : namespaces) {
                PropertyDeluxe properties = getProperties(namespace.toArray(new String[namespace.size()]));
                //write namespace beginnings
                for (String name : namespace)
                    writer.println("+" + name);
                for (String key : properties.getKeys()) {
                    Object o = properties.getProperty(key);
                    String type = buildType(o);
                    if (type.equals("S"))
                        type = "";
                    if (type.length() > 0)
                        type = "(" + type + ")";
                    writer.println(key + type + " = " + buildObject(o));
                }
                //write namespace ends
                for (String name : namespace)
                    writer.println("-" + name);
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }

    }

    private List<List<String>> getNamespacePermutations(Set<String> namespace) {
        return getNamespacePermutation(new LinkedList<String>(namespace),new LinkedList<String>());
    }

    private List<List<String>> getNamespacePermutation(List<String> namespaceLeft, List<String> currentPermutation) {
        List<List<String>> list = new LinkedList<List<String>>();
        if (namespaceLeft.size() == 0) {
            list.add(currentPermutation);
        } else {
            for (String p : namespaceLeft) {
                List<String> ls = new LinkedList<String>(namespaceLeft);
                List<String> cp = new LinkedList<String>(currentPermutation);
                ls.remove(p);
                cp.add(p);
                for (List<String> lst : getNamespacePermutation(ls,cp)) {
                    List<String> cpp = new LinkedList<String>();
                    for (String s : lst)
                        cpp.add(s);
                    list.add(cpp);
                }
            }
        }
        return list;
    }

    private File getFileFromResource(String resource) {
        URL url = ClassLoader.getSystemResource(resource);
        if (url == null)
            url = ClassLoader.getSystemResource(resource + ".properties");
        return new File(url == null ? resource : url.getFile());
    }

    private Iterable<String> getIterableResource(String resource) {
        if (!resource.contains("\n") && !resource.contains("\r")) { //might be a file...
            File f = getFileFromResource(resource);
            if (f.exists() || !resource.contains("="))  //last one (=) might be possible, but unlikely, so make an educated guess in this case
                return IterableFileReader.getLineIterableFile(f);
        }
        return Arrays.asList(LINE_TERMINATOR_REGEX.split(resource));
    }

    private static final Pattern LINE_TERMINATOR_REGEX = Pattern.compile("(?m)\\n\\r|\\n|\\r");
    private Map<Set<String>,StringBuilder> pullNamespaces(String resource) {
        Map<Set<String>,StringBuilder> namespaceMap = new HashMap<Set<String>,StringBuilder>();
        StringBuilder currentBuilder = new StringBuilder();
        Set<String> currentNamespace = new HashSet<String>();
        namespaceMap.put(currentNamespace,currentBuilder);
        boolean consumeNext = false;
        Iterable<String> it = getIterableResource(resource);
        for (String line : it) {
            String subline = line.trim();
            if (subline.startsWith("+") && !consumeNext) {
                String namespace = subline.substring(1); //take off '+'
                Set<String> namespaceKey = new HashSet<String>(currentNamespace);
                namespaceKey.add(namespace);
                currentNamespace = namespaceKey;
                if (!namespaceMap.containsKey(namespaceKey))
                    namespaceMap.put(namespaceKey,new StringBuilder());
                currentBuilder = namespaceMap.get(namespaceKey);
                continue;
            }
            if (subline.startsWith("-") && !consumeNext) {
                String namespace = subline.substring(1); //take off '-'
                Set<String> namespaceKey = new HashSet<String>(currentNamespace);
                namespaceKey.remove(namespace);
                currentNamespace = namespaceKey;
                if (!namespaceMap.containsKey(namespaceKey))
                    namespaceMap.put(namespaceKey,new StringBuilder());
                currentBuilder = namespaceMap.get(namespaceKey);
                continue;
            }
            currentBuilder.append(line + FileUtil.getLineSeparator());
            consumeNext = subline.endsWith("\\");
        }
        return namespaceMap;
    }

//    private Map<Set<String>,StringBuilder> pullNamespaces(File propertyFile) {
//        Map<Set<String>,StringBuilder> namespaceMap = new HashMap<Set<String>,StringBuilder>();
//        StringBuilder currentBuilder = new StringBuilder();
//        Set<String> currentNamespace = new HashSet<String>();
//        namespaceMap.put(currentNamespace,currentBuilder);
//        boolean consumeNext = false;
//        for (String line : IterableFileReader.getLineIterableFileWithLineTerminator(propertyFile)) {
//            String subline = line.trim();
//            if (subline.startsWith("+") && !consumeNext) {
//                String namespace = subline.substring(1); //take off '+'
//                Set<String> namespaceKey = new HashSet<String>(currentNamespace);
//                namespaceKey.add(namespace);
//                currentNamespace = namespaceKey;
//                if (!namespaceMap.containsKey(namespaceKey))
//                    namespaceMap.put(namespaceKey,new StringBuilder());
//                currentBuilder = namespaceMap.get(namespaceKey);
//                continue;
//            }
//            if (subline.startsWith("-") && !consumeNext) {
//                String namespace = subline.substring(1); //take off '-'
//                Set<String> namespaceKey = new HashSet<String>(currentNamespace);
//                namespaceKey.remove(namespace);
//                currentNamespace = namespaceKey;
//                if (!namespaceMap.containsKey(namespaceKey))
//                    namespaceMap.put(namespaceKey,new StringBuilder());
//                currentBuilder = namespaceMap.get(namespaceKey);
//                continue;
//            }
//            currentBuilder.append(line);
//            consumeNext = subline.endsWith("\\");
//        }
//        return namespaceMap;
//    }

    private void joinNamespacesTo(Map<Set<String>,StringBuilder> main, Map<Set<String>,StringBuilder> additions) {
        for (Set<String> key : additions.keySet())
            if (main.containsKey(key))
                main.put(key,main.get(key).append("\n").append(additions.get(key)));
            else
                main.put(key,additions.get(key));
    }

    private Properties getPropertiesFromStringBuilder(StringBuilder sb) {
        Properties p = new Properties();
        try {
            p.load(new StringReader(sb.toString()));
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        return p;
    }

    private Map<String,String> pullTokens(Properties p) {
        Map<String,String> tokens = new HashMap<String,String>();
        for (String key : p.stringPropertyNames())
            if (key.startsWith(tokenSpecifier))
                tokens.put(key.substring(tokenSpecifier.length()),p.getProperty(key));
        for (String token : tokens.keySet())
            p.put(token,p.remove(tokenSpecifier + token));
        return tokens;
    }

    private boolean isContainedIn(Set<String> namespace, Set<String> isInThisNamespace) {
        for (String n : isInThisNamespace)
            if (!namespace.contains(n))
                return false;
        return true;
    }

    private Map<String,String> detokenizeProperties(Properties p, Map<String,String> tokens) {
        Map<String,String> properties = new HashMap<String,String>();
        for (String key : p.stringPropertyNames()) {
            String value = p.getProperty(key);
            for (String token : tokens.keySet())
                value = value.replace(token,tokens.get(token));
//            for (String token : tokens.keySet()) {
//                if (value.indexOf(tokenSpecifier) > -1)
//                    value = value.replace(token,tokens.get(token));
//                else
//                    break; //no tokens left to detokenize
//            }
            properties.put(key,value);
        }
        return properties;
    }

    private Map<String,Object> typeCheckProperties(Map<String,String> p) {
        Map<String,Object> props = new HashMap<String,Object>();
        for (String key : p.keySet()) {
            Matcher m = TYPE_PATTERN.matcher(key);
            if (m.matches())
                props.put(m.group(1),typeCheck(p.get(key),m.group(2).toUpperCase()));
            else
                props.put(key,p.get(key));
        }
        return props;
    }

    private Map<String,Object> dontTypeCheckProperties(Map<String,String> p) {
        Map<String,Object> props = new HashMap<String,Object>();
        for (String key : p.keySet())
            props.put(key,p.get(key));
        return props;
    }

    private static enum PropertyType {
        PROPERTIES,
        NAMESPACE,
        PREVIOUS
    }
    
    private static enum ValueType {
        INT('I'),LONG('N'),FLOAT('F'),DOUBLE('D'),BOOLEAN('B'),STRING('S'),LIST('L'),MAP('M');

        private final char typeChar;

        private ValueType(char typeChar) {
            this.typeChar = typeChar;
        }

        private static ValueType getValueType(char identifier) {
            for (ValueType type : ValueType.values())
                if (type.typeChar == identifier)
                    return type;
            throw new IllegalArgumentException("Unknown value type: " + identifier);
        }
        
        private char getCharId() {
           return typeChar;
        }

        private boolean isCompositeType() {
            return this == MAP || this == LIST;
        }
    }
        
    private Object typeCheck(String value, String subType) {
        ValueType type = ValueType.getValueType(subType.charAt(0)); 
        switch (type) {
            case LIST :
            case MAP : break;
            default : 
                if (subType.length() > 1)
                    throw new IllegalArgumentException("Invalid property type: " + subType);
        }
        
        switch (type) {
            case INT : return parseInteger(value);
            case LONG : return parseLong(value);
            case FLOAT : return parseFloat(value);
            case DOUBLE: return parseDouble(value);
            case BOOLEAN : return parseBoolean(value);
            case STRING : return value;
            case LIST : return parseList(value,subType.substring(1));
            case MAP : return parseMap(value,subType.substring(1));
            default : throw new IllegalStateException("Shouldn't be here.");
        }
    }

    private int parseInteger(String value) {
        return Integer.parseInt(value);
    }

    private long parseLong(String value) {
        return Long.parseLong(value);
    }

    private float parseFloat(String value) {
        return Float.parseFloat(value);
    }

    private double parseDouble(String value) {
        return Double.parseDouble(value);
    }

    private boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    private String getSplitRegex(String subType) {
        if (subType.length() == 0)
            return ",";
        ValueType type = ValueType.getValueType(subType.charAt(0));
        if (type == ValueType.LIST)
            return "\\]\\s+,\\s+\\[";
        else if (type == ValueType.MAP)
            return "\\}\\s+,\\s+\\{";
        else
            return ",";
    }

    private List<String> splitList(String value) {
        List<String> list = new LinkedList<String>();
        int lCounter = 0;
        int mCounter = 0;
        int currentChar = 0;
        int last = 0;
        for (char c : value.toCharArray()) {
            if (c == ',' && lCounter == 0 && mCounter == 0) {
                list.add(value.substring(last,currentChar).trim());
                last = currentChar+1;
            } else if (c == '[') {
                lCounter++;
            } else if (c == ']') {
                lCounter--;
            } else if (c == '{') {
                mCounter++;
            } else if (c == '}') {
                mCounter--;
            }
            currentChar++;
        }
        if (lCounter != 0 || mCounter != 0)
            throw new IllegalArgumentException("Invalid list: [" + value + "]");
        list.add(value.substring(last,currentChar).trim());
        return list;
    }

    private List<Object> parseList(String value, String subType) {
        String v = value.trim();
        if (!v.startsWith("[") || !v.endsWith("]"))
            throw new IllegalArgumentException("List property value must be surrounded with [ ]: " + value);
        if (subType.length() == 0) 
            subType = String.valueOf(ValueType.STRING.getCharId());
        List<Object> list = new LinkedList<Object>();
        String contents = value.substring(1,value.length()-1);
        ValueType nextType = ValueType.getValueType(subType.charAt(0));
        //list is empty if only whitespace in it; however, if sub type is string, then list empty only if no whitespace
        if ((nextType == ValueType.STRING && contents.length() > 0) || contents.trim().length() != 0)
            for (String s : splitList(contents))
                list.add(typeCheck(s,subType));
        return list;
    }

    private List<String> splitMap(String value) {
        List<String> list = new LinkedList<String>();
        int lCounter = 0;
        int mCounter = 0;
        int currentChar = 0;
        int point = 0; // 0 : 1 , 0
        int last = 0;
        for (char c : value.toCharArray()) {
            if (c == ',' && lCounter == 0 && mCounter == 0) {
                if (point == 0)
                    throw new IllegalArgumentException("Invalid map: {" + value + "}");
                list.add(value.substring(last,currentChar).trim());
                last = currentChar+1;
                point = 0;
            } else if (c == ':' && lCounter == 0 && mCounter == 0) {
                if (point == 0)  { //':' in values will be ignored
    //                if (point == 1)
    //                    throw new IllegalArgumentException("Invalid map: {" + value + "}");
                    list.add(value.substring(last,currentChar).trim());
                    last = currentChar+1;
                    point = 1;
                }
            } else if (c == '[') {
                lCounter++;
            } else if (c == ']') {
                lCounter--;
            } else if (c == '{') {
                mCounter++;
            } else if (c == '}') {
                mCounter--;
            }
            currentChar++;
        }
        if (lCounter != 0 || mCounter != 0 || point == 0)
            throw new IllegalArgumentException("Invalid map: {" + value + "}");
        list.add(value.substring(last,currentChar).trim());
        return list;
    }
    
    private Map<Object,Object> parseMap(String value, String subType) {
        String v = value.trim();
        if (!v.startsWith("{") || !v.endsWith("}"))
            throw new IllegalArgumentException("Map property value must be surrounded with { }: " + value);
        if (subType.length() == 1)
            throw new IllegalArgumentException("Map property type cannot have only one subtype: " + subType);
        if (subType.length() == 0) {
            subType = String.valueOf(ValueType.STRING.getCharId());
            subType += subType;
        }
        char kChar = subType.charAt(0);
        String kString = String.valueOf(kChar);
        String vString = subType.substring(1);
        if (kChar == ValueType.LIST.getCharId() || kChar == ValueType.MAP.getCharId())
            throw new IllegalArgumentException("Map property key type cannot be a list or map: " + subType);
        Map<Object,Object> map = new HashMap<Object,Object>();
        String contents = value.substring(1,value.length()-1);
        if (contents.trim().length() > 0) { //if map is empty, then don't bother parsing
            Iterator<String> mapIt = splitMap(contents).iterator();
            while (mapIt.hasNext()) {
                String key = mapIt.next();
                String val = mapIt.next();
                map.put(typeCheck(key,kString),typeCheck(val,vString));
            }
        }
        return map;
    }

    /**
     * Form a string that can be used as a loadable resource with the {@code PropertyDeluxe} class. This is useful for adding
     * properties as text without having to write them to a file.This method simply joins the resource entries with the
     * {@code '\n'} character and returns the result.
     *
     * @param resourceEntries
     *        The resource entries.
     *
     * @return {@code resourceEntries} formatted as a loadable property resource.
     */
    public static String formLoadableResource(String ... resourceEntries) {
        return formLoadableResource(Arrays.asList(resourceEntries));
    }

    /**
     * Form a string that can be used as a loadable resource with the {@code PropertyDeluxe} class. This is useful for adding
     * properties as text without having to write them to a file.This method simply joins the resource entries with the
     * {@code '\n'} character and returns the result.
     *
     * @param resourceEntries
     *        The resource entries.
     *
     * @return {@code resourceEntries} formatted as a loadable property resource.
     */
    public static String formLoadableResource(List<String> resourceEntries) {
        StringBuilder resource = new StringBuilder();
        for (String s : resourceEntries)
            resource.append(s).append('\n');
        return resource.toString();
    }

}
