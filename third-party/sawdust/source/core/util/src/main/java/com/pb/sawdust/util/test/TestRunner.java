package com.pb.sawdust.util.test;


import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.util.ThreadTimer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.MethodRule;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * The {@code TestRunner} class provides a simple framework for running jUnit tests. The reason for using it (instead
 * of the built-in jUnit runners) is to incorporate additional test reporting: test class, test name, additional test
 * information, and test duration. This additional reporting is optional, and only available from tests which extend
 * {@code TestBase} or use the {@code @RunWith(TestRunner.TimerRunner)} annotation.
 * <p>
 * This class can be used to write information to output streams attached to this class.  By default, {@code System.out}
 * is attached, but it is also possible to attach (or detach) other {@code java.io.OutputStream}s.
 *
 * @author crf <br/>
 *         Started: Jul 18, 2008 7:54:49 PM
 */
public class TestRunner {

    private static final QueuedTextListener textListener = new QueuedTextListener();
    private static ThreadLocal<TestRunner> instance = new ThreadLocal<TestRunner>() {
        protected TestRunner initialValue() {
            return new TestRunner();
        }
    };

    private final JUnitCore juc;
    private final ThreadLocal<Boolean> detailedReporting = new ThreadLocal<Boolean>() {
                protected Boolean initialValue() {
                    return true;
                }
            };


    private TestRunner() {
        juc = new JUnitCore();
        juc.addListener(textListener);
    }

    /**
     * Geta a {@code TestRunner} instance.  There will be a separate instance for each thread.
     *
     * @return the test runner.
     */
    public static TestRunner getInstance() {
        return instance.get();
    }

    /**
     * Specify if the runner should provide detail reporting for the test classes that support it. Detailed reporting
     * includes listing the name of the test class being run, the name of the test method being run, the test
     * execution time, and any additional test information set through this class. If set to {@code false}, then the
     * default "dots" behavior of jUnit will be used. The class default is to have detailed reporting turned on.
     *
     * @param detailedReporting
     *        {@code true} if detailed reporting is to be used, {@code false} if not.
     */
    public void setDetailedReporting(boolean detailedReporting) {
        this.detailedReporting.set(detailedReporting);
    }

    /**
     * Run the tests in a series of specified classes. The test classes should each have at least one method annotated
     * with {@code @Test}. If the test classes inherit from {@code TestBase}, and detailed reporting is turned on, the
     * detailed reports for those tests will be printed.
     *
     * @param testClasses
     *        The classes holding the test methods to run.
     *
     * @return the result of the test run.
     */
    public Result runTests(Class ... testClasses) {
        return juc.run(testClasses);
    }

    private ThreadTimer timer = new ThreadTimer(TimeUnit.MILLISECONDS);
    private ThreadLocal<Long> currentTestDuration = new ThreadLocal<Long>();
    private ThreadLocal<Class<?>> currentTestClass = new ThreadLocal<Class<?>>();
    private ThreadLocal<String> additionalTestInformation =
            new ThreadLocal<String>() {
                protected String initialValue() {
                    return "";
                }
            };


    /**
     * Get the duration for the current test to finish. If the test has not finished, or if this method is called from a
     * thread in which no test is currently being run, then {@code null} will be returned.
     *
     * @return the duration of time it took to run the current test most recently finished in this thread.
     */
    public long getCurrentTestDuration() {
        return currentTestDuration.get();
    }

    /**
     * Set additional test information for tests run from this thread. This information is reported in the detailed
     * test reports created by this class. This information is only valid for tests run from the same thread that called
     * this method.
     *
     * @param additionalTestInformation
     *        Additional test information to use with this test.
     */
    public void setAdditionalTestInformation(String additionalTestInformation) {
        this.additionalTestInformation.set(additionalTestInformation);
    }

    /**
     * Get the additional test information last set from this thread through {@code setAdditionalTestInfomration}. If
     * that method has not been called, then an empty string will be returned.
     *
     * @return the additional test information preveiously set in this thread.
     */
    public String getAdditionalTestInformation() {
        return additionalTestInformation.get();
    }

    /**
     * Write some text to the output stream(s) attached to this class.
     *
     * @param text
     *        The text to write.
     */
    public static void writeTextToOutput(String text) {
        textListener.addTextToQueue(text);
    }

