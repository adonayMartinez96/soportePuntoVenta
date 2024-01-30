package Kernel.loader;


/* 
 * 
 * Esta clase no la logro hacer funcionar, lo unico que que quiero 
 * es que reciba un proceso, ejecute ese proceso y mientras tanto me muestre una pantalla de carga
 * pero no se como hacerlo
 */

import javax.swing.SwingWorker;

public class LoadingDialog extends SwingWorker<Void, Void> {

    private Runnable proceso;

    public LoadingDialog(Runnable proceso) {
        this.proceso = proceso;
        this.setProgress(0);
    }

    @Override
    protected Void doInBackground() throws Exception {
        proceso.run(); 
        return null;
    }
}



