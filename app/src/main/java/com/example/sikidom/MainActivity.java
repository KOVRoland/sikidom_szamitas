package com.example.sikidom;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerShapes, spinnerUnits;
    EditText input1, input2, input3;
    TextView result;
    Button btnCalc;

    String selectedUnit = "cm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spinnerShapes = findViewById(R.id.spinnerShapes);
        spinnerUnits = findViewById(R.id.spinnerUnits);
        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        input3 = findViewById(R.id.input3);
        result = findViewById(R.id.result);
        btnCalc = findViewById(R.id.btnCalc);

        String[] shapes = {"Válassz síkidomot...", "Négyzet", "Téglalap", "Kör", "Trapéz"};
        ArrayAdapter<String> shapesAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shapes);
        shapesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShapes.setAdapter(shapesAdapter);


        String[] units = {"Válassz mértékegységet...","mm", "cm", "dm", "m"};
        ArrayAdapter<String> unitsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnits.setAdapter(unitsAdapter);

        spinnerUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUnit = units[position];

                updateInputHints(spinnerShapes.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        spinnerShapes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateInputHints(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        btnCalc.setOnClickListener(v -> calculate());
    }
    private void updateInputHints(int position) {
        input1.setVisibility(View.GONE);
        input2.setVisibility(View.GONE);
        input3.setVisibility(View.GONE);

        switch (position) {
            case 1: // Négyzet
                input1.setHint("Oldal (" + selectedUnit + ")");
                input1.setVisibility(View.VISIBLE);
                break;

            case 2: // Téglalap
                input1.setHint("Hossz (" + selectedUnit + ")");
                input2.setHint("Szélesség (" + selectedUnit + ")");
                input1.setVisibility(View.VISIBLE);
                input2.setVisibility(View.VISIBLE);
                break;

            case 3: // Kör
                input1.setHint("Sugár (" + selectedUnit + ")");
                input1.setVisibility(View.VISIBLE);
                break;


            case 4: // Trapéz
                input1.setHint("a alap (" + selectedUnit + ")");
                input2.setHint("b alap (" + selectedUnit + ")");
                input3.setHint("Magasság (" + selectedUnit + ")");
                input1.setVisibility(View.VISIBLE);
                input2.setVisibility(View.VISIBLE);
                input3.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void calculate() {
        int pos = spinnerShapes.getSelectedItemPosition();
        double a = 0, b = 0, c = 0, h = 0, ker = 0, ter = 0;

        try {
            switch (pos) {
                case 1: // Négyzet
                    a = Double.parseDouble(input1.getText().toString());
                    ker = 4 * a;
                    ter = a * a;
                    break;

                case 2: // Téglalap
                    a = Double.parseDouble(input1.getText().toString());
                    b = Double.parseDouble(input2.getText().toString());
                    ker = 2 * (a + b);
                    ter = a * b;
                    break;

                case 3: // Kör
                    a = Double.parseDouble(input1.getText().toString());
                    ker = 2 * Math.PI * a;
                    ter = Math.PI * a * a;
                    break;



                case 4: // Trapéz
                    a = Double.parseDouble(input1.getText().toString());
                    b = Double.parseDouble(input2.getText().toString());
                    h = Double.parseDouble(input3.getText().toString());
                    double szar = Math.sqrt(Math.pow((a - b) / 2, 2) + h * h);
                    ker = a + b + 2 * szar;
                    ter = ((a + b) / 2) * h;
                    break;

                default:
                    result.setText("Kérlek, válassz síkidomot!");
                    return;
            }

            result.setText(
                    "Kerület: " + String.format("%.2f", ker)  + " " + selectedUnit +
                            "\nTerület: " + String.format("%.2f", ter) + " " + selectedUnit + "²"
            );

        } catch (NumberFormatException e) {
            result.setText("Hiba: tölts ki minden mezőt helyesen!");
        }
    }
}
