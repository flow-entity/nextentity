package io.github.nextentity.integration;

import io.github.nextentity.integration.config.ApplicationContexts;
import io.github.nextentity.integration.config.IntegrationTestContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class AutoIncrementInsertPerformanceVerificationTest {

    private static final int ITERATIONS = 100;

    @Test
    void shouldMeasureMysqlPerformanceForExistingInsertIsolationScenario() {
        AutoIncrementInsertIntegrationTest target = new AutoIncrementInsertIntegrationTest();
        List<IntegrationTestContext> contexts = ApplicationContexts.contexts().stream()
                .flatMap(ctx -> ctx.getBeansOfType(IntegrationTestContext.class).values().stream())
                .filter(context -> context.toString().startsWith("mysql-"))
                .sorted(Comparator.comparing(Object::toString))
                .toList();

        for (IntegrationTestContext context : contexts) {
            context.reset();

            List<Long> bodyDurations = new ArrayList<>(ITERATIONS);
            List<Long> resetDurations = new ArrayList<>(ITERATIONS);

            for (int i = 0; i < ITERATIONS; i++) {
                long bodyStart = System.nanoTime();
                target.shouldNotAffectExistingEntitiesOnNewInsert(context);
                bodyDurations.add(System.nanoTime() - bodyStart);

                long resetStart = System.nanoTime();
                context.reset();
                resetDurations.add(System.nanoTime() - resetStart);
            }

            logStats(context + " body", bodyDurations);
            logStats(context + " reset", resetDurations);
            logCombinedStats(context + " total", bodyDurations, resetDurations);
        }
    }

    private static void logCombinedStats(String label, List<Long> bodyDurations, List<Long> resetDurations) {
        List<Long> totals = new ArrayList<>(bodyDurations.size());
        for (int i = 0; i < bodyDurations.size(); i++) {
            totals.add(bodyDurations.get(i) + resetDurations.get(i));
        }
        logStats(label, totals);
    }

    private static void logStats(String label, List<Long> nanos) {
        long min = nanos.stream().mapToLong(Long::longValue).min().orElse(0L);
        long max = nanos.stream().mapToLong(Long::longValue).max().orElse(0L);
        double avg = nanos.stream().mapToLong(Long::longValue).average().orElse(0D);
        double total = nanos.stream().mapToLong(Long::longValue).sum();

        System.out.printf(
                "%s -> iterations=%d total=%.2fms avg=%.2fms min=%.2fms max=%.2fms%n",
                label,
                nanos.size(),
                total / 1_000_000D,
                avg / 1_000_000D,
                min / 1_000_000D,
                max / 1_000_000D
        );
    }
}
