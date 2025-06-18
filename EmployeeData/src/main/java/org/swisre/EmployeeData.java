package org.swisre;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.swisre.model.Employee;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class EmployeeData {
    private static final Logger logger = LogManager.getLogger(EmployeeData.class);

    private static final String SOURCE_FILE_NAME = "src/main/resources/Employees.xlsx";
    private static final String FILE_NAME = "Employees.xlsx";
    private static final String OUTPUT_JSON_FILE = "src/main/resources/output.json";
    public static void main(String[] args) {
        logger.info("Processing the Employee data");
        EmployeeOperations empOperations = new EmployeeOperations();
        try{
            //1. generate Employee data in Excel File
            List<Employee> employees = empOperations.generateSampleEmployees(50);
            empOperations.writeToExcel(employees, SOURCE_FILE_NAME);

            //2. read Data from Excel file
            List<Employee> employeeList = empOperations.readEmployeesFromExcel(FILE_NAME);

            //3. gratuity Check
            List<Employee> eligibleEmployees = empOperations.getEligibleForGratuity(employeeList);
            logger.info("Gratuity Eligible Employees: "+eligibleEmployees);

            //4.get Employees whose salary greater than manager's salary
            List<Employee> greaterEmpSalaryList = empOperations.getEmployeesWithHigherSalaryThanManager(employeeList);
            logger.info("Employees with Higher salary than managers: "+greaterEmpSalaryList);

            //5.creating Employee Hierarchy
            empOperations.buildHierarchyTree(employeeList,OUTPUT_JSON_FILE);



        }catch(Exception ex){
            logger.error("Exception while processing the employee data: "+ex.getMessage());
        }
    }


}