package net.cubitum.fortylife;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;

import net.sourceforge.jeval.Evaluator;

public class CalculatorView extends TableLayout {

    EditText mEditText;
    static Evaluator mEvaluator= null;

    ImageButton mCalcDel;
    public CalculatorView(Context context) {
        super(context);

        loadViews();
    }

    public CalculatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.calculator, this);

        loadViews();
    }

    private void loadViews() {
        mEvaluator = new Evaluator();
        ViewGroup group = (ViewGroup) findViewById(R.id.tableLayout);
        setAllButtonListener(group);
        mEditText = (EditText) findViewById(R.id.editText);
        mCalcDel = (ImageButton) findViewById(R.id.calc_del);
        mCalcDel.setOnLongClickListener(new OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                mEditText.setText("");
                return true;
            }
        }
        );
    }

    public void setAllButtonListener(ViewGroup viewGroup) {

        View v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                setAllButtonListener((ViewGroup) v);
            } else if (v instanceof Button || v instanceof ImageButton) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calculatorButtonClicked(v);
                    }
                });
            }
        }
    }

    public static String evaluate(String input) {
        if(mEvaluator == null){
            mEvaluator = new Evaluator();
        }
        try {
            if (input.length() > 0) {
                if (isCalcSymbol(input.substring(input.length() - 1, input.length()))) {
                    input = input.substring(0, input.length() - 1);
                }
            }
            String resultString = mEvaluator.evaluate(input.replaceAll("÷", "/").replaceAll("×", "*").replaceAll("−", "-").replaceAll("\u221e", "Infinity"));

            if (resultString.equalsIgnoreCase("Infinity")) {
                return "\u221e";
            } else if (resultString.equalsIgnoreCase("NaN")) {
                return "NaN";
            }
            Double result = Double.parseDouble(resultString);
            return String.valueOf(result.intValue()).replaceAll("-", "−");
        } catch (Exception e) {
            return "Error";
        }
    }

    public void calculatorButtonClicked(View v) {
        StringBuilder newText;
        String newTextString;
        switch (v.getId()) {

            case R.id.calc_equals:
                newTextString = mEditText.getText().toString();
                mEditText.setText(evaluate(newTextString));
                break;
            case R.id.calc_del:
                newTextString = mEditText.getText().toString();
                if (newTextString.length() == 1 || newTextString.equalsIgnoreCase("NaN") || newTextString.equalsIgnoreCase("Error")) {
                    mEditText.setText("");
                } else {
                    newText = new StringBuilder();

                    newText.append(mEditText.getText().toString());
                    if (newText.length() >= 1) {
                        newText.delete(newText.length() - 1, newText.length());
                    }
                    mEditText.setText(newText.toString());
                }
                break;
            default:
                newText = new StringBuilder();
                Button vB = (Button) v;
                newText.append(mEditText.getText().toString());

                String newSymbol = vB.getText().toString();
                if (newText.length() > 0) {

                    //TODO: Messy code.. simplify algorithm
                    if (newText.length() != 1 && !isCalcSymbol(newSymbol)) {
                        newText.append(newSymbol);
                    } else if (newText.length() == 1 && !isCalcSymbol(newSymbol)) {
                        newText.append(newSymbol);
                    } else if (newText.length() == 1 && isCalcSymbol(newSymbol)) {
                        String lastSymbol = newText.substring(newText.length() - 1, newText.length());
                        if (!lastSymbol.contentEquals("−")) {
                            newText.append(newSymbol);
                        }
                    } else if (newText.length() != 1 && isCalcSymbol(newSymbol)) {
                        String lastSymbol = newText.substring(newText.length() - 1, newText.length());
                        if (isCalcSymbol(lastSymbol)) {
                            newText.replace(newText.length() - 1, newText.length(), newSymbol);
                        } else {
                            newText.append(newSymbol);
                        }
                    }
                } else if (newSymbol.contentEquals("−") || !isCalcSymbol(newSymbol)) {
                    newText.append(newSymbol);
                }
                mEditText.setText(newText.toString());

                break;
        }
    }

    public static boolean isCalcSymbol(String symbol) {
        if (symbol.contentEquals("+") || symbol.contentEquals("÷") || symbol.contentEquals("×") || symbol.contentEquals("−")) {
            return true;
        } else {
            return false;
        }
    }

}
