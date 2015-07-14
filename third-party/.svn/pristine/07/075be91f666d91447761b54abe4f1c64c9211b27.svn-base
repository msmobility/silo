package com.pb.sawdust.util.test;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.util.ThreadTimer;
import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.BeforeClass;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import org.junit.internal.ArrayComparisonFailure;
import com.pb.sawdust.util.RandomDeluxe;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.pb.sawdust.util.MathUtil.*;


/**
 * The {@code TestBase} class is the base class from which all test classes should inherit from to enable the
 * descriptive timing reporting provided by {@code TestRunner}. The test class merely needs to extend this class to
 * enable the functionality, though some additional coding boilerplate is recommended:
 * <ul>
 *     <li>
 *         A {@code main(String[])} method should be included which looks like this:
 *         <pre><tt>
 *             public static void main(String ... args) {
 *                 TestBase.main();
 *             }
 *         </tt></pre>
 *         This will enable the default behavior of running the tests in the class from its main method.
 *     </li>
 *     <li>
 *         The org.junit.Assert class should be statically imported:
 *         <pre><tt>
 *             import static org.junit.Assert.*;
 *         </tt></pre>
 *         This allows the use of {@code assertEquals(...)}, {@code assertArrayEquals(...)}, <i>etc.</i> directly rather
 *         than the clunkier {@code Assert.assertEquals(...)}.
 *     </li>
 * </ul>
 * <p>
 * When tests are run from {@link #main(String...)}, they will be multithreaded in the sense that the running of each test
 * class instance (not each test) will be perfromed in parallel. The default maximum threadcount for this parallelization
 * is set to the number of processors on the machine plus two, but can be overridden by specifying the threadcount as the
 * first argument to the {@code main} method.  Because of this default parallelization, great care must be taken to ensure
 * that test classes are independant and threadsafe. As a specific example, static state should not be shared across test
 * class instances, unless that static state is immutable. If this is not possible, then the tests must be run with the
 * threadcount set to one.
 * <p>
 * This class also offers some functionality which allows a test class to be applied more flexibly to different situations,
 * such as dealing with multiple inheritence (from interfaces), shared test data, and test contexts.  To deal with multiple
 * inheritance, it is possible to specify "child" {@code TestBase} classes that should be run in addition to the current
 * test class. This specification is performed through the {@link #getAdditionalTestClasses()} method.
 * <p>
 * Sharing test data across test classes is often desirable - this may be due to the need to minimize expensive test data
 * construction or to pass along test-specific information to test classes returned by {@code getAdditionalTestClasses()}.
 * Every test class instance can have test data associated with it through the {@code addTestData} methods. These provide
 * a way of mapping a given ({@code String}) key to some test data specific to that class.  This test data will be inherited
 * by additional classes from {@code getAdditionalTestClasses()} which are run.
 * <p>
 * Sometimes a single test class needs to be run multiple times for different contexts. For example, a class to test may
 * be constructed in different ways, but the tests remain the same across all instances, so different context instances for
 * each constructor (without repeating tests) would be useful. This class allows contexts to be attached to test classes
 * through the {@link #addClassRunContext(Class, java.util.List)} method. A context is defined as a map ({@code String}
 * keys to object values) holding the context information. The {@link #getClassRunContext(Class)} method can then be used
 * for a specific test instance to determine which context it is running under. This framework is set up to be threadsafe
 * and is preferable to static state variables (which will not work with the parallel test runs performed by this class).
 * <p>
 * To help ensure that the contexts are being set correctly, the context information is printed out in the test summaries
 * as they run. It is also possible to perform a "dry run" (see {@link #setDryRun(boolean)}) where the test classes and
 * contexts are listed, but no tests actually run, to quickly determine that the contexts are being established correctly.
 * <p>
 * Due to the parallelization of the test running, it is essentially impossible to perform accurate test cleanup at a class
 * level if multiple contexts or additional test classes have been specified. To assist with this cleanup, it is possible
 * to "capture" which tests have been started between two code positions (specified by {@link #classStarted()} and
 * {@link #classFinished(Runnable)}, and then run some cleanup operations (defined by the runnable passed to the
 * {@code classFinished} method) after all of these tests have finished.  It is also possible, if desired, to register a
 * cleanup method that will be performed after all tests have completed ({@link #registerCleanupMethod(Runnable)}).
 * <p>
 * Some reporting-level granularity is also provided through this class.  Specifically, it is possible to add some additional
 * test information which will be reported as each test is performed. Also, it is possible to set some test failure information
 * (such as details about the failure beyond the assertion errors) which will be reported if the test fails.
 * <p>
 * Additionally, this class contains a number of "(assert) almost equals" methods which can be used to compare {@code float}
 * and {@code double} values and arrays. These methods determine whether values are within some error range of one
 * another. Also, convenience methods dealing with creating temporary files a provided through this class.  Finally,
 * an instance of {@code RandomDeluxe} is available through this class ({@link #random}).
 *
 * @author crf <br/>
 *         Started: Jul 19, 2008 12:42:14 AM
 */
@RunWith(TestRunner.TimerRunner.class)
public abstract class TestBase {
    private static boolean printoutExceptions = false;

    /**
     * An instance of {@code RandomDeluxe} which can be used to create random data/access for testing.
     */
    protected static final RandomDeluxe random = new RandomDeluxe();

    /**
     * Specify whether exceptions caught during tests should be printed out explicitly by this class upon completion.
     * The default behavior is to not print them out.
     *
     * @param printoutExceptions
     *        If {@code true}, exceptions will be printed out by this class, if {@code false}, they will not.
     */
    //todo: this may not be needed anymore
    public static void setPrintoutExceptions(boolean printoutExceptions) {
        TestBase.printoutExceptions = printoutExceptions;
    }

