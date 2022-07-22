package de.tum.bgu.msm.data.vehicle;

public class Car implements Vehicle {


    private final int id;
    private final CarType carType;
    private int age;

    public Car(int id, CarType carType, int age) {
        this.id = id;
        this.carType = carType;
        this.age = age;
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public VehicleType getType() {
        return VehicleType.CAR;
    }

    @Override
    public int getAge() {
        return age;
    }

    public void increaseAgeByOne(){
        age++;
    }

    public CarType getCarType() {
        return carType;
    }
}
