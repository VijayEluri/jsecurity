/*
 * Copyright 2005-2008 Les Hazlewood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsecurity.config;

import org.jsecurity.mgt.DefaultSecurityManager;
import org.jsecurity.mgt.RealmSecurityManager;
import org.jsecurity.mgt.SecurityManager;
import org.jsecurity.realm.Realm;
import org.jsecurity.realm.RealmFactory;
import org.jsecurity.util.LifecycleUtils;
import org.jsecurity.util.ResourceUtils;
import static org.jsecurity.util.StringUtils.clean;
import static org.jsecurity.util.StringUtils.splitKeyValue;

import java.text.ParseException;
import java.util.*;

/**
 * @since 0.9
 * @author Les Hazlewood
 */
public class IniConfiguration extends TextConfiguration {

    public static final String DEFAULT_INI_RESOURCE_PATH = "classpath:jsecurity.ini";
   
    public static final String MAIN = "main";
    public static final String INTERCEPTORS = "interceptors";
    public static final String URLS = "urls";

    public static final String SESSION_MODE_PROPERTY_NAME = "sessionMode";

    private static final String HEADER_PREFIX = "[";
    private static final String HEADER_SUFFIX = "]";

    private static final String[] SECTION_HEADERS = {
            HEADER_PREFIX + MAIN + HEADER_SUFFIX,
            HEADER_PREFIX + INTERCEPTORS + HEADER_SUFFIX,
            HEADER_PREFIX + URLS + HEADER_SUFFIX
    };

    protected Map<String, String> sections = new LinkedHashMap<String, String>();

    public IniConfiguration() {
        if ( ResourceUtils.resourceExists( DEFAULT_INI_RESOURCE_PATH ) ) {
            load( DEFAULT_INI_RESOURCE_PATH, null );
        }
        //else defaults are fine
    }

    public IniConfiguration(String configBodyOrResourcePath) {
        super(configBodyOrResourcePath);
    }

    public IniConfiguration(String configBodyOrResourcePath, String charsetName) {
        super(configBodyOrResourcePath, charsetName);
    }

    protected static boolean isSectionHeader(String name) {
        for (String s : SECTION_HEADERS) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected static String getSectionName(String header) {
        if ( isSectionHeader( header ) ) {
            return header.substring(1, header.length() - 1 );
        }
        return null;
    }

    protected void doLoad(Scanner scanner) throws Exception {

        String currSectionName = null;

        StringBuffer section = new StringBuffer();
        
        while (scanner.hasNextLine()) {

            String line = clean(scanner.nextLine());

            if (line == null || line.startsWith("#")) {
                //skip empty lines and comments:
                continue;
            }

            String sectionName = getSectionName( line.toLowerCase() );
            if ( sectionName != null ) {
                String sectionBody = clean( section.toString() );
                if ( sectionBody != null ) {
                    sections.put( currSectionName, sectionBody );
                }    
                currSectionName = sectionName;
                section = new StringBuffer();
                if (log.isDebugEnabled()) {
                    log.debug("Parsing " + HEADER_PREFIX + currSectionName + HEADER_SUFFIX );
                }
            } else {
                section.append(line).append("\n");
            }
        }

        if ( section.length() > 0 ) {
            String sectionBody = clean( section.toString() );
            if ( sectionBody != null ) {
                sections.put( currSectionName, sectionBody );
            }
        }
    }

    protected void processSections( Map<String,String> sections ) {
        SecurityManager securityManager = createSecurityManager( sections );
        if ( securityManager == null ) {
            String msg = "A " + SecurityManager.class + " instance must be created at startup.";
            throw new ConfigurationException( msg );
        }
        setSecurityManager( securityManager );

        afterSecurityManagerSet( sections );
    }

    protected SecurityManager createSecurityManager( Map<String,String> sections ) {
        String mainSectionBody = sections.get( MAIN );
        return createSecurityManager( mainSectionBody );
    }

    protected String getSessionMode( String main ) {
        if ( main != null ) {
            Scanner scanner = new Scanner(main);
            while( scanner.hasNextLine() ) {
                String line = scanner.nextLine();
                //we only process sessionMode so far:
                String[] nameAndValue;
                try {
                    nameAndValue = splitKeyValue(line);
                } catch (ParseException e) {
                    throw new ConfigurationException(e);
                }
                String name = nameAndValue[0];
                String value = nameAndValue[1];
                if ( SESSION_MODE_PROPERTY_NAME.equalsIgnoreCase(name) && value != null ) {
                    return value;
                }
            }
        }
        return null;
    }

    protected RealmSecurityManager newSecurityManagerInstance() {
        return new DefaultSecurityManager();
    }

    protected SecurityManager createSecurityManager( String mainSection ) {

        Map<String,Object> defaults = new LinkedHashMap<String,Object>();

        RealmSecurityManager securityManager = newSecurityManagerInstance();
        defaults.put( "securityManager", securityManager );
        ReflectionBuilder builder = new ReflectionBuilder(defaults);
        Map<String,Object> objects = builder.buildObjects(mainSection);

        //realms and realm factory might have been created - pull them out first so we can
        //initialize the securityManager:

        List<Realm> realms = new ArrayList<Realm>();

        //iterate over the map entries to pull out the realm factory(s):

        for( Map.Entry<String,Object> entry : objects.entrySet() ) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if ( value instanceof RealmFactory ) {
                RealmFactory factory = (RealmFactory)value;
                LifecycleUtils.init(factory);
                Collection<Realm> factoryRealms = factory.getRealms();
                if ( factoryRealms != null && !factoryRealms.isEmpty() ) {
                    realms.addAll( factoryRealms );
                }
            } else if ( value instanceof Realm ) {
                Realm realm = (Realm)value;
                //set the name if null:
                String existingName = realm.getName();
                if ( existingName == null || existingName.startsWith( realm.getClass().getName() ) ) {
                    try {
                        builder.applyProperty( realm, "name", name );
                    } catch ( Exception ignored ) {}
                }
                realms.add( realm );
            }
        }

        //set them on the SecurityManager
        if ( !realms.isEmpty() ) {
            securityManager.setRealms(realms);
        }

        securityManager.init();

        LifecycleUtils.init(realms);

        return securityManager;
    }

    protected void afterSecurityManagerSet( Map<String,String> sections ) {}
}