    /**
     * Get additional test classes to run tests on. This method can be overridden to capture multiple interface inheritance.
     * By default, this returns an empty collection. Any test classes returned by this method will be run whenever this
     * test class is run.
     *
     * @return additional test classes.
     */
    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        return new LinkedList<Class<? extends TestBase>>();
    }

    private static ThreadLocal<Map<Class<? extends TestBase>,Class<? extends TestBase>>> contextInstance = new ThreadLocal<Map<Class<? extends TestBase>,Class<? extends TestBase>>>() {
        protected Map<Class<? extends TestBase>,Class<? extends TestBase>> initialValue() {
            return new HashMap<Class<? extends TestBase>,Class<? extends TestBase>>();
        }
    };

    /**
     * Get an instance of the calling context class. That is, if this class was added to the test classes through a call to
     * {@link #getAdditionalTestClasses()}, then get an instance (freshly constructed) of the class whose {@code getAdditionalTestClasses()}
     * method was called to get this class. This is slightly experimental and should only be used in a conservative manner.
     *
     * @return a calling context class instance.
     */
    protected TestBase getCallingContextInstance() {
        //todo: maybe need beforeClass method called?
        try {
            return contextInstance.get().get(this.getClass()).newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static ThreadLocal<Map<Class<? extends TestBase>,Map<String,Object>>> testData = new ThreadLocal<Map<Class<? extends TestBase>,Map<String,Object>>>() {
        protected Map<Class<? extends TestBase>,Map<String,Object>> initialValue() {
            return new WeakHashMap<Class<? extends TestBase>,Map<String,Object>>();
        }
    };

    /**
     * Get the test data for a key associated with this instance's class. This method must be called from the same class
     * instance as {@link #addTestData(String, Object)} and thread to ensure correct context.
     *
     * @param key
     *        The data key.
     *
     * @return the data associated with {@code key} and this class.
     */
    protected Object getTestData(String key) {
        return getTestData(this.getClass(),key);
    }

    /**
     * Get the test data for a key associated with a specified class. This method must be called from the same thread as
     * that which added the key to ensure correct context.
     *
     * @param testClass
     *        The test class the key is associated with.
     *
     * @param key
     *        The data key.
     *
     * @return the data associated with {@code key} and this class.
     */
    protected Object getTestData(Class<? extends TestBase> testClass, String key) {
        return testData.get().get(testClass).get(key);
    }

    private static Map<String,Object> getTestData(Class<? extends TestBase> testClass) {
        Map<Class<? extends TestBase>,Map<String,Object>> testDataMap = testData.get();
        if (!testDataMap.containsKey(testClass))
            testDataMap.put(testClass,new HashMap<String,Object>());
        return testDataMap.get(testClass);
    }

    /**
     * Add test data for this class.  This data may be retrieved via {@link #getTestData(String)}.
     *
     * @param key
     *        The data key.
     *
     * @param data
     *        The data value.
     */
    protected void addTestData(String key, Object data) {
        getTestData(this.getClass()).put(key,data);
    }

    /**
     * Add test data for a specified class.  This data may be retrieved via {@link #getTestData(String)}. The test data
     * will be associated with this class only in the calling thread, or for test classes called by this test class instance.
     *
     * @param testClass
     *        The test class to add the data for.
     *
     * @param key
     *        The data key.
     *
     * @param data
     *        The data value.
     */
    protected void addTestData(Class<? extends TestBase> testClass, String key, Object data) {
        getTestData(testClass).put(key,data);
    }

    private static ThreadLocal<Map<Class<? extends TestBase>,List<Map<String,Object>>>> classRunContext = new ThreadLocal<Map<Class<? extends TestBase>,List<Map<String,Object>>>>() {
        protected Map<Class<? extends TestBase>,List<Map<String,Object>>> initialValue() {
            return new WeakHashMap<Class<? extends TestBase>,List<Map<String,Object>>>();
        }
    };

    /**
     * Convenience method to build a class run context from a single data key-value pair. This method can be used to initialize
     * a class run context, which can then be modified further, if needed.
     *
     * @param key
     *        The data key.
     *
     * @param value
     *        The data value.
     *
     * @return a map holding the {@code key}-{@code value} pair, suitable for use as a class run context.
     *
     * @see #addClassRunContext(Class, java.util.List)
     */
    public static Map<String,Object> buildContext(String key, Object value) {
        Map<String,Object> m = new HashMap<String,Object>();
        m.put(key,value);
        return m;
    }

    /**
     * Add a series of class run contexts for a given test class.  A class run context is a set of test data that can be
     * used by a given class during a test (<i>e.g.</i> whether a certain algorithm is being tested in parallel or single-thread
     * mode); it allows a single test class to be reused over a variety of circumstances.  When this class's {@code main}
     * method is called, a given test class will be run against each class run context specified.  When a class's tests
     * are run using a given context, then that context data is available through {@link #getTestData(String)}.
     * <p>
     * For each class run context in the passed data, a new list of contexts combining it and the current contexts already
     * existing for the test calss will be built (that is, they will be compounded/interleaved with each other). The run
     * context is only valid for the specified class, and will not translate to subclasses (though subclasses may take
     * advantage of the context by overriding {@link #getAdditionalTestClasses()} and using {@link #getCallingContextInstance()}).
     *
     * @param testClass
     *        The class to add the context for.
     *
     * @param contextData
     *        The list of data, each defining a new context.
     */
    public static void addClassRunContext(Class<? extends TestBase> testClass,List<Map<String,Object>> contextData) {
        List<Map<String,Object>> originalContexts = new LinkedList<Map<String, Object>>(getClassRunContext(testClass));
        clearClassContext(testClass);
        if (originalContexts.size() > 0) {
            for (Map<String,Object> oc : originalContexts) {
                for (Map<String,Object> contextAdditions : contextData) {
                    Map<String,Object> nc = new HashMap<String,Object>(contextAdditions);
                    nc.putAll(oc);
                    getClassRunContext(testClass).add(nc);
                }
            }
        } else {
            getClassRunContext(testClass).addAll(contextData);
        }
    }

    /**
     * Set additional test information for tests run from this thread. This information is reported in the detailed
     * test reports created for this class. This information is only valid for tests run from the same thread that called
     * this method.
     *
     * @param additionalInfo
     *        Additional test information to use with this test.
     */
    protected void setAdditionalTestInformation(String additionalInfo) {
        TestRunner.getInstance().setAdditionalTestInformation(additionalInfo + startingAdditionalInfo.get());
    }

    /**
     * Add additional test information for tests run from this thread. This method appends the information to the already
     * existing additional information for the test reports created for this class. This information is only valid for tests
     * run from the same thread that called this method.
     *
     * @param additionalInfo
     *        Additional test information to use with this test.
     */
    protected void addAdditionalTestInformation(String additionalInfo) {
        TestRunner.getInstance().setAdditionalTestInformation(TestRunner.getInstance().getAdditionalTestInformation() + additionalInfo);
    }

    private static List<Map<String,Object>> getClassRunContext(Class<? extends TestBase> testClass) {
        Map<Class<? extends TestBase>,List<Map<String,Object>>> crc = classRunContext.get();
        if (!crc.containsKey(testClass))
            crc.put(testClass,new LinkedList<Map<String, Object>>());
        return crc.get(testClass);
    }

    private static void clearClassContext(Class<? extends TestBase> testClass) {
        classRunContext.get().remove(testClass);
    }

    private static ThreadLocal<String> startingAdditionalInfo = new ThreadLocal<String>();

    private static boolean dryRun = false;

    /**
     * Sets whether tests will be a dry run or not.  A dry run will print out all of the classes (and contexts) that would
     * be tested, but no actual tests are run. The default is (of course) {@code false}.
     *
     * @param dryRun
     *        Whether tests should be run as a dry run or not.
     */
    public static void setDryRun(boolean dryRun) {
        TestBase.dryRun = dryRun;
    }

    private static final List<TestResult> resultList = Collections.synchronizedList(new LinkedList<TestResult>());

    /**
     * Get the list of test results. This method will block until all tests have finished running.
     *
     * @return the list of test results.
     */
    public static List<TestResult> getResultList() {
        awaitFinish();
        return Collections.unmodifiableList(resultList);
    }

    /**
     * Print the test result summary to whatever output streams have been set.  Failure information will be printed from
     * this method.
     */
    public static void printResultSummary() {
        printResultSummary(true);
    }

    /**
     * Print the test result summary to whatever output streams have been attached to this class, specifying whether
     * failure details should be printed as well. Failure details will include stack traces and extra error information,
     * if present.
     *
     * @param printFailures
     *        If {@code true}, then failure information will be printed, otherwise it will not be.
     */
    public static void printResultSummary(boolean printFailures) {
        printResultSummary(printFailures,-1);
    }

    /**
     * Add an output stream to print test reports and summaries to.
     *
     * @param output
     *        The output stream to add.
     */
    public static void addOuputStream(PrintStream output) {
        TestRunner.addOutputStream(output);
    }

    /**
     * Remove an output stream from the list that test reports and summaries will be printed to.
     *
     * @param output
     *        The output stream to remove.
     */
    public static void removeOuputStream(PrintStream output) {
        TestRunner.removeOutputStream(output);
    }
    
    private static void printResultSummary(boolean printFailures, long absoluteDuration) {
        awaitFinish();
        TestRunner.writeLineToOutput("***Test Summaries***");
        List<Failure> failures = new LinkedList<Failure>();
        Map<Failure,Map<String,Object>> failureContext = new HashMap<Failure,Map<String,Object>>();
        Map<Failure,Object> extraFailureInformation = new HashMap<Failure,Object>();
        int testCount = 0;
        int ignoreCount = 0;
        int failureCount = 0;
        long totalTime = 0L;
        for (TestResult result : resultList) {
            testCount += result.result.getRunCount();
            failureCount += result.result.getFailureCount();
            ignoreCount += result.result.getIgnoreCount();
            totalTime += result.result.getRunTime();
            if (printFailures) {
                for (Failure f : result.result.getFailures()) {
                    failures.add(f);
                    failureContext.put(f,result.context);
                }
                extraFailureInformation.putAll(result.extraFailureInformation);
                if (result.unmatchedFailureInformation.size() > 0) {
                    TestRunner.writeLineToOutput("Unmatched failure information:");
                    for (String s : result.unmatchedFailureInformation.keySet())
                        TestRunner.writeLineToOutput(" " + s + " : " + result.unmatchedFailureInformation.get(s));
                }
            }
            TestRunner.writeLineToOutput(result.summary());
        }
        TestRunner.writeLineToOutput("");
        StringBuilder sb = new StringBuilder();
        sb.append("***Total Summary (").append(resultList.size()).append(" test classes)***").append(FileUtil.getLineSeparator())
          .append("    ")
          .append("tests: ").append(testCount)
          .append(", failures: ").append(failureCount)
          .append(", ignores: ").append(ignoreCount).append(", ")
          .append("run time: ").append(totalTime/60000).append(" min ").append((totalTime - totalTime/60000*60000)/1000).append(" sec");
        if (absoluteDuration > 0)
            sb.append(", absolute run time: ").append(absoluteDuration/60000).append(" min ").append((absoluteDuration - absoluteDuration/60000*60000)/1000).append(" sec");
        TestRunner.writeLineToOutput(sb.toString());

        if (printFailures) {
            TestRunner.writeLineToOutput("");
            if (failures.size() > 0)
                TestRunner.writeLineToOutput("***Failures***");
            for (Failure f : failures) {
                Map<String,Object> context = failureContext.get(f);
                TestRunner.writeLineToOutput(f.getTestHeader() + (context.size() > 0 ? " " + context : ""));
                f.getException().printStackTrace();
                if (extraFailureInformation.containsKey(f)) {
                    TestRunner.writeLineToOutput("Extra failure information:");
                    TestRunner.writeLineToOutput(extraFailureInformation.get(f).toString());
                }
            }
        }
        TestRunner.flushOutput();
    }
    
    private static ThreadLocal<Map<String,Object>> testFailureInformation = new ThreadLocal<Map<String, Object>>() {
        protected Map<String,Object> initialValue() {
            return new HashMap<String,Object>();
        }
    };
    
    private static String getTestMethod(Throwable e) {
        for (StackTraceElement ste : e.getStackTrace()) {
            try {
                if (ste.getClass().getMethod(ste.getMethodName()).getAnnotation(Test.class) != null) 
                    return ste.getMethodName();
            } catch (NoSuchMethodException e1) {
                //swallow - shouldn't be in here, though
            }
        }
        return null;
    }

    /**
     * Add test failure information for the currently running test. If the test failed, then this information will be
     * printed out in test summary.
     *
     * @param information
     *        The additional information on the failure.
     */
    public static void addTestFailureInformation(Object information) {
        testFailureInformation.get().put(getTestMethod(new Exception()),information);
    }
    
    private static List<Failure> runActualTests(Class<? extends TestBase> testClass) {
        if (dryRun) {
            System.out.println("(Dry) running tests for " + testClass + " " + getTestData(testClass));
            return new LinkedList<Failure>();
        } else {
            Result r = TestRunner.getInstance().runTests(testClass);
            Map<String,Object> tfi = testFailureInformation.get();
            TestResult result = new TestResult(r,testClass,new HashMap<String,Object>(getTestData(testClass)),tfi);
            tfi.clear();
            resultList.add(result);
            return result.getFailures();
        }
    }

    private static List<Failure> runTests(Class<? extends TestBase> testClass, Map<Class<? extends TestBase>,Map<String,Object>> contextClassData) throws IllegalAccessException, InstantiationException {
        final List<Failure> failures = new LinkedList<Failure>();
        //run this up here so it is precalled
        Collection<Class<? extends TestBase>> additionalTestClasses = testClass.newInstance().getAdditionalTestClasses();
        List<Map<String,Object>> cc = getClassRunContext(testClass);
        if (cc.size() > 0) {
            for (Map<String,Object> ccd : cc) {
                getTestData(testClass).putAll(ccd);
                startingAdditionalInfo.set(" " + getTestData(testClass));
                submitTests(testClass, contextClassData);
            }
        } else {
            Map<?,?> m = getTestData(testClass);
            startingAdditionalInfo.set(m.size() == 0 ? "" : " " + m);
            submitTests(testClass,contextClassData);
        }
        clearClassContext(testClass);
        contextClassData.put(testClass,getTestData(testClass));
        for (Class<? extends TestBase> additionalTestClass : additionalTestClasses) {
            if (!(additionalTestClass.getEnclosingClass() == testClass))
                getTestData(additionalTestClass).put("calling class",testClass.getSimpleName());
            contextInstance.get().put(additionalTestClass,testClass);
            failures.addAll(runTests(additionalTestClass,contextClassData));
            contextInstance.get().remove(additionalTestClass);
        }
        contextClassData.remove(testClass);
        return failures;
    }

    private static List<Failure> runTests(Class<? extends TestBase> testClass) throws IllegalAccessException, InstantiationException {
        return runTests(testClass,new HashMap<Class<? extends TestBase>,Map<String,Object>>());
    }

    private static void submitTests(final Class<? extends TestBase> testClass, Map<Class<? extends TestBase>,Map<String,Object>> contextClassData) {
        final Map<String,Object> testData = new HashMap<String,Object>(getTestData(testClass));
        final Map<Class<? extends TestBase>,Map<String,Object>> ccd = new HashMap<Class<? extends TestBase>, Map<String, Object>>();
        for (Class<? extends TestBase> cc : contextClassData.keySet())
            ccd.put(cc,new HashMap<String,Object>(contextClassData.get(cc)));
        final String additionalInfo = startingAdditionalInfo.get();
        final Class<? extends TestBase> contextInstanceClass = contextInstance.get().get(testClass);
        testStarted(testClass);
        testResults.add(
            executors.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        getTestData(testClass).putAll(testData);
                        for (Class<? extends TestBase> cc : ccd.keySet())
                            getTestData(cc).putAll(ccd.get(cc));
                        contextInstance.get().put(testClass,contextInstanceClass);
                        startingAdditionalInfo.set(additionalInfo);
                        runActualTests(testClass);
                        contextInstance.get().remove(testClass);
                        getTestData(testClass).clear(); //for cleanup
                        testFinished(testClass);
                    }
                }
            )
         );
    }

    private static volatile ExecutorService executors = null;
    private static Queue<Future<?>> testResults = new ConcurrentLinkedQueue<Future<?>>();

    private static void awaitFinish() {
        while (!testResults.isEmpty()) {
            try {
                testResults.poll().get();
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeWrappingException(e);
            }
        }
    }

    private static void runTests() {
        for (Class<?> potentialTestClass : new SecurityManager() {
                    public Class[] getClassContext() {
                        return super.getClassContext();
                    }
                }.getClassContext()) {
            if (!potentialTestClass.equals(TestBase.class) && (TestBase.class.isAssignableFrom(potentialTestClass))) {
                try {
                    potentialTestClass.asSubclass(TestBase.class);
                    @SuppressWarnings("unchecked") //previous line ensures we are ok with cast
                    Class<? extends TestBase> testClass = (Class<? extends TestBase>) potentialTestClass;
                    //testRunner.runTests(testClass);
                    List<Failure> failures = runTests(testClass);
                    if (printoutExceptions)
                        for (Failure failure : failures)
                            failure.getException().printStackTrace();
                    return;
                } catch (ClassCastException e) {
                    /*not a usable class*/
                } catch (InstantiationException e) {
                   throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                   throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalStateException("Cannot find test class.");
    }

    /**
     * Perform some specific activity before the tests in the class are run. Currently just sets the additional
     * test information to nothing (an empty string).
     */
    @BeforeClass
    public static void beforeClass() {
        TestRunner.getInstance().setAdditionalTestInformation(startingAdditionalInfo.get());
    }

    private static final Set<Class<?>> runningClasses = new HashSet<Class<?>>();
    private static final Map<Class<?>,List<Class<? extends TestBase>>> classRunningHooks = new HashMap<Class<?>,List<Class<? extends TestBase>>>();
    private static final Map<Class<?>,Runnable> classFinishedMethods = new HashMap<Class<?>,Runnable>();

    /**
     * Indicate that a class has started calling tests. This method should be paired with a call to {@link #classFinished(Runnable)}
     * to setup class-level cleanup operations.
     */
    public static void classStarted() {
        Class<?> callingClass = getCallingClass();
        runningClasses.add(callingClass);
        classRunningHooks.put(callingClass,new LinkedList<Class<? extends TestBase>>());
    }

    /**
     * Indicate that a class has finished calling tests, and what to do when those tests have finished. This method should
     * be called after {@link #classStarted()} for the test class.
     *
     * @param finishMethod
     *        The cleanup actions to perform after all the tests captured by this class have finished.
     */
    public static void classFinished(Runnable finishMethod) {
        Class<?> callingClass = getCallingClass();
        runningClasses.remove(callingClass);
        classFinishedMethods.put(callingClass,finishMethod);
    }

    private static final String THIS_CLASS = TestBase.class.getCanonicalName();

    private static Class<?> getCallingClass() {
        //cycle till we get out of this class's stack chain
        for (StackTraceElement ste : new RuntimeException().getStackTrace()) {
            if (!ste.getClassName().equals(THIS_CLASS)) {
                try {
                    return Class.forName(ste.getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace(); //for debugging, but hopefully won't get here
                }
            }
        }
        throw new IllegalStateException("Shouldn't be here");
    }

    private static void testStarted(Class<? extends TestBase> testClass) {
        for (Class<?> c : runningClasses)
            classRunningHooks.get(c).add(testClass);
    }

    private static Queue<Class<? extends TestBase>> finishedClasses = new ConcurrentLinkedQueue<Class<? extends TestBase>>();
    private static AtomicBoolean runningFinishedActions = new AtomicBoolean(false);

    private static void testFinished(Class<? extends TestBase> testClass) {
        finishedClasses.add(testClass);
        testFinishedActions();
    }

    private static void testFinishedActions() {
        if (!runningFinishedActions.compareAndSet(false,true))
            return; //somebody is already doing this
        Class<? extends TestBase> testClass;
        while ((testClass = finishedClasses.poll()) != null) {
            //make a new copy of key set to avoid concurrent modification exception
            //  if a class is finished while this is running
            //  this, of course, expects that a classStarted() will not be called from a given class more than once
            Set<Class<?>> hookSet = new HashSet<Class<?>>(classRunningHooks.keySet());
            for (Class<?> c : hookSet) {
                if (classRunningHooks.get(c).remove(testClass) && !runningClasses.contains(c) && classRunningHooks.get(c).size() == 0) {
                    classRunningHooks.remove(c);
                    try {
                        classFinishedMethods.remove(c).run();
                    } catch (Exception e) {
                        e.printStackTrace(); //let user know what is going on, but don't halt progress
                    }
                }
            }
        }
        runningFinishedActions.set(false);
    }

    private static final List<Runnable> cleanupMethods = new LinkedList<Runnable>();

    /**
     * Register a cleanup method.  This method will be run after all tests have finished (not just the ones from the
     * class(es) that this method was called from.
     *
     * @param runnable
     *        The method (as a runnable) which will be run to cleanup after all tests have finished.
     */
    public static void registerCleanupMethod(Runnable runnable) {
        cleanupMethods.add(runnable);
    }

    /**
     * Main method which will run any tests contained in the calling class (does not have to (nor should it) be
     * {@code TestBase}). These are multi-threaded by default, using a threadcount equal to the number of processors
     * plus two. Alternatively, the user may override this default by specifying the first argument (as a positive integer)
     * which will be used as the threadcount.
     *
     * @param args
     *        If {@code args.length > 0} then the first argument will be used as the threadcount for the tests. This
     *        argument is only relevant if this is the first time this method is called in the test suite chain.
     *
     * @throws IllegalArgumentException if {@code args.length > 0} and either {@code args[0]} cannot be parsed into an
     *                                  integer or {@code args[0] < 1}.
     */
    public static void main(String ... args) {
        boolean master = executors == null;
        final ThreadTimer t = master ? new ThreadTimer(TimeUnit.MILLISECONDS) : null;
        int threadcount = args.length > 0 ? Integer.parseInt(args[0]) : Runtime.getRuntime().availableProcessors()+2; //give a little overhead for slow build/teardowns
        if (threadcount < 1)
            throw new IllegalArgumentException("Threadcount must be strictly positive: " + threadcount);
        if (master) {
            executors = Executors.newFixedThreadPool(threadcount);
//            executors = Executors.newFixedThreadPool(1);   //this is for quick single threaded mode
            t.startTimer();
        }
        runTests();
        if (master) {
            final long firstTime = t.endTimer();
            new Thread(new Runnable() {
                public void run() {
                    t.startTimer();
                    awaitFinish();
                    long totalTime = firstTime + t.endTimer();
                    executors.shutdown();
                    executors = null;
                    testFinishedActions(); //finish up whatever is out there
                    for (Runnable r : cleanupMethods) {
                        try {
                            r.run();
                        } catch (Exception e) {
                            e.printStackTrace(); //just to let the user know it is happening
                        }
                    }
                    printResultSummary(true,totalTime);
                }
            }).start();
        }
    }

    /* ********************Test Failure Class*********************** */

    /**
     * The {@code TestResult} class represents a finished set of tests for a given {@code TestBase} instance. It captures
     * information on the test class, the context, the test results, and any extra failure information attached to tests.
     */
    public static class TestResult {
        private final Result result;
        private final Class<? extends TestBase> testClass;
        private final Map<String,Object> context;
        private final Map<Failure,Object> extraFailureInformation;
        private final Map<String,Object> unmatchedFailureInformation;

        private TestResult(Result result, Class<? extends TestBase> testClass, Map<String,Object> context, Map<String,Object> testFailureInformation) {
            this.result = result;
            this.testClass = testClass;
            this.context = context;
            extraFailureInformation = new HashMap<Failure,Object>();
            for (Failure f : getFailures()) {
                if (testFailureInformation.size() == 0) 
                    break;
                String t = getTestMethod(f.getException()); 
                if (testFailureInformation.containsKey(t)) 
                    extraFailureInformation.put(f,testFailureInformation.remove(t));
            }
            unmatchedFailureInformation = new HashMap<String,Object>(testFailureInformation);
        }

        /**
         * Get a list of failures from this test result.
         *
         * @return a list of the failures held by this test result.
         */
        public List<Failure> getFailures() {
            return result.getFailures();
        }

        /**
         * Get a summary of this test result, including test, failure, and ignore counts, as well as total runtime.
         *
         * @return a string summary of this test result.
         */
        public String summary() {
            StringBuilder sb = new StringBuilder();
            sb.append(testClass).append(" ").append(context.size() > 0 ? context : "").append(FileUtil.getLineSeparator());
            sb.append("    ")
              .append("tests: ").append(result.getRunCount())
              .append(", failures: ").append(result.getFailureCount())
              .append(", ignores: ").append(result.getIgnoreCount()).append("; ")
              .append("run time: ").append(((double) result.getRunTime()) / 1000).append(" sec");
            return sb.toString();
        }
    }

    /* ********************Temp File Stuff*************************** */

    /**
     * Get a temporary file location for a test class.  The temporary location will be the same as the (compiled) class'
     * classpath location.  That is, if the class is {@code packageA.packageB.Clazz}, then the temporary file path
     * will be the location of the {@code packaeA} directory, or the location of the jar file the class is in (or
     * wherever else the class's "root" is located). This file will be set to be deleted when the jvm exits.
     *
     * @param testClass
     *        The test class to generate the temporary file for; the classpath root for this class will be used for the
     *        temp file location.
     *
     * @param fileName
     *        The temporary file name.
     *
     * @return a {@code File} object pointing to the temporary file.
     */
    public static File getTemporaryFile(Class testClass, String fileName) {
        return new File(getTemporaryFileDirectory(testClass),fileName);
    }
    /**
     * Get a temporary file location for a test class.  The temporary location will be the same as the (compiled) class'
     * classpath location, plus the specified sub-directories.  That is, if the class is {@code packageA.packageB.Clazz},
     * then the temporary file path will be the location of the {@code packaeA} directory, or the location of the jar file
     * the class is in (or wherever else the class's "root" is located), plus the sub-directories. If they do not exist,
     * the sub-directries will be created. This file and its subdirectories will be set to be deleted when the jvm exits.
     *
     * @param testClass
     *        The test class to generate the temporary file for; the classpath root for this class will be used for the
     *        temp file location.
     *
     * @param fileName
     *        The temporary file name.
     *
     * @param subDir
     *        The sub-directories to place the file in.
     *
     * @return a {@code File} object pointing to the temporary file.
     */
    public static File getTemporaryFile(Class testClass, File subDir, String fileName) {
        return new File(getTemporaryFileDirectory(testClass,subDir),fileName);
    }

    /**
     * Get a temporary directory location for a test class.  The temporary location will be the same as the (compiled) class's
     * classpath location, plus the specified sub-directories.  That is, if the class is {@code packageA.packageB.Clazz},
     * then the temporary path will be the location of the {@code packaeA} directory, or the location of the jar file the
     * class is in (or wherever else the class's "root" is located), plus the sub-directories. If they do not exist,
     * the sub-directries will be created. The sub-directories will be set to be deleted when the jvm exits.
     *
     * @param testClass
     *        The test class to locate the temporary directory for; the classpath root for this class will be used for the
     *        temp directory.
     *
     * @param subDir
     *        The sub-directories.
     *
     * @return a {@code File} object pointing to the temporary directory location.
     *
     * @throws RuntimeException if the directories specified by {@code subDir} could not (if necessary) be created.
     */
    public static File getTemporaryFileDirectory(Class testClass, File subDir) {
        String clsUri = testClass.getName().replace('.','/') + ".class";
        String clsPath = testClass.getClassLoader().getResource(clsUri).getPath();
        File tempDir = new File(new File(clsPath.substring(0, clsPath.length() - clsUri.length())).getParentFile(),"" + Thread.currentThread().getId());
        if (tempDir.mkdir())
            FileUtil.deleteDirOnExit(tempDir);
        File deleteOnExitDir = tempDir;
        if (subDir != null) {
            File tempDir2 = new File(tempDir,subDir.getPath());
            File tempDir3 = tempDir2;
            while (!tempDir2.equals(tempDir)) {
                tempDir3 = tempDir2;
                tempDir2 = tempDir2.getParentFile();
            }
            deleteOnExitDir = tempDir3;
            tempDir = new File(tempDir,subDir.getPath()); 
        }
        if (!tempDir.exists() && !tempDir.mkdirs())
            throw new RuntimeException("Failed to make temp directory: " + tempDir);
        if (subDir != null)
            FileUtil.deleteDirOnExit(deleteOnExitDir);
        return tempDir;
    }

    /**
     * Get a temporary directory location for a test class.  The temporary location will be the same as the (compiled) class's
     * classpath location.  That is, if the class is {@code packageA.packageB.Clazz}, then the temporary path
     * will be the location of the {@code packaeA} directory, or the location of the jar file the class is in (or
     * wherever else the class's "root" is located).
     *
     * @param testClass
     *        The test class to locate the temporary directory for; the classpath root for this class will be used for the
     *        temp directory.
     *
     * @return a {@code File} object pointing to the temporary directory location.
     */
    public static File getTemporaryFileDirectory(Class testClass) {
        return getTemporaryFileDirectory(testClass,null);
    }



    /*********************Almost Equals Stuff************************/
    
    /**
     * Assert that two float values are almost equal. Two non-zero values, <code>v1</code> and <code>v2</code>, are
     * almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @param delta
     *        The delta to use in the almost equals calculation.
     *
     * @param epsilon
     *        The epsilon to use in the almost equal calculation.
     */
    public void assertAlmostEquals(float expected, float actual, float delta, float epsilon) {
        if (!almostEquals(expected,actual,delta,epsilon))
            fail("Float values not almost equal, expected = " + expected + ", actual = " + actual + " (criteria = " + delta + " & " + epsilon + ")");
    }

     /**
     * Assert that two float values are almost equal within the default delta {@link com.pb.sawdust.util.MathUtil#DEFAULT_ALMOST_EQUALS_DELTA} and
      * the default epsilon {@link com.pb.sawdust.util.MathUtil#DEFAULT_EQUALS_ZERO_EPSILON}.  Two non-zero values, <code>v1</code> and <code>v2</code>,
      * are almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     */
    public void assertAlmostEquals(float expected, float actual) {
        assertAlmostEquals(expected,actual,(float) DEFAULT_ALMOST_EQUALS_DELTA,(float) DEFAULT_EQUALS_ZERO_EPSILON);
    }

    /**
     * Assert that two double values are almost equal. Two non-zero values, <code>v1</code> and <code>v2</code>, are
     * almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @param delta
     *        The delta to use in the almost equals calculation.
     *
     * @param epsilon
     *        The epsilon to use in the almost equal calculation.
     */
    public void assertAlmostEquals(double expected, double actual, double delta, double epsilon) {
        if (!almostEquals(expected,actual,delta,epsilon))
            fail("Double values not almost equal, expected = " + expected + ", actual = " + actual + " (criteria = " + delta + " & " + epsilon + ")");
    }

     /**
     * Assert that two double values are almost equal within the default delta {@link com.pb.sawdust.util.MathUtil#DEFAULT_ALMOST_EQUALS_DELTA} and
      * the defaul epsilon {@link com.pb.sawdust.util.MathUtil#DEFAULT_EQUALS_ZERO_EPSILON}.  Two non-zero values, <code>v1</code> and <code>v2</code>,
      * are almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     */
    public void assertAlmostEquals(double expected, double actual) {
        assertAlmostEquals(expected, actual, DEFAULT_ALMOST_EQUALS_DELTA, DEFAULT_EQUALS_ZERO_EPSILON);
    }

    /**
     * Assert that two values are almost equal. Two non-zero values, <code>v1</code> and <code>v2</code>, are
     * almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}. If the expected and actual values
     * are not both {@code Double} or both {@code Float} instances, then equality is determined using <code>expected.equals(actual)</code>.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @param delta
     *        The delta to use in the almost equals calculation.
     *
     * @param epsilon
     *        The epsilon to use in the almost equal calculation.
     */
    public void assertAlmostEquals(Object expected, Object actual, double delta, double epsilon) {
        if (expected != null && actual != null) {
            if (expected.getClass() == Float.class && actual.getClass() == Float.class) {
                assertAlmostEquals((float) (Float) expected,(float) (Float) actual,delta,epsilon);
                return;
            } else if (expected.getClass() == Double.class && actual.getClass() == Double.class) {
                assertAlmostEquals((double) (Double) expected,(double) (Double) actual,delta,epsilon);
                return;
            }
        }
        assertEquals(expected, actual);
    }

    /**
     * Assert if two values are almost equal within the default delta {@link com.pb.sawdust.util.MathUtil#DEFAULT_ALMOST_EQUALS_DELTA} and
     * the defaul epsilon {@link com.pb.sawdust.util.MathUtil#DEFAULT_EQUALS_ZERO_EPSILON}.  Two non-zero values, <code>v1</code> and <code>v2</code>,
     * are almost equal if {@code ||v1 - v2|/v1| < delta}. If either of the values are zero, then the values are almost
     * equal if the absolute value of the other value is less than {@code epsilon}. If the expected and
     * actual values are not both {@code Double} or both {@code Float} instances, then equality is determined using
     * <code>expected.equals(actual)</code>.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     */
    public void assertAlmostEquals(Object expected, Object actual) {
        assertAlmostEquals(expected, actual, DEFAULT_ALMOST_EQUALS_DELTA, DEFAULT_EQUALS_ZERO_EPSILON);
    }

    /**
     * Assert two float arrays are almost equal. The arrays are almost equal if they each have the same length and each
     * corresponding element, <code>e1</code> and <code>e2</code>, are almost equal (see {@link com.pb.sawdust.util.MathUtil#almostEquals(float, float, float, float)})
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @param delta
     *        The delta to use in the almost equals calculation.
     *
     * @param epsilon
     *        The epsilon to use in the almost equal calculation.
     */
    public void assertArrayAlmostEquals(float[] expected, float[] actual, double delta, double epsilon) {
        internalArrayAlmostEquals(expected, actual, delta, epsilon);
    }

    /**
     * Assert two float arrays are almost equal within the default delta {@link com.pb.sawdust.util.MathUtil#DEFAULT_ALMOST_EQUALS_DELTA} and
     * default epsilon {@link com.pb.sawdust.util.MathUtil#DEFAULT_EQUALS_ZERO_EPSILON}. The arrays are almost equal if they each have the same
     * length and each corresponding element, <code>e1</code> and <code>e2</code>, are almost equal: (see {@link com.pb.sawdust.util.MathUtil#almostEquals(float, float)})
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     */
    public void assertArrayAlmostEquals(float[] expected, float[] actual) {
        assertArrayAlmostEquals(expected, actual, DEFAULT_ALMOST_EQUALS_DELTA, DEFAULT_EQUALS_ZERO_EPSILON);
    }

    /**
     * Assert two float double are almost equal. The arrays are almost equal if they each have the same length and each
     * corresponding element, <code>e1</code> and <code>e2</code>, are almost equal (see {@link com.pb.sawdust.util.MathUtil#almostEquals(double, double, double, double)})
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @param delta
     *        The delta to use in the almost equals calculation.
     *
     * @param epsilon
     *        The epsilon to use in the almost equal calculation.
     */
    public void assertArrayAlmostEquals(double[] expected, double[] actual, double delta, double epsilon) {
        internalArrayAlmostEquals(expected, actual, delta, epsilon);
    }

    /**
     * Assert two double arrays are almost equal within the default delta {@link com.pb.sawdust.util.MathUtil#DEFAULT_ALMOST_EQUALS_DELTA} and
     * default epsilon {@link com.pb.sawdust.util.MathUtil#DEFAULT_EQUALS_ZERO_EPSILON}. The arrays are almost equal if they each have the same
     * length and each corresponding element, <code>e1</code> and <code>e2</code>, are almost equal: see {@link com.pb.sawdust.util.MathUtil#almostEquals(double, double)}.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     */
    public void assertArrayAlmostEquals(double[] expected, double[] actual) {
        assertArrayAlmostEquals(expected, actual, DEFAULT_ALMOST_EQUALS_DELTA, DEFAULT_EQUALS_ZERO_EPSILON);
    }

    /**
     * Assert two object arrays are almost equal. The arrays are almost equal if they each have the same length and each
     * corresponding element, <code>e1</code> and <code>e2</code>, are almost equal: see {@link com.pb.sawdust.util.MathUtil#almostEquals(java.lang.Object, java.lang.Object, double, double)}.
     * If two corresponding elements are not both {@code Float} or both {@code Double} instances then
     * {@code org.junit.Assert.assertEquals(expected,actual)} is called to determine equality.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     *
     * @param delta
     *        The delta to use in the almost equals calculation.
     *
     * @param epsilon
     *        The epsilon to use in the almost equal calculation.
     */
    public void assertArrayAlmostEquals(Object expected, Object actual, double delta, double epsilon) {
        if (expected != null && !isArray(expected))
            fail("Expected not an array");
        if (actual != null && !isArray(actual))
            fail("Actual not an array");
        internalArrayAlmostEquals(expected, actual, delta, epsilon);
    }

    /**
     * Assert two object arrays are almost equal within the default delta {@link com.pb.sawdust.util.MathUtil#DEFAULT_ALMOST_EQUALS_DELTA} and
     * the default epsilon {@link com.pb.sawdust.util.MathUtil#DEFAULT_EQUALS_ZERO_EPSILON}. The
     * arrays are almost equal if they each have the same length and each corresponding element, <code>e1</code> and
     * <code>e2</code>, are almost equal: see {@link com.pb.sawdust.util.MathUtil#almostEquals(java.lang.Object, java.lang.Object)}. If two
     * corresponding elements are not both {@code Float} or both {@code Double} instances then
     * {@code org.junit.Assert.assertEquals(expected,actual)} is called to determine equality.
     *
     * @param expected
     *        The expected value.
     *
     * @param actual
     *        The actual value.
     */
    public void assertArrayAlmostEquals(Object expected, Object actual) {
        assertArrayAlmostEquals(expected,actual,DEFAULT_ALMOST_EQUALS_DELTA,DEFAULT_EQUALS_ZERO_EPSILON);
    }

    private void internalArrayAlmostEquals(Object expected, Object actual, double delta, double epsilon) throws ArrayComparisonFailure {
        if (expected == actual)
            return;
        if (expected == null)
            fail("expected array was null");
        if (actual == null)
            fail("actual array was null");
        int actualsLength= Array.getLength(actual);
        int expectedsLength= Array.getLength(expected);
        if (actualsLength != expectedsLength)
            fail("array lengths differed, expected.length=" + expectedsLength + " actual.length=" + actualsLength);

        for (int i= 0; i < expectedsLength; i++) {
            Object expectedValue= Array.get(expected, i);
            Object actualValue= Array.get(actual, i);
            if (isArray(expectedValue) && isArray(actualValue)) {
                try {
                    internalArrayAlmostEquals(expectedValue,actualValue,delta,epsilon);
                } catch (ArrayComparisonFailure e) {
                    e.addDimension(i);
                    throw e;
                }
            } else
                try {
                    assertAlmostEquals(expectedValue,actualValue,delta,epsilon);
                } catch (AssertionError e) {
                    throw new ArrayComparisonFailure("", e, i);
                }
        }
    }

    private boolean isArray(Object expected) {
        return expected != null && expected.getClass().isArray();
    }
}
