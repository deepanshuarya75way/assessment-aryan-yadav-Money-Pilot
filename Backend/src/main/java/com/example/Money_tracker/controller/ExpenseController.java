package com.example.Money_tracker.controller;

import com.example.Money_tracker.model.Expense;
import com.example.Money_tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;
    private final ExpenseReviewsService reviewService;

    public ExpenseController(ExpenseReviewsService reviewService){
        this.reviewService=reviewService;
    }
    @PostMapping("/review")
        public ResponseEntity<List<FlaggedItem>> reviewExpenses(@RequestBody List<Expense> expenses){
            List<FlaggedItems> flagged=reviewService.reviewExpenses(expenses);
            return ResponseEntity.ok(flagged);
        }
    @PatchMapping("/flags/{expenseId}")
             public ResponseEntity<String> handleFlaggedAction(
            @PathVariable String expenseId,
            @RequestBody Map<String, String> payload) {
        
        String action = payload.get("action");
        
        if (!"DISMISS".equalsIgnoreCase(action) && !"CONFIRMED".equalsIgnoreCase(action)) {
            return ResponseEntity.badRequest().body("Invalid action. Use 'DISMISS' or 'CONFIRMED'.");
        }
        return ResponseEntity.ok("flag status updated to" +action.toUpperCase()+"for Expense ID :"+expenseId)
    }

    

    @PostMapping("/add")
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Expense savedExpense = expenseService.saveExpense(expense, email);
        return ResponseEntity.ok(savedExpense);
    }



    @GetMapping("/all")
    public List<Expense> getMyExpenses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return expenseService.getExpensesByUser(email);
    }
    @GetMapping("/total")
    public ResponseEntity<Double> getTotal() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(expenseService.getTotalSpent(email));
    }

    @GetMapping("/category-summary")
    public ResponseEntity<Map<String, Double>> getCategorySummary() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(expenseService.getSpendingByCategory(email));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable String id) {
        boolean isDeleted = expenseService.deleteExpense(id);
        if (isDeleted) {
            return ResponseEntity.ok("Expense deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Error: Expense ID not found in database");
        }
    }
    @GetMapping("/monthly")
    public ResponseEntity<List<Expense>> getMonthlyExpenses(
            @RequestParam String month
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(
                expenseService.getMonthlyExpenses(email, month)
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable String id,
            @RequestBody Expense updatedExpense
    ) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        Expense expense =
                expenseService.updateExpense(
                        id,
                        updatedExpense,
                        email
                );

        return ResponseEntity.ok(expense);
    }
}