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

//    /**
//     * Finds all expenses where 1 participant is the payer
//     * @param eventId event to look for expenses
//     * @param participantId id of the participant which is the payer
//     * @return list of expenses related to that participant
//     */
//    List<Expense> findExpensesParticipantPayer(Long eventId, Long participantId);
//
//    /**
//     * Finds all expenses where a participant is being paid for
//     * @param eventId event to look for expenses
//     * @param participantId id of the participant which is the ower
//     * @return list of expenses related to that participant
//     */
//    List<Expense> findExpensesParticipantOwer(Long eventId, Long participantId);
}