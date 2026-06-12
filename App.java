import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class App extends Application {

    private Stage primaryStage;
    private BorderPane mainLayout;
    private Scene mainScene;
    private static final String TACTICAL_CSS = App.class.getResource("aegis-theme.css").toExternalForm();
    private int currentLoggedInMemberId;

    // =========================================================
    // ENTRY POINT
    // =========================================================
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("ARTEMIS COMMAND");
        showAuthScreen();
        primaryStage.show();
    }

    // =========================================================
    // SCENE SWITCHER
    // =========================================================
    private void applyScene(javafx.scene.Parent root, double w, double h) {
        Scene scene = new Scene(root, w, h);
        scene.getStylesheets().add(TACTICAL_CSS);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    // =========================================================
    // SCREEN 1 — AUTH SCREEN (Login / Create Account)
    // =========================================================
    private void showAuthScreen() {
        HBox authRoot = new HBox();
        authRoot.getStyleClass().addAll("main-layout", "root");

        // ── LEFT PANE (60%) ─────────────────────────────────────────────
        VBox leftPane = new VBox(20);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.getStyleClass().add("login-branding-panel");
        leftPane.prefWidthProperty().bind(authRoot.widthProperty().multiply(0.6));
        HBox.setHgrow(leftPane, Priority.ALWAYS);

        Label hexIcon = new Label("⬡");
        hexIcon.getStyleClass().add("login-branding-title");
        hexIcon.setStyle("-fx-font-size: 80px;");

        Label brand = new Label("ARTEMIS OVERWATCH");
        brand.getStyleClass().add("login-branding-title");
        brand.setStyle("-fx-font-size: 48px;");

        Label sub = new Label("GLOBAL EMERGENCY RESPONSE SYSTEM");
        sub.getStyleClass().add("login-branding-subtitle");

        leftPane.getChildren().addAll(hexIcon, brand, sub);

        // ── RIGHT PANE (40%) ─────────────────────────────────────────────
        VBox rightPane = new VBox();
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setStyle("-fx-background-color: #1E293B;");
        rightPane.prefWidthProperty().bind(authRoot.widthProperty().multiply(0.4));

        VBox card = new VBox(16);
        card.getStyleClass().add("surface-card");
        card.setPadding(new Insets(36, 44, 36, 44));
        card.setMaxWidth(360);

        Label modeLbl = new Label("IDENTITY VERIFICATION");
        modeLbl.getStyleClass().add("subtitle-text");

        Label userLbl = new Label("USERNAME");
        userLbl.getStyleClass().add("white-subtext");
        TextField userField = new TextField();
        userField.getStyleClass().add("text-field");
        userField.setPromptText("Username");

        Label passLbl = new Label("PASSWORD");
        passLbl.getStyleClass().add("white-subtext");
        PasswordField passField = new PasswordField();
        passField.getStyleClass().add("text-field");
        passField.setPromptText("Password");

        Label statusLbl = new Label("");
        statusLbl.getStyleClass().add("error-message");
        statusLbl.setWrapText(true);

        Button loginBtn = new Button("LOGIN");
        loginBtn.getStyleClass().add("button-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> {
            String u = userField.getText().trim();
            String p = passField.getText();
            if (u.isEmpty() || p.isEmpty()) {
                setStatus(statusLbl, "error-message", "Username and password are required.");
                return;
            }
            Member found = new MemberDAO().getMemberByUsername(u);
            if (found == null || !found.getPassword().equals(p)) {
                setStatus(statusLbl, "error-message", "Invalid credentials. Please try again.");
            } else {
                currentLoggedInMemberId = found.getMemberId();
                showMainDashboard();
            }
        });

        Label orLbl = new Label("— OR —");
        orLbl.getStyleClass().add("subtitle-text");
        orLbl.setMaxWidth(Double.MAX_VALUE);
        orLbl.setAlignment(Pos.CENTER);

        Button createBtn = new Button("CREATE ACCOUNT");
        createBtn.getStyleClass().add("button-primary");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> {
            String u = userField.getText().trim();
            String p = passField.getText();
            if (u.isEmpty() || p.isEmpty()) {
                setStatus(statusLbl, "error-message", "Choose a username and password to create your account.");
                return;
            }
            if (new MemberDAO().getMemberByUsername(u) != null) {
                setStatus(statusLbl, "error-message", "Username already exists. Please login instead.");
                return;
            }
            new MemberDAO().addMember(new Member(u, p, u, "", "", "", ""));
            Member created = new MemberDAO().getMemberByUsername(u);
            if (created != null) {
                currentLoggedInMemberId = created.getMemberId();
            }
            setStatus(statusLbl, "status-success", "✔ Account created! Loading dashboard...");
            new Timeline(new KeyFrame(Duration.seconds(0.9), ev -> showMainDashboard())).play();
        });

        card.getChildren().addAll(modeLbl, userLbl, userField, passLbl, passField,
                loginBtn, orLbl, createBtn, statusLbl);

        rightPane.getChildren().add(card);
        authRoot.getChildren().addAll(leftPane, rightPane);

        applyScene(authRoot, 1280, 760);
    }

    // =========================================================
    // SCREEN 2 — MAIN DASHBOARD
    // =========================================================
    private void showMainDashboard() {
        mainLayout = new BorderPane();
        mainLayout.getStyleClass().addAll("main-layout", "root");

        // Top Bar
        HBox topBar = new HBox();
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));

        Label titleLabel = new Label("ARTEMIS COMMAND");
        titleLabel.getStyleClass().add("title-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnTopProfile = new Button("My Profile");
        btnTopProfile.getStyleClass().add("sidebar-button");

        Button btnTopLogout = new Button("Log Out");
        btnTopLogout.getStyleClass().add("sidebar-button");

        topBar.getChildren().addAll(titleLabel, spacer, btnTopProfile, btnTopLogout);
        mainLayout.setTop(topBar);

        // Sidebar (Left Bar)
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(240);
        sidebar.getStyleClass().add("sidebar");

        Button btnLiveRadar = sideBtn("Live Radar");
        Button btnDispatch = sideBtn("Dispatch Center");
        Button btnSos = sideBtn("Active SOS Feed");
        Button btnTriggerSos = sideBtn("Trigger SOS");
        Button btnShadow = sideBtn("Digital Shadow");
        Button btnGlobalConfig = sideBtn("⬡ Global Config");

        sidebar.getChildren().addAll(btnLiveRadar, btnDispatch, btnSos, btnTriggerSos, btnShadow, btnGlobalConfig);
        mainLayout.setLeft(sidebar);

        // Default panel
        showSosDashboard();

        // Wiring
        btnLiveRadar.setOnAction(e -> showLiveOperationsDashboard());
        btnDispatch.setOnAction(e -> showDispatchCenter());
        btnSos.setOnAction(e -> showSosDashboard());
        btnTriggerSos.setOnAction(e -> showTriggerSosForm());
        btnShadow.setOnAction(e -> showDigitalShadow());
        btnGlobalConfig.setOnAction(e -> showGlobalConfigScreen());

        btnTopProfile.setOnAction(e -> showProfileDashboard());
        btnTopLogout.setOnAction(e -> {
            currentLoggedInMemberId = 0;
            showAuthScreen();
        });

        mainScene = new Scene(mainLayout, 1280, 760);
        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add(TACTICAL_CSS);
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    // =========================================================
    // TRAVEL ITINERARY CARD (single source of truth)
    // =========================================================
    private Node createItineraryCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("surface-card");

        Label header = new Label("TRAVEL ITINERARY & COMPANIONS");
        header.getStyleClass().add("title-text");

        // Companion type
        Label compTypeLbl = new Label("COMPANION TYPE");
        compTypeLbl.getStyleClass().add("white-subtext");
        ComboBox<String> companionType = new ComboBox<>(FXCollections.observableArrayList(
                "Solo", "Spouse", "Family", "Colleagues", "VIP Delegation"));
        companionType.setPromptText("Select companion type");
        companionType.setMaxWidth(Double.MAX_VALUE);

        // Companion contact number
        Label contactLbl = new Label("COMPANION CONTACT NUMBER");
        contactLbl.getStyleClass().add("white-subtext");
        TextField contactField = new TextField();
        contactField.getStyleClass().add("text-field");
        contactField.setPromptText("Phone number of companion");

        // Travel Agency
        Label agencyLbl = new Label("TRAVEL AGENCY");
        agencyLbl.getStyleClass().add("white-subtext");
        TextField agencyField = new TextField();
        agencyField.getStyleClass().add("text-field");
        agencyField.setPromptText("Agency name");

        // Hotel / Safehouse
        Label hotelLbl = new Label("HOTEL / SAFEHOUSE ADDRESS");
        hotelLbl.getStyleClass().add("white-subtext");
        TextField hotelField = new TextField();
        hotelField.getStyleClass().add("text-field");
        hotelField.setPromptText("Full address");

        Button saveBtn = new Button("SAVE ITINERARY");
        saveBtn.getStyleClass().add("button-success");
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        Label itinStatus = new Label("");
        itinStatus.getStyleClass().add("status-success");
        itinStatus.setWrapText(true);

        saveBtn.setOnAction(e -> {
            // ── Rule 1: Hotel / Safehouse must never be blank ───────────
            // ── Rule 2: Contact required when not travelling Solo ───────
            StringBuilder errors = new StringBuilder();

            if (hotelField.getText().trim().isEmpty()) {
                errors.append("\u2022 Hotel / Safehouse Address cannot be empty.\n");
            }
            String compVal = companionType.getValue();
            if (compVal != null && !"Solo".equals(compVal) && contactField.getText().trim().isEmpty()) {
                errors.append("\u2022 Companion Contact Number is required when travelling with ").append(compVal)
                        .append(".\n");
            }

            if (errors.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ARTEMIS — Validation Error");
                alert.setHeaderText("Itinerary Incomplete");
                alert.setContentText(errors.toString().trim());
                alert.showAndWait();
                setStatus(itinStatus, "error-message", "\u26a0 Please fix the highlighted errors.");
            } else {
                String compVal2 = companionType.getValue() == null ? "" : companionType.getValue();
                String contactVal = contactField.getText().trim();
                String agencyVal = agencyField.getText().trim();
                String hotelVal = hotelField.getText().trim();

                boolean saved = new MemberDAO().updateTravelItinerary(
                        currentLoggedInMemberId, compVal2, contactVal, agencyVal, hotelVal);

                if (saved) {
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setTitle("ARTEMIS — Itinerary Saved");
                    ok.setHeaderText("Travel Itinerary Updated");
                    ok.setContentText("Your itinerary has been saved to the database.");
                    ok.showAndWait();
                    setStatus(itinStatus, "status-success", "\u2714 Itinerary saved to database.");
                } else {
                    Alert err = new Alert(Alert.AlertType.ERROR);
                    err.setTitle("ARTEMIS — Database Error");
                    err.setHeaderText("Save Failed");
                    err.setContentText("Could not save itinerary. Please check the terminal for details.");
                    err.showAndWait();
                    setStatus(itinStatus, "error-message", "\u26a0 Database save failed.");
                }
            }
        });

        card.getChildren().addAll(
                header,
                compTypeLbl, companionType,
                contactLbl, contactField,
                agencyLbl, agencyField,
                hotelLbl, hotelField,
                saveBtn, itinStatus);

        return card;
    }

    // =========================================================
    // SCREEN: MY PROFILE (hosts the itinerary card)
    // =========================================================
    private void showProfileDashboard() {
        VBox page = new VBox(0);
        page.setPadding(new Insets(30));

        Node itinCard = createItineraryCard();
        page.getChildren().add(itinCard);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        mainLayout.setCenter(scroll);
    }

    // =========================================================
    // LIVE OPERATIONS — TACTICAL RADAR (60/40 HBox split)
    // =========================================================
    private void showLiveOperationsDashboard() {

        // ── Root: header + body ────────────────────────────────────
        BorderPane root = new BorderPane();
        root.getStyleClass().add("radar-root");
        root.setPadding(new Insets(20));

        // Page title
        Label title = new Label("LIVE OPERATIONS: TACTICAL RADAR");
        title.getStyleClass().add("radar-heading");
        StackPane titlePane = new StackPane(title);
        titlePane.setPadding(new Insets(0, 0, 14, 0));
        root.setTop(titlePane);

        // ── LEFT SIDE (60%) — Radar ────────────────────────────────
        // StackPane centres everything; no absolute X/Y needed for rings
        StackPane radarStack = new StackPane();
        radarStack.getStyleClass().add("radar-pane");
        radarStack.setStyle("-fx-background-color: #05140A;");
        radarStack.setMinSize(0, 0);

        // Grid lines (absolute Pane overlaid on the StackPane)
        Pane gridPane = new Pane();
        gridPane.setMouseTransparent(true);
        // We draw grid lines at render time via a listener so they fill the pane
        gridPane.widthProperty().addListener((obs, ov, nv) -> drawGrid(gridPane));
        gridPane.heightProperty().addListener((obs, ov, nv) -> drawGrid(gridPane));
        gridPane.prefWidthProperty().bind(radarStack.widthProperty());
        gridPane.prefHeightProperty().bind(radarStack.heightProperty());

        // Concentric sweep rings — centred by StackPane automatically
        Circle ring1 = sweepRing(60);
        Circle ring2 = sweepRing(120);
        Circle ring3 = sweepRing(180);
        Circle ring4 = sweepRing(240);

        // Crosshair lines (short, centred)
        Line chH = new Line(-12, 0, 12, 0);
        Line chV = new Line(0, -12, 0, 12);
        chH.setStroke(Color.web("#00FF41"));
        chV.setStroke(Color.web("#00FF41"));
        chH.setStrokeWidth(1.5);
        chV.setStrokeWidth(1.5);

        // Version label (anchor bottom-left manually inside a separate Pane)
        Pane labelPane = new Pane();
        labelPane.setMouseTransparent(true);
        Text versionText = new Text(8, 18, "ARTEMIS-RADAR v5.0");
        versionText.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        versionText.setFill(Color.web("#006600"));
        labelPane.getChildren().add(versionText);
        labelPane.prefWidthProperty().bind(radarStack.widthProperty());
        labelPane.prefHeightProperty().bind(radarStack.heightProperty());
        labelPane.setMouseTransparent(true);

        // Dots (will be positioned via StackPane translate offsets)
        // Member dot — always at centre (0 offset)
        Circle memberDot = new Circle(9);
        memberDot.setFill(Color.web("#ff3333"));
        memberDot.setStroke(Color.web("#ff6666"));
        memberDot.setStrokeWidth(2);
        memberDot.setVisible(false);

        // Threat radius ring around member dot
        Circle threatRadius = new Circle(55);
        threatRadius.setFill(Color.web("#ff000022"));
        threatRadius.setStroke(Color.web("#ff4444"));
        threatRadius.setStrokeWidth(1.5);
        threatRadius.getStrokeDashArray().addAll(8.0, 4.0);
        threatRadius.setVisible(false);

        // Responder dot — translated to a random edge on mission select
        Circle responderDot = new Circle(9);
        responderDot.setFill(Color.web("#3399ff"));
        responderDot.setStroke(Color.web("#66bbff"));
        responderDot.setStrokeWidth(2);
        responderDot.setVisible(false);

        // Labels (positioned inside a free-layout Pane on top of the stack)
        Pane dotLabelPane = new Pane();
        dotLabelPane.setMouseTransparent(true);
        dotLabelPane.prefWidthProperty().bind(radarStack.widthProperty());
        dotLabelPane.prefHeightProperty().bind(radarStack.heightProperty());

        Text memberLabel = dotLabel("MEMBER", "#ff8888");
        Text responderLabel = dotLabel("RESPONDER", "#88bbff");
        memberLabel.setVisible(false);
        responderLabel.setVisible(false);
        dotLabelPane.getChildren().addAll(memberLabel, responderLabel);

        radarStack.getChildren().addAll(
                gridPane, ring4, ring3, ring2, ring1,
                threatRadius, chH, chV,
                memberDot, responderDot,
                labelPane, dotLabelPane);

        // Wrap so it fills 60% of the body
        VBox radarColumn = new VBox(radarStack);
        VBox.setVgrow(radarStack, Priority.ALWAYS);
        radarColumn.setFillWidth(true);

        // ── RIGHT SIDE (40%) — Mission controls only ──────────────
        VBox rightPanel = new VBox(20);
        rightPanel.setPadding(new Insets(0, 0, 0, 20));
        rightPanel.setMinWidth(360);
        rightPanel.setMaxWidth(420);

        // Mission selector card
        VBox missionCard = new VBox(12);
        missionCard.getStyleClass().add("surface-card");

        Label missionLbl = new Label("ACTIVE MISSION");
        missionLbl.getStyleClass().add("subtitle-text");

        ComboBox<Mission> missionCombo = new ComboBox<>();
        missionCombo.setMaxWidth(Double.MAX_VALUE);
        missionCombo.getStyleClass().add("combo-dark");
        List<Mission> dispatched = new MissionDAO().getActiveMissions()
                .stream()
                .filter(m -> "Dispatched".equalsIgnoreCase(m.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        missionCombo.setItems(FXCollections.observableArrayList(dispatched));
        missionCombo.setCellFactory(lv -> new ListCell<Mission>() {
            @Override
            protected void updateItem(Mission m, boolean empty) {
                super.updateItem(m, empty);
                setText((empty || m == null) ? null
                        : "[" + m.getMissionId() + "] " + m.getClassification() + " | " + m.getThreatLevel());
            }
        });
        missionCombo.setButtonCell(new ListCell<Mission>() {
            @Override
            protected void updateItem(Mission m, boolean empty) {
                super.updateItem(m, empty);
                setText((empty || m == null) ? "-- Select mission --"
                        : "[" + m.getMissionId() + "] " + m.getClassification());
            }
        });

        Label respLbl = new Label("RESPONSIBLE RESPONDER");
        respLbl.getStyleClass().add("subtitle-text");

        ComboBox<Responder> responderCombo = new ComboBox<>();
        responderCombo.setMaxWidth(Double.MAX_VALUE);
        responderCombo.getStyleClass().add("combo-dark");
        responderCombo.setItems(FXCollections.observableArrayList(new ResponderDAO().getDeployedResponders()));
        responderCombo.setCellFactory(lv -> new ListCell<Responder>() {
            @Override
            protected void updateItem(Responder r, boolean empty) {
                super.updateItem(r, empty);
                setText((empty || r == null) ? null : "[" + r.getResponderId() + "] " + r.getUnitName());
            }
        });
        responderCombo.setButtonCell(new ListCell<Responder>() {
            @Override
            protected void updateItem(Responder r, boolean empty) {
                super.updateItem(r, empty);
                setText((empty || r == null) ? "-- Select responder --"
                        : "[" + r.getResponderId() + "] " + r.getUnitName());
            }
        });

        Label statusLbl = new Label("Awaiting mission selection...");
        statusLbl.getStyleClass().add("status-info");
        statusLbl.setWrapText(true);

        final double[] speed = { 1.5 };
        final Timeline[] trackingTimeline = { null };

        Button trackBtn = new Button("⬡  INITIATE TRACKING");
        trackBtn.getStyleClass().clear();
        trackBtn.getStyleClass().add("button-track");
        trackBtn.setMaxWidth(Double.MAX_VALUE);

        Button completeBtn = new Button("✔  COMPLETE & AUDIT MISSION");
        completeBtn.getStyleClass().clear();
        completeBtn.getStyleClass().add("button-audit");
        completeBtn.setMaxWidth(Double.MAX_VALUE);

        missionCard.getChildren().addAll(
                missionLbl, missionCombo,
                respLbl, responderCombo,
                trackBtn, completeBtn, statusLbl);
        VBox.setVgrow(missionCard, Priority.ALWAYS);

        rightPanel.getChildren().add(missionCard);
        VBox.setVgrow(rightPanel, Priority.ALWAYS);

        // ── 60/40 HBox body ───────────────────────────────────────
        HBox body = new HBox(16, radarColumn, rightPanel);
        HBox.setHgrow(radarColumn, Priority.ALWAYS);
        body.setFillHeight(true);
        root.setCenter(body);

        // ── LOGIC: Mission selected ────────────────────────────────
        missionCombo.setOnAction(e -> {
            Mission sel = missionCombo.getValue();
            if (sel == null)
                return;
            if (trackingTimeline[0] != null) {
                trackingTimeline[0].stop();
                trackingTimeline[0] = null;
            }

            // Member dot stays at centre of StackPane (no translation)
            memberDot.setTranslateX(0);
            memberDot.setTranslateY(0);
            memberDot.setVisible(true);
            memberLabel.setVisible(true);

            // Responder dot: random offset within ±230px of centre
            double rxOff = (Math.random() * 460) - 230;
            double ryOff = (Math.random() * 340) - 170;
            responderDot.setTranslateX(rxOff);
            responderDot.setTranslateY(ryOff);
            responderDot.setVisible(true);
            responderLabel.setVisible(true);

            // Sync dot-label positions (managed in a free-layout Pane on top)
            // Member label: just below/right of centre
            centerDotLabel(memberLabel, radarStack, 0, 0, 12, -14);
            centerDotLabel(responderLabel, radarStack, rxOff, ryOff, 12, -14);

            // Threat level
            if ("CRITICAL".equalsIgnoreCase(sel.getThreatLevel())) {
                speed[0] = 3.5;
                threatRadius.setTranslateX(0);
                threatRadius.setTranslateY(0);
                threatRadius.setVisible(true);
                setStatus(statusLbl, "error-message", "⚠ THREAT: CRITICAL — Speed: FAST — Threat radius active");
            } else {
                speed[0] = 1.5;
                threatRadius.setVisible(false);
                setStatus(statusLbl, "status-done",
                        "Threat: " + sel.getThreatLevel() + " — Speed: SLOW — Tracking ready");
            }
        });

        // ── LOGIC: Initiate Tracking ───────────────────────────────
        trackBtn.setOnAction(e -> {
            if (missionCombo.getValue() == null) {
                setStatus(statusLbl, "error-message", "Error: Select a mission first.");
                return;
            }
            if (trackingTimeline[0] != null)
                trackingTimeline[0].stop();

            Timeline tl = new Timeline(new KeyFrame(Duration.millis(16), ev -> {
                double dx = memberDot.getTranslateX() - responderDot.getTranslateX();
                double dy = memberDot.getTranslateY() - responderDot.getTranslateY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < speed[0]) {
                    responderDot.setTranslateX(memberDot.getTranslateX());
                    responderDot.setTranslateY(memberDot.getTranslateY());
                    if (trackingTimeline[0] != null)
                        trackingTimeline[0].stop();
                    setStatus(statusLbl, "status-done", "✔ Responder reached member. Click COMPLETE to audit.");
                } else {
                    responderDot.setTranslateX(responderDot.getTranslateX() + (dx / dist) * speed[0]);
                    responderDot.setTranslateY(responderDot.getTranslateY() + (dy / dist) * speed[0]);
                    // Keep label in sync
                    centerDotLabel(responderLabel, radarStack,
                            responderDot.getTranslateX(), responderDot.getTranslateY(), 12, -14);
                }
            }));
            tl.setCycleCount(Timeline.INDEFINITE);
            tl.play();
            trackingTimeline[0] = tl;
            setStatus(statusLbl, "status-tracking", "Tracking in progress...");
        });

        // ── LOGIC: Complete & Audit ────────────────────────────────
        completeBtn.setOnAction(e -> {
            Mission selM = missionCombo.getValue();
            Responder selR = responderCombo.getValue();
            if (selM == null) {
                setStatus(statusLbl, "error-message", "Error: Select a mission.");
                return;
            }
            if (selR == null) {
                setStatus(statusLbl, "error-message", "Error: Select a responder.");
                return;
            }

            if (trackingTimeline[0] != null) {
                trackingTimeline[0].stop();
                trackingTimeline[0] = null;
            }

            int mId = selM.getMissionId();
            int rId = selR.getResponderId();
            String ts = LocalDateTime.now().toString().replace("T", " ").substring(0, 19);

            new MissionDAO().updateMissionStatus(mId, "Completed");
            new ResponderDAO().returnResponderToBase(rId);

            AuditLedger ledger = new AuditLedger(mId, rId, selM.getThreatLevel(), ts);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ARTEMIS // MISSION CLOSURE REPORT");
            alert.setHeaderText("Audit Ledger — Mission #" + mId + " Sealed");
            TextArea ta = new TextArea(ledger.generateReport());
            ta.setEditable(false);
            ta.setWrapText(true);
            ta.setPrefSize(620, 380);
            ta.getStyleClass().add("audit-report");
            alert.getDialogPane().setContent(ta);
            alert.showAndWait();

            memberDot.setVisible(false);
            responderDot.setVisible(false);
            threatRadius.setVisible(false);
            memberLabel.setVisible(false);
            responderLabel.setVisible(false);
            missionCombo.setValue(null);
            responderCombo.setValue(null);

            // Refresh combos
            missionCombo.setItems(FXCollections.observableArrayList(
                    new MissionDAO().getActiveMissions().stream()
                            .filter(m -> "Dispatched".equalsIgnoreCase(m.getStatus()))
                            .collect(java.util.stream.Collectors.toList())));
            responderCombo.setItems(FXCollections.observableArrayList(
                    new ResponderDAO().getDeployedResponders()));

            setStatus(statusLbl, "status-done", "✔ MISSION #" + mId + " SEALED. AUDIT LEDGER GENERATED.");
        });

        mainLayout.setCenter(root);
    }

    // ── Radar helpers ────────────────────────────────────────────
    private Circle sweepRing(double radius) {
        Circle c = new Circle(radius);
        c.setFill(Color.TRANSPARENT);
        c.setStroke(Color.web("#00FF41"));
        c.setStrokeWidth(1);
        return c;
    }

    private Text dotLabel(String text, String hexColor) {
        Text t = new Text(text);
        t.setFill(Color.web(hexColor));
        t.setFont(Font.font("Monospace", 10));
        return t;
    }

    /**
     * Position a dot-label relative to the StackPane centre + a dot's translate
     * offset.
     */
    private void centerDotLabel(Text label, StackPane stack, double dotTx, double dotTy, double offsetX,
            double offsetY) {
        double cx = stack.getWidth() / 2;
        double cy = stack.getHeight() / 2;
        label.setX(cx + dotTx + offsetX);
        label.setY(cy + dotTy + offsetY);
    }

    /**
     * Draw a green grid on a free-layout Pane. Called whenever pane size changes.
     */
    private void drawGrid(Pane pane) {
        pane.getChildren().clear();
        double w = pane.getWidth();
        double h = pane.getHeight();
        Color gridColor = Color.web("#00FF41"); // Neon Green grid lines
        for (double x = 0; x < w; x += 60) {
            Line l = new Line(x, 0, x, h);
            l.setStroke(gridColor);
            l.setOpacity(0.3); // Slight opacity so it's not blinding
            pane.getChildren().add(l);
        }
        for (double y = 0; y < h; y += 50) {
            Line l = new Line(0, y, w, y);
            l.setStroke(gridColor);
            l.setOpacity(0.3);
            pane.getChildren().add(l);
        }
    }

    // ── Status label helper ──────────────────────────────────────
    private void setStatus(Label lbl, String cssClass, String text) {
        lbl.getStyleClass().removeAll("status-info", "error-message", "status-done", "status-tracking");
        lbl.getStyleClass().add(cssClass);
        lbl.setText(text);
    }

    // =========================================================
    // REMAINING DASHBOARD SCREENS
    // =========================================================
    private void showRegisterForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(40));
        grid.setVgap(15);
        grid.setHgap(15);
        grid.setAlignment(Pos.TOP_CENTER);

        Label titleLbl = new Label("REGISTER TRAVEL ITINERARY");
        titleLbl.getStyleClass().add("section-title");
        grid.add(titleLbl, 0, 0, 2, 1);

        Label userLbl = new Label("Username:");
        userLbl.getStyleClass().add("white-subtext");
        Label passLbl = new Label("Password:");
        passLbl.getStyleClass().add("white-subtext");
        Label nameLbl = new Label("Full Name:");
        nameLbl.getStyleClass().add("white-subtext");
        Label compLbl = new Label("Companions:");
        compLbl.getStyleClass().add("white-subtext");
        Label agencyLbl = new Label("Travel Agency:");
        agencyLbl.getStyleClass().add("white-subtext");
        Label hotelLbl = new Label("Hotel Address:");
        hotelLbl.getStyleClass().add("white-subtext");
        Label hotelCLbl = new Label("Hotel Contact:");
        hotelCLbl.getStyleClass().add("white-subtext");

        TextField userInput = new TextField();
        PasswordField passInput = new PasswordField();
        TextField nameInput = new TextField();
        TextField compInput = new TextField();
        TextField agencyInput = new TextField();
        TextField hotelInput = new TextField();
        TextField hotelCInput = new TextField();

        grid.add(userLbl, 0, 1);
        grid.add(userInput, 1, 1);
        grid.add(passLbl, 0, 2);
        grid.add(passInput, 1, 2);
        grid.add(nameLbl, 0, 3);
        grid.add(nameInput, 1, 3);
        grid.add(compLbl, 0, 4);
        grid.add(compInput, 1, 4);
        grid.add(agencyLbl, 0, 5);
        grid.add(agencyInput, 1, 5);
        grid.add(hotelLbl, 0, 6);
        grid.add(hotelInput, 1, 6);
        grid.add(hotelCLbl, 0, 7);
        grid.add(hotelCInput, 1, 7);

        Button submitBtn = new Button("AUTHORIZE & SAVE");
        submitBtn.getStyleClass().add("danger-button");

        Label statusMessage = new Label("");
        statusMessage.getStyleClass().add("status-success");

        submitBtn.setOnAction(e -> {
            String u = userInput.getText().trim(), p = passInput.getText(),
                    n = nameInput.getText().trim();
            if (u.isEmpty() || p.isEmpty() || n.isEmpty()) {
                setStatus(statusMessage, "error-message", "Error: Username, Password, and Full Name are required.");
                return;
            }
            new MemberDAO().addMember(new Member(u, p, n,
                    compInput.getText().trim(), agencyInput.getText().trim(),
                    hotelInput.getText().trim(), hotelCInput.getText().trim()));
            setStatus(statusMessage, "status-success", "Success: Traveler Profile Saved!");
            userInput.clear();
            passInput.clear();
            nameInput.clear();
            compInput.clear();
            agencyInput.clear();
            hotelInput.clear();
            hotelCInput.clear();
        });

        HBox btnBox = new HBox(15, submitBtn, statusMessage);
        btnBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(btnBox, 1, 8);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        mainLayout.setCenter(scroll);
    }

    private void showSosDashboard() {
        VBox page = new VBox(24);
        page.setPadding(new Insets(30));

        Label pageTitle = new Label("ACTIVE MISSION BOARD");
        pageTitle.getStyleClass().add("title-text");

        // ── Stats bar ──────────────────────────────────────────────
        HBox statsBar = new HBox(30);
        statsBar.getStyleClass().add("surface-card");
        statsBar.setAlignment(Pos.CENTER_LEFT);
        Label totalLbl = new Label("TOTAL ACTIVE: 2");
        totalLbl.getStyleClass().clear();
        totalLbl.getStyleClass().add("stat-total");
        Label criticalLbl = new Label("CRITICAL: 1");
        criticalLbl.getStyleClass().clear();
        criticalLbl.getStyleClass().add("stat-critical");
        Label elevatedLbl = new Label("ELEVATED: 1");
        elevatedLbl.getStyleClass().clear();
        elevatedLbl.getStyleClass().add("stat-elevated");
        statsBar.getChildren().addAll(totalLbl, criticalLbl, elevatedLbl);

        // ── Table card ─────────────────────────────────────────────
        VBox tableCard = new VBox(14);
        tableCard.getStyleClass().add("surface-card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        Label cardTitle = new Label("LIVE SOS FEED");
        cardTitle.getStyleClass().add("subtitle-text");

        TableView<MissionAlert> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<MissionAlert, Integer> idCol = new TableColumn<>("Mission ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("missionId"));

        TableColumn<MissionAlert, String> memberCol = new TableColumn<>("Member");
        memberCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));

        TableColumn<MissionAlert, String> threatCol = new TableColumn<>("Threat Level");
        threatCol.setCellValueFactory(new PropertyValueFactory<>("threatLevel"));

        TableColumn<MissionAlert, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, memberCol, threatCol, statusCol);
        List<MissionAlert> liveAlerts = new MissionDAO().getActiveMissionAlerts();
        table.setItems(FXCollections.observableArrayList(liveAlerts));

        long totalCount = liveAlerts.size();
        long criticalCount = liveAlerts.stream().filter(a -> "CRITICAL".equalsIgnoreCase(a.getThreatLevel())).count();
        long elevatedCount = liveAlerts.stream().filter(a -> "ELEVATED".equalsIgnoreCase(a.getThreatLevel())).count();

        totalLbl.setText("TOTAL ACTIVE: " + totalCount);
        criticalLbl.setText("CRITICAL: " + criticalCount);
        elevatedLbl.setText("ELEVATED: " + elevatedCount);

        tableCard.getChildren().addAll(cardTitle, table);
        page.getChildren().addAll(pageTitle, statsBar, tableCard);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        mainLayout.setCenter(scroll);
    }

    private void showDigitalShadow() {
        // ── Root page ──────────────────────────────────────────────────
        BorderPane page = new BorderPane();
        page.setPadding(new Insets(24));

        // ── TOP: Control bar ──────────────────────────────────────────
        VBox topBar = new VBox(10);
        topBar.getStyleClass().add("surface-card");
        topBar.setPadding(new Insets(16, 20, 16, 20));

        Label barTitle = new Label("⬡  INTELLIGENCE DOSSIER — DIGITAL SHADOW RETRIEVAL");
        barTitle.getStyleClass().add("title-text");

        TextField searchInput = new TextField();
        searchInput.setPromptText("Enter numeric Member ID...");
        searchInput.setPrefWidth(220);

        Button fetchBtn = new Button("FETCH PROFILE");
        fetchBtn.getStyleClass().add("button-success");

        HBox searchRow = new HBox(14, searchInput, fetchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        topBar.getChildren().addAll(barTitle, searchRow);
        page.setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(0, 0, 16, 0));

        // ── CENTER: StackPane — watermark + dossier ───────────────────
        StackPane centerStack = new StackPane();

        // Empty state watermark
        Label watermark = new Label("[ AWAITING TARGET DESIGNATION ]");
        watermark.getStyleClass().add("subtitle-text");
        watermark.setStyle("-fx-text-fill: #1a2a1a; -fx-font-size: 22px; -fx-font-weight: bold;");
        StackPane.setAlignment(watermark, Pos.CENTER);

        // ── Dossier panels (hidden by default) ───────────────────────
        VBox dossier = new VBox(16);
        dossier.setVisible(false);
        dossier.setPadding(new Insets(4, 0, 0, 0));

        // Card 1 — Identity
        VBox identityCard = new VBox(10);
        identityCard.getStyleClass().add("surface-card");

        Label idCardTitle = new Label("MEMBER IDENTITY");
        idCardTitle.getStyleClass().add("subtitle-text");

        Label nameDisp = field("FULL NAME", "—");
        Label memberDisp = field("MEMBER ID", "—");
        Label userDisp = field("USERNAME", "—");

        identityCard.getChildren().addAll(idCardTitle, nameDisp, memberDisp, userDisp);

        // Card 2 — Travel Intel
        VBox travelCard = new VBox(10);
        travelCard.getStyleClass().add("surface-card");

        Label travelCardTitle = new Label("TRAVEL INTEL");
        travelCardTitle.getStyleClass().add("subtitle-text");

        Label compDisp = field("COMPANIONS", "—");
        Label hcDisp = field("COMPANION CONTACT", "—");
        Label agencyDisp = field("TRAVEL AGENCY", "—");
        Label hotelDisp = field("HOTEL / SAFEHOUSE", "—");

        travelCard.getChildren().addAll(travelCardTitle, compDisp, hcDisp, agencyDisp, hotelDisp);

        // Card 3 — Incident History
        VBox incidentCard = new VBox(10);
        incidentCard.getStyleClass().add("surface-card");
        VBox.setVgrow(incidentCard, Priority.ALWAYS);

        Label incidentCardTitle = new Label("INCIDENT HISTORY");
        incidentCardTitle.getStyleClass().add("subtitle-text");

        TableView<Mission> missionTable = new TableView<>();
        missionTable.getStyleClass().add("table-view");
        missionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        missionTable.setPrefHeight(200);

        TableColumn<Mission, Integer> midCol = new TableColumn<>("Mission ID");
        midCol.setCellValueFactory(new PropertyValueFactory<>("missionId"));

        TableColumn<Mission, String> tsCol = new TableColumn<>("Timestamp");
        tsCol.setCellValueFactory(new PropertyValueFactory<>("triggerTimestamp"));

        TableColumn<Mission, String> classCol = new TableColumn<>("Classification");
        classCol.setCellValueFactory(new PropertyValueFactory<>("classification"));

        TableColumn<Mission, String> threatCol = new TableColumn<>("Threat");
        threatCol.setCellValueFactory(new PropertyValueFactory<>("threatLevel"));

        TableColumn<Mission, String> statCol = new TableColumn<>("Status");
        statCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        missionTable.getColumns().addAll(midCol, tsCol, classCol, threatCol, statCol);

        Label noMissionsLbl = new Label("No missions on record.");
        noMissionsLbl.getStyleClass().add("status-info");
        missionTable.setPlaceholder(noMissionsLbl);

        incidentCard.getChildren().addAll(incidentCardTitle, missionTable);

        // Arrange cards in a 2-column top row + full-width history below
        HBox topCards = new HBox(16, identityCard, travelCard);
        HBox.setHgrow(identityCard, Priority.ALWAYS);
        HBox.setHgrow(travelCard, Priority.ALWAYS);

        dossier.getChildren().addAll(topCards, incidentCard);

        centerStack.getChildren().addAll(watermark, dossier);
        page.setCenter(centerStack);

        // ── FETCH LOGIC ───────────────────────────────────────────────
        fetchBtn.setOnAction(e -> {
            String raw = searchInput.getText().trim();
            int memberId;
            try {
                memberId = Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ARTEMIS — Input Error");
                alert.setHeaderText("Invalid Member ID");
                alert.setContentText("Please enter a valid numeric Member ID.");
                alert.showAndWait();
                return;
            }

            Member m = new MemberDAO().getMemberById(memberId);
            if (m == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ARTEMIS — Target Not Found");
                alert.setHeaderText("No Record in Database");
                alert.setContentText("Target ID [" + memberId + "] not found in the ARTEMIS database.");
                alert.showAndWait();
                dossier.setVisible(false);
                watermark.setVisible(true);
                return;
            }

            // Populate Identity card
            setText(nameDisp, "FULL NAME", m.getFullName());
            setText(memberDisp, "MEMBER ID", String.valueOf(memberId));
            setText(userDisp, "USERNAME", m.getUsername());

            // Populate Travel Intel card
            setText(compDisp, "COMPANIONS", nullSafe(m.getCompanions()));
            setText(hcDisp, "COMPANION CONTACT", nullSafe(m.getHotelContact()));
            setText(agencyDisp, "TRAVEL AGENCY", nullSafe(m.getTravelAgency()));
            setText(hotelDisp, "HOTEL / SAFEHOUSE", nullSafe(m.getHotelAddress()));

            // Populate Incident History table
            List<Mission> missions = new MissionDAO().getMissionsByMemberId(memberId);
            missionTable.setItems(FXCollections.observableArrayList(missions));

            // Swap states
            watermark.setVisible(false);
            dossier.setVisible(true);
        });

        // Allow Enter key to trigger fetch
        searchInput.setOnAction(e -> fetchBtn.fire());

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.getStyleClass().add("scroll-pane");
        mainLayout.setCenter(scroll);
    }

    // ── Dossier field helpers ────────────────────────────────────────
    private Label field(String labelKey, String value) {
        Label l = new Label(labelKey + ":  " + value);
        l.getStyleClass().add("white-subtext");
        l.setWrapText(true);
        return l;
    }

    private void setText(Label lbl, String key, String value) {
        lbl.setText(key + ":  " + value);
    }

    private String nullSafe(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    private void showTriggerSosForm() {
        Label titleLbl = new Label("⚠  EMERGENCY: TRIGGER SOS");
        titleLbl.getStyleClass().add("title-text");

        VBox formCard = new VBox(15);
        formCard.getStyleClass().add("surface-card");
        formCard.setPrefWidth(520);

        Label idLbl = new Label("MEMBER ID");
        idLbl.getStyleClass().add("subtitle-text");
        TextField idInput = new TextField();
        idInput.setPromptText("Numeric member ID");

        Label classLbl = new Label("MISSION CLASSIFICATION");
        classLbl.getStyleClass().add("subtitle-text");
        ComboBox<String> classInput = new ComboBox<>(
                FXCollections.observableArrayList("Medical", "Hostile Extraction", "Environmental"));
        classInput.setPrefWidth(Double.MAX_VALUE);
        classInput.setPromptText("Select classification");

        Label descLbl = new Label("DESCRIBE EMERGENCY");
        descLbl.getStyleClass().add("subtitle-text");
        TextArea descInput = new TextArea();
        descInput.getStyleClass().add("text-area");
        descInput.setStyle("-fx-prompt-text-fill: #9CA3AF;");
        descInput.setPrefRowCount(4);
        descInput.setPromptText("Describe the situation for AI threat assessment...");
        descInput.setWrapText(true);

        Button submitBtn = new Button("⚡  INITIATE PROTOCOL");
        submitBtn.getStyleClass().add("button-danger");
        submitBtn.setMaxWidth(Double.MAX_VALUE);

        Label statusMessage = new Label("");

        submitBtn.setOnAction(e -> {
            try {
                int memberId = Integer.parseInt(idInput.getText());
                Member m = new MemberDAO().getMemberById(memberId);
                if (m == null) {
                    setStatus(statusMessage, "error-message", "Error: Member ID not found.");
                    return;
                }

                String threat = ThreatAnalyzer.calculateThreatLevel(descInput.getText(), m);
                new MissionDAO().addMission(new Mission(memberId, LocalDateTime.now().toString(),
                        "Pending Verification", classInput.getValue(), threat));
                setStatus(statusMessage, "status-success", "✔  SOS Logged — AI assessed threat as: " + threat);
                idInput.clear();
                classInput.setValue(null);
                descInput.clear();
            } catch (Exception ex) {
                setStatus(statusMessage, "error-message", "Error: Please fill all fields correctly.");
            }
        });

        formCard.getChildren().addAll(idLbl, idInput, classLbl, classInput, descLbl, descInput, submitBtn,
                statusMessage);

        VBox center = new VBox(20, titleLbl, formCard);
        center.setPadding(new Insets(30));
        center.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(center);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        mainLayout.setCenter(scroll);
    }

    private void showDispatchCenter() {
        VBox page = new VBox(24);
        page.setPadding(new Insets(30));

        Label pageTitle = new Label("⬡  DISPATCH CENTER");
        pageTitle.getStyleClass().add("title-text");

        // ── Mission card ───────────────────────────────────────────
        VBox missionCard = new VBox(12);
        missionCard.getStyleClass().add("surface-card");
        HBox.setHgrow(missionCard, Priority.ALWAYS);
        VBox.setVgrow(missionCard, Priority.ALWAYS);

        Label mT = new Label("▲  AWAITING DISPATCH");
        mT.getStyleClass().add("subtitle-text");
        Label mHint = new Label("Select a mission to dispatch a unit.");
        mHint.getStyleClass().add("white-subtext");

        ListView<String> missionList = new ListView<>();
        missionList.getStyleClass().add("list-view");
        missionList.setPrefHeight(260);
        VBox.setVgrow(missionList, Priority.ALWAYS);
        refreshMissionList(missionList);
        missionCard.getChildren().addAll(mT, mHint, missionList);

        // ── Responder card ─────────────────────────────────────────
        VBox responderCard = new VBox(12);
        responderCard.getStyleClass().add("surface-card");
        HBox.setHgrow(responderCard, Priority.ALWAYS);
        VBox.setVgrow(responderCard, Priority.ALWAYS);

        Label rT = new Label("● AVAILABLE PROXIMITY ASSETS");
        rT.getStyleClass().add("subtitle-text");
        Label rHint = new Label("Select a responder to pair with the mission.");
        rHint.getStyleClass().add("white-subtext");

        ListView<String> responderList = new ListView<>();
        responderList.getStyleClass().add("list-view");
        responderList.setPrefHeight(260);
        VBox.setVgrow(responderList, Priority.ALWAYS);
        refreshResponderList(responderList);
        responderCard.getChildren().addAll(rT, rHint, responderList);

        HBox listsRow = new HBox(24, missionCard, responderCard);
        listsRow.setFillHeight(true);

        // ── Deploy action card ─────────────────────────────────────
        VBox deployCard = new VBox(14);
        deployCard.getStyleClass().add("surface-card");

        Label dT = new Label("DEPLOY COMMAND");
        dT.getStyleClass().add("subtitle-text");

        Button deployBtn = new Button("⬡  DEPLOY SELECTED ASSET");
        deployBtn.getStyleClass().clear();
        deployBtn.getStyleClass().add("button-success");

        Button refreshBtn = new Button("↺  REFRESH LISTS");
        refreshBtn.getStyleClass().clear();
        refreshBtn.getStyleClass().add("button-primary");
        refreshBtn.setOnAction(e -> {
            refreshMissionList(missionList);
            refreshResponderList(responderList);
        });

        Label deployMsg = new Label("");
        deployMsg.getStyleClass().add("status-success");
        deployMsg.setWrapText(true);

        HBox actionRow = new HBox(16, deployBtn, refreshBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        deployBtn.setOnAction(e -> {
            String sM = missionList.getSelectionModel().getSelectedItem();
            String sR = responderList.getSelectionModel().getSelectedItem();
            if (sM == null || sR == null) {
                setStatus(deployMsg, "error-message", "Select one mission AND one responder first.");
                return;
            }
            try {
                int mId = Integer.parseInt(sM.split("\\[")[1].split("\\]")[0].trim());
                int rId = Integer.parseInt(sR.split("\\[")[1].split("\\]")[0].trim());
                new ResponderDAO().deployResponder(rId);
                new MissionDAO().updateMissionStatus(mId, "Dispatched");
                refreshMissionList(missionList);
                refreshResponderList(responderList);
                setStatus(deployMsg, "status-success", "✔  Responder deployed successfully.");
            } catch (Exception ex) {
                setStatus(deployMsg, "error-message", "Error: " + ex.getMessage());
            }
        });

        deployCard.getChildren().addAll(dT, actionRow, deployMsg);
        page.getChildren().addAll(pageTitle, listsRow, deployCard);
        VBox.setVgrow(listsRow, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        mainLayout.setCenter(scroll);
    }

    // =========================================================
    // SCREEN: GLOBAL CONFIGURATIONS — THREAT MATRIX (Use Case 12)
    // =========================================================
    private void showGlobalConfigScreen() {

        // ── Root layout ──────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #080E1A;");
        root.setPadding(new Insets(24));

        // ── Title ────────────────────────────────────────────────────
        Label title = new Label("GLOBAL CONFIGURATIONS: THREAT MATRIX");
        title.getStyleClass().add("radar-heading");
        title.setStyle("-fx-text-fill: #06B6D4;"
                + "-fx-effect: dropshadow(three-pass-box, rgba(6,182,212,0.55), 14, 0, 0, 0);"
                + "-fx-font-size: 26px; -fx-font-weight: 900;");
        StackPane titlePane = new StackPane(title);
        titlePane.setPadding(new Insets(0, 0, 18, 0));
        root.setTop(titlePane);

        // =========================================================
        // TOPOLOGY MAP — fixed 800×400 Pane
        // =========================================================
        Pane topologyGrid = new Pane();
        topologyGrid.setPrefSize(800, 400);
        topologyGrid.setMinSize(800, 400);
        topologyGrid.setMaxSize(800, 400);
        topologyGrid.setStyle("-fx-background-color: #0B1320;"
                + "-fx-border-color: #06B6D4;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 4;"
                + "-fx-background-radius: 4;");

        // ── Cyan grid lines ──────────────────────────────────────────
        Color gridColor = Color.web("#06B6D4", 0.2);
        for (double x = 0; x <= 800; x += 50) {
            Line vLine = new Line(x, 0, x, 400);
            vLine.setStroke(gridColor);
            vLine.setStrokeWidth(1);
            topologyGrid.getChildren().add(vLine);
        }
        for (double y = 0; y <= 400; y += 40) {
            Line hLine = new Line(0, y, 800, y);
            hLine.setStroke(gridColor);
            hLine.setStrokeWidth(1);
            topologyGrid.getChildren().add(hLine);
        }

        // ── Helper: build a glowing sector node ──────────────────────
        // Arguments: x, y centre position, radius, fill colour hex
        // Returns the Circle so we can mutate it later.

        // ── 6 Continental Sector Nodes ───────────────────────────────
        // Name Continent x y
        Circle nodeAlpha = makeThreatNode(160, 110); // North America – Top Left
        Circle nodeBravo = makeThreatNode(180, 290); // South America – Bottom Left
        Circle nodeCharlie = makeThreatNode(380, 100); // Europe – Top Middle
        Circle nodeDelta = makeThreatNode(400, 280); // Africa – Bottom Middle
        Circle nodeEcho = makeThreatNode(600, 95); // Asia – Top Right
        Circle nodeFoxtrot = makeThreatNode(630, 310); // Oceania – Bottom Right

        // ── Connecting lines between nodes (faint topology edges) ────
        topologyGrid.getChildren().addAll(
                topoEdge(160, 110, 180, 290), // Alpha → Bravo
                topoEdge(160, 110, 380, 100), // Alpha → Charlie
                topoEdge(380, 100, 400, 280), // Charlie → Delta
                topoEdge(380, 100, 600, 95), // Charlie → Echo
                topoEdge(400, 280, 180, 290), // Delta → Bravo
                topoEdge(400, 280, 630, 310), // Delta → Foxtrot
                topoEdge(600, 95, 630, 310), // Echo → Foxtrot
                topoEdge(180, 290, 630, 310) // Bravo → Foxtrot
        );

        // ── Node labels ──────────────────────────────────────────────
        topologyGrid.getChildren().addAll(
                nodeAlpha, nodeLabel("ALPHA\n(N.AMERICA)", 130, 80),
                nodeBravo, nodeLabel("BRAVO\n(S.AMERICA)", 150, 262),
                nodeCharlie, nodeLabel("CHARLIE\n(EUROPE)", 353, 70),
                nodeDelta, nodeLabel("DELTA\n(AFRICA)", 375, 252),
                nodeEcho, nodeLabel("ECHO\n(ASIA)", 576, 65),
                nodeFoxtrot, nodeLabel("FOXTROT\n(OCEANIA)", 600, 282));

        // ── ARTEMIS watermark ─────────────────────────────────────────
        Text watermark = new Text(8, 16, "ARTEMIS-MATRIX v1.0  |  GLOBAL THREAT TOPOLOGY");
        watermark.setFill(Color.web("#06B6D4", 0.35));
        watermark.setFont(Font.font("Monospace", FontWeight.BOLD, 10));
        topologyGrid.getChildren().add(watermark);

        // Map wrapper — centres the fixed-size pane
        StackPane mapWrapper = new StackPane(topologyGrid);
        mapWrapper.setPadding(new Insets(0, 0, 20, 0));
        root.setCenter(mapWrapper);

        // =========================================================
        // CONTROL PANEL
        // =========================================================
        VBox controlArea = new VBox(16);
        controlArea.setPadding(new Insets(0, 0, 0, 0));

        // ── Sector + Threat selectors row ────────────────────────────
        ComboBox<String> sectorCombo = new ComboBox<>(FXCollections.observableArrayList(
                "NODE ALPHA", "NODE BRAVO", "NODE CHARLIE",
                "NODE DELTA", "NODE ECHO", "NODE FOXTROT"));
        sectorCombo.setPromptText("Select Sector");
        sectorCombo.getStyleClass().add("combo-dark");
        sectorCombo.setPrefWidth(210);

        ComboBox<String> threatCombo = new ComboBox<>(FXCollections.observableArrayList(
                "LOW (CYAN)", "ELEVATED (AMBER)", "CRITICAL (RED)"));
        threatCombo.setPromptText("Set Threat Level");
        threatCombo.getStyleClass().add("combo-dark");
        threatCombo.setPrefWidth(210);

        Button updateBtn = new Button("UPDATE BASELINE THREAT");
        updateBtn.getStyleClass().add("button-danger");

        Label feedbackLbl = new Label("");
        feedbackLbl.getStyleClass().add("white-subtext");
        feedbackLbl.setStyle("-fx-text-fill: #06B6D4; -fx-font-weight: bold;");

        HBox controlRow = new HBox(20, sectorCombo, threatCombo, updateBtn, feedbackLbl);
        controlRow.setAlignment(Pos.CENTER);
        controlArea.getChildren().add(controlRow);

        // ── Legend row ───────────────────────────────────────────────
        HBox legend = new HBox(24,
                legendDot("LOW", Color.web("#06B6D4")),
                legendDot("ELEVATED", Color.web("#F59E0B")),
                legendDot("CRITICAL", Color.web("#EF4444")));
        legend.setAlignment(Pos.CENTER);
        controlArea.getChildren().add(legend);

        root.setBottom(controlArea);
        BorderPane.setMargin(controlArea, new Insets(12, 0, 0, 0));

        // =========================================================
        // BUTTON LOGIC — Update the selected node
        // =========================================================
        updateBtn.setOnAction(e -> {
            String sector = sectorCombo.getValue();
            String threat = threatCombo.getValue();

            if (sector == null || threat == null) {
                feedbackLbl.setText("⚠ Select BOTH a sector and a threat level.");
                feedbackLbl.setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold;");
                return;
            }

            // Resolve which node Circle to update
            Circle target;
            switch (sector) {
                case "NODE ALPHA":
                    target = nodeAlpha;
                    break;
                case "NODE BRAVO":
                    target = nodeBravo;
                    break;
                case "NODE CHARLIE":
                    target = nodeCharlie;
                    break;
                case "NODE DELTA":
                    target = nodeDelta;
                    break;
                case "NODE ECHO":
                    target = nodeEcho;
                    break;
                case "NODE FOXTROT":
                    target = nodeFoxtrot;
                    break;
                default:
                    return;
            }

            // Resolve colour + glow from threat level
            Color fillColor;
            String hexGlow;
            String threatLabel;
            if (threat.startsWith("LOW")) {
                fillColor = Color.web("#06B6D4");
                hexGlow = "#06B6D4";
                threatLabel = "LOW";
            } else if (threat.startsWith("ELEVATED")) {
                fillColor = Color.web("#F59E0B");
                hexGlow = "#F59E0B";
                threatLabel = "ELEVATED";
            } else {
                fillColor = Color.web("#EF4444");
                hexGlow = "#EF4444";
                threatLabel = "CRITICAL";
            }

            DropShadow glow = new DropShadow();
            glow.setColor(fillColor);
            glow.setRadius(22);
            glow.setSpread(0.55);

            target.setFill(fillColor);
            target.setEffect(glow);
            target.setStroke(fillColor.brighter());

            feedbackLbl.setText("✔  " + sector + " → THREAT: " + threatLabel);
            feedbackLbl.setStyle("-fx-text-fill: " + hexGlow + "; -fx-font-weight: bold;");
        });

        // ── Wrap in scroll pane ───────────────────────────────────────
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.getStyleClass().add("scroll-pane");
        mainLayout.setCenter(scroll);
    }

    // ── Global Config helpers ────────────────────────────────────────

    /** Creates a styled sector node Circle with default Cyan glow. */
    private Circle makeThreatNode(double cx, double cy) {
        Circle c = new Circle(cx, cy, 15);
        Color cyan = Color.web("#06B6D4");
        c.setFill(cyan);
        c.setStroke(cyan.brighter());
        c.setStrokeWidth(1.5);
        DropShadow glow = new DropShadow();
        glow.setColor(cyan);
        glow.setRadius(20);
        glow.setSpread(0.5);
        c.setEffect(glow);
        return c;
    }

    /** Creates a faint cyan topology edge line between two node centres. */
    private Line topoEdge(double x1, double y1, double x2, double y2) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(Color.web("#06B6D4", 0.18));
        l.setStrokeWidth(1);
        l.getStrokeDashArray().addAll(6.0, 4.0);
        return l;
    }

    /** Creates a small multi-line label for a topology node. */
    private Text nodeLabel(String text, double x, double y) {
        Text t = new Text(x, y, text);
        t.setFill(Color.web("#06B6D4", 0.8));
        t.setFont(Font.font("Monospace", FontWeight.BOLD, 9));
        return t;
    }

    /** Creates a legend row item: coloured dot + label. */
    private HBox legendDot(String label, Color color) {
        Circle dot = new Circle(7, color);
        DropShadow g = new DropShadow(8, color);
        dot.setEffect(g);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-font-weight: bold;");
        HBox box = new HBox(6, dot, lbl);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    // =========================================================
    // UTILITIES
    // =========================================================
    private void refreshMissionList(ListView<String> list) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Mission m : new MissionDAO().getActiveMissions())
            items.add("[" + m.getMissionId() + "] " + m.getThreatLevel() + " - " + m.getStatus());
        list.setItems(items);
    }

    private void refreshResponderList(ListView<String> list) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Responder r : new ResponderDAO().getAvailableResponders())
            items.add("[" + r.getResponderId() + "] " + r.getUnitName() + " (" + r.getSpecialtyType() + ")");
        list.setItems(items);
    }

    private Button sideBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(40);
        b.getStyleClass().add("sidebar-button");
        return b;
    }

    /**
     * Creates a labelled TextField and appends both to the parent VBox. Returns the
     * TextField.
     */
    private TextField field(VBox parent, String labelText) {
        TextField tf = new TextField();
        tf.setPromptText(labelText.charAt(0) + labelText.substring(1).toLowerCase());
        labeled(parent, labelText, tf);
        return tf;
    }

    private void labeled(VBox parent, String labelText, Control ctrl) {
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("white-subtext");
        parent.getChildren().addAll(lbl, ctrl);
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        DatabaseSetup.initializeDatabase();
        launch(args);
    }

    // =========================================================
    // INNER CLASS — MissionAlert (used by SOS Dashboard TableView)
    // =========================================================
    public static class MissionAlert {
        private final int missionId;
        private final String memberName, threatLevel, status;

        public MissionAlert(int id, String name, String threat, String status) {
            this.missionId = id;
            this.memberName = name;
            this.threatLevel = threat;
            this.status = status;
        }

        public int getMissionId() {
            return missionId;
        }

        public String getMemberName() {
            return memberName;
        }

        public String getThreatLevel() {
            return threatLevel;
        }

        public String getStatus() {
            return status;
        }
    }
}
