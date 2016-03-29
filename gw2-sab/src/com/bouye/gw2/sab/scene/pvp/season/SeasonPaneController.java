/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.pvp.season;

import api.web.gw2.mapping.core.URLReference;
import api.web.gw2.mapping.v2.pvp.seasons.Season;
import api.web.gw2.mapping.v2.pvp.seasons.SeasonDivision;
import api.web.gw2.mapping.v2.pvp.seasons.SeasonDivisionFlag;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.query.ImageCache;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class SeasonPaneController extends SABControllerBase {

    @FXML
    private Label seasonNameLabel;

    @FXML
    private Label seasonDescriptionLabel;

    @FXML
    private ImageView seasonIcon;

    @FXML
    private StackPane divisionProgressionContainer;

    @FXML
    private Label divisionProgressionLabel;

    @FXML
    private GridPane bottomGridPane;

    @FXML
    private Label divisionOverviewLabel;

    @FXML
    private HBox divisionOverviewContainer;

    @FXML
    private Label divisionRulesLabel;

    @FXML
    private VBox divisionRulesContainer;

    @FXML
    private Label upcomingRewardsLabel;

    @FXML
    private HBox upcomingRewardsContainer;

    /**
     * Creates a new instance.
     */
    public SeasonPaneController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        seasonProperty().addListener(seasonChangeListener);
        division.addListener(divisionChangeListener);
    }

    private InvalidationListener seasonChangeListener = observable -> updateContent();
    private InvalidationListener divisionChangeListener = observable -> updateContent();

    @Override
    protected void clearContent(final Node parent) {
        seasonIcon.setImage(null);
        divisionOverviewContainer.getChildren().clear();
        divisionRulesContainer.getChildren().clear();
        upcomingRewardsContainer.getChildren().clear();
    }

    @Override
    protected void installContent(final Node parent) {
        final Season season = getSeason();
        final int divisionIndex = division.get();
        if ((season == null) || (divisionIndex < 0) || (divisionIndex >= season.getDivisions().size())) {
            return;
        }
        final List<SeasonDivision> divisions = season.getDivisions()
                .stream()
                .collect(Collectors.toList());
        final SeasonDivision currentDivision = divisions.get(divisionIndex);
        // Season icon.
        final URLReference largeIcon = currentDivision.getLargeIcon();
        largeIcon.ifPresent(url -> {
            final Image image = ImageCache.INSTANCE.getImage(url.toExternalForm());
            seasonIcon.setImage(image);
        });
        // Division overview.
        final ToggleGroup overviewToggleGroup = new ToggleGroup();
        final List<ToggleButton> overviewButtons = IntStream.range(0, divisions.size())
                .mapToObj(index -> {
                    final SeasonDivision division = divisions.get(index);
                    return createToggleForDivision(index, division, currentDivision, overviewToggleGroup);
                })
                .collect(Collectors.toList());
        divisionOverviewContainer.getChildren().setAll(overviewButtons);
        // Division rules.
        final List<Node> rulesLabels = currentDivision.getFlags()
                .stream()
                .map(flag -> createNodeForDivisionFlag(flag, currentDivision))
                .collect(Collectors.toList());
        divisionRulesContainer.getChildren().setAll(rulesLabels);
        // Upcoming rewards.
    }

    private final ToggleButton createToggleForDivision(final int index, final SeasonDivision division, final SeasonDivision currentDivision, final ToggleGroup overviewToggleGroup) {
        final ToggleButton button = new ToggleButton();
        final String text = SABConstants.I18N.getString(String.format("season-pane.division%d.label", index + 1)); // NOI18N.
        button.setText(text);
        division.getSmallIcon().ifPresent(url -> {
            final Image image = ImageCache.INSTANCE.getImage(url.toExternalForm());
            final ImageView imageView = new ImageView(image);
            button.setGraphic(imageView);
        });
        button.setSelected(division == currentDivision);
        button.setToggleGroup(overviewToggleGroup);
        button.setOnAction(actionEvent -> selectDivision(index));
        return button;
    }

    private final Node createNodeForDivisionFlag(final SeasonDivisionFlag flag, final SeasonDivision currentDivision) {
        final Label result = new Label();
        String text = null;
        Node graphic = null;
        switch (flag) {
            case CAN_LOSE_POINTS:
                text = SABConstants.I18N.getString("season-pane.rules.can-loose-pips.label"); // NOI18N.
                final URLReference pipIconURL = currentDivision.getPipIcon();
                if (pipIconURL.isPresent()) {
                    final URL url = pipIconURL.get();
                    final Image icon = ImageCache.INSTANCE.getImage(url.toExternalForm());
                    final ImageView imageView = new ImageView(icon);
                    graphic = imageView;
                }
                break;
            case CAN_LOSE_TIERS:
                text = SABConstants.I18N.getString("season-pane.rules.can-loose-tiers.label"); // NOI18N.
                break;
            case REPEATABLE:
                text = SABConstants.I18N.getString("season-pane.rules.repeatable.label"); // NOI18N.
                break;
            default:
        };
        result.setText(text);
        result.setGraphic(graphic);
        return result;
    }

    private final ObjectProperty<Season> season = new SimpleObjectProperty<>(this, "season", null); // NOI18N.

    public final Season getSeason() {
        return season.get();
    }

    public final void setSeason(final Season value) {
        season.set(value);
    }

    public final ObjectProperty<Season> seasonProperty() {
        return season;
    }

    public final void selectDivision(final int index) {
        final Season season = getSeason();
        int newValue = -1;
        if (season != null) {
            if (index < 0 || index >= season.getDivisions().size()) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            newValue = index;
        }
        division.set(newValue);
    }

    private final IntegerProperty division = new SimpleIntegerProperty(this, "division", -1); // NOI18N.
}
