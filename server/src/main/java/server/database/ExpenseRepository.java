package server.database;

import commons.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Returns all expenses related to an event
     * @param eventId event of all the expenses
     * @return list of expenses
     */
    List<Expense> findExpensesByEventId(Long eventId);

}