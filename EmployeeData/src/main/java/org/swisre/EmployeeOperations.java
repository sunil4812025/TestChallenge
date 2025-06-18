package org.swisre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.swisre.model.Employee;
import org.swisre.model.OrgNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class EmployeeOperations {

    private static final Logger logger = LogManager.getLogger(EmployeeOperations.class);
    public List<Employee> generateSampleEmployees(int count) {
        String[] cities = {"Hyderabad", "Bangalore", "Chennai", "Mumbai", "Mangalore", "Delhi", "Pune"};
        String[] states = {"Telangana", "Karnataka", "Tamil Nadu", "Maharashtra", "Kerala"};
        String[] categories = {"employee", "manager", "Director"};
        Random random = new Random();

        List<Employee> list = new ArrayList<>();
        int baseId = 1000;
        Integer[] managerIds = {456, 789, 1020};

        for (int i = 0; i < count; i++) {
            int id = baseId + i;
            String name = "Emp" + id;
            String city = cities[random.nextInt(cities.length)];
            String state = states[random.nextInt(states.length)];
            String category = categories[random.nextInt(categories.length)];
            Integer managerId = category.equals("Director") ? null : managerIds[random.nextInt(managerIds.length)];
            double salary = 30000 + random.nextInt(120000);
            LocalDate doj = LocalDate.of(2018 + random.nextInt(8), 1 + random.nextInt(12), 1 + random.nextInt(28));
            list.add(new Employee(id, name, city, state, category, managerId, salary, doj));
        }

        return list;
    }

    public void writeToExcel(List<Employee> employees, String filename) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");

        String[] headers = {"id", "name", "city", "state", "category", "manager_id", "salary", "DOJ"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Employee emp : employees) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(emp.getId());
            row.createCell(1).setCellValue(emp.getName());
            row.createCell(2).setCellValue(emp.getCity());
            row.createCell(3).setCellValue(emp.getState());
            row.createCell(4).setCellValue(emp.getCategory());
            if (emp.getManagerId() != null) {
                row.createCell(5).setCellValue(emp.getManagerId());
            } else {
                row.createCell(5).setCellValue("null");
            }
            row.createCell(6).setCellValue(emp.getSalary());
            row.createCell(7).setCellValue(emp.getDoj().toString());
        }
        try(FileOutputStream fileOut = new FileOutputStream(filename)){
            workbook.write(fileOut);
            workbook.close();
            logger.info("Excel file written successfully: " + filename);
        }catch (Exception ex){
           throw new IOException("Exception occurred while writing data to Excel File: "+ex.getMessage());
        }
    }

    public List<Employee> readEmployeesFromExcel(String fileName) {
            List<Employee> employees = new ArrayList<>();
            try (InputStream inputStream = ExcelExtractor.class.getClassLoader().getResourceAsStream(fileName);
                 Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0);
                DataFormatter formatter = new DataFormatter();
                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Employee emp = new Employee();
                    emp.setId((int) row.getCell(0).getNumericCellValue());
                    emp.setName(formatter.formatCellValue(row.getCell(1)));
                    emp.setCity(formatter.formatCellValue(row.getCell(2)));
                    emp.setState(formatter.formatCellValue(row.getCell(3)));
                    emp.setCategory(formatter.formatCellValue(row.getCell(4)));

                    String managerIdStr = formatter.formatCellValue(row.getCell(5));
                    emp.setManagerId(managerIdStr.equalsIgnoreCase("null") ? null : Integer.parseInt(managerIdStr));
                    emp.setSalary(row.getCell(6).getNumericCellValue());
                    emp.setDoj(LocalDate.parse(formatter.formatCellValue(row.getCell(7))));

                    employees.add(emp);
                }

            } catch (Exception e) {
                logger.error("Exception while reading the employee data: "+e.getMessage());
            }
            return employees;
    }

    public List<Employee> getEligibleForGratuity(List<Employee> employees) {
        LocalDate today = LocalDate.now();
        return employees.stream()
                .filter(emp -> {
                    LocalDate doj = emp.getDoj();
                    long monthsWorked = ChronoUnit.MONTHS.between(doj, today);
                    return monthsWorked > 60;
                })
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployeesWithHigherSalaryThanManager(List<Employee> employees) {
        // Step 1: Map employeeId to salary
        Map<Integer, Double> idToSalary = employees.stream()
                .collect(Collectors.toMap(Employee::getId, Employee::getSalary));

        // Step 2: Filter employees whose salary > their manager's salary
        return employees.stream()
                .filter(emp -> emp.getManagerId() != null)
                .filter(emp -> {
                    Double managerSalary = idToSalary.get(emp.getManagerId());
                    return managerSalary != null && emp.getSalary() > managerSalary;
                })
                .collect(Collectors.toList());
    }

    public void buildHierarchyTree(List<Employee> employees, String outputFilePath) throws Exception {

        Map<Integer, List<Employee>> managerMap = employees.stream()
                .filter(e -> e.getManagerId() != null)
                .collect(Collectors.groupingBy(e -> e.getManagerId()));


        Employee rootEmp = employees.stream()
                .filter(e -> e.getManagerId() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No top-level manager found"));


        OrgNode root = buildNode(rootEmp, managerMap);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(outputFilePath), root);



        logger.info("Hierarchy written to: " + outputFilePath);
    }

    private OrgNode buildNode(Employee emp, Map<Integer, List<Employee>> managerMap) {
        OrgNode node = new OrgNode(emp.getId(), emp.getName(), emp.getCategory());
        List<Employee> reportees = managerMap.get(emp.getId());
        if (reportees != null) {
            for (Employee e : reportees) {
                node.reportees.add(buildNode(e, managerMap));
            }
        }
        return node;
    }

}
