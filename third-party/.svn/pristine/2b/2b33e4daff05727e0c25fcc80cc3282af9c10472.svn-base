package com.pb.sawdust.util.concurrent;

import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The {@code DnCRecursivePrimitiveAction} provides simple abstract {@code RecursiveAction} implementations for dealing
 * with primitives. As the intention behind these classes is to avoid autoboxing, even procedures which naturally would
 * be executed through {@code DnCRecursiveTask}s should use one of these classes, as {@code DnCRecursiveTask} will
 * perform autoboxing in its combination of sub-task results.
 * <p>
 * To give an example of how a primitive task may be reformulated as an action, the following code example shows a
 * primitive action which takes a {@code double} array and computes the sum of the squares of its values (this is the
 * primitive equivalent of the example in {@link com.pb.sawdust.util.concurrent.DnCRecursiveTask}).  To get the
 * final result, {@code getResult()} would be called.
 *
 * <pre><code>
 *     public class ArraySquareSum extends DnCRecursiveDoubleAction {
 *         private final double[] array;
 *
 *         public ArraySquareSum(double[] array) {
 *             super(0,array.length);
 *             this.array = array;
 *         }
 *
 *         private ArraySquareSum(double[] array, long start, long length, DnCRecursiveTask&lt;Double&gt; next) {
 *             super(start,length,next);
 *             this.array = array;
 *         }
 *
 *         protected void computeAction(long start, long length) {
 *             long end = start + length;
 *             double sum = 0.0;
 *             for (long i = start; i < end; i++)
 *                 sum += array[i]*array[i];
 *             setResult(sum);
 *         }
 *
 *         protected DnCRecursiveDoubleAction getNextTask(long start, long length, DnCRecursiveDoubleAction next) {
 *             return new ArraySquareSum(array,start,length,next);
 *         }
 *
 *         protected boolean continueDividing(long newLength) {
 *             return getSurplusQueuedTaskCount() < 3;
 *         }
 *     }
 * </code></pre>
 *
 * The idiom for primitive actions is slightly different than that shown in the {@link com.pb.sawdust.util.concurrent.DnCRecursiveAction}
 * documentation, because of the additional framework set up to handle primitive tasks.  Specifically the {@code combineResults}
 * method must be overridden to (effectively) do nothing. Here is the example from the {@code DnCRecursiveAction} class
 * reformulated to deal with primitives:
 *
 * <pre><code>
 *     public class ArraySquarer extends DnCRecursiveDoubleAction {
 *         private final double[] array;
 *
 *         public ArraySquarer(double[] array) {
 *             super(0,array.length);
 *             this.array = array;
 *         }
 *
 *         private ArraySquarer(double[] array, long start, long length, DnCRecursiveAction next) {
 *             super(start,length,next);
 *             this.array = array;
 *         }
 *
 *         protected void computeAction(long start, long length) {
 *             long end = start + length;
 *             for (long i = start; i < end; i++)
 *                 array[i] = array[i]*array[i];
 *         }
 *
 *         protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
 *             return new ArraySquarer(array,start,length,next);
 *         }
 *
 *         protected boolean continueDividing(long newLength) {
 *             return getSurplusQueuedTaskCount() < 3;
 *         }
 *
 *         protected double combineResults(double thisResult, double subActionResult) {
 *             return thisResult; //stub that will be ignored in practice
 *         }
 *     }
 * </code></pre>
 *
 * @author crf <br/>
 *         Started: Sep 19, 2009 6:09:39 AM
 */
public class DnCRecursivePrimitiveAction {
    private DnCRecursivePrimitiveAction() {}

    /**
     * The {@code DnCRecursiveByteAction} class provides a "divide-and-conquer" {@code RecursiveTask} which deals with
     * {@code byte} values. By making it an "action", autoboxing (which would be forced by a {@code RecursiveTask<Byte>}'s
     * use of the object equivalent {@code Byte} class) is avoided and all tasks/actions can deal explicitly with
     * primitive values. For more information on primitive action divide-and-conquer strategies and idioms, see the
     * documentation for the {@link com.pb.sawdust.util.concurrent.DnCRecursivePrimitiveAction} class.
     */
    public static abstract class DnCRecursiveByteAction  extends RecursiveAction {
        private final long start;
        private long length;
        private final DnCRecursiveByteAction next;
        private byte result;

        /**
         * Constructor for the main action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        public DnCRecursiveByteAction(long start, long length) {
            this(start,length,null);
        }

        /**
         * Constructor for subactions. This should not be used to construct the main task passed to a fork-join pool.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         */
        protected DnCRecursiveByteAction(long start, long length, DnCRecursiveByteAction next) {
            this.start = start;
            this.length = length;
            this.next = next;
        }

