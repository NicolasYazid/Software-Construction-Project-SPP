module mx.uv.spp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens mx.uv.spp to javafx.fxml;
    opens mx.uv.spp.controladores.comun to javafx.fxml;
    opens mx.uv.spp.controladores.coordinador to javafx.fxml;
    opens mx.uv.spp.controladores.practicante to javafx.fxml;
    opens mx.uv.spp.controladores.profesor to javafx.fxml;
    exports mx.uv.spp;
}
