package Model;
 
import java.math.BigDecimal;
 
/** Implemented by any entity that has a calculable payment amount. */
public interface Payable {
    BigDecimal getAmountToPay();
}
