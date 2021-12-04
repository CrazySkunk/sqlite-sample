package uk.ac.gre.mf9669w.m_expense.models;

public class Expense {
    private String typeOfExpense,amountOfExpense,timeOfExpense;
    private Integer id;

    public Expense(Integer id,String typeOfExpense, String amountOfExpense, String timeOfExpense) {
        this.id = id;
        this.typeOfExpense = typeOfExpense;
        this.amountOfExpense = amountOfExpense;
        this.timeOfExpense = timeOfExpense;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
