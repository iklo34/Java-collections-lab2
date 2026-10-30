package ua.kpi.comsys.collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionHistoryManagerTest {

    private ActionHistoryManager manager;

    @BeforeEach
    void setUp() {
        manager = new ActionHistoryManager(3);
    }

    @Nested
    @DisplayName("Конструктор")
    class Constructor {

        @ParameterizedTest
        @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
        void nonPositiveCapacityThrows(int capacity) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new ActionHistoryManager(capacity));
            assertEquals("maxCapacity must be greater than 0", ex.getMessage());
        }

        @Test
        void positiveCapacityCreatesEmptyHistory() {
            var m = assertDoesNotThrow(() -> new ActionHistoryManager(1));
            assertEquals(0, m.getHistorySize());
            assertTrue(m.getHistory().isEmpty());
        }
    }

    @Nested
    @DisplayName("performAction")
    class PerformAction {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n", "   "})
        void nullOrBlankActionThrows(String action) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> manager.performAction(action));
            assertEquals("Action cannot be null or blank", ex.getMessage());
        }

        @Test
        void invalidActionDoesNotChangeHistory() {
            manager.performAction("A");
            assertThrows(IllegalArgumentException.class, () -> manager.performAction(" "));
            assertEquals(List.of("A"), manager.getHistory());
        }

        @Test
        void newestActionGoesFirst() {
            manager.performAction("A");
            manager.performAction("B");
            assertEquals(List.of("B", "A"), manager.getHistory());
            assertEquals(2, manager.getHistorySize());
        }

        @Test
        void oldestActionIsEvictedWhenCapacityExceeded() {
            manager.performAction("A");
            manager.performAction("B");
            manager.performAction("C");
            manager.performAction("D");
            assertEquals(List.of("D", "C", "B"), manager.getHistory());
            assertEquals(3, manager.getHistorySize());
        }

        @Test
        void historyFilledExactlyToCapacityKeepsAllActions() {
            manager.performAction("A");
            manager.performAction("B");
            manager.performAction("C");
            assertEquals(List.of("C", "B", "A"), manager.getHistory());
        }

        @Test
        void capacityOfOneKeepsOnlyLatestAction() {
            var m = new ActionHistoryManager(1);
            m.performAction("A");
            m.performAction("B");
            assertEquals(List.of("B"), m.getHistory());
        }

        @Test
        void duplicateActionsAreAllowed() {
            manager.performAction("A");
            manager.performAction("A");
            assertEquals(List.of("A", "A"), manager.getHistory());
        }
    }

    @Nested
    @DisplayName("undo")
    class Undo {

        @Test
        void undoOnEmptyHistoryReturnsNull() {
            assertNull(manager.undo());
        }

        @Test
        void undoRemovesAndReturnsMostRecentAction() {
            manager.performAction("A");
            manager.performAction("B");
            manager.performAction("C");

            assertEquals("C", manager.undo());
            assertEquals("B", manager.undo());
            assertEquals(List.of("A"), manager.getHistory());
            assertEquals("A", manager.undo());
            assertNull(manager.undo());
            assertEquals(0, manager.getHistorySize());
        }

        @Test
        void undoAfterEvictionDoesNotReturnEvictedAction() {
            manager.performAction("A");
            manager.performAction("B");
            manager.performAction("C");
            manager.performAction("D");
            manager.undo();
            manager.undo();
            assertEquals("B", manager.undo());
            assertNull(manager.undo());
        }
    }

    @Nested
    @DisplayName("peekRecentAction")
    class PeekRecentAction {

        @Test
        void peekOnEmptyHistoryReturnsNull() {
            assertNull(manager.peekRecentAction());
        }

        @Test
        void peekReturnsMostRecentWithoutRemoving() {
            manager.performAction("A");
            manager.performAction("B");
            assertEquals("B", manager.peekRecentAction());
            assertEquals("B", manager.peekRecentAction());
            assertEquals(2, manager.getHistorySize());
        }
    }

    @Nested
    @DisplayName("getHistory")
    class GetHistory {

        @Test
        void emptyHistoryReturnsEmptyList() {
            assertTrue(manager.getHistory().isEmpty());
        }

        @Test
        void modifyingReturnedListDoesNotAffectInternalHistory() {
            manager.performAction("A");
            manager.performAction("B");

            List<String> copy = manager.getHistory();
            copy.clear();
            copy.add("X");

            assertEquals(List.of("B", "A"), manager.getHistory());
            assertEquals(2, manager.getHistorySize());
        }

        @Test
        void returnedListIsSnapshotNotLiveView() {
            manager.performAction("A");
            List<String> snapshot = manager.getHistory();
            manager.performAction("B");
            assertEquals(List.of("A"), snapshot);
        }
    }

    @Nested
    @DisplayName("clear")
    class Clear {

        @Test
        void clearRemovesAllActions() {
            manager.performAction("A");
            manager.performAction("B");
            manager.clear();

            assertEquals(0, manager.getHistorySize());
            assertTrue(manager.getHistory().isEmpty());
            assertNull(manager.peekRecentAction());
            assertNull(manager.undo());
        }

        @Test
        void historyIsUsableAfterClear() {
            manager.performAction("A");
            manager.clear();
            manager.performAction("B");
            assertEquals(List.of("B"), manager.getHistory());
        }

        @Test
        void clearOnEmptyHistoryIsSafe() {
            assertDoesNotThrow(manager::clear);
            assertEquals(0, manager.getHistorySize());
        }
    }
}
