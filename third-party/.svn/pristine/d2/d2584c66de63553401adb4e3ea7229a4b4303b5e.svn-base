--------------------------------------------------------------------------
X M L - R P C
--------------------------------------------------------------------------

The Apache XmlRpc package is an implementation of the XML-RPC
specification (http://www.xml-rpc.com) with optional Servlet
and SSL extensions.

.           Location of Ant build.xml and build.properties files.
bin/        Temporary directory for building the project.
lib/        Final location of the jar files
examples/   Some examples and instructions on how to run them.
src/        Location of Java sources.
xdocs/      XmlRpc documention in XDOC format.
docs/       The rendered documentation in HTML format.


--------------------------------------------------------------------------
R E Q U I R E M E N T S
--------------------------------------------------------------------------

To build the XmlRpc package, you need to have Ant (and JDK 1.2+) installed
(http://jakarta.apache.org/ant/).  Execute "ant -help" from this directory
to get basic help or just "ant" to build the jars.


--------------------------------------------------------------------------
B U I L D I N G
--------------------------------------------------------------------------

You can build the core XmlRpc package with the classes provided
using JDK 1.2+. If you wish to use the Servlet and/or SSL extensions
than you must set the following properties in either your
${user.home}/build.properties file, or the default.properties
file provided in the XmlRpc build/ directory:

jsse.jar
jnet.jar
jcert.jar
servlet.jar

These properties define full paths to JARs files.

If you are using the Eclipse IDE, Apache's XML-RPC package comes ready
with basic .classpath and .project files. Simply run "ant copy-deps" to
populate the "lib" directory


--------------------------------------------------------------------------
R U N N I N G
--------------------------------------------------------------------------

The default SAX parser that is used is the MinML parser which is
included in the download. If you want to use an alternative parser
you have to make sure it is included in your CLASSPATH.
