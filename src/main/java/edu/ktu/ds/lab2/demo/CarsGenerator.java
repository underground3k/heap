package edu.ktu.ds.lab2.demo;

import edu.ktu.ds.lab2.gui.ValidationException;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CarsGenerator {

    private boolean isStart = true;

    private Deque<Car> cars;

    public Car[] generateShuffle(int setSize, double shuffleCoef) throws ValidationException {
        return generateShuffle(setSize, setSize, shuffleCoef);
    }

    /**
     * @param setSize
     * @param setTake
     * @param shuffleCoef
     * @return Gražinamas setSize ilgio Automobilių masyvas
     * @throws ValidationException
     */
    public Car[] generateShuffle(int setSize,
                                 int setTake,
                                 double shuffleCoef) throws ValidationException {

        Car[] cars = IntStream.range(0, setSize)
                .mapToObj(i -> new Car.Builder().buildRandom())
                .toArray(Car[]::new);
        return shuffle(cars, setTake, shuffleCoef);
    }

    public Car takeCar() throws ValidationException {
        if (cars == null || cars.isEmpty()) {
            throw new ValidationException("Automobilių aibė tuščia", 4);
        }
        // Vieną kartą Automobilis imamas iš masyvo pradžios, kitą kartą - iš galo.
        isStart = !isStart;
        return isStart ? cars.pollFirst() : cars.pollLast();
    }

    private Car[] shuffle(Car[] cars, int amountToReturn, double shuffleCoef) throws ValidationException {
        if (cars == null) {
            throw new IllegalArgumentException("Automobilių nėra (null)");
        }
        if (amountToReturn < 0) {
            throw new ValidationException(String.valueOf(amountToReturn), 1);
        }
        if (cars.length < amountToReturn) {
            throw new ValidationException(cars.length + " >= " + amountToReturn, 2);
        }
        if (shuffleCoef < 0 || shuffleCoef > 1) {
            throw new ValidationException(String.valueOf(shuffleCoef), 3);
        }

        int amountToLeave = cars.length - amountToReturn;
        int startIndex = (int) (amountToLeave * shuffleCoef / 2);

        Car[] takeToReturn = Arrays.copyOfRange(cars, startIndex, startIndex + amountToReturn);
        Car[] takeToLeave = Stream
                .concat(Arrays.stream(Arrays.copyOfRange(cars, 0, startIndex)),
                        Arrays.stream(Arrays.copyOfRange(cars, startIndex + amountToReturn, cars.length)))
                .toArray(Car[]::new);

        Collections.shuffle(Arrays.asList(takeToReturn)
                .subList(0, (int) (takeToReturn.length * shuffleCoef)));
        Collections.shuffle(Arrays.asList(takeToLeave)
                .subList(0, (int) (takeToLeave.length * shuffleCoef)));

        this.cars = Arrays.stream(takeToLeave).collect(Collectors.toCollection(ArrayDeque::new));
        return takeToReturn;
    }
}
