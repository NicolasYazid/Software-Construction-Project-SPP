module mx.uv.spp {
    requires javafx.controls;
    requires javafx.fxml;

    opens mx.uv.spp to javafx.fxml;
    exports mx.uv.spp;
}
