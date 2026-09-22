package com.example.Money_tracker.service;

import com.example.Money_tracker.model.Budget;
import com.example.Money_tracker.model.Expense;
import com.example.Money_tracker.model.User;
import com.example.Money_tracker.repository.BudgetRepository;
import com.example.Money_tracker.repository.ExpenseRepository;
import com.example.Money_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private UserRepository userRepository;

    public Expense saveExpense(Expense expense, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRemainingBalance() < expense.getAmount()) {
            throw new RuntimeException("Insufficient Balance! Current balance: " + user.getRemainingBalance());
        }

        String month = YearMonth.from(expense.getDate()).toString();
        Optional<Budget> budget = budgetRepository.findByUserEmailAndCategoryAndMonth(email, expense.getCategory(), month);

        if (budget.isPresent()) {
            double limit = budget.get().getLimitAmount();
            double totalSpentInMonth = expenseRepository.findByUserEmailAndCategory(email, expense.getCategory())
                    .stream()
                    .filter(e -> YearMonth.from(e.getDate()).toString().equals(month))
                    .mapToDouble(Expense::getAmount)
                    .sum();

            if (totalSpentInMonth + expense.getAmount() > limit) {
                throw new RuntimeException("Budget Exceeded for " + expense.getCategory() + "!");
            }
        }

        user.setRemainingBalance(user.getRemainingBalance() - expense.getAmount());
        userRepository.save(user);

        expense.setUserEmail(email);
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesByUser(String email) {
        return expenseRepository.findByUserEmail(email);
    }

    public Double getTotalSpent(String email) {
        return expenseRepository.findByUserEmail(email).stream()
                .mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> getSpendingByCategory(String email) {
        return expenseRepository.findByUserEmail(email).stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));
    }

    public boolean deleteExpense(String id) {
        Optional<Expense> expenseOpt = expenseRepository.findById(id);
        if (expenseOpt.isPresent()) {
            Expense expense = expenseOpt.get();

            User user = userRepository.findByEmail(expense.getUserEmail())
                    .orElseThrow(() -> new RuntimeException("User not found for this expense"));
            user.setRemainingBalance(user.getRemainingBalance() + expense.getAmount());
            userRepository.save(user);

            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public class ExpenseReviewsService{
        private static final BigDecimal LUXURY_THRESHOLD =new BigDecimal("5000.00");
        public List<FlaggedItem> reviewExpenses(List<Expense> expenses){
            List<FlaggedItem> flaggedItems=new ArrayList<>();
            for(Expense expense: expenses){
                String reason=null;

                if(expense.amount().compareTo(LUXURY_THRESHOLD)>0){
                    reason ="Ammount Exceed Normal Threshold";
                }
                else if(expense.description.toLowecase().matches(*.*(test|fake|placehold|secret|fund))){
                    reason="Fraud or unusal";
                }
                else if(expense.amount().compareTo(BigDecimal.Zero)<=0){
                    reason="Expense Amount must be greater than zero";
                }
                if(reason!=null){
                    flaggedItems.add(new FlaggedItem(
                        expense.id(),expense.description(),expense.amount,reason));
                    
                }
            }

            }
            return flaggedItems;
        }
    }

    public List<Expense> getMonthlyExpenses(String email, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        return expenseRepository.findByUserEmailAndDateBetween(email, yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    public Expense updateExpense(String id, Expense updatedExpense, String email) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double difference = updatedExpense.getAmount() - existing.getAmount();
        if (user.getRemainingBalance() < difference) {
            throw new RuntimeException("Insufficient balance to increase expense amount.");
        }

        user.setRemainingBalance(user.getRemainingBalance() - difference);
        userRepository.save(user);

        existing.setTitle(updatedExpense.getTitle());
        existing.setAmount(updatedExpense.getAmount());
        existing.setCategory(updatedExpense.getCategory());
        existing.setDate(updatedExpense.getDate());
        existing.setDescription(updatedExpense.getDescription());

        return expenseRepository.save(existing);
    }
}