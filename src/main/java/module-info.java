module aa {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.apache.commons.codec;
    requires activation;

    exports com.alimajidi.aa.view;
    opens com.alimajidi.aa.view to javafx.fxml;
}