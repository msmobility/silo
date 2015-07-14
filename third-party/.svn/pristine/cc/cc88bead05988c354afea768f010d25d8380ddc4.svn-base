SlimJim ver. 1.1

The SlimJim program takes a java program and creates a jar file holding only those classes necessary to run it.
Essentially what it does is, given some starting class(es), it uses a java decompiler to find all of the linked classes
in the classfile(s), and then recursively perform this operation on the linked classes.

This can be used to reduce the size of a projects jar distribution, as well as combining it into a single jar file.
The original intention, though, was to provide a way to give access to tools without requiring a huge jar file holding
many unwanted tools/classes.  The former use might be a little sketchy if you are using third party libraries, as, they
often must be kept in their entirety, and, depending on the situation, this tool may violate their license.  The second
use is generally more valid, as internal tool distribution tends to be less dependent on third party libraries.

This program requires access to a tools.jar library from a Sun/Oracle JDK or JVM distribution, version 1.7 or later.
If one is not available, you may use the one included with the JVM distributed with this release. (Note that although
the JVM in this release is a Windows distribution, the /lib/tools.jar file is not platform-dependant.)

This program/code is licenses under the BSD 3-Clause License (see LICENSE.txt).

The following is the command line usage:

java -cp [classpath] SlimJim [-p] [-d] [-v] [-k mask] [-i mask] [-o file] [-a file] [-u] [-f file] [-e file:path] [-j file:entry] [-z file] classes ...
where:
    classpath = the classpath for the programs to slim
    -p = print class dependencies
    -d = print classpath dependencies
    -v = print verbose output
    -k = skip classes starting with mask
    -ka = skip all classes (use with -i to build package-limited jar)
    -i = force inclusion of classes starting with mask
    -o = the output jar file
    -a = auto-add non-class jar entries from (assumed jar) file
    -u = same as -a, only automatically includes all jar files
    -f = add file to output jar
    -e = add file to output jar in path (jar entry will be path/filename)
    -j = add entry from jar to output jarfile
    -z = include all entries from jar file in output jar
    classes ... = classes to determine dependencies for and add to output jarfile
