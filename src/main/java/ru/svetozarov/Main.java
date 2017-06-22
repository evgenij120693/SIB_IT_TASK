package ru.svetozarov;

import ru.svetozarov.Exception.ExceptionCountFloor;
import ru.svetozarov.Home.Home;
import ru.svetozarov.Lift.ElectricMotor.ElectricMotor;
import ru.svetozarov.Lift.Lift;
import ru.svetozarov.Passenger.Passenger;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Evgenij on 19.06.2017.
 */

public class Main {
    public static void main(String[] args) {

        /*ru.svetozarov.Lift.ElectricMotor.IElectricMotor electricMotor = new ElectricMotor(1000, 1500, 2000);
        ru.svetozarov.Lift.ILift smallLift = new Lift(electricMotor, 9, "пассажирский");


        Passenger passenger1 = new Passenger(1, 5, smallLift);



        Passenger passenger2 = new Passenger(8, 2, smallLift);



        Passenger passenger4 = new Passenger(1, 5, smallLift);



        Passenger passenger5 = new Passenger(3, 7, smallLift);


        Thread thread3 = new Thread( smallLift);
        thread3.start();
        passenger1.start();
        passenger2.start();
        passenger4.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        passenger5.start();
        /*Scanner in = new Scanner(System.in);
        while (true) {
            if (in.nextLine().equals("exit")) {
                break;
            } else {
                System.out.println("Введите ваше имя:");
                String name = in.nextLine();
                System.out.println("Введите начальный этаж: ");
                int startFloor = Integer.valueOf(in.nextLine());
                System.out.println("Введите конечный этаж:");
                int finalFloor = Integer.valueOf(in.nextLine());
                Thread tempThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Passenger passenger = new Passenger(startFloor, finalFloor, smallLift);
                    }
                });
            }
        }*/
        /*queueCalls.add(new Call(2,false, false));
        queueCalls.add(new Call(2,false, false));
        System.out.println(queueCalls.size());
        Call temp = queueCalls.remove(0);
        System.out.println(queueCalls.size());
        System.out.println(queueCalls.contains(temp));
        while (queueCalls.contains(temp))
            queueCalls.remove(temp);
        System.out.println(queueCalls.size());*/
        try {
            Home home = new Home(10, 99);
            home.startWork();
        } catch (ExceptionCountFloor exceptionCountFloor) {
            exceptionCountFloor.printStackTrace();
        }
    }
}