        /**
         * The action that will be performed when the task has stopped dividing. This method should call {@link #setResult(byte)}
         * at some point to save result of the action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        abstract protected void computeAction(long start, long length);

        /**
         * Get a {@code DnCRecursiveByteAction} instance that is split from a "parent" (next) one. This should just call
         * the constructor which calls the {@code DnCRecursiveByteAction(long, long, DnCRecursiveByteAction)}
         * super constructor.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         *
         * @return a recursive action with the given start, length, and next parameters.
         */
        abstract protected DnCRecursiveByteAction getNextAction(long start, long length, DnCRecursiveByteAction next);

        /**
         * Determine whether the action should continue dividing, or perform its calculations. This determination might
         * involve both specifics about the problem at hand as well as those involving the fork join pool.
         *
         * @param length
         *        The length of this action.
         *
         * @return {@code true} if division should continue, {@code false} if the calculations should be performed instead.
         */
        abstract protected boolean continueDividing(long length);

        /**
         * Combine the results from two individual actions. By default they are added, though this method can be overridden
         * to perform any action.
         *
         * @param thisResult
         *        This action's result.
         *
         * @param subActionResult
         *        Te result of the sub-action.
         *
         * @return the result of combining the two results.
         */
        protected byte combineResults(byte thisResult, byte subActionResult) {
            return (byte) (thisResult+subActionResult);
        }

        /**
         * Set the result for this action. This should only be called in the {@link #computeAction(long, long)}
         * method.
         *
         * @param result
         *        The value of the result.
         */
        protected void setResult(byte result) {
            this.result = result;
        }

