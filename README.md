# Student Employment Assignment Optimization

**Author:** Cecilia Garcia Lopez de Munain

This repository contains code and resources for solving the Student Employment Assignment problem using optimization techniques, as well as the optimization problem formulation.

[Problem Description](https://github.com/cgarcialm/student_employment_assignment/#problem-description)

[Features](https://github.com/cgarcialm/student_employment_assignment/#features)

[Usage](https://github.com/cgarcialm/student_employment_assignment/#usage)

[Contributing](https://github.com/cgarcialm/student_employment_assignment/#contributing)

[Solving the Student Employment Assignment Problem](https://github.com/cgarcialm/student_employment_assignment/#solving-the-student-employment-assignment-problem)

[Optimization Model](https://github.com/cgarcialm/student_employment_assignment/#optimization-model)

[Acknowledgments](https://github.com/cgarcialm/student_employment_assignment/#acknowledgments)

## Problem Description

The Student Employment Assignment problem is a combinatorial optimization problem where students need to be assigned to classes based on both student and professor preferences. The goal is to maximize overall satisfaction while respecting various constraints such as student availability and class time requirements.

## Features

- **Solver Implementation:** We provide a Java implementation of the optimization solver using OR-Tools.

- **Input Data:** Input data for professor and student preferences, class slots and time requirements, and student availability can be provided via text files.

- **Optimization Objective:** The objective of the optimization is to maximize overall satisfaction based on preferences.

- **Constraints:** The solver respects constraints such as class slots required, student availability, student total hours, and professor-student preferences.

## Usage

1. Clone the repository to your local machine:

   ```bash
   git clone https://github.com/cgarcialm/student_employment_assignment.git

2. **Install the Required Dependencies:** To get started with this project, you'll need to install the necessary dependencies, including OR-Tools. Follow the instructions below to install them:

   - Install OR-Tools for Java by following the installation guide provided [here](https://developers.google.com/optimization/install). Make sure to set up the necessary environment variables as instructed.

3. **Prepare Input Data:**

   - Prepare input data for professor and student preferences, class capacities, and student availability. You can create text files in the required format to provide this data. Sample input data files are provided in the repository's `input/` directory as examples.

4. **Run the Optimization Solver:**

   - Use the provided Java implementation to run the optimization solver with your input data. You can specify the input file paths and any additional parameters as needed.

5. **View Results:**

   - After running the solver, you can view the optimized student-class assignments and the corresponding satisfaction scores. The solver will provide information about which students are assigned to which classes.

## Contributing

Contributions to this project are welcome. If you have ideas for improvements or would like to report issues, please open a GitHub issue or submit a pull request.

## Solving the Student Employment Assignment Problem

The Student Employment Assignment problem can be modeled as a Mixed-Integer Programming (MIP) problem. In a MIP problem, we have both integer and continuous variables, and the objective is to find an optimal solution that maximizes or minimizes a given objective function while satisfying a set of constraints.

The optimization problem can be solved using various optimization solvers such as OR-Tools, CPLEX, or Gurobi.
## Optimization Model

### Variables
Let's define the decision variables for our optimization model:

$$x_{ij} =
\begin{cases}
1 & \text{if student } i \text{ is assigned to class } j \\
0 & \text{otherwise}
\end{cases}$$

Where:
- $i \in$ Set of students
- $j \in$ Set of classes

Assignment variables $x_{ij}$ are created only when the student's busy slots do not contain the required slots for the class. Specifically, $x_{ij}$ is created if and only if:

- $classSlot(j, d) \neq 0$ and $classSlot(j, d) \notin studentSlot(i, d)$ 

Where:
- $classSlot(j, d)$ represents the slot required by class $j$ on weekday $d$ (0 if none, 1 if afternoon, 2 if night).
- $studentSlot(i, d)$ represents the array of busy slots for student $i$ on weekday $d$ (empty if no slots are busy, contains 1 and/or 2 if those slots are busy).

This constraint ensures that assignment variables are created only when the student's busy slots do not contain the required slots for the class on the specified days.

### Objective Function
The objective is to maximize the overall satisfaction based on professor and student preferences, with each term weighted by a relative weight. We can formulate the objective function as follows:

Maximize: $$\sum_{i} \sum_{j} (\alpha \cdot \text{studentPreference}(i, j) + \beta \cdot \text{professorPreference}(i, j)) \cdot x_{ij}$$

Where:
- $α$ = Relative weight for student preferences
- $β$ = Relative weight for professor preferences
- $studentPreference(i, j)$ = Preference value of student $i$ for class $j$
- $professorPreference(i, j)$ = Preference value of professor for student $i$ in class $j$

### Constraints
We must satisfy the following constraints:

#### 1. At Most One Student per Class:

$$\sum_{i} x_{ij} \leq 1, \forall j$$

This constraint ensures that each class is assigned at most one student, preventing overassignment.

#### 2. Limiting Weekly Assignment Hours for Each Student:
For each student $i$, let:

$$\sum_{j} hoursPerClass(j) \cdot x_{ij} \leq 20, \forall i$$

Where:
- $hoursPerClass(j)$ represents the required hours for class $j$.

This constraint ensures that each student's total weekly assignment hours, considering the required hours for each class they are assigned to, is limited to 20 or less.

## Acknowledgments

This project is based on the OR-Tools library by Google.

Feel free to explore the code and resources in this repository to learn more about solving the Student Employment Assignment problem using optimization techniques.
