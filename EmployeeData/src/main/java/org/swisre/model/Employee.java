package org.swisre.model;


import java.time.LocalDate;

public class Employee {

    private int id;
    private String name;
    private String city;
    private String state;
    private String category;
    private Integer managerId;
    private double salary;
    private LocalDate doj;

    public Employee(){

    }

    public Employee(int id, String name, String city, String state, String category,
                    Integer managerId, double salary, LocalDate doj) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.category = category;
        this.managerId = managerId;
        this.salary = salary;
        this.doj = doj;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getDoj() {
        return doj;
    }

    public void setDoj(LocalDate doj) {
        this.doj = doj;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", category='" + category + '\'' +
                ", managerId=" + managerId +
                ", salary=" + salary +
                ", doj=" + doj +
                '}';
    }
}
