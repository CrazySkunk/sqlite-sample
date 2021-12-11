package uk.ac.gre.mf9669w.m_expense.adapters;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_FADE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

import uk.ac.gre.mf9669w.m_expense.R;
import uk.ac.gre.mf9669w.m_expense.database.DBHelper;
import uk.ac.gre.mf9669w.m_expense.databinding.ExpenseBinding;
import uk.ac.gre.mf9669w.m_expense.databinding.ExpenseDialogBinding;
import uk.ac.gre.mf9669w.m_expense.models.Expense;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private final List<Expense> expenses;
    private OnItemLongClick listener;
    private final Context context;

    public ExpenseAdapter(List<Expense> expenses, Context context) {
        this.expenses = expenses;
        this.context = context;
    }

    interface OnItemLongClick {
        void onLongClick(int position);
    }

    public void setOnLongClickListener(OnItemLongClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpenseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.expense, parent, false), context
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
        DBHelper database;
        private Expense expense;

        public ExpenseViewHolder(View view, Context context) {
            super(view);
            database = new DBHelper(context);
            binding = ExpenseBinding.bind(view);
            binding.getRoot().setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, binding.getRoot());
                popupMenu.getMenu().add("Edit");
                popupMenu.getMenu().add("Delete");
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Edit")) {
                        View exp = LayoutInflater.from(context).inflate(R.layout.expense_dialog, binding.getRoot());
                        ExpenseDialogBinding dialogBinding = ExpenseDialogBinding.bind(exp);
                        dialogBinding.typeOfExpenseEt.setText(expense.getTypeOfExpense());
                        dialogBinding.amountOfExpenseEt.setText(expense.getAmountOfExpense());
                        dialogBinding.timeOfExpenseEt.setText(expense.getTimeOfExpense());
                        dialogBinding.button.setOnClickListener(v1 -> {
                            Expense expensez = new Expense(
                                    expense.getId(),
                                    expense.getExpenseOwner(),
                                    Objects.requireNonNull(dialogBinding.typeOfExpenseEt.getText()).toString().trim(),
                                    Objects.requireNonNull(dialogBinding.amountOfExpenseEt.getText()).toString().trim(),
                                    Objects.requireNonNull(dialogBinding.timeOfExpenseEt.getText()).toString().trim()
                            );
                            boolean state = database.updateExpense(expensez);
                            if (state)
                                Snackbar.make(dialogBinding.getRoot(), "Expense updated successfully", Snackbar.LENGTH_LONG).setAnimationMode(ANIMATION_MODE_FADE).show();
                        });
                    } else if (item.getTitle().equals("Delete")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("M Expenses")
                                .setMessage("Are you sure you want o delete this expense?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    int deleted = database.deleteExpense(expense.getId());
                                    if (deleted == expense.getId()) {
                                        Snackbar.make(binding.getRoot(), "Expense deleted successfully", Snackbar.LENGTH_LONG).setAnimationMode(ANIMATION_MODE_FADE).show();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    return true;
                });
                popupMenu.show();
                return true;
            });
        }

        public void bind(Expense expense) {
            this.expense = expense;
            binding.titleTv.setText(expense.getTypeOfExpense());
            binding.priceTv.setText(expense.getAmountOfExpense());
            binding.dateTv.setText(expense.getTimeOfExpense());
        }
    }
}
