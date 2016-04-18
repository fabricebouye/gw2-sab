/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.specializations;

import api.web.gw2.mapping.core.JsonpUtils;
import api.web.gw2.mapping.core.URLReference;
import api.web.gw2.mapping.v2.characters.CharacterProfession;
import api.web.gw2.mapping.v2.specializations.Specialization;
import com.bouye.gw2.sab.SAB;
import com.bouye.gw2.sab.query.ImageCache;
import com.bouye.gw2.sab.text.LabelUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * Allows to edit a specialization and its traits.
 * @author Fabrice Bouyé
 */
public final class SpecializationEditor extends Region {

    private final ImageView background = new ImageView();
    private final Region eliteMarker = new Region();
    private final Region button = new Region();
    private final Region minor1 = new Minor(1);
    private final Region minor2 = new Minor(2);
    private final Region minor3 = new Minor(3);
    private final ToggleTrait adept1 = new ToggleTrait(1, "adept");
    private final ToggleTrait adept2 = new ToggleTrait(2, "adept");
    private final ToggleTrait adept3 = new ToggleTrait(3, "adept");
    private final ToggleGroup adeptGroup = new ToggleGroup();
    private final ToggleTrait master1 = new ToggleTrait(4, "master");
    private final ToggleTrait master2 = new ToggleTrait(5, "master");
    private final ToggleTrait master3 = new ToggleTrait(6, "master");
    private final ToggleGroup masterGroup = new ToggleGroup();
    private final ToggleTrait grandMaster1 = new ToggleTrait(7, "grand-master");
    private final ToggleTrait grandMaster2 = new ToggleTrait(8, "grand-master");
    private final ToggleTrait grandMaster3 = new ToggleTrait(9, "grand-master");
    private final ToggleGroup grandMasterGroup = new ToggleGroup();
    private final Line connector0 = new Line();
    private final Line connector1 = new Line();
    private final Line connector2 = new Line();
    private final Line connector3 = new Line();
    private final Line connector4 = new Line();
    private final Line connector5 = new Line();

