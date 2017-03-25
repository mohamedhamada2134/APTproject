import sun.security.krb5.internal.PAForUserEnc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by user on 3/11/2017.
 */
public class Main {

    public static void main(String[] args){


        PageCollector.body();



/*
        Runnable t1 = new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getId());
                PageCollector.body();
            }
        };

        Runnable t2 = new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getId());
                PageCollector.body();

            }
        };

        new Thread(t1).start();

        new Thread(t2).start();



*/



    }

}
