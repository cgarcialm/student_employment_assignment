# Student Employment Assignment Optimization

This repository contains code and resources for solving the Student Employment Assignment problem using optimization techniques.

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

## Contributing:

Contributions to this project are welcome. If you have ideas for improvements or would like to report issues, please open a GitHub issue or submit a pull request.

## Acknowledgments:

This project is based on the OR-Tools library by Google.

Feel free to explore the code and resources in this repository to learn more about solving the Student Employment Assignment problem using optimization techniques.
