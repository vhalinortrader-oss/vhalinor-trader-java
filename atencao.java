/**
 * Módulo foco_total – Controle avançado de atenção e foco.
 * Fornece as classes {@link ControladorAtencao} e {@link FocoTotal} para
 * gerenciamento de estados de concentração.
 */
package foco_total;

import java.util.logging.Logger;

public class FocoTotalModule {
    public static final String VERSION = "1.0.0";
    private static final Logger LOGGER = Logger.getLogger(FocoTotalModule.class.getName());

    static {
        try {
            // Verify that essential classes are available at runtime
            Class.forName("foco_total.ControladorAtencao");
            Class.forName("foco_total.FocoTotal");
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Falha ao carregar classes do módulo foco_total: " + e.getMessage());
        }
    }

    // Private constructor to prevent instantiation
    private FocoTotalModule() {}
}
package foco_total;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Módulo foco_total – Controle avançado de atenção e foco.
 * Fornece as classes {@link ControladorAtencao} e {@link FocoTotal}
 * para gerenciamento de estados de concentração.
 *
 * <p>Versão: 1.0.0</p>
 */
public final class FocoTotalModule {

    /** Versão do módulo. */
    public static final String VERSION = "1.0.0";

    private static final Logger LOGGER =
            Logger.getLogger(FocoTotalModule.class.getName());

    static {
        // Verifica a disponibilidade das classes essenciais em tempo de execução
        // (útil quando partes do módulo são carregadas dinamicamente)
        try {
            Class.forName("foco_total.ControladorAtencao");
            Class.forName("foco_total.FocoTotal");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING,
                       "Falha ao carregar classes do módulo foco_total: {0}",
                       e.getMessage());
        }
    }

    /** Construtor privado para evitar instanciação. */
    private FocoTotalModule() {
        throw new UnsupportedOperationException("Classe de módulo, não instancie.");
    }
}