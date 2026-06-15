package Services;
 
import Model.Employee;

/** Strategy interface for any deduction calculation. */
public interface DeductionCalculator {
    void   calculate(Employee employee);
    double getAmount();
    String getDescription();
}
