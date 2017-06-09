/*
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.account.masteries;

import api.web.gw2.mapping.core.URLReference;
import api.web.gw2.mapping.v2.masteries.MasteryLevel;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

/**
 * FXML controller.
 * @author Fabrice Bouyé
 */
public final class MasteryLevelRendererController implements Initializable {

    @FXML
    private Node rootPane;
    @FXML
    private ImageView icon;
    @FXML
    private Label nameLabel;
    @FXML
    private FlowPane pointsFlow;

    private final Tooltip tooltip = new Tooltip();

    public MasteryLevelRendererController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        masteryLevelProperty().addListener(masteryLevelChangeListener);
    }

    /**
     * Called whenever the mastery level changes.
     */
    private final ChangeListener<MasteryLevel> masteryLevelChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallMasteryLevel);
        Optional.ofNullable(newValue)
                .ifPresent(this::installMasteryLevel);
    };

    /**
     * Uninstall previous mastery level.
     * @param masteryLevel The mastery level, never {@code null}.
     */
    private void uninstallMasteryLevel(final MasteryLevel masteryLevel) {
        nameLabel.setText(null);
        icon.setImage(null);
        pointsFlow.getChildren().clear();
        Tooltip.uninstall(rootPane, tooltip);
    }

    /**
     * Install new mastery level.
     * @param masteryLevel The mastery level, never {@code null}.
     */
    private void installMasteryLevel(final MasteryLevel masteryLevel) {
        final String name = masteryLevel.getName();
        nameLabel.setText(name);
        final URLReference iconURL = masteryLevel.getIcon();
//        iconURL.ifPresent(url -> {
//            final Image image = new Image(url.toExternalForm(), true);
//            icon.setImage(image);
//        });
        final int pointCost = masteryLevel.getPointCost();
        pointsFlow.getChildren().addAll(IntStream.range(0, pointCost)
                .mapToObj(index -> {
//                    final Node point = new Circle(7);
                    final Node point = new Region();
                    point.getStyleClass().add("mastery-point");
                    return point;
                })
                .toArray(Node[]::new));
        final String description = masteryLevel.getDescription();
        tooltip.setText(description);
        Tooltip.install(rootPane, tooltip);
    }

    /**
     * The mastery level to display.`
     */
    private final ObjectProperty<MasteryLevel> masteryLevel = new SimpleObjectProperty<>(this, "masteryLevel"); // NOI18N.

    public final MasteryLevel getMasteryLevel() {
        return masteryLevel.get();
    }

    public final void setMasteryLevel(final MasteryLevel value) {
        masteryLevel.set(value);
    }

    public final ObjectProperty<MasteryLevel> masteryLevelProperty() {
        return masteryLevel;
    }
}