    /**
     * Creates a new instance.
     */
    public SpecializationEditor() {
        super();
        setId("SpecializationEditor"); // NOI18N.
        getStyleClass().add("specialization-editor"); // NOI18N.
        //
        background.setPreserveRatio(false);
        //
        eliteMarker.getStyleClass().add("elite-marker"); // NOI18N.
        //
        button.getStyleClass().add("button"); // NOI18N.
        button.visibleProperty().bind(editableProperty());
        //
        adept1.setToggleGroup(adeptGroup);
        adept2.setToggleGroup(adeptGroup);
        adept3.setToggleGroup(adeptGroup);
        //
        master1.setToggleGroup(masterGroup);
        master2.setToggleGroup(masterGroup);
        master3.setToggleGroup(masterGroup);
        //
        grandMaster1.setToggleGroup(grandMasterGroup);
        grandMaster2.setToggleGroup(grandMasterGroup);
        grandMaster3.setToggleGroup(grandMasterGroup);
        //
        connector0.getStyleClass().addAll("connector"); // NOI18N.
        connector0.visibleProperty().bind(specializationProperty().isNotNull());
        //
        connector1.getStyleClass().addAll("connector"); // NOI18N.
        connector2.getStyleClass().addAll("connector"); // NOI18N.
        connector3.getStyleClass().addAll("connector"); // NOI18N.
        connector4.getStyleClass().addAll("connector"); // NOI18N.
        connector5.getStyleClass().addAll("connector"); // NOI18N.
        getChildren().setAll(background,
                eliteMarker,
                connector0, connector1, connector2, connector3, connector4, connector5,
                minor1,
                adept1, adept2, adept3,
                minor2,
                master1, master2, master3,
                minor3,
                grandMaster1, grandMaster2, grandMaster3,
                button);
        //
        uninstallSpecialization(null);
        profession.addListener(professionChangeListener);
        specializationProperty().addListener(specializationChangeListener);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double width = getWidth();
        final double height = getHeight();
        final Insets insets = getInsets();
        System.out.println(insets);
        final double areaX = insets.getLeft();
        final double areaY = insets.getTop();
        final double areaW = Math.max(0, width - (insets.getLeft() + insets.getRight()));
        final double areaH = Math.max(0, height - (insets.getTop() + insets.getBottom()));
        //
        background.setLayoutX(areaX);
        background.setLayoutY(areaY);
        background.setFitWidth(areaW - 1);
        background.setFitHeight(areaH - 1);
        //
        layoutInArea(eliteMarker, areaX, areaY, areaW, areaH, 0, HPos.LEFT, VPos.TOP);
        //        
        final double minorX1 = areaX + areaW / 3d;
        final double minorX2 = minorX1 + areaW / 4.5d;
        final double minorX3 = minorX2 + areaW / 4.5d;
        final double minorY = areaY + areaH / 2d;
        layoutInArea(minor1, minorX1, minorY, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(minor2, minorX2, minorY, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(minor3, minorX3, minorY, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        final double adeptX = minorX1 + (minorX2 - minorX1) / 2d;
        final double masterX = minorX2 + (minorX2 - minorX1) / 2d;
        final double grandMasterX = minorX3 + (minorX2 - minorX1) / 2d;
        final double adeptY1 = minorY - areaH / 3.5d;
        final double adeptY2 = minorY;
        final double adeptY3 = minorY + areaH / 3.5d;
        layoutInArea(adept1, adeptX, adeptY1, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(adept2, adeptX, adeptY2, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(adept3, adeptX, adeptY3, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(master1, masterX, adeptY1, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(master2, masterX, adeptY2, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(master3, masterX, adeptY3, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(grandMaster1, grandMasterX, adeptY1, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(grandMaster2, grandMasterX, adeptY2, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(grandMaster3, grandMasterX, adeptY3, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        //
        double adeptY = minorY;
        final Toggle selectedAdept = adeptGroup.getSelectedToggle();
        connector1.setVisible(selectedAdept != null);
        connector2.setVisible(selectedAdept != null);
        if (selectedAdept == adept1) {
            adeptY = adeptY1;
        } else if (selectedAdept == adept2) {
            adeptY = adeptY2;
        } else if (selectedAdept == adept3) {
            adeptY = adeptY3;
        }
        //
        final double buttonX = minorX1 - areaW / 6d;
        final double buttonY = minorY;
        layoutInArea(button, buttonX, buttonY, -1, -1, 0, HPos.CENTER, VPos.CENTER);
        //
        connector0.setStartX(buttonX);
        connector0.setStartY(buttonY);
        connector0.setEndX(minorX1);
        connector0.setEndY(minorY);
        //
        connector1.setStartX(minorX1);
        connector1.setStartY(minorY);
        connector1.setEndX(adeptX);
        connector1.setEndY(adeptY);
        //
        connector2.setStartX(adeptX);
        connector2.setStartY(adeptY);
        connector2.setEndX(minorX2);
        connector2.setEndY(minorY);
        //
        double masterY = minorY;
        final Toggle selectedMaster = masterGroup.getSelectedToggle();
        connector3.setVisible(selectedMaster != null);
        connector4.setVisible(selectedMaster != null);
        if (selectedMaster == master1) {
            masterY = adeptY1;
        } else if (selectedMaster == master2) {
            masterY = adeptY2;
        } else if (selectedMaster == master3) {
            masterY = adeptY3;
        }
        //
        connector3.setStartX(minorX2);
        connector3.setStartY(minorY);
        connector3.setEndX(masterX);
        connector3.setEndY(masterY);
        //
        connector4.setStartX(masterX);
        connector4.setStartY(masterY);
        connector4.setEndX(minorX3);
        connector4.setEndY(minorY);
        //
        double grandMasterY = minorY;
        final Toggle selectedGrandMaster = grandMasterGroup.getSelectedToggle();
        connector5.setVisible(selectedGrandMaster != null);
        if (selectedGrandMaster == grandMaster1) {
            grandMasterY = adeptY1;
        } else if (selectedGrandMaster == grandMaster2) {
            grandMasterY = adeptY2;
        } else if (selectedGrandMaster == grandMaster3) {
            grandMasterY = adeptY3;
        }
        //
        connector5.setStartX(minorX3);
        connector5.setStartY(minorY);
        connector5.setEndX(grandMasterX);
        connector5.setEndY(grandMasterY);
    }

    @Override
    public String getUserAgentStylesheet() {
        final URL url = SAB.class.getResource("styles/scene/specializations/SpecializationEditor.css"); // NOI18N.
        return url.toExternalForm();
    }

    private final ChangeListener<CharacterProfession> professionChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallProfession);
        Optional.ofNullable(newValue)
                .ifPresent(this::installProfession);
    };

    private final ChangeListener<Specialization> specializationChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallSpecialization);
        Optional.ofNullable(newValue)
                .ifPresent(this::installSpecialization);
    };

    ////////////////////////////////////////////////////////////////////////////
    private void uninstallProfession(final CharacterProfession profession) {
        final PseudoClass pseudoClass = PseudoClass.getPseudoClass(JsonpUtils.INSTANCE.javaEnumToJavaClassName(profession));
        pseudoClassStateChanged(pseudoClass, false);
    }

    private void installProfession(final CharacterProfession profession) {
        final PseudoClass pseudoClass = PseudoClass.getPseudoClass(JsonpUtils.INSTANCE.javaEnumToJavaClassName(profession));
        pseudoClassStateChanged(pseudoClass, true);
    }

    private void uninstallSpecialization(final Specialization specialization) {
        setProfession(null);
        background.setImage(null);
        background.setViewport(null);
        eliteMarker.setVisible(false);
    }

    private void installSpecialization(final Specialization specialization) {
        // Profession.
        setProfession(specialization.getProfession());
        // Spec background.
        final URLReference backgroundURL = specialization.getBackground();
        backgroundURL.ifPresent(url -> {
            System.out.println(url);
            final String urlValue = url.toExternalForm();
            final Image specializationBackground = ImageCache.INSTANCE.getImage(urlValue);
            if (specializationBackground == null) {
                return;
            }
            specializationBackground.progressProperty().addListener((ob, ov, nv) -> {
                if (nv.intValue() == 1) {
                    // Specialization images contain large blank areas that need to be removed.
                    Rectangle2D crop = getOverrideCropArea(urlValue);
                    if (crop == Rectangle2D.EMPTY) {
                        crop = autoCropBackgroundImage(specializationBackground);
                    }
                    background.setViewport(crop);
                    background.setImage(specializationBackground);
                }
            });
        });
        // Elite marker.
        eliteMarker.setVisible(specialization.isElite());
    }

    ////////////////////////////////////////////////////////////////////////////    
    /**
     * Sets wether this control is editable.
     */
    public final BooleanProperty editable = new SimpleBooleanProperty(this, "editable", true); // NOI18N.

    public final boolean isEditable() {
        return editable.get();
    }

    public final void setEditable(final boolean value) {
        editable.set(value);
    }

    public final BooleanProperty editableProperty() {
        return editable;
    }

    private final ReadOnlyObjectWrapper<CharacterProfession> profession = new ReadOnlyObjectWrapper<>(this, "profession", null); // NOI18N.

    public final CharacterProfession getProfession() {
        return profession.get();
    }

    public void setProfession(final CharacterProfession value) {
        profession.set(value);
    }

    public final ReadOnlyObjectProperty<CharacterProfession> professionProperty() {
        return profession.getReadOnlyProperty();
    }

    private final ObjectProperty<Specialization> specialization = new SimpleObjectProperty<>(this, "specialization", null); // NOI18N.

    public final Specialization getSpecialization() {
        return specialization.get();
    }

    public final void setSpecialization(final Specialization value) {
        specialization.set(value);
    }

    public final ObjectProperty<Specialization> specializationProperty() {
        return specialization;
    }

    ////////////////////////////////////////////////////////////////////////////    
    private static final int BLACK_COLOR = 0xFF000000;
    private static final int TRANSPARENT_COLOR = 0x00000000;

    private static Properties CROP_DEFS;

    private static void loadCropDefs() {
        if (CROP_DEFS == null) {
            CROP_DEFS = new Properties();
            try (final InputStream input = SAB.class.getResource("properties/scene/specializations/backgroundcrop.properties").openStream()) {
                CROP_DEFS.load(input);
            } catch (IOException ex) {
                Logger.getLogger(SpecializationEditor.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Gets the override value for the crop area of this specialization background image.
     * @param urlValue The URL to the specialization background image.
     * @return The crop rectangle or {@code Rectangle2D.EMPTY} if no override value found.
     */
    private static Rectangle2D getOverrideCropArea(final String urlValue) {
        loadCropDefs();
        final String imageFile = urlValue.substring(urlValue.lastIndexOf('/') + 1, urlValue.length()); // NOI18N.
        Rectangle2D result = Rectangle2D.EMPTY;
        for (String name : new String[]{"default", imageFile}) {
            final String cropDef = CROP_DEFS.getProperty(name);
            if (cropDef != null) {
                final String[] tokens = cropDef.split("\\s+"); // NOI18N.
                if (tokens.length == 4) {
                    try {
                        final int x = Integer.parseInt(tokens[0]);
                        final int y = Integer.parseInt(tokens[1]);
                        final int w = Integer.parseInt(tokens[2]);
                        final int h = Integer.parseInt(tokens[3]);
                        result = new Rectangle2D(x, y, w, h);
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(SpecializationEditor.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
            if (result != Rectangle2D.EMPTY) {
                break;
            }
        }
        return result;
    }

    /**
     * For most specialization background images, the empty space is just plain black pixels.
     * @param image The image to crop, never {@code null}.
     * @return The crop rectangle.
     */
    private static Rectangle2D autoCropBackgroundImage(final Image image) {
        final PixelReader sourceReader = image.getPixelReader();
        final int imageWidth = (int) Math.ceil(image.getWidth());
        final int imageHeight = (int) Math.ceil(image.getHeight());
        int cutoutX = 0;
        int cutoutY = 0;
        int cutoutW = imageWidth;
        int cutoutH = imageHeight;
        for (int y = 0; y < imageHeight; y++) {
            final int pixel = sourceReader.getArgb(0, y);
            if (pixel != BLACK_COLOR && pixel != TRANSPARENT_COLOR) {
                cutoutY = y;
                cutoutH = imageHeight - cutoutY;
                break;
            }
        }
        for (int x = imageWidth - 1; x >= 0; x--) {
            final int pixel = sourceReader.getArgb(x, imageHeight - 1);
            if (pixel != BLACK_COLOR && pixel != TRANSPARENT_COLOR) {
                cutoutW = x;
                break;
            }
        }
        return new Rectangle2D(cutoutX, cutoutY, cutoutW, cutoutH);
    }

    private final class Minor extends StackPane {

        public Minor(final int index) {
            super();
            //
            getStyleClass().addAll("trait", "minor", "minor" + index); // NOI18N.
            // Label.
            final String indexStr = String.valueOf(index);
            final Text label = new Text(indexStr);
            getChildren().add(label);
        }
    }

    /**
     * Toggle that allow to activate / deactivate a trait.
     * @author Fabrice Bouyé
     */
    private final class ToggleTrait extends StackPane implements Toggle {

        /**
         * Creates a new instance.
         * @param index The index of the trait.
         */
        public ToggleTrait(final int index, final String type) {
            super();
            //
            final int offset = ((index - 1) % 3) + 1;
            getStyleClass().addAll("trait", type, type + offset); // NOI18N.
            // Label.
            final String romanIndex = LabelUtils.INSTANCE.toRomanNumeral(index);
            final Text label = new Text(romanIndex);
            getChildren().add(label);
            // Listeners.
            toggleGroupProperty().addListener(toggleGroupChangeListener);
            selectedProperty().addListener(selectedChangeListener);
            setOnMouseClicked(mouseEventHandler);
        }

        /**
         * Called whenever the toggle group changes.
         */
        private final ChangeListener<ToggleGroup> toggleGroupChangeListener = (observable, oldValue, newValue) -> {
            final Optional<ToggleGroup> oldToggleGroup = Optional.ofNullable(oldValue);
            oldToggleGroup.ifPresent(tg -> tg.getToggles().remove(ToggleTrait.this));
            final Optional<ToggleGroup> newToggleGroup = Optional.ofNullable(newValue);
            newToggleGroup.ifPresent(tg -> tg.getToggles().add(ToggleTrait.this));
        };

        /**
         * Called whenever the selected state of this trait changes.
         */
        private final ChangeListener<Boolean> selectedChangeListener = (observable, oldValue, newValue) -> {
            final Optional<ToggleGroup> toggleGroup = Optional.ofNullable(getToggleGroup());
            toggleGroup.ifPresent(tg -> tg.selectToggle(newValue ? ToggleTrait.this : null));
            ToggleTrait.this.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), newValue); // NOI18N.
        };

        /**
         * Called whenever the user clicks on this trait.
         */
        private final EventHandler<MouseEvent> mouseEventHandler = mouseEvent -> {
            if (isEditable() && getSpecialization() != null) {
                setSelected(true);
                SpecializationEditor.this.requestLayout();
            }
        };

        private final ObjectProperty<ToggleGroup> toggleGroup = new SimpleObjectProperty<>(this, "toggleGroup", null); // NOI18N.

        @Override
        public ToggleGroup getToggleGroup() {
            return toggleGroup.get();
        }

        @Override
        public void setToggleGroup(final ToggleGroup value) {
            toggleGroup.set(value);
        }

        @Override
        public ObjectProperty<ToggleGroup> toggleGroupProperty() {
            return toggleGroup;
        }

        private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);

        @Override
        public boolean isSelected() {
            return selected.get();
        }

        @Override
        public void setSelected(final boolean value) {
            selected.set(value);
        }

        @Override
        public BooleanProperty selectedProperty() {
            return selected;
        }
    }
}
