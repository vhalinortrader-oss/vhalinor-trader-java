package ai.trading.bot.modules.predictor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import ai.trading.bot.config.AppSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Java conversion of the original Python predictor structural test with improvements:
 * <ul>
 *   <li><b>Type‑safe inputs:</b> Uses a {@link MarketSnapshot} record instead of a loose dict.</li>
 *   <li><b>Type‑safe output:</b> {@link PredictionResult} is a record, avoiding string‑based attribute access.</li>
 *   <li><b>Grouped assertions:</b> {@code assertAll} ensures all checks are always evaluated.</li>
 *   <li><b>Fluent assertions:</b> AssertJ provides human‑readable failure messages.</li>
 *   <li><b>Self‑documenting:</b> {@code @DisplayName} describes the test’s purpose.</li>
 *   <li><b>Edge case readiness:</b> The test structure can easily be extended to verify boundary
 *       scores (exactly 0.0 or 1.0) and empty price series.</li>
 * </ul>
 */
class EnsemblePredictorTest {

    @Test
    @DisplayName("Predictor should return a valid structure with correct symbol, direction, and score bounds")
    void shouldReturnValidPredictionStructure() {
        // Arrange
        AppSettings settings = new AppSettings();
        EnsemblePredictor predictor = new EnsemblePredictor(settings);
        MarketSnapshot snapshot = new MarketSnapshot("BTC/USDT",
                List.of(100.0, 105.0, 107.0, 110.0, 115.0));

        // Act
        PredictionResult result = predictor.predict(snapshot);

        // Assert
        assertAll(
                () -> assertThat(result.symbol())
                        .as("Returned symbol should match input")
                        .isEqualTo("BTC/USDT"),
                () -> assertThat(result.direction())
                        .as("Direction must be one of long/short/hold")
                        .isIn("long", "short", "hold"),
                () -> assertThat(result.score())
                        .as("Score must be between 0.0 and 1.0 inclusive")
                        .isBetween(0.0, 1.0)
        );
    }
}
// ai/trading/bot/modules/predictor/MarketSnapshot.java
package ai.trading.bot.modules.predictor;

import java.util.List;

public record MarketSnapshot(String symbol, List<Double> prices) {}
// ai/trading/bot/modules/predictor/PredictionResult.java
package ai.trading.bot.modules.predictor;

public record PredictionResult(String symbol, String direction, double score) {}
