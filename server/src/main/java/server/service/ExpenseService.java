package server.service;

import commons.Expense;
import org.springframework.stereotype.Service;
import server.database.ExpenseRepository;

import java.util.List;


@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;

    /**
     * Constructor for expenseService
     * @param expenseRepository the repository for this service
     */
    protected ExpenseService(ExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
    }

    /**
     * create an expense
     * @param expense the new Expense
     * @return the new Expense
     */
    public Expense createExpense(Expense expense){
        Expense expenseEntity = new Expense(expense.getTitle(), expense.getAmount(), 
        expense.getPayer(), expense.getOwers(), expense.getTag());
        return expenseEntity;
    }

    /**
     * getter for all the expenses 
     * @return all the expenses in the repository
     */
    public List<Expense> getExpenses(){
        return expenseRepository.findAll();
    }

    /**
     * getter for all the expenses related to an Event
     * @param id EventID all expenses should be related to
     * @return all the expenses in the repository related to this event
     */
    public List<Expense> getExpensesFromEvent(Integer id){
    // @Query("SELECT EXPENSE_ID FROM EVENT_EXPENSES WHERE EVENT_ID = " + id)
        return expenseRepository.findAll();
    }


    /**
     * a getter by id for an expense
     * @param id Integer ID of an expense
     * @return the found Expense
     */
    public Expense getExpenseById(Integer id){
        return expenseRepository.findById(id).orElse(null);
    }

    /**
     * delete a expense by id
     * @param id the id from the to-be-deleted expense
     * @return the deleted expense
     */
    public Expense deleteExpense(Integer id){
        Expense toDelete = getExpenseById(id);
        if (getExpenseById(id) != null)
            expenseRepository.delete(toDelete);
        return toDelete;
    }

    /**
     * update an existing expense
     * @param expense the updated version of the expense
     * @param id ID of the old expense
     * @return the new expense
     */
    public Expense updateExpense(Expense expense, Integer id){
        if (getExpenseById(id) == null)
            return null;
        deleteExpense(id);
        createExpense(expense);
        return expense;
    }

}