    /**
     * Write a line text to the output stream(s) attached to this class.
     *
     * @param text
     *        The text to write. A new line will be added to the printed text.
     */
    public static void writeLineToOutput(String text) {
        textListener.addLineToQueue(text);
    }

    /**
     * Flush the output to all of the streams attached to this class.
     */
    public static void flushOutput() {
        textListener.writeText();
    }

    /**
     * Attach an output stream for writing text to from this class.
     *
     * @param output
     *        The output stream to attach.
     */
    public static void addOutputStream(PrintStream output) {
        textListener.writers.add(output);
    }

    /**
     * Detach an ouput stream from this class.  It will be flushed upon removal.
     *
     * @param output
     *        The output stream to detach.
     */
    public static void removeOutputStream(PrintStream output) {
        if (textListener.writers.remove(output)) {
            Queue<String> tq = new LinkedList<String>();
            while (!tq.isEmpty()) {
                String text = tq.remove();
                output.print(text);
            }
        }
    }

    private static class QueuedTextListener extends RunListener {
        private final List<PrintStream> writers = new LinkedList<PrintStream>();
        private final ThreadLocal<Queue<String>> textQueue = new ThreadLocal<Queue<String>>() {
            protected Queue<String> initialValue() {
                return new LinkedList<String>();
            }
        };

        private QueuedTextListener() {
            writers.add(System.out);
	    }

        private void addTextToQueue(StringBuilder text) {
            addTextToQueue(text.toString());
        }

        private void addTextToQueue(String text) {
            textQueue.get().add(text);
        }

        private void addLineToQueue(StringBuilder text) {
            addLineToQueue(text.toString());
        }

        private void addLineToQueue(String text) {
            textQueue.get().add(text + FileUtil.getLineSeparator());
        }

        public void testRunStarted(Description description) {
            addLineToQueue("");
            addLineToQueue(getInstance().currentTestClass.get().getName());
        }

        public void testStarted(Description description) {
            addTextToQueue(".");
        }

        private void testFinishedActions(Description description) {
            StringBuilder line = new StringBuilder();
            if (getInstance().detailedReporting.get())
                line.append(" ").append(description.getMethodName()).append(getInstance().getAdditionalTestInformation())
                        .append(": ").append(String.valueOf(getInstance().getCurrentTestDuration())).append(" msec");
            addLineToQueue(line);
        }

        public void testIgnored(Description description) {
            addTextToQueue("I");
            getInstance().currentTestDuration.set(0L); //ignores take no time
            testFinishedActions(description);
        }

        public void testFinished(Description description) {
            testFinishedActions(description);
        }

        public void testFailure(Failure failure) {
            addTextToQueue("E");
        }

        public void testRunFinished(Result result) {
            addLineToQueue("");
		    addLineToQueue("Time: " + NumberFormat.getInstance().format(result.getRunTime() / 1000.0) + " sec");

            List<Failure> failures= result.getFailures();
            if (failures.size() > 0)
                addLineToQueue("Failure count: " + failures.size());

            int i= 1;
            for (Failure each : failures) {
                addLineToQueue((i++) + ") " + each.getTestHeader());
                addTextToQueue(each.getTrace());
            }
            addLineToQueue("");
            if (result.wasSuccessful()) {
                addLineToQueue("OK (" + result.getRunCount() + " test" + (result.getRunCount() == 1 ? "" : "s") + ")");
            } else {
                addLineToQueue("FAILURES!!!");
                addLineToQueue("Tests run: " + result.getRunCount() + ",  Failures: " + result.getFailureCount());
            }
            writeText();
        }

        private synchronized void writeText() {
            Queue<String> tq = textQueue.get();
            while (!tq.isEmpty()) {
                String text = tq.remove();
                for (PrintStream writer : writers)
                    writer.print(text);
            }
        }
    }

    /**
     * The {@code TimeRunner} class extends the base jUnit test runner to include test duration information.
     */
    public static class TimerRunner extends BlockJUnit4ClassRunner {
        public TimerRunner(Class<?> testClass) throws InitializationError {
           super(testClass);
            instance.get().currentTestClass.set(testClass); //annoying, but don't know how else to get this info back to runner
        }
        
        protected Statement methodBlock(FrameworkMethod method) {
            Object test;
            try {
                test = new ReflectiveCallable() {
                    @Override
                    protected Object runReflectiveCall() throws Throwable {
                        return createTest();
                    }
                }.run();
            } catch (Throwable e) {
                return new Fail(e);
            }
    
            Statement statement = methodInvoker(method, test);
            
            Test annotation = method.getAnnotation(Test.class);

            if (annotation != null && annotation.expected() != Test.None.class)
		        statement =  new ExpectException(statement,annotation.expected());
            
            if (annotation != null && annotation.timeout() > 0)
                statement = new Timeout((int) annotation.timeout()).apply(statement,method,test);

            statement = getTimingStatement(statement);
            
            List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(Before.class);
            if (befores.size() > 0)
		        statement = new RunBefores(statement,befores,test);

            List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(After.class);
            if (afters.size() > 0)
		        statement = new RunAfters(statement,afters,test);
            
            for (MethodRule each : getTestClass().getAnnotatedFieldValues(test,Rule.class, MethodRule.class))
                statement= each.apply(statement, method, test);
            
            return statement;
        }

        private Statement getTimingStatement(final Statement statement) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    instance.get().currentTestDuration.set(null);
                    instance.get().timer.startTimer();
                    try {
                        statement.evaluate();
                    } finally {
                        instance.get().currentTestDuration.set(instance.get().timer.endTimer());
                    }
                }
            };
        }
    }

//    /**
//     * The {@code TimerRunner} is a specialized jUnit runner which records the time durations for each individual test,
//     * as well as additional test information. This information is only recorded if the test class inherits from
//     * {@code TestBase}, or if the test class includes the {@code @RunWith(TestRunner.TimerRunner)} annotation.
//     */
//    public static class TimerRunner extends JUnit4ClassRunner {
//        private final Class<?> testClass;
//        private boolean firstTest = true;
//
//        /**
//         * Consturctor specifying the test class to apply the runner to.
//         *
//         * @param testClass
//         *        This runner's test class.
//         *
//         * @throws InitializationError if the class does not validate as a proper jUnit test class.
//         */
//        public TimerRunner(Class<?> testClass) throws InitializationError {
//            super(testClass);
//            this.testClass = testClass;
//        }
//
//        protected void invokeTestMethod(Method method, RunNotifier notifier) {
//            if (firstTest) {
//                if (instance.get().detailedReporting.get())
//                    instance.get().textListener.writeMessage("\n" + testClass.getName() + "\n");
//                firstTest = false;
//            }
//            Description description= methodDescription(method);
//            Object test;
//            try {
//                test= createTest();
//            } catch (InvocationTargetException e) {
//                notifier.testAborted(description, e.getCause());
//                return;
//            } catch (Exception e) {
//                notifier.testAborted(description, e);
//                return;
//            }
//            TestMethod testMethod= wrapMethod(method);
//    		new TimerMethodRoadie(method,test,testMethod,notifier,description).run();
//        }
//    }
//
//    private static class TimerMethodRoadie extends MethodRoadie {
//
//        private TimerMethodRoadie(Method testMethod, Object test, TestMethod method, RunNotifier notifier, Description description) {
//            super(test,method,notifier,description);
//            instance.get().currentTest.set(testMethod);
//            instance.get().currentTestDuration.set(null);
//        }
//
//        protected void runTestMethod() {
//            instance.get().timer.startTimer();
//            super.runTestMethod();
//            instance.get().currentTestDuration.set(instance.get().timer.endTimer());
//        }
//    }

    /**
     * Main method which runs all of the classes entered as arguments. The class names should be the fully qualified names.
     *
     * @param args
     *        The test classes to run.
     */
    public static void main(String ... args) {
        List<Class<?>> classes= new ArrayList<Class<?>>();
		List<Failure> missingClasses= new ArrayList<Failure>();
		for (String testClass : args) {
			try {
				classes.add(Class.forName(testClass));
			} catch (ClassNotFoundException e) {
				System.out.println("Could not find class: " + testClass);
				missingClasses.add(new Failure(Description.createSuiteDescription(testClass), e));
			}
        }
        Result result = instance.get().runTests(classes.toArray(new Class[classes.size()]));
		for (Failure each : missingClasses)
			result.getFailures().add(each);
        //clear out any remaining threads by explicity shutting down jvm
        System.exit(result.wasSuccessful() ? 0 : 1);
    }


}
