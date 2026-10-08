# Java Distillation Simulator
A Java console application that simulates binary distillation using the McCabe-Thiele method. It fits vapor-liquid equilibrium (VLE) data, calculates reflux ratios, estimates the required theoretical stages and feed-stage location, and determines distillate and bottoms flow rates.

## Features
- Quadratic regression of vapor-liquid equilibrium data.
- Minimum and operating reflux ratio calculations.
- Construction of rectifying, stripping, and feed operating lines.
- Estimation of theoretical stages and feed-stage location.
- Calculation of distillate and bottoms flow rates.
- Export of results and stage compositions to `output.txt`.

## Calculation Steps
1. Read the feed and product specifications from `input.txt`.
2. Load liquid and vapor compositions from `Equilibrium Data.txt`.
3. Fit a quadratic curve to the equilibrium data.
4. Calculate the minimum reflux ratio and set operating reflux to 1.5 times that value.
5. Construct the operating lines.
6. Perform McCabe-Thiele stepping from the distillate toward the bottoms.
7. Determine the theoretical stage count and feed-stage location.
8. Calculate product flow rates using material balances.
9. Print a summary and export detailed results.

## Inputs
Keep both input files in the same folder as the Java files:

- **`input.txt`**: feed quality, feed composition, distillate composition, bottoms composition, and feed flow rate—in that order, using `Label = Value`.
- **`Equilibrium Data.txt`**: two numeric columns containing liquid and vapor mole fractions, without a header.

Compositions are mole fractions, and flow rates are in kmol/h.

## How to Run
Requires a Java Development Kit (JDK). From the folder containing the Java and input files:

```bash
javac *.java
java Main
```

Enter **Y** to use the existing inputs or **N** to enter new values.

## Output
The program generates `output.txt` containing reflux ratios, stage count, feed-stage location, product flow rates, regression coefficients, operating-line parameters, and stage compositions.

## Reference Results
For the supplied methanol–ethanol case:

| Output                                 | Value      |
| ---                                    |    ---:    |
| Minimum reflux ratio                   |     2.64   |
| Operating reflux ratio                 |    3.96    |
| Theoretical stages, excluding reboiler |     20     |
| Feed stage                             |     10     |
| Distillate flow rate                   | 225 kmol/h |
| Bottoms flow rate                      | 275 kmol/h |

The original project compared these results with an independent Excel solution. Main outputs agreed at the reported precision, except the Excel feed stage was 11.

## Limitations
Uses equilibrium stages and a quadratic VLE fit. Input validation is incomplete, and the model does not account for actual tray efficiency or energy duties.

## Authors
Ziad Elhassan, Michael Mekalopolos, and Nassar Shakir.
