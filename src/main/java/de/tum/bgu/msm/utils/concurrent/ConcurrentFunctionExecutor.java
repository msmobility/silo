package de.tum.bgu.msm.utils.concurrent;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.util.concurrent.ForkJoinPoolFactory;
import com.pb.sawdust.util.concurrent.IteratorAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class ConcurrentFunctionExecutor {

    private final List<ConcurrentFunction> functions = new ArrayList<>();

    public void addFunction(ConcurrentFunction function) {
        this.functions.add(function);
    }

    public void execute() {
        // Multi-threading code
        Function1<ConcurrentFunction, Void> threadableFunction = function -> {
            function.execute();
            return null;
        };

        Iterator<ConcurrentFunction> incomeChangeIterator = functions.listIterator();
        IteratorAction<ConcurrentFunction> itTask = new IteratorAction<>(incomeChangeIterator, threadableFunction);
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();
        pool.execute(itTask);
        itTask.waitForCompletion();
    }
}