        /**
         * Wait for the action to finish, then get the value of the result.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception.
         */
        public byte getResult() {
            try {
                get();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Wait for the action to finish within some specified time limit, then get the value of the result. If the
         * timeout limit is exceeded, a runtime exception wrapping a {@code TimeoutException} is thrown.
         *
         * @param timeout
         *        The length of time to wait for the method to return.
         *
         * @param timeUnit
         *        The units for {@code timeout}.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception or.
         *                                  wrapping a {@code TimeoutException} if the computation did not return within {@code timeout}.
         */
        public byte getResult(long timeout, TimeUnit timeUnit) {
            try {
                get(timeout,timeUnit);
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Get the value of the result, without waiting for action to finish. This should only be called within (or within
         * the context of) the compute method (because calling {@code getResult()} would require {@code compute()} to finish,
         * and could deadlock), though only if the computation is known to have completed.
         *
         * @return the result.
         */
        protected byte getResultImmediately() {
            return result;
        }

        protected void compute() {
            long newLength = length;
            DnCRecursiveByteAction b = null;
            while (continueDividing(newLength)) {
                newLength = newLength >>> 1; //divide by dos
                if (newLength == length) //newLength < 2
                    break;
                b = getNextAction(start+newLength,length-newLength,b);
                b.fork();
                length = newLength;
            }
            try {
                computeAction(start,length);
            } catch (Exception | Error e) {
                completeExceptionally(e);
            }
            while (b != null) {
                if (b.tryUnfork())
                    b.compute();
                else
                    b.join();
                result = combineResults(result,b.result);
                b = b.next;
            }
        }
    }

    /**
     * The {@code DnCRecursiveShortAction} class provides a "divide-and-conquer" {@code RecursiveTask} which deals with
     * {@code short} values. By making it an "action", autoboxing (which would be forced by a {@code RecursiveTask<Short>}'s
     * use of the object equivalent {@code Short} class) is avoided and all tasks/actions can deal explicitly with
     * primitive values. For more information on primitive action divide-and-conquer strategies and idioms, see the
     * documentation for the {@link com.pb.sawdust.util.concurrent.DnCRecursivePrimitiveAction} class.
     */
    public static abstract class DnCRecursiveShortAction  extends RecursiveAction {
        private final long start;
        private long length;
        private final DnCRecursiveShortAction next;
        private short result;

        /**
         * Constructor for the main action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        public DnCRecursiveShortAction(long start, long length) {
            this(start,length,null);
        }

        /**
         * Constructor for subactions. This should not be used to construct the main task passed to a fork-join pool.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         */
        protected DnCRecursiveShortAction(long start, long length, DnCRecursiveShortAction next) {
            this.start = start;
            this.length = length;
            this.next = next;
        }

        /**
         * The action that will be performed when the task has stopped dividing. This method should call {@link #setResult(short)}
         * at some point to save result of the action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        abstract protected void computeAction(long start, long length);

        /**
         * Get a {@code DnCRecursiveShortAction} instance that is split from a "parent" (next) one. This should just call
         * the constructor which calls the {@code DnCRecursiveShortAction(long, long, DnCRecursiveShortAction)}
         * super constructor.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         *
         * @return a recursive action with the given start, length, and next parameters.
         */
        abstract protected DnCRecursiveShortAction getNextAction(long start, long length, DnCRecursiveShortAction next);

        /**
         * Determine whether the action should continue dividing, or perform its calculations. This determination might
         * involve both specifics about the problem at hand as well as those involving the fork join pool.
         *
         * @param length
         *        The length of this action.
         *
         * @return {@code true} if division should continue, {@code false} if the calculations should be performed instead.
         */
        abstract protected boolean continueDividing(long length);

        /**
         * Combine the results from two individual actions. By default they are added, though this method can be overridden
         * to perform any action.
         *
         * @param thisResult
         *        This action's result.
         *
         * @param subActionResult
         *        Te result of the sub-action.
         *
         * @return the result of combining the two results.
         */
        protected short combineResults(short thisResult, short subActionResult) {
            return (short) (thisResult+subActionResult);
        }

        /**
         * Set the result for this action. This should only be called in the {@link #computeAction(long, long)}
         * method.
         *
         * @param result
         *        The value of the result.
         */
        protected void setResult(short result) {
            this.result = result;
        }

        /**
         * Wait for the action to finish, then get the value of the result.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception.
         */
        public short getResult() {
            try {
                get();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Wait for the action to finish within some specified time limit, then get the value of the result. If the
         * timeout limit is exceeded, a runtime exception wrapping a {@code TimeoutException} is thrown.
         *
         * @param timeout
         *        The length of time to wait for the method to return.
         *
         * @param timeUnit
         *        The units for {@code timeout}.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception or.
         *                                  wrapping a {@code TimeoutException} if the computation did not return within {@code timeout}.
         */
        public short getResult(long timeout, TimeUnit timeUnit) {
            try {
                get(timeout,timeUnit);
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Get the value of the result, without waiting for action to finish. This should only be called within (or within
         * the context of) the compute method (because calling {@code getResult()} would require {@code compute()} to finish,
         * and could deadlock), though only if the computation is known to have completed.
         *
         * @return the result.
         */
        protected short getResultImmediately() {
            return result;
        }

        protected void compute() {
            long newLength = length;
            DnCRecursiveShortAction b = null;
            while (continueDividing(newLength)) {
                newLength = newLength >>> 1; //divide by dos
                if (newLength == length) //newLength < 2
                    break;
                b = getNextAction(start+newLength,length-newLength,b);
                b.fork();
                length = newLength;
            }
            try {
                computeAction(start,length);
            } catch (Exception | Error e) {
                completeExceptionally(e);
            }
            while (b != null) {
                if (b.tryUnfork())
                    b.compute();
                else
                    b.join();
                result = combineResults(result,b.result);
                b = b.next;
            }
        }
    }

    /**
     * The {@code DnCRecursiveIntAction} class provides a "divide-and-conquer" {@code RecursiveTask} which deals with
     * {@code int} values. By making it an "action", autoboxing (which would be forced by a {@code RecursiveTask<Int>}'s
     * use of the object equivalent {@code Int} class) is avoided and all tasks/actions can deal explicitly with
     * primitive values. For more information on primitive action divide-and-conquer strategies and idioms, see the
     * documentation for the {@link com.pb.sawdust.util.concurrent.DnCRecursivePrimitiveAction} class.
     */
    public static abstract class DnCRecursiveIntAction  extends RecursiveAction {
        private final long start;
        private long length;
        private final DnCRecursiveIntAction next;
        private int result;

        /**
         * Constructor for the main action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        public DnCRecursiveIntAction(long start, long length) {
            this(start,length,null);
        }

        /**
         * Constructor for subactions. This should not be used to construct the main task passed to a fork-join pool.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         */
        protected DnCRecursiveIntAction(long start, long length, DnCRecursiveIntAction next) {
            this.start = start;
            this.length = length;
            this.next = next;
        }

        /**
         * The action that will be performed when the task has stopped dividing. This method should call {@link #setResult(int)}
         * at some point to save result of the action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        abstract protected void computeAction(long start, long length);

        /**
         * Get a {@code DnCRecursiveIntAction} instance that is split from a "parent" (next) one. This should just call
         * the constructor which calls the {@code DnCRecursiveIntAction(long, long, DnCRecursiveIntAction)}
         * super constructor.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         *
         * @return a recursive action with the given start, length, and next parameters.
         */
        abstract protected DnCRecursiveIntAction getNextAction(long start, long length, DnCRecursiveIntAction next);

        /**
         * Determine whether the action should continue dividing, or perform its calculations. This determination might
         * involve both specifics about the problem at hand as well as those involving the fork join pool.
         *
         * @param length
         *        The length of this action.
         *
         * @return {@code true} if division should continue, {@code false} if the calculations should be performed instead.
         */
        abstract protected boolean continueDividing(long length);

        /**
         * Combine the results from two individual actions. By default they are added, though this method can be overridden
         * to perform any action.
         *
         * @param thisResult
         *        This action's result.
         *
         * @param subActionResult
         *        Te result of the sub-action.
         *
         * @return the result of combining the two results.
         */
        protected int combineResults(int thisResult, int subActionResult) {
            return thisResult+subActionResult;
        }

        /**
         * Set the result for this action. This should only be called in the {@link #computeAction(long, long)}
         * method.
         *
         * @param result
         *        The value of the result.
         */
        protected void setResult(int result) {
            this.result = result;
        }

        /**
         * Wait for the action to finish, then get the value of the result.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception.
         */
        public int getResult() {
            try {
                get();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Wait for the action to finish within some specified time limit, then get the value of the result. If the
         * timeout limit is exceeded, a runtime exception wrapping a {@code TimeoutException} is thrown.
         *
         * @param timeout
         *        The length of time to wait for the method to return.
         *
         * @param timeUnit
         *        The units for {@code timeout}.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception or.
         *                                  wrapping a {@code TimeoutException} if the computation did not return within {@code timeout}.
         */
        public int getResult(long timeout, TimeUnit timeUnit) {
            try {
                get(timeout,timeUnit);
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Get the value of the result, without waiting for action to finish. This should only be called within (or within
         * the context of) the compute method (because calling {@code getResult()} would require {@code compute()} to finish,
         * and could deadlock), though only if the computation is known to have completed.
         *
         * @return the result.
         */
        protected int getResultImmediately() {
            return result;
        }

        protected void compute() {
            long newLength = length;
            DnCRecursiveIntAction b = null;
            while (continueDividing(newLength)) {
                newLength = newLength >>> 1; //divide by dos
                if (newLength == length) //newLength < 2
                    break;
                b = getNextAction(start+newLength,length-newLength,b);
                b.fork();
                length = newLength;
            }
            try {
                computeAction(start,length);
            } catch (Exception | Error e) {
                completeExceptionally(e);
            }
            while (b != null) {
                if (b.tryUnfork())
                    b.compute();
                else
                    b.join();
                result = combineResults(result,b.result);
                b = b.next;
            }
        }
    }

    /**
     * The {@code DnCRecursiveLongAction} class provides a "divide-and-conquer" {@code RecursiveTask} which deals with
     * {@code long} values. By making it an "action", autoboxing (which would be forced by a {@code RecursiveTask<Long>}'s
     * use of the object equivalent {@code Long} class) is avoided and all tasks/actions can deal explicitly with
     * primitive values. For more information on primitive action divide-and-conquer strategies and idioms, see the
     * documentation for the {@link com.pb.sawdust.util.concurrent.DnCRecursivePrimitiveAction} class.
     */
    public static abstract class DnCRecursiveLongAction  extends RecursiveAction {
        private final long start;
        private long length;
        private final DnCRecursiveLongAction next;
        private long result;

        /**
         * Constructor for the main action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        public DnCRecursiveLongAction(long start, long length) {
            this(start,length,null);
        }

        /**
         * Constructor for subactions. This should not be used to construct the main task passed to a fork-join pool.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         */
        protected DnCRecursiveLongAction(long start, long length, DnCRecursiveLongAction next) {
            this.start = start;
            this.length = length;
            this.next = next;
        }

        /**
         * The action that will be performed when the task has stopped dividing. This method should call {@link #setResult(long)}
         * at some point to save result of the action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        abstract protected void computeAction(long start, long length);

        /**
         * Get a {@code DnCRecursiveLongAction} instance that is split from a "parent" (next) one. This should just call
         * the constructor which calls the {@code DnCRecursiveLongAction(long, long, DnCRecursiveLongAction)}
         * super constructor.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         *
         * @return a recursive action with the given start, length, and next parameters.
         */
        abstract protected DnCRecursiveLongAction getNextAction(long start, long length, DnCRecursiveLongAction next);

        /**
         * Determine whether the action should continue dividing, or perform its calculations. This determination might
         * involve both specifics about the problem at hand as well as those involving the fork join pool.
         *
         * @param length
         *        The length of this action.
         *
         * @return {@code true} if division should continue, {@code false} if the calculations should be performed instead.
         */
        abstract protected boolean continueDividing(long length);

        /**
         * Combine the results from two individual actions. By default they are added, though this method can be overridden
         * to perform any action.
         *
         * @param thisResult
         *        This action's result.
         *
         * @param subActionResult
         *        Te result of the sub-action.
         *
         * @return the result of combining the two results.
         */
        protected long combineResults(long thisResult, long subActionResult) {
            return thisResult+subActionResult;
        }

        /**
         * Set the result for this action. This should only be called in the {@link #computeAction(long, long)}
         * method.
         *
         * @param result
         *        The value of the result.
         */
        protected void setResult(long result) {
            this.result = result;
        }

        /**
         * Wait for the action to finish, then get the value of the result.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception.
         */
        public long getResult() {
            try {
                get();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Wait for the action to finish within some specified time limit, then get the value of the result. If the
         * timeout limit is exceeded, a runtime exception wrapping a {@code TimeoutException} is thrown.
         *
         * @param timeout
         *        The length of time to wait for the method to return.
         *
         * @param timeUnit
         *        The units for {@code timeout}.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception or.
         *                                  wrapping a {@code TimeoutException} if the computation did not return within {@code timeout}.
         */
        public long getResult(long timeout, TimeUnit timeUnit) {
            try {
                get(timeout,timeUnit);
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Get the value of the result, without waiting for action to finish. This should only be called within (or within
         * the context of) the compute method (because calling {@code getResult()} would require {@code compute()} to finish,
         * and could deadlock), though only if the computation is known to have completed.
         *
         * @return the result.
         */
        protected long getResultImmediately() {
            return result;
        }

        protected void compute() {
            long newLength = length;
            DnCRecursiveLongAction b = null;
            while (continueDividing(newLength)) {
                newLength = newLength >>> 1; //divide by dos
                if (newLength == length) //newLength < 2
                    break;
                b = getNextAction(start+newLength,length-newLength,b);
                b.fork();
                length = newLength;
            }
            try {
                computeAction(start,length);
            } catch (Exception | Error e) {
                completeExceptionally(e);
            }
            while (b != null) {
                if (b.tryUnfork())
                    b.compute();
                else
                    b.join();
                result = combineResults(result,b.result);
                b = b.next;
            }
        }
    }

    /**
     * The {@code DnCRecursiveFloatAction} class provides a "divide-and-conquer" {@code RecursiveTask} which deals with
     * {@code float} values. By making it an "action", autoboxing (which would be forced by a {@code RecursiveTask<Float>}'s
     * use of the object equivalent {@code Float} class) is avoided and all tasks/actions can deal explicitly with
     * primitive values. For more information on primitive action divide-and-conquer strategies and idioms, see the
     * documentation for the {@link com.pb.sawdust.util.concurrent.DnCRecursivePrimitiveAction} class.
     */
    public static abstract class DnCRecursiveFloatAction  extends RecursiveAction {
        private final long start;
        private long length;
        private final DnCRecursiveFloatAction next;
        private float result;

        /**
         * Constructor for the main action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        public DnCRecursiveFloatAction(long start, long length) {
            this(start,length,null);
        }

        /**
         * Constructor for subactions. This should not be used to construct the main task passed to a fork-join pool.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         */
        protected DnCRecursiveFloatAction(long start, long length, DnCRecursiveFloatAction next) {
            this.start = start;
            this.length = length;
            this.next = next;
        }

        /**
         * The action that will be performed when the task has stopped dividing. This method should call {@link #setResult(float)}
         * at some point to save result of the action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        abstract protected void computeAction(long start, long length);

        /**
         * Get a {@code DnCRecursiveFloatAction} instance that is split from a "parent" (next) one. This should just call
         * the constructor which calls the {@code DnCRecursiveFloatAction(long, long, DnCRecursiveFloatAction)}
         * super constructor.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         *
         * @return a recursive action with the given start, length, and next parameters.
         */
        abstract protected DnCRecursiveFloatAction getNextAction(long start, long length, DnCRecursiveFloatAction next);

        /**
         * Determine whether the action should continue dividing, or perform its calculations. This determination might
         * involve both specifics about the problem at hand as well as those involving the fork join pool.
         *
         * @param length
         *        The length of this action.
         *
         * @return {@code true} if division should continue, {@code false} if the calculations should be performed instead.
         */
        abstract protected boolean continueDividing(long length);

        /**
         * Combine the results from two individual actions. By default they are added, though this method can be overridden
         * to perform any action.
         *
         * @param thisResult
         *        This action's result.
         *
         * @param subActionResult
         *        Te result of the sub-action.
         *
         * @return the result of combining the two results.
         */
        protected float combineResults(float thisResult, float subActionResult) {
            return thisResult+subActionResult;
        }

        /**
         * Set the result for this action. This should only be called in the {@link #computeAction(long, long)}
         * method.
         *
         * @param result
         *        The value of the result.
         */
        protected void setResult(float result) {
            this.result = result;
        }

        /**
         * Wait for the action to finish, then get the value of the result.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception.
         */
        public float getResult() {
            try {
                get();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Wait for the action to finish within some specified time limit, then get the value of the result. If the
         * timeout limit is exceeded, a runtime exception wrapping a {@code TimeoutException} is thrown.
         *
         * @param timeout
         *        The length of time to wait for the method to return.
         *
         * @param timeUnit
         *        The units for {@code timeout}.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception or.
         *                                  wrapping a {@code TimeoutException} if the computation did not return within {@code timeout}.
         */
        public float getResult(long timeout, TimeUnit timeUnit) {
            try {
                get(timeout,timeUnit);
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Get the value of the result, without waiting for action to finish. This should only be called within (or within
         * the context of) the compute method (because calling {@code getResult()} would require {@code compute()} to finish,
         * and could deadlock), though only if the computation is known to have completed.
         *
         * @return the result.
         */
        protected float getResultImmediately() {
            return result;
        }

        protected void compute() {
            long newLength = length;
            DnCRecursiveFloatAction b = null;
            while (continueDividing(newLength)) {
                newLength = newLength >>> 1; //divide by dos
                if (newLength == length) //newLength < 2
                    break;
                b = getNextAction(start+newLength,length-newLength,b);
                b.fork();
                length = newLength;
            }
            try {
                computeAction(start,length);
            } catch (Exception | Error e) {
                completeExceptionally(e);
            }
            while (b != null) {
                if (b.tryUnfork())
                    b.compute();
                else
                    b.join();
                result = combineResults(result,b.result);
                b = b.next;
            }
        }
    }

    /**
     * The {@code DnCRecursiveDoubleAction} class provides a "divide-and-conquer" {@code RecursiveTask} which deals with
     * {@code double} values. By making it an "action", autoboxing (which would be forced by a {@code RecursiveTask<Double>}'s
     * use of the object equivalent {@code Double} class) is avoided and all tasks/actions can deal explicitly with
     * primitive values. For more information on primitive action divide-and-conquer strategies and idioms, see the
     * documentation for the {@link com.pb.sawdust.util.concurrent.DnCRecursivePrimitiveAction} class.
     */
    public static abstract class DnCRecursiveDoubleAction  extends RecursiveAction {
        private final long start;
        private long length;
        private final DnCRecursiveDoubleAction next;
        private double result;

        /**
         * Constructor for the main action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        public DnCRecursiveDoubleAction(long start, long length) {
            this(start,length,null);
        }

        /**
         * Constructor for subactions. This should not be used to construct the main task passed to a fork-join pool.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         */
        protected DnCRecursiveDoubleAction(long start, long length, DnCRecursiveDoubleAction next) {
            this.start = start;
            this.length = length;
            this.next = next;
        }

        /**
         * The action that will be performed when the task has stopped dividing. This method should call {@link #setResult(double)}
         * at some point to save result of the action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        abstract protected void computeAction(long start, long length);

        /**
         * Get a {@code DnCRecursiveDoubleAction} instance that is split from a "parent" (next) one. This should just call
         * the constructor which calls the {@code DnCRecursiveDoubleAction(long, long,DnCRecursiveDoubleAction)}
         * super constructor.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         *
         * @return a recursive action with the given start, length, and next parameters.
         */
        abstract protected DnCRecursiveDoubleAction getNextAction(long start, long length, DnCRecursiveDoubleAction next);

        /**
         * Determine whether the action should continue dividing, or perform its calculations. This determination might
         * involve both specifics about the problem at hand as well as those involving the fork join pool.
         *
         * @param length
         *        The length of this action.
         *
         * @return {@code true} if division should continue, {@code false} if the calculations should be performed instead.
         */
        abstract protected boolean continueDividing(long length);

        /**
         * Combine the results from two individual actions. By default they are added, though this method can be overridden
         * to perform any action.
         *
         * @param thisResult
         *        This action's result.
         *
         * @param subActionResult
         *        Te result of the sub-action.
         *
         * @return the result of combining the two results.
         */
        protected double combineResults(double thisResult, double subActionResult) {
            return thisResult+subActionResult;
        }

        /**
         * Set the result for this action. This should only be called in the {@link #computeAction(long, long)}
         * method.
         *
         * @param result
         *        The value of the result.
         */
        protected void setResult(double result) {
            this.result = result;
        }

        /**
         * Wait for the action to finish, then get the value of the result.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception.
         */
        public double getResult() {
            try {
                get();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Wait for the action to finish within some specified time limit, then get the value of the result. If the
         * timeout limit is exceeded, a runtime exception wrapping a {@code TimeoutException} is thrown.
         *
         * @param timeout
         *        The length of time to wait for the method to return.
         *
         * @param timeUnit
         *        The units for {@code timeout}.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception or.
         *                                  wrapping a {@code TimeoutException} if the computation did not return within {@code timeout}.
         */
        public double getResult(long timeout, TimeUnit timeUnit) {
            try {
                get(timeout,timeUnit);
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Get the value of the result, without waiting for action to finish. This should only be called within (or within
         * the context of) the compute method (because calling {@code getResult()} would require {@code compute()} to finish,
         * and could deadlock), though only if the computation is known to have completed.
         *
         * @return the result.
         */
        protected double getResultImmediately() {
            return result;
        }

        protected void compute() {
            long newLength = length;
            DnCRecursiveDoubleAction b = null;
            while (continueDividing(newLength)) {
                newLength = newLength >>> 1; //divide by dos
                if (newLength == length) //newLength < 2
                    break;
                b = getNextAction(start+newLength,length-newLength,b);
                b.fork();
                length = newLength;
            }
            try {
                computeAction(start,length);
            } catch (Exception | Error e) {
                completeExceptionally(e);
            }
            while (b != null) {
                if (b.tryUnfork())
                    b.compute();
                else
                    b.join();
                result = combineResults(result,b.result);
                b = b.next;
            }
        }
    }

    /**
     * The {@code DnCRecursiveBooleanAction} class provides a "divide-and-conquer" {@code RecursiveTask} which deals with
     * {@code boolean} values. By making it an "action", autoboxing (which would be forced by a {@code RecursiveTask<Boolean>}'s
     * use of the object equivalent {@code Boolean} class) is avoided and all tasks/actions can deal explicitly with
     * primitive values. For more information on primitive action divide-and-conquer strategies and idioms, see the
     * documentation for the {@link com.pb.sawdust.util.concurrent.DnCRecursivePrimitiveAction} class.
     */
    public static abstract class DnCRecursiveBooleanAction  extends RecursiveAction {
        private final long start;
        private long length;
        private final DnCRecursiveBooleanAction next;
        private boolean result;

        /**
         * Constructor for the main action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        public DnCRecursiveBooleanAction(long start, long length) {
            this(start,length,null);
        }

        /**
         * Constructor for subactions. This should not be used to construct the main task passed to a fork-join pool.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         */
        protected DnCRecursiveBooleanAction(long start, long length, DnCRecursiveBooleanAction next) {
            this.start = start;
            this.length = length;
            this.next = next;
        }

        /**
         * The action that will be performed when the task has stopped dividing. This method should call {@link #setResult(boolean)}
         * at some point to save result of the action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        abstract protected void computeAction(long start, long length);

        /**
         * Get a {@code DnCRecursiveBooleanAction} instance that is split from a "parent" (next) one. This should just call
         * the constructor which calls the {@code DnCRecursiveBooleanAction(long, long,DnCRecursiveBooleanAction)}
         * super constructor.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         *
         * @return a recursive action with the given start, length, and next parameters.
         */
        abstract protected DnCRecursiveBooleanAction getNextAction(long start, long length, DnCRecursiveBooleanAction next);

        /**
         * Determine whether the action should continue dividing, or perform its calculations. This determination might
         * involve both specifics about the problem at hand as well as those involving the fork join pool.
         *
         * @param length
         *        The length of this action.
         *
         * @return {@code true} if division should continue, {@code false} if the calculations should be performed instead.
         */
        abstract protected boolean continueDividing(long length);

        /**
         * Combine the results from two individual actions. By default they are "anded" (<code>thisResult & subActionResult</code>),
         * though this method can be overridden to perform any action.
         *
         * @param thisResult
         *        This action's result.
         *
         * @param subActionResult
         *        Te result of the sub-action.
         *
         * @return the result of combining the two results.
         */
        protected boolean combineResults(boolean thisResult, boolean subActionResult) {
            return thisResult & subActionResult;
        }

        /**
         * Set the result for this action. This should only be called in the {@link #computeAction(long, long)}
         * method.
         *
         * @param result
         *        The value of the result.
         */
        protected void setResult(boolean result) {
            this.result = result;
        }

        /**
         * Wait for the action to finish, then get the value of the result.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception.
         */
        public boolean getResult() {
            try {
                get();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Wait for the action to finish within some specified time limit, then get the value of the result. If the
         * timeout limit is exceeded, a runtime exception wrapping a {@code TimeoutException} is thrown.
         *
         * @param timeout
         *        The length of time to wait for the method to return.
         *
         * @param timeUnit
         *        The units for {@code timeout}.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception or.
         *                                  wrapping a {@code TimeoutException} if the computation did not return within {@code timeout}.
         */
        public boolean getResult(long timeout, TimeUnit timeUnit) {
            try {
                get(timeout,timeUnit);
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Get the value of the result, without waiting for action to finish. This should only be called within (or within
         * the context of) the compute method (because calling {@code getResult()} would require {@code compute()} to finish,
         * and could deadlock), though only if the computation is known to have completed.
         *
         * @return the result.
         */
        protected boolean getResultImmediately() {
            return result;
        }

        protected void compute() {
            long newLength = length;
            DnCRecursiveBooleanAction b = null;
            while (continueDividing(newLength)) {
                newLength = newLength >>> 1; //divide by dos
                if (newLength == length) //newLength < 2
                    break;
                b = getNextAction(start+newLength,length-newLength,b);
                b.fork();
                length = newLength;
            }
            try {
                computeAction(start,length);
            } catch (Exception | Error e) {
                completeExceptionally(e);
            }
            while (b != null) {
                if (b.tryUnfork())
                    b.compute();
                else
                    b.join();
                result = combineResults(result,b.result);
                b = b.next;
            }
        }
    }

    /**
     * The {@code DnCRecursiveCharAction} class provides a "divide-and-conquer" {@code RecursiveTask} which deals with
     * {@code char} values. By making it an "action", autoboxing (which would be forced by a {@code RecursiveTask<Char>}'s
     * use of the object equivalent {@code Char} class) is avoided and all tasks/actions can deal explicitly with
     * primitive values. For more information on primitive action divide-and-conquer strategies and idioms, see the
     * documentation for the {@link com.pb.sawdust.util.concurrent.DnCRecursivePrimitiveAction} class.
     */
    public static abstract class DnCRecursiveCharAction  extends RecursiveAction {
        private final long start;
        private long length;
        private final DnCRecursiveCharAction next;
        private char result;

        /**
         * Constructor for the main action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        public DnCRecursiveCharAction(long start, long length) {
            this(start,length,null);
        }

        /**
         * Constructor for subactions. This should not be used to construct the main task passed to a fork-join pool.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         */
        protected DnCRecursiveCharAction(long start, long length, DnCRecursiveCharAction next) {
            this.start = start;
            this.length = length;
            this.next = next;
        }

        /**
         * The action that will be performed when the task has stopped dividing. This method should call {@link #setResult(char)}
         * at some point to save result of the action.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         */
        abstract protected void computeAction(long start, long length);

        /**
         * Get a {@code DnCRecursiveCharAction} instance that is split from a "parent" (next) one. This should just call
         * the constructor which calls the {@code DnCRecursiveCharAction(long, long,DnCRecursiveCharAction)}
         * super constructor.
         *
         * @param start
         *        The starting point of the action.
         *
         * @param length
         *        The length of the action.
         *
         * @param next
         *        The recursive action this action was split from.
         *
         * @return a recursive action with the given start, length, and next parameters.
         */
        abstract protected DnCRecursiveCharAction getNextAction(long start, long length, DnCRecursiveCharAction next);

        /**
         * Determine whether the action should continue dividing, or perform its calculations. This determination might
         * involve both specifics about the problem at hand as well as those involving the fork join pool.
         *
         * @param length
         *        The length of this action.
         *
         * @return {@code true} if division should continue, {@code false} if the calculations should be performed instead.
         */
        abstract protected boolean continueDividing(long length);

        /**
         * Combine the results from two individual actions. There is no default for this method as the semantics are not obvious.
         *
         * @param thisResult
         *        This action's result.
         *
         * @param subActionResult
         *        Te result of the sub-action.
         *
         * @return the result of combining the two results.
         */
        abstract protected char combineResults(char thisResult, char subActionResult);

        /**
         * Set the result for this action. This should only be called in the {@link #computeAction(long, long)}
         * method.
         *
         * @param result
         *        The value of the result.
         */
        protected void setResult(char result) {
            this.result = result;
        }

        /**
         * Wait for the action to finish, then get the value of the result.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception.
         */
        public char getResult() {
            try {
                get();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Wait for the action to finish within some specified time limit, then get the value of the result. If the
         * timeout limit is exceeded, a runtime exception wrapping a {@code TimeoutException} is thrown.
         *
         * @param timeout
         *        The length of time to wait for the method to return.
         *
         * @param timeUnit
         *        The units for {@code timeout}.
         *
         * @return the result.
         *
         * @throws RuntimeInterruptedException if the current thread was interrupted while waiting.
         * @throws RuntimeWrappingException wrapping a {@code ExecutionException} if the computation threw an exception or.
         *                                  wrapping a {@code TimeoutException} if the computation did not return within {@code timeout}.
         */
        public char getResult(long timeout, TimeUnit timeUnit) {
            try {
                get(timeout,timeUnit);
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeWrappingException(e);
            }
        }

        /**
         * Get the value of the result, without waiting for action to finish. This should only be called within (or within
         * the context of) the compute method (because calling {@code getResult()} would require {@code compute()} to finish,
         * and could deadlock), though only if the computation is known to have completed.
         *
         * @return the result.
         */
        protected char getResultImmediately() {
            return result;
        }

        protected void compute() {
            long newLength = length;
            DnCRecursiveCharAction b = null;
            while (continueDividing(newLength)) {
                newLength = newLength >>> 1; //divide by dos
                if (newLength == length) //newLength < 2
                    break;
                b = getNextAction(start+newLength,length-newLength,b);
                b.fork();
                length = newLength;
            }
            try {
                computeAction(start,length);
            } catch (Exception | Error e) {
                completeExceptionally(e);
            }
            while (b != null) {
                if (b.tryUnfork())
                    b.compute();
                else
                    b.join();
                result = combineResults(result,b.result);
                b = b.next;
            }
        }
    }
}
