module com.example.diplom {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;
    requires commons.csv;
    opens com.example.diplom.Products;



    opens com.example.diplom to javafx.fxml;
    exports com.example.diplom;
    exports com.example.diplom.Controllers;
    opens com.example.diplom.Controllers to javafx.fxml;

}