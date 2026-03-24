https://chatgpt.com/share/69c1f279-4754-8006-a903-d5c057b6d293 
https://chatgpt.com/share/69b8d296-50f4-8000-b4fb-653c5fd292c6

STEP 1: Create MySQL Database + Tables

✅ 1. Create Database
CREATE DATABASE ecommerce_db;
USE ecommerce_db;

✅ 2. Create Tables
🔹 Customer Table
CREATE TABLE customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    firstname VARCHAR(100),
    email VARCHAR(100),
    phone_number VARCHAR(15)
);

🔹 Product Table
CREATE TABLE product (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    description VARCHAR(255),
    price DECIMAL(10,2),
    qty INT
);

🔹 OrderItem Table
CREATE TABLE order_item (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    customer_id INT,
    qty INT,
    total_amount DECIMAL(10,2),
    
    FOREIGN KEY (product_id) REFERENCES product(product_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

🔷 STEP 2: Insert Sample Data
🔹 Customers
INSERT INTO customer (firstname, email, phone_number) VALUES
('Aditya', 'adi@gmail.com', '9876543210'),
('Rahul', 'rahul@gmail.com', '9123456780'),
('Sneha', 'sneha@gmail.com', '9988776655');

🔹 Products
INSERT INTO product (name, description, price, qty) VALUES
('Laptop', 'Gaming Laptop', 75000, 10),
('Phone', 'Android Smartphone', 20000, 25),
('Headphones', 'Noise Cancelling', 3000, 50);

🔹 Order Items
INSERT INTO order_item (product_id, customer_id, qty, total_amount) VALUES
(1, 1, 1, 75000),
(2, 2, 2, 40000),
(3, 3, 3, 9000);


STEP 2: NetBeans Project Setup
✅ Create Project
File → New Project
Java Web → Web Application
Name: EcommerceMVC
✅ Add MySQL Dependency (pom.xml)
  
<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.0.33</version>
    </dependency>
</dependencies>
  
🔷 STEP 3: MVC Folder Structure (IMPORTANT)

Inside Source Packages, create:

com.mvc.model
com.mvc.dao
com.mvc.controller
  
🔷 STEP 4: MODEL (Customer.java)

📁 com.mvc.model.Customer

package com.mvc.model;

public class Customer {
    private int customerId;
    private String firstname;
    private String email;
    private String phoneNumber;

    // Getters & Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
🔷 STEP 5: DAO (CustomerDAO.java)

📁 com.mvc.dao.CustomerDAO

package com.mvc.dao;

import java.sql.*;
import java.util.*;
import com.mvc.model.Customer;

public class CustomerDAO {

    private String jdbcURL = "jdbc:mysql://localhost:3306/ecommerce_db";
    private String jdbcUsername = "root";
    private String jdbcPassword = "root"; // change if needed

    private static final String INSERT_CUSTOMER =
        "INSERT INTO customer (firstname, email, phone_number) VALUES (?, ?, ?)";

    private static final String SELECT_ALL =
        "SELECT * FROM customer";

    private static final String DELETE_CUSTOMER =
        "DELETE FROM customer WHERE customer_id=?";

    private static final String UPDATE_CUSTOMER =
        "UPDATE customer SET firstname=?, email=?, phone_number=? WHERE customer_id=?";

    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    // INSERT
    public void insertCustomer(Customer customer) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_CUSTOMER)) {
            ps.setString(1, customer.getFirstname());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhoneNumber());
            ps.executeUpdate();
        }
    }

    // SELECT ALL
    public List<Customer> selectAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFirstname(rs.getString("firstname"));
                c.setEmail(rs.getString("email"));
                c.setPhoneNumber(rs.getString("phone_number"));
                customers.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    // DELETE
    public void deleteCustomer(int id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_CUSTOMER)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // UPDATE
    public void updateCustomer(Customer customer) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_CUSTOMER)) {
            ps.setString(1, customer.getFirstname());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhoneNumber());
            ps.setInt(4, customer.getCustomerId());
            ps.executeUpdate();
        }
    }
}
🔷 STEP 6: CONTROLLER (Servlet)

📁 com.mvc.controller.CustomerServlet

package com.mvc.controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.util.*;

import com.mvc.dao.CustomerDAO;
import com.mvc.model.Customer;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

    private CustomerDAO dao;

    public void init() {
        dao = new CustomerDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null) action = "list";

            switch (action) {
                case "insert":
                    insertCustomer(request, response);
                    break;
                case "delete":
                    deleteCustomer(request, response);
                    break;
                case "update":
                    updateCustomer(request, response);
                    break;
                default:
                    listCustomers(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertCustomer(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Customer c = new Customer();
        c.setFirstname(request.getParameter("firstname"));
        c.setEmail(request.getParameter("email"));
        c.setPhoneNumber(request.getParameter("phone"));

        dao.insertCustomer(c);
        response.sendRedirect("customer");
    }

    private void listCustomers(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        List<Customer> list = dao.selectAllCustomers();
        request.setAttribute("list", list);
        RequestDispatcher rd = request.getRequestDispatcher("customer-list.jsp");
        rd.forward(request, response);
    }

    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        int id = Integer.parseInt(request.getParameter("id"));
        dao.deleteCustomer(id);
        response.sendRedirect("customer");
    }

    private void updateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Customer c = new Customer();
        c.setCustomerId(Integer.parseInt(request.getParameter("id")));
        c.setFirstname(request.getParameter("firstname"));
        c.setEmail(request.getParameter("email"));
        c.setPhoneNumber(request.getParameter("phone"));

        dao.updateCustomer(c);
        response.sendRedirect("customer");
    }
}
🔷 STEP 7: VIEW (JSP)
📄 customer-form.jsp (Insert + Update)
<form action="customer" method="get">
    <input type="hidden" name="action" value="insert"/>

    Name: <input type="text" name="firstname"/><br>
    Email: <input type="text" name="email"/><br>
    Phone: <input type="text" name="phone"/><br>

    <input type="submit" value="Add Customer"/>
</form>
📄 customer-list.jsp (Display + Delete)
<%@ page import="java.util.*, com.mvc.model.Customer" %>

<h2>Customer List</h2>

<table border="1">
<tr>
    <th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Action</th>
</tr>

<%
List<Customer> list = (List<Customer>) request.getAttribute("list");
for(Customer c : list){
%>
<tr>
    <td><%= c.getCustomerId() %></td>
    <td><%= c.getFirstname() %></td>
    <td><%= c.getEmail() %></td>
    <td><%= c.getPhoneNumber() %></td>
    <td>
        <a href="customer?action=delete&id=<%=c.getCustomerId()%>">Delete</a>
    </td>
</tr>
<% } %>
</table>

<a href="customer-form.jsp">Add New Customer</a>
