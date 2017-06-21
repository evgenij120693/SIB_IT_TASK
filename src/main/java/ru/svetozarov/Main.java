package ru.svetozarov;

import ru.svetozarov.Exception.ExceptionCountFloor;
import ru.svetozarov.Home.Home;
import ru.svetozarov.Lift.Lift;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Evgenij on 19.06.2017.
 */
public class Main {
    public static void main(String[] args) {
       /* CopyOnWriteArrayList<Call> queueCalls = new CopyOnWriteArrayList<Call>();
        ru.svetozarov.Lift.ElectricMotor.IElectricMotor electricMotor = new ru.svetozarov.Lift.ElectricMotor(1000,1500, 2000);
        ru.svetozarov.Lift.ILift smallLift = new Lift(electricMotor,9, "пассажирский");


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                ru.svetozarov.Passenger.Passenger passenger1 = new ru.svetozarov.Passenger.Passenger(1,4,  smallLift);
                passenger1.callLift();
               // smallLift.callDownInner(passenger1.getFinalFloor());
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                ru.svetozarov.Passenger.Passenger passenger2 = new ru.svetozarov.Passenger.Passenger(8,2, smallLift);
                passenger2.callLift();
            }
        });

        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                ru.svetozarov.Passenger.Passenger passenger4 = new ru.svetozarov.Passenger.Passenger(1,5, smallLift);
                passenger4.callLift();
            }
        });
        Thread thread5 = new Thread(new Runnable() {
            @Override
            public void run() {
                ru.svetozarov.Passenger.Passenger passenger5 = new ru.svetozarov.Passenger.Passenger(3,7,  smallLift);
                passenger5.callLift();
            }
        });
        Thread thread3 = new Thread((Runnable) smallLift);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread5.start();
        Scanner in = new Scanner(System.in);
        while(true){
            if(in.nextLine().equals("exit")){
                break;
            }else{
                System.out.println("Введите ваше имя:");
                String name = in.nextLine();
                System.out.println("Введите начальный этаж: ");
                int startFloor = Integer.valueOf(in.nextLine());
                System.out.println("Введите конечный этаж:");
                int finalFloor = Integer.valueOf(in.nextLine());
                Thread tempThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ru.svetozarov.Passenger.Passenger passenger = new ru.svetozarov.Passenger.Passenger(startFloor, finalFloor,smallLift);
                    }
                });
            }
        }
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
            Home home = new Home(10, 4);
            home.startWork();
        } catch (ExceptionCountFloor exceptionCountFloor) {
            exceptionCountFloor.printStackTrace();
        }
    }
}
