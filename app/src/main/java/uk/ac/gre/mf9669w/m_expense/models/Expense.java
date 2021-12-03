package uk.ac.gre.mf9669w.m_expense.models;

public class Expense {
    private String typeOfExpense,amountOfExpense,timeOfExpense;

    public Expense(String typeOfExpense, String amountOfExpense, String timeOfExpense) {
        this.typeOfExpense = typeOfExpense;
        this.amountOfExpense = amountOfExpense;
        this.timeOfExpense = timeOfExpense;
    }

    public String getTypeOfExpense() {
        return typeOfExpense;
    }

    public void setTypeOfExpense(String typeOfExpense) {
        this.typeOfExpense = typeOfExpense;
    }

    public String getAmountOfExpense() {
        return amountOfExpense;
    }

    public void setAmountOfExpense(String amountOfExpense) {
        this.amountOfExpense = amountOfExpense;
    }

    public String getTimeOfExpense() {
        return timeOfExpense;
    }

    public void setTimeOfExpense(String timeOfExpense) {
        this.timeOfExpense = timeOfExpense;
    }
}
