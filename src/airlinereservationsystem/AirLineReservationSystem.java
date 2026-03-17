package airlinereservationsystem;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AirLineReservationSystem extends Application {

    // ── Color Palette ──────────────────────────────────────────────────────────
    private static final String BG_DARK       = "#0f172a";
    private static final String BG_CARD       = "#1e293b";
    private static final String BG_CARD2      = "#263548";
    private static final String ACCENT_BLUE   = "#3b82f6";
    private static final String ACCENT_GREEN  = "#22c55e";
    private static final String ACCENT_ORANGE = "#f97316";
    private static final String ACCENT_RED    = "#ef4444";
    private static final String TEXT_PRIMARY  = "#f1f5f9";
    private static final String TEXT_MUTED    = "#94a3b8";
    private static final String BORDER_COLOR  = "#334155";

    private final FlightManager manager = new FlightManager();

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        manager.loadFromFile();
        primaryStage.setTitle("Airline Reservation System");

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        root.getChildren().add(buildHeader());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-tab-min-height: 38px;" +
            "-fx-tab-max-height: 38px;"
        );
        styleTabPane(tabPane);

        Tab t1 = styledTab("  Add Flight  ", ACCENT_BLUE,   buildAddFlightPane());
        Tab t2 = styledTab(" View Flights ", "#8b5cf6",      buildViewFlightsPane());
        Tab t3 = styledTab("  Book Seat   ", ACCENT_GREEN,   buildBookSeatPane());
        Tab t4 = styledTab("   Cancel     ", ACCENT_RED,     buildCancelPane());

        tabPane.getTabs().addAll(t1, t2, t3, t4);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        root.getChildren().add(tabPane);

        Scene scene = new Scene(root, 800, 580);
        scene.setFill(Color.web(BG_DARK));
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // ── Header ──────────────────────────────────────────────────────────────────
    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 28, 18, 28));
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, #1e3a5f, #0f172a);" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label icon = new Label("\u2708");
        icon.setFont(Font.font("System", FontWeight.BOLD, 28));
        icon.setTextFill(Color.web(ACCENT_BLUE));

        VBox titleBox = new VBox(2);
        Label title = new Label("Airline Reservation System");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setTextFill(Color.web(TEXT_PRIMARY));
        Label sub = new Label("Manage flights, bookings and passengers");
        sub.setFont(Font.font("System", 12));
        sub.setTextFill(Color.web(TEXT_MUTED));
        titleBox.getChildren().addAll(title, sub);

        header.getChildren().addAll(icon, titleBox);
        return header;
    }

    // ── Tab Factory ─────────────────────────────────────────────────────────────
    private Tab styledTab(String text, String color, javafx.scene.Node content) {
        Tab tab = new Tab();
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web(color));
        tab.setGraphic(lbl);
        tab.setContent(content);
        return tab;
    }

    private void styleTabPane(TabPane tp) {
        tp.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-border-color: transparent;"
        );
    }

    // ── Add Flight ──────────────────────────────────────────────────────────────
    private ScrollPane buildAddFlightPane() {
        VBox outer = new VBox(16);
        outer.setPadding(new Insets(24));
        outer.setStyle("-fx-background-color: " + BG_DARK + ";");
        outer.getChildren().add(sectionHeading("Add New Flight", ACCENT_BLUE));

        GridPane grid = createGrid();

        TextField tfNumber      = styledField("e.g. AB123");
        TextField tfDeparture   = styledField("e.g. Cairo");
        TextField tfDestination = styledField("e.g. Dubai");
        TextField tfSeats       = styledField("e.g. 150");

        DatePicker dpDep = styledDatePicker();
        TextField  tfDepTime = styledField("HH:MM");
        HBox depBox = dateTimeBox(dpDep, tfDepTime);

        DatePicker dpArr = styledDatePicker();
        TextField  tfArrTime = styledField("HH:MM");
        HBox arrBox = dateTimeBox(dpArr, tfArrTime);

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton rbDomestic = styledRadio("Domestic", typeGroup, true);
        RadioButton rbInternational = styledRadio("International", typeGroup, false);
        HBox typeBox = new HBox(20, rbDomestic, rbInternational);
        typeBox.setAlignment(Pos.CENTER_LEFT);

        Label extraLabel = fieldLabel("Region:");
        TextField tfExtra = styledField("e.g. North");
        CheckBox cbVisa = new CheckBox("Visa Required");
        cbVisa.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 13px;");
        cbVisa.setVisible(false);

        typeGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean intl = n == rbInternational;
            extraLabel.setText(intl ? "Country:" : "Region:");
            cbVisa.setVisible(intl);
        });

        Label lblStatus = createStatusLabel();
        Button btnAdd = accentButton("Add Flight", ACCENT_BLUE);

        btnAdd.setOnAction(e -> {
            try {
                String num  = tfNumber.getText().trim();
                String dep  = tfDeparture.getText().trim();
                String dest = tfDestination.getText().trim();
                String extra = tfExtra.getText().trim();
                int seats   = Integer.parseInt(tfSeats.getText().trim());

                if (dpDep.getValue() == null || dpArr.getValue() == null) {
                    setError(lblStatus, "Please select departure and arrival dates."); return;
                }
                String depT = tfDepTime.getText().trim();
                String arrT = tfArrTime.getText().trim();
                if (num.isEmpty() || dep.isEmpty() || dest.isEmpty() || depT.isEmpty() || arrT.isEmpty() || extra.isEmpty()) {
                    setError(lblStatus, "All fields are required."); return;
                }
                String depDateTime = dpDep.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " " + depT;
                String arrDateTime = dpArr.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " " + arrT;

                String msg = rbDomestic.isSelected()
                    ? manager.addDomesticFlight(num, dep, dest, depDateTime, arrDateTime, seats, extra)
                    : manager.addInternationalFlight(num, dep, dest, depDateTime, arrDateTime, seats, extra, cbVisa.isSelected());
                manager.saveToFile();
                setSuccess(lblStatus, msg);
                clearFields(tfNumber, tfDeparture, tfDestination, tfDepTime, tfArrTime, tfSeats, tfExtra);
                dpDep.setValue(null); dpArr.setValue(null);
                cbVisa.setSelected(false);
            } catch (NumberFormatException ex) {
                setError(lblStatus, "Seats must be a valid number.");
            }
        });

        int row = 0;
        addRow(grid, row++, fieldLabel("Flight Number:"), tfNumber);
        addRow(grid, row++, fieldLabel("Departure:"),     tfDeparture);
        addRow(grid, row++, fieldLabel("Destination:"),   tfDestination);
        addRow(grid, row++, fieldLabel("Dep. Date & Time:"), depBox);
        addRow(grid, row++, fieldLabel("Arr. Date & Time:"), arrBox);
        addRow(grid, row++, fieldLabel("Seats:"),         tfSeats);
        addRow(grid, row++, fieldLabel("Flight Type:"),   typeBox);
        addRow(grid, row,   extraLabel,                   tfExtra);
        row++;
        grid.add(cbVisa, 1, row++);
        grid.add(btnAdd, 1, row++);
        grid.add(lblStatus, 1, row);

        outer.getChildren().add(wrapCard(grid));
        ScrollPane sp = new ScrollPane(outer);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: " + BG_DARK + "; -fx-background-color: " + BG_DARK + ";");
        return sp;
    }

    // ── View Flights ─────────────────────────────────────────────────────────────
    private VBox buildViewFlightsPane() {
        VBox outer = new VBox(16);
        outer.setPadding(new Insets(24));
        outer.setStyle("-fx-background-color: " + BG_DARK + ";");
        outer.getChildren().add(sectionHeading("Flight List", "#8b5cf6"));

        TableView<Flight> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-table-cell-border-color: " + BORDER_COLOR + ";" +
            "-fx-control-inner-background: " + BG_CARD + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 8;"
        );

        table.getColumns().addAll(
            tableCol("Flight #",    c -> new SimpleStringProperty(c.getValue().getFlightNumber())),
            tableCol("Departure",   c -> new SimpleStringProperty(c.getValue().getDeparture())),
            tableCol("Destination", c -> new SimpleStringProperty(c.getValue().getDestination())),
            tableCol("Dep. Time",   c -> new SimpleStringProperty(c.getValue().getDepartureTime())),
            tableCol("Arr. Time",   c -> new SimpleStringProperty(c.getValue().getArrivalTime())),
            tableColInt("Seats",    c -> new SimpleIntegerProperty(c.getValue().getAvailableSeats()).asObject()),
            tableCol("Type / Extra", c -> {
                Flight f = c.getValue();
                if (f instanceof DomesticFlight)
                    return new SimpleStringProperty("Domestic - " + ((DomesticFlight) f).getRegion());
                if (f instanceof InternationalFlight) {
                    InternationalFlight i = (InternationalFlight) f;
                    return new SimpleStringProperty("International - " + i.getCountry() + (i.isVisaRequired() ? " (Visa)" : ""));
                }
                return new SimpleStringProperty("-");
            })
        );

        Button btnRefresh = accentButton("Refresh", "#8b5cf6");
        btnRefresh.setOnAction(e -> table.getItems().setAll(manager.getFlights()));

        VBox.setVgrow(table, Priority.ALWAYS);
        outer.getChildren().addAll(btnRefresh, table);
        return outer;
    }

    // ── Book Seat ────────────────────────────────────────────────────────────────
    private ScrollPane buildBookSeatPane() {
        VBox outer = new VBox(16);
        outer.setPadding(new Insets(24));
        outer.setStyle("-fx-background-color: " + BG_DARK + ";");
        outer.getChildren().add(sectionHeading("Book a Seat", ACCENT_GREEN));

        GridPane grid = createGrid();
        TextField tfFlight = styledField("Flight number");
        TextField tfName   = styledField("Full name");
        TextField tfID     = styledField("Passenger ID");
        Label lblStatus    = createStatusLabel();

        Button btnBook = accentButton("Book Seat", ACCENT_GREEN);
        btnBook.setOnAction(e -> {
            String flight = tfFlight.getText().trim();
            String name   = tfName.getText().trim();
            String id     = tfID.getText().trim();
            if (flight.isEmpty() || name.isEmpty() || id.isEmpty()) {
                setError(lblStatus, "All fields are required."); return;
            }
            String msg = manager.bookSeat(flight, name, id);
            if (msg.startsWith("Seat booked")) { manager.saveToFile(); setSuccess(lblStatus, msg); }
            else setError(lblStatus, msg);
            clearFields(tfFlight, tfName, tfID);
        });

        int row = 0;
        addRow(grid, row++, fieldLabel("Flight Number:"),  tfFlight);
        addRow(grid, row++, fieldLabel("Passenger Name:"), tfName);
        addRow(grid, row++, fieldLabel("Passenger ID:"),   tfID);
        grid.add(btnBook,   1, row++);
        grid.add(lblStatus, 1, row);

        outer.getChildren().add(wrapCard(grid));
        ScrollPane sp = new ScrollPane(outer);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: " + BG_DARK + "; -fx-background-color: " + BG_DARK + ";");
        return sp;
    }

    // ── Cancel Reservation ───────────────────────────────────────────────────────
    private ScrollPane buildCancelPane() {
        VBox outer = new VBox(16);
        outer.setPadding(new Insets(24));
        outer.setStyle("-fx-background-color: " + BG_DARK + ";");
        outer.getChildren().add(sectionHeading("Cancel Reservation", ACCENT_RED));

        GridPane grid = createGrid();
        TextField tfID  = styledField("Passenger ID");
        Label lblStatus = createStatusLabel();

        Button btnCancel = accentButton("Cancel Reservation", ACCENT_RED);
        btnCancel.setOnAction(e -> {
            String id = tfID.getText().trim();
            if (id.isEmpty()) { setError(lblStatus, "Passenger ID is required."); return; }
            String msg = manager.cancelReservation(id);
            if (msg.startsWith("Reservation canceled")) { manager.saveToFile(); setSuccess(lblStatus, msg); }
            else setError(lblStatus, msg);
            tfID.clear();
        });

        int row = 0;
        addRow(grid, row++, fieldLabel("Passenger ID:"), tfID);
        grid.add(btnCancel, 1, row++);
        grid.add(lblStatus, 1, row);

        outer.getChildren().add(wrapCard(grid));
        ScrollPane sp = new ScrollPane(outer);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: " + BG_DARK + "; -fx-background-color: " + BG_DARK + ";");
        return sp;
    }

    // ── Widget Helpers ───────────────────────────────────────────────────────────
    private Label sectionHeading(String text, String color) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 17));
        lbl.setTextFill(Color.web(color));
        lbl.setPadding(new Insets(0, 0, 4, 0));
        lbl.setStyle(
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 0 0 2 0;" +
            "-fx-padding: 0 0 6 0;"
        );
        return lbl;
    }

    private VBox wrapCard(GridPane grid) {
        VBox card = new VBox(grid);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 18;"
        );
        return card;
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        ColumnConstraints col1 = new ColumnConstraints(150);
        col1.setHalignment(javafx.geometry.HPos.RIGHT);
        ColumnConstraints col2 = new ColumnConstraints(280, 280, Double.MAX_VALUE);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color: " + BG_CARD2 + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 7 10 7 10;" +
            "-fx-font-size: 13px;"
        );
        tf.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) tf.setStyle(tf.getStyle().replace("-fx-border-color: " + BORDER_COLOR, "-fx-border-color: " + ACCENT_BLUE));
            else         tf.setStyle(tf.getStyle().replace("-fx-border-color: " + ACCENT_BLUE,  "-fx-border-color: " + BORDER_COLOR));
        });
        return tf;
    }

    private Label fieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        lbl.setAlignment(Pos.CENTER_RIGHT);
        return lbl;
    }

    private RadioButton styledRadio(String text, ToggleGroup group, boolean selected) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(group);
        rb.setSelected(selected);
        rb.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 13px;");
        return rb;
    }

    private Button accentButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("System", FontWeight.BOLD, 13));
        btn.setTextFill(Color.WHITE);
        String base = "-fx-background-color: " + color + ";" +
                      "-fx-background-radius: 7;" +
                      "-fx-padding: 8 22 8 22;" +
                      "-fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(base + "-fx-effect: dropshadow(gaussian, " + color + ", 10, 0.4, 0, 0);"));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
        return btn;
    }

    private void addRow(GridPane grid, int row, Label label, javafx.scene.Node field) {
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private Label createStatusLabel() {
        Label lbl = new Label();
        lbl.setWrapText(true);
        lbl.setMaxWidth(280);
        lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        return lbl;
    }

    private void setSuccess(Label lbl, String msg) {
        lbl.setStyle("-fx-background-color: #14532d; -fx-background-radius: 5; -fx-padding: 6 10; -fx-text-fill: #86efac; -fx-font-size: 12px;");
        lbl.setText(msg);
    }

    private void setError(Label lbl, String msg) {
        lbl.setStyle("-fx-background-color: #450a0a; -fx-background-radius: 5; -fx-padding: 6 10; -fx-text-fill: #fca5a5; -fx-font-size: 12px;");
        lbl.setText(msg);
    }

    private void clearFields(TextField... fields) { for (TextField f : fields) f.clear(); }

    private DatePicker styledDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setPromptText("dd-MM-yyyy");
        dp.setPrefWidth(160);
        dp.setStyle(
            "-fx-background-color: " + BG_CARD2 + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );
        dp.getEditor().setStyle(
            "-fx-background-color: " + BG_CARD2 + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-size: 13px;"
        );
        return dp;
    }

    private HBox dateTimeBox(DatePicker dp, TextField timeTf) {
        timeTf.setPrefWidth(90);
        Label sep = new Label("at");
        sep.setTextFill(Color.web(TEXT_MUTED));
        sep.setFont(Font.font("System", 12));
        HBox box = new HBox(8, dp, sep, timeTf);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    // ── Table Helpers ────────────────────────────────────────────────────────────
    private <S> TableColumn<S, String> tableCol(String title,
            java.util.function.Function<TableColumn.CellDataFeatures<S, String>, javafx.beans.value.ObservableValue<String>> factory) {
        TableColumn<S, String> col = new TableColumn<>(title);
        col.setCellValueFactory(factory::apply);
        col.setStyle("-fx-alignment: CENTER; -fx-text-fill: " + TEXT_PRIMARY + ";");
        return col;
    }

    private <S> TableColumn<S, Integer> tableColInt(String title,
            java.util.function.Function<TableColumn.CellDataFeatures<S, Integer>, javafx.beans.value.ObservableValue<Integer>> factory) {
        TableColumn<S, Integer> col = new TableColumn<>(title);
        col.setCellValueFactory(factory::apply);
        col.setStyle("-fx-alignment: CENTER;");
        return col;
    }
}
