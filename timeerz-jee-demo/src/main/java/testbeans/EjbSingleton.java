package testbeans;

import com.github.utiliteez.timeerz.jee.annotation.Timeer;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class EjbSingleton {

    /*
    @EJB
    EjbTxNever ejbTxNever;
    */

    @Timeer(value = "0/1 * * * * ?", exclusive = false)
    public void singletonMethod() {
        // ejbTxNever.ejbTxNever(); // test if timer-triggered call is transactional !

        System.out.println("IN singletonMethod in thread " + Thread.currentThread().getName());
        Utils.sleep(5000);
    }

}
