package com.pb.sawdust.util;

import java.util.*;

/**
 * The {@code SystemType} enum is used for identifying operating systems.
 *
 * @author crf <br/>
 *         Started Mar 15, 2010 8:06:18 AM
 */
public enum SystemType {
    //not exhaustive, but good enough - this class is specific to Sun JVMs
    //AIX
    //Digital Unix
    //FreeBSD
    //HP UX
    //Irix
    //Linux
    //Mac OS
    //Mac OS X
    //MPE/iX
    //Netware 4.11
    //OS/2
    //Solaris
    //Windows 2000
    //Windows 7
    //Windows 95
    //Windows 98
    //Windows NT
    //Windows Vista
    //Windows XP
    /**
     * The Linux operating system.
     */
    LINUX("Linux",SystemFamily.UNIX),
    /**
     * The Macintosh OS (pre-OS X) operating system.
     */
    MAC_OS("Mac OS",SystemFamily.MAC),
    /**
     * The Macintosh OS X operating system.
     */
    MAC_OS_X("Mac OS X",SystemFamily.UNIX,SystemFamily.MAC),
    /**
     * The Windows NT operating system.
     */
    WINDOWS_NT("Windows NT",SystemFamily.WINDOWS),
    /**
     * The Windows 95 operating system.
     */
    WINDOWS_95("Windows 95",SystemFamily.WINDOWS),
    /**
     * The Windows 98 operating system.
     */
    WINDOWS_98("Windows 98",SystemFamily.WINDOWS),
    /**
     * The Windows 2000 operating system.
     */
    WINDOWS_2000("Windows 2000",SystemFamily.WINDOWS),
    /**
     * The Windows XP operating system.
     */
    WINDOWS_XP("Windows XP",SystemFamily.WINDOWS),
    /**
     * The Windows Vista operating system.
     */
    WINDOWS_VISTA("Windows Vista",SystemFamily.WINDOWS),
    /**
     * The Windows 7 operating system.
     */
    WINDOWS_7("Windows 7",SystemFamily.WINDOWS);

    private final String sunJvmOsValue;
    private final EnumSet<SystemFamily> families;
    private SystemType(String sunJvmOsValue, SystemFamily family, SystemFamily ... otherFamilies) {
        this.sunJvmOsValue = sunJvmOsValue;
        families = EnumSet.of(family,otherFamilies);
    }

    /**
     * Get the line separator for this operating system.
     *
     * @return this operating system's line separator.
     */
    public String getLineSeparator() {
        //order important: mac os x is unix like and mac like, so get unix eol first
        //enum set will iterate in order constants declared
        return families.iterator().next().lineSeparator;
    }

    /**
     * Get the operating system families this operating system belongs to.  If an operating system belongs to two families,
     * they are ranked in order of precendence (<i>e.g.</i> the families of {@code MAC_OS_X} are {@code UNIX} and
     * {@code MAC}, in that order, since Macintosh OS X is generally though to be more "Unix-like" than "Mac-like").
     *
     * @return this operating system's families.
     */
    public Set<SystemFamily> getFamilies() {
        return EnumSet.copyOf(families);
    }

    /**
     * Get the system type the current JVM is running on.
     *
     * @return the system this JVM is running on.
     */
    public static SystemType getSystemType() {
        String osType = System.getProperty("os.name");
        for (SystemType type : SystemType.values())
            if (type.sunJvmOsValue.equals(osType))
                return type;
        throw new IllegalStateException("Undefined os name.");
    }

    /**
     * The {@code SystemFamily} enum is used to identify operating system families.  These families are not necessarily
     * exclusive (though they generally are), but still represent the broad categories that operating systems are generally
     * grouped into.
     */
    public static enum SystemFamily {
        /**
         * The Windows family of operating systems.
         */
        WINDOWS("\r\n"),
        /**
         * The "Unix-like" family of operating systems.
         */
        UNIX("\n"),
        /**
         * The Macintosh family of operating systems.
         */
        MAC("\r");//mac is representative of pre os x mac, mixin unix and mac for os x, so be careful with eol

        private final String lineSeparator;
        private SystemFamily(String lineSeparator) {
            this.lineSeparator = lineSeparator;
        }

        /**
         * Get the line separator used by this operating system family.
         *
         * @return this operating system family's line separator.
         */
        public String getLineSeparator() {
            return lineSeparator;
        }
    }

    public static void main(String ... args) {
        Properties p = System.getProperties();
        Enumeration<Object> i = p.keys();
        while (i.hasMoreElements()) {
            Object key = i.nextElement();
            System.out.println(key + " : " + p.get(key));
        }
        System.out.println(getSystemType());
        System.out.println(getSystemType().getFamilies());
    }
}
