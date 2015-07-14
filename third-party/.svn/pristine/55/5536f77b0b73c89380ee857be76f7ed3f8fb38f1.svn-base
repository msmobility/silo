/*
 * Copyright 1999,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.xmlrpc.secure;


public interface SecurityConstants
{
    /**
     * Default security provider class. If you are using
     * something like Cryptix then you would override
     * default with setSecurityProviderClass().
     */
    public final static String DEFAULT_SECURITY_PROVIDER_CLASS = 
        "com.sun.net.ssl.internal.ssl.Provider";

    public final static String SECURITY_PROVIDER_CLASS =
        "security.provider";

    /**
     * Default security protocol. You probably don't need to
     * override this default.
     */
    public final static String DEFAULT_SECURITY_PROTOCOL = "TLS";
    
    public final static String SECURITY_PROTOCOL = "security.protocol";

    /**
     * Default key store location. This is just for testing, you
     * will want to override this default in a production environment.
     */
    public final static String DEFAULT_KEY_STORE = "testkeys";
    
    public final static String KEY_STORE = "javax.net.ssl.keyStore";

    /**
     * Default key store format. You probably don't need to
     * override this default.
     */
    public final static String DEFAULT_KEY_STORE_TYPE = "JKS";

    public final static String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";

    /**
     * Default key store password. This default is only
     * used for testing because the sample key store provided
     * with the Sun JSSE uses this password. Do <strong>not</strong>
     * use this password in a production server.
     */
    public final static String DEFAULT_KEY_STORE_PASSWORD = "password";
    
    public final static String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";

    /**
     * Default key store format. You probably don't need to
     * override this default.
     */
    public final static String DEFAULT_TRUST_STORE_TYPE = "JKS";

    public final static String TRUST_STORE_TYPE =
        "javax.net.ssl.trustStoreType";

    /**
     * Default key store location. This is just for testing, you
     * will want to override this default in a production environment.
     */
    public final static String DEFAULT_TRUST_STORE = "truststore";
    
    public final static String TRUST_STORE = "javax.net.ssl.trustStore";

    /**
     * Default key store password. This default is only
     * used for testing because the sample key store provided
     * with the Sun JSSE uses this password. Do <strong>not</strong>
     * use this password in a production server.
     */
    public final static String DEFAULT_TRUST_STORE_PASSWORD = "password";
    
    public final static String TRUST_STORE_PASSWORD =
        "javax.net.ssl.trustStorePassword";

    /**
     * Default key manager type. You probably don't need to
     * override this default.
     */
    public final static String DEFAULT_KEY_MANAGER_TYPE = "SunX509";

    public final static String KEY_MANAGER_TYPE = 
        "sun.ssl.keymanager.type";

    public final static String TRUST_MANAGER_TYPE =
        "sun.ssl.trustmanager.type";

    /**
     * Default protocol handler packages. Change this if you
     * are using something other than the Sun JSSE.
     */
    public final static String DEFAULT_PROTOCOL_HANDLER_PACKAGES = 
        "com.sun.net.ssl.internal.www.protocol";

    public final static String PROTOCOL_HANDLER_PACKAGES =
        "java.protocol.handler.pkgs";
}
