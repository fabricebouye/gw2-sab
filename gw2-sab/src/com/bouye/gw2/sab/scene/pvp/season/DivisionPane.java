/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.pvp.season;

import api.web.gw2.mapping.core.URLReference;
import api.web.gw2.mapping.v2.pvp.seasons.SeasonDivision;
import api.web.gw2.mapping.v2.pvp.seasons.SeasonTier;
import com.bouye.gw2.sab.query.ImageCache;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

/**
 * Displays progress in a PvP league division.
 * @author Fabrice Bouyé
 */
public final class DivisionPane extends Region {

    private final GridPane gridPane = new GridPane();

    /**
     * Creates a new instance.
     */
    public DivisionPane() {
        super();
        setId("divisionPane"); // NOI18N.
        //
        gridPane.setId("GridPane"); // NOI18N.
        final List<RowConstraints> rConstraints = IntStream.range(0, 2)
                .mapToObj(index -> {
                    final RowConstraints rowContraints = new RowConstraints();
                    return rowContraints;
                })
                .collect(Collectors.toList());
        gridPane.getRowConstraints().setAll(rConstraints);
        getChildren().add(gridPane);
        //
        divisionProperty().addListener(observable -> updateContent());
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double width = getWidth();
        final double height = getHeight();
        final Insets insets = getInsets();
        final double areaX = insets.getLeft();
        final double areaY = insets.getTop();
        final double areaW = Math.max(0, width - (insets.getLeft() + insets.getRight()));
        final double areaH = Math.max(0, height - (insets.getTop() + insets.getBottom()));
        gridPane.resizeRelocate(areaX, areaY, areaW, areaH);
    }

    /**
     * Update the content of this node when division changes.
     */
    private void updateContent() {
        // Clear previous display.
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        // Create new display.
        final Optional<SeasonDivision> division = Optional.ofNullable(getDivision());
        division.ifPresent(d -> installDivisionContent(d));
    }

    /**
     * Creates a new division display.
     * @param division The division, never {@code null}.
     */
    private void installDivisionContent(SeasonDivision division) {
        final List<SeasonTier> tiers = division.getTiers()
                .stream()
                .collect(Collectors.toList());
        // Create new column contraints.
        final List<ColumnConstraints> cContraints = IntStream.range(0, tiers.size())
                .mapToObj(index -> {
                    final ColumnConstraints columnContraints = new ColumnConstraints();
                    return columnContraints;
                })
                .collect(Collectors.toList());
        gridPane.getColumnConstraints().setAll(cContraints);
        // Pips.
        final List<Node> pips = IntStream.range(0, tiers.size())
                .mapToObj(index -> {
                    final SeasonTier tier = tiers.get(index);
                    return createPipsForTier(index, tier, division);
                })
                .collect(Collectors.toList());
        gridPane.getChildren().addAll(pips);
        // Bottom labels.
        final List<Label> pipsLabels = IntStream.range(0, tiers.size())
                .mapToObj(index -> createLabelForTier(index))
                .collect(Collectors.toList());
        gridPane.getChildren().addAll(pipsLabels);
    }

    /**
     * Creates a pip representation for given tier.
     * @param tierIndex The tier index.
     * @param tier The tier object.
     * @param division The division.
     * @return A {@code Node}, never {@code null}.
     */
    private Node createPipsForTier(final int tierIndex, final SeasonTier tier, final SeasonDivision division) {
        final HBox result = new HBox();
        result.setId(String.format("tier%dHBox", tierIndex + 1)); // NOI18N.
        final List<Node> pips = IntStream.range(0, tier.getPoints())
                .mapToObj(pointIndex -> {
                    final URLReference pipIconURL = division.getPipIcon();
                    final ImageView imageView = new ImageView();
                    imageView.setId(String.format("tier%d-%dIcon", tierIndex + 1, pointIndex + 1)); // NOI18N.
                    pipIconURL.ifPresent(url -> {
                        final Image image = ImageCache.INSTANCE.getImage(url.toExternalForm());
                        imageView.setImage(image);
                    });
                    return imageView;
                })
                .collect(Collectors.toList());
        result.getChildren().setAll(pips);
        GridPane.setConstraints(result, tierIndex, 0);
        return result;
    }

    /**
     * Creates a label for given tier.
     * @param tierIndex The tier index.
     * @return a {@code Label}, never {@code null}.
     */
    private Label createLabelForTier(final int tierIndex) {
        final Label result = new Label();
        result.setId(String.format("tier%dLabel", tierIndex + 1)); // NOI18N.
        final String text = String.valueOf(tierIndex);
        result.setText(text);
        GridPane.setConstraints(result, tierIndex, 1);
        return result;
    }

    /**
     * The division to display.
     */
    private final ObjectProperty<SeasonDivision> division = new SimpleObjectProperty<>(this, "division", null); // NOI18N.

    public final SeasonDivision getDivision() {
        return division.get();
    }

    public final void setDivision(final SeasonDivision value) {
        division.set(value);
    }

    public final ObjectProperty<SeasonDivision> divisionProperty() {
        return division;
    }
}
