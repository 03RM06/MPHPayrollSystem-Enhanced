package Services;
 
import javax.swing.JOptionPane;
 
/** Shared UI-error helper for all service classes. */
public abstract class BaseService {
    protected void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
