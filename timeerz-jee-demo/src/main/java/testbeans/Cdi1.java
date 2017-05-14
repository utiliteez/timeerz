package testbeans;

import com.github.utiliteez.timeerz.jee.annotation.Timeer;

import javax.inject.Named;

@Named
public class Cdi1 {

    @Timeer(value = "0/10 * * * * ?")
    public void cdiMethod() {
        System.out.println("cdiMethod in thread " + Thread.currentThread().getName());
        Utils.sleep(5000);
    }
}
