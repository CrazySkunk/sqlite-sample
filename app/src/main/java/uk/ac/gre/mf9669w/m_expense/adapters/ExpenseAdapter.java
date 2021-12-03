package uk.ac.gre.mf9669w.m_expense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.gre.mf9669w.m_expense.R;
import uk.ac.gre.mf9669w.m_expense.databinding.ExpenseBinding;
import uk.ac.gre.mf9669w.m_expense.models.Expense;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private final List<Expense> expenses;

    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpenseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.expense, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.bind(expenses.get(position));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final ExpenseBinding binding;

        public ExpenseViewHolder(View view) {
            super(view);
            binding = ExpenseBinding.bind(view);
        }

        public void bind(Expense expense) {
            binding.titleTv.setText(expense.getTypeOfExpense());
            binding.priceTv.setText(expense.getAmountOfExpense());
            binding.dateTv.setText(expense.getTimeOfExpense());
        }
    }
}
