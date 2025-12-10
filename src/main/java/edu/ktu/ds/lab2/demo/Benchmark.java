package edu.ktu.ds.lab2.demo;

import edu.ktu.ds.lab2.utils.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 1, timeUnit = TimeUnit.SECONDS)
public class Benchmark {

    @State(Scope.Benchmark)
    public static class FullSet {

        Car[] cars;
        Car[] carsForInsertion;

        FibonacciHeap<Car> carHeap;

        @Setup(Level.Iteration)
        public void generateElements(BenchmarkParams params) {
            cars = Benchmark.generateElements(Integer.parseInt(params.getParam("elementCount")));
            carsForInsertion = Benchmark.generateElements(1000);
        }

        @Setup(Level.Invocation)
        public void setupSets(BenchmarkParams params) {
            carHeap = new FibonacciHeap<>();
            addElements(cars, carHeap);
            carHeap.extractMin();
        }
    }


    @Param({"10000", "20000", "40000", "80000"})
    public int elementCount;

    Car[] cars;

    @Setup(Level.Iteration)
    public void generateElements() {
        cars = generateElements(elementCount);
    }

    static Car[] generateElements(int count) {
        return new CarsGenerator().generateShuffle(count, 1.0);
    }

    /// performs 1000 insert() operations
    @org.openjdk.jmh.annotations.Benchmark
    public void heapInsert(FullSet fullSet) {

        for(var i : fullSet.carsForInsertion){
            fullSet.carHeap.insert(i);
        }
    }
    /// performs 1000 findMin() operations
    @org.openjdk.jmh.annotations.Benchmark
    public void heapFindMin(FullSet fullSet) {

        for(int i = 0; i < 1000; i++){
            fullSet.carHeap.findMin();
        }
    }

    /// performs 1000 extractMin() operations
    @org.openjdk.jmh.annotations.Benchmark
    public void heapExtractMin(FullSet fullSet) {

        for(int i = 0; i < 1000; i++){
            fullSet.carHeap.extractMin();
        }
    }
    public static void addElements(Car[] carArray, FibonacciHeap<Car> carHeap) {
        for (Car car : carArray) {
            carHeap.insert(car);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmark.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
