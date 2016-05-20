/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.pvp.seasons;

import api.web.gw2.mapping.core.URLReference;
import api.web.gw2.mapping.v2.pvp.seasons.SeasonDivision;
import api.web.gw2.mapping.v2.pvp.seasons.SeasonTier;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.query.ImageCache;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

/**
 * Displays progress in a PvP league division.
 * @author Fabrice Bouyé
 */
public final class DivisionPane extends Region {

    /**
     * There are 7 pip images, the 8th one is the glow.
     */
    private static final int PIP_IMAGE_NUMBER = 8;

    private final GridPane gridPane = new GridPane();

    /**
     * Creates a new instance.
     */
    public DivisionPane() {
        super();
        setId("divisionPane"); // NOI18N.
        getStyleClass().add("division-pane"); // NOI18N.
        //
        gridPane.setId("GridPane"); // NOI18N.
        gridPane.getStyleClass().add("GridPane"); // NOI18N.
        final List<RowConstraints> rConstraints = IntStream.range(0, 2)
                .mapToObj(rowIndex -> {
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

    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/pvp/seasons/DivisionPane.css"); // NOI18N.
        return (url == null) ? null : url.toExternalForm();
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
        division.ifPresent(d -> {
            preparePipIcons(d);
            installDivisionContent(d);
        });
    }

    private final List<Image> pipIcons = new LinkedList<>();

    private void preparePipIcons(final SeasonDivision division) {
        pipIcons.clear();
        final URLReference pipIconURL = division.getPipIcon();
        pipIconURL.ifPresent(url -> {
            final Image image = ImageCache.INSTANCE.getImage(url.toExternalForm(), false);
            final PixelReader source = image.getPixelReader();
            final double imageW = image.getWidth();
            final double imageH = image.getHeight();
            final double iconW = imageW / PIP_IMAGE_NUMBER;
            final double iconH = imageH;
            IntStream.range(0, PIP_IMAGE_NUMBER)
                    .forEach(iconIndex -> {
                        final int iconX = (int) Math.ceil(iconIndex * iconW);
                        final int iconY = 0;
                        final WritableImage pipImage = new WritableImage((int) iconW, (int) iconH);
                        final PixelWriter destination = pipImage.getPixelWriter();
                        destination.setPixels(0, 0, (int) iconW, (int) iconH, source, iconX, iconY);
                        if (iconIndex == PIP_IMAGE_NUMBER - 1) {
                            final PixelReader source2 = pipImage.getPixelReader();
                            for (int y = 0; y < iconH; y++) {
                                for (int x = 0; x < iconW; x++) {
                                    int rgb = source2.getArgb(x, y);
                                    if (rgb == 0xFF000000) {
                                        rgb = 0x00000000;
                                        destination.setArgb(x, y, rgb);
                                    }
                                }
                            }
                        }
                        pipIcons.add(pipImage);
                    });
        });
    }

    /**
     * Creates a new division display.
     * @param division The division, never {@code null}.
     */
    private void installDivisionContent(final SeasonDivision division) {
        final List<SeasonTier> tiers = division.getTiers()
                .stream()
                .collect(Collectors.toList());
        // Create new column contraints.
        final List<ColumnConstraints> cContraints = IntStream.range(0, tiers.size())
                .mapToObj(tierIndex -> {
                    final ColumnConstraints columnContraints = new ColumnConstraints();
                    return columnContraints;
                })
                .collect(Collectors.toList());
        gridPane.getColumnConstraints().setAll(cContraints);
        // Pips.
        {
            int currentPoints = 0;
            int tierIndex = 0;
            for (final SeasonTier tier : tiers) {
                final Node node = createPipsForTier(tierIndex, currentPoints, tier, division);
                gridPane.getChildren().addAll(node);
                currentPoints += tier.getPoints();
                tierIndex++;
            }
        }
        // Bottom labels.
        final List<Label> pipsLabels = IntStream.range(0, tiers.size())
                .mapToObj(tierIndex -> createLabelForTier(tierIndex))
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
    private Node createPipsForTier(final int tierIndex, final int cumulatedPoints, final SeasonTier tier, final SeasonDivision division) {
        final HBox result = new HBox();
        result.setId(String.format("tier%dHBox", tierIndex + 1)); // NOI18N.
        result.getStyleClass().add("pip-hbox"); // NOI18N.
        final List<Node> pips = IntStream.range(0, tier.getPoints())
                .mapToObj(pointIndex -> {
                    final int pipIndex = (cumulatedPoints + pointIndex) % (PIP_IMAGE_NUMBER - 1);
                    final Image pipImage = pipIcons.get(pipIndex);
                    final ImageView pipIcon = new ImageView();
                    pipIcon.setImage(pipImage);
                    final Image glowImage = pipIcons.get(PIP_IMAGE_NUMBER - 1);
                    //
                    final ImageView lightenIcon = new ImageView();
                    lightenIcon.setImage(glowImage);
                    final StackPane lightenStack = new StackPane();
                    lightenStack.setOpacity(0.5);
                    lightenStack.setBlendMode(BlendMode.LIGHTEN);
                    lightenStack.getChildren().setAll(lightenIcon);
                    //
                    final ImageView screenIcon = new ImageView();
                    screenIcon.setImage(glowImage);
                    final StackPane screenStack = new StackPane();
                    screenStack.setOpacity(0.5);
                    screenStack.setBlendMode(BlendMode.SCREEN);
                    screenStack.getChildren().setAll(screenIcon);
                    final StackPane iconStack = new StackPane();
                    iconStack.getChildren().setAll(pipIcon, lightenStack, screenStack);
                    return iconStack;
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
