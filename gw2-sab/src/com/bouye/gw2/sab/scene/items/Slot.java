/* 
 * Copyright (C) 2016-2017 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.items;

import api.web.gw2.mapping.v2.finishers.Finisher;
import api.web.gw2.mapping.v2.gliders.Glider;
import api.web.gw2.mapping.v2.items.Item;
import api.web.gw2.mapping.v2.items.ItemRarity;
import api.web.gw2.mapping.v2.skins.Skin;
import com.bouye.gw2.sab.query.ImageCache;
import com.bouye.gw2.sab.scene.SABUIUtils;
import com.bouye.gw2.sab.text.LabelUtils;
import com.bouye.gw2.sab.wrappers.ItemWrapper;
import com.bouye.gw2.sab.wrappers.TreasuryWrapper;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 * Renders an item, skin or other into a square slot.
 * @author Fabrice Bouyé
 */
public final class Slot extends Region {

    private static final String STYLE_CLASS = "slot"; // NOI18N.
    public static final PseudoClass SMALL_PSEUDO_CLASS = PseudoClass.getPseudoClass("small"); // NOI18N.
    public static final PseudoClass LARGE_PSEUDO_CLASS = PseudoClass.getPseudoClass("large"); // NOI18N.
    public static final PseudoClass EXTRA_LARGE_PSEUDO_CLASS = PseudoClass.getPseudoClass("x-large"); // NOI18N.
    public static final PseudoClass EXTRA_EXTRA_LARGE_PSEUDO_CLASS = PseudoClass.getPseudoClass("xx-large"); // NOI18N.

    private final ImageView icon = new ImageView();
    private final Region border = new Region();

    /**
     * Creates a empty instance.
     */
    public Slot() {
        super();
        init();
    }

    /**
     * Creates a new item instance.
     * @param item The item to show.
     */
    public Slot(final Item item) {
        super();
        init();
        setValue(item);
    }

    /**
     * Creates a new skin instance.
     * @param skin The skin to show.
     */
    public Slot(final Skin skin) {
        super();
        init();
        setValue(skin);
    }

    /**
     * Creates a new treasury instance.
     * @param treasuryWrapper The wrapper to the treasury to show.
     */
    public Slot(final TreasuryWrapper treasuryWrapper) {
        super();
        init();
        setValue(treasuryWrapper);

    }

    /**
     * Creates a new glider instance.
     * @param glider The skin to show.
     */
    public Slot(final Glider glider) {
        super();
        init();
        setValue(glider);
    }

    /**
     * Creates a new finisher instance.
     * @param finisher The finisher to show.
     */
    public Slot(final Finisher finisher) {
        super();
        init();
        setValue(finisher);
    }

//    @Override
//    public boolean isResizable() {
//        return false;
//    }
    @Override
    public String getUserAgentStylesheet() {
        final URL url = getClass().getResource("/com/bouye/gw2/sab/styles/scene/items/Slot.css");
        return (url == null) ? null : url.toExternalForm();
    }

    /**
     * Initialize the slot.
     */
    private void init() {
        setId(STYLE_CLASS);
        getStyleClass().add(STYLE_CLASS);
        //
        icon.setId("icon");
        icon.getStyleClass().add("icon");
        icon.setMouseTransparent(true);
        icon.fitWidthProperty().bind(prefWidthProperty());
        icon.fitHeightProperty().bind(prefHeightProperty());
        //
        border.setId("border");
        border.getStyleClass().add("border");
        border.setMouseTransparent(true);
        border.minWidthProperty().bind(minWidthProperty());
        border.minHeightProperty().bind(minHeightProperty());
        border.prefWidthProperty().bind(prefWidthProperty());
        border.prefHeightProperty().bind(prefHeightProperty());
        border.maxWidthProperty().bind(maxWidthProperty());
        border.maxHeightProperty().bind(maxHeightProperty());
        //
        getChildren().addAll(icon, border);
        tooltip.addListener(tooltipChangeListener);
        showRarityProperty().addListener(observable -> Optional.ofNullable(getValue()).ifPresent(this::installValue));
        showTooltipProperty().addListener(observable -> Optional.ofNullable(getValue()).ifPresent(this::installValue));
        valueProperty().addListener(valueChangeListener);
    }

    protected void dispose() {
        tooltip.removeListener(tooltipChangeListener);
        valueProperty().removeListener(valueChangeListener);
        uninstallValue(null);
        getChildren().clear();
        icon.fitWidthProperty().unbind();
        icon.fitHeightProperty().unbind();
        border.minWidthProperty().unbind();
        border.minHeightProperty().unbind();
        border.prefWidthProperty().unbind();
        border.prefHeightProperty().unbind();
        border.maxWidthProperty().unbind();
        border.maxHeightProperty().unbind();
    }

    /**
     * Called whenever the value changes.
     */
    private final ChangeListener<Object> valueChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallValue);
        Optional.ofNullable(newValue)
                .ifPresent(this::installValue);
    };

    /**
     * Called whenever the tooltip changes.
     */
    private final ChangeListener<Tooltip> tooltipChangeListener = (observable, oldValue, newValue) -> {
        Optional.ofNullable(oldValue)
                .ifPresent(this::uninstallTooltip);
        Optional.ofNullable(newValue)
                .ifPresent(this::installTooltip);
    };

    /**
     * Uninstall a value, revert to the blank state.
     * @param value The value to install.
     */
    private void uninstallValue(final Object value) {
        uninstallIcon(null);
        uninstallTooltip(null);
    }

    /**
     * Install a new value.
     * @param value The value to install. 
     */
    private void installValue(final Object value) {
        if (value instanceof Item) {
            asItem((Item) value);
        } else if (value instanceof Skin) {
            asSkin((Skin) value);
        } else if (value instanceof TreasuryWrapper) {
            asTreasury((TreasuryWrapper) value);
        } else if (value instanceof Glider) {
            asGlider((Glider) value);
        } else if (value instanceof Finisher) {
            asFinisher((Finisher) value);
        }
    }

    /**
     * Displays an item in the slot.
     * @param item The item, never {@code null}.
     */
    private void asItem(final Item item) {
        item.getIcon()
                .ifPresent(this::installIcon);
        // @todo generate tooltip.
        if (isShowTooltip()) {
            final ItemWrapper wrapper = new ItemWrapper(item, null);
            final ItemTooltipRenderer renderer = new ItemTooltipRenderer();
            renderer.setItem(wrapper);
            final Tooltip tooltip = new Tooltip();
            tooltip.setGraphic(renderer);
            this.tooltip.set(tooltip);
        }
        SABUIUtils.INSTANCE.updateRarityStyle(this, isShowRarity() ? item.getRarity() : null);
    }

    /**
     * Displays a skin in the slot.
     * @param skin The skin, never {@code null}.
     */
    private void asSkin(final Skin skin) {
        skin.getIcon()
                .ifPresent(this::installIcon);
        // @todo generate tooltip.
        SABUIUtils.INSTANCE.updateRarityStyle(this, isShowRarity() ? skin.getRarity() : null);
    }

    /**
     * Displays a guild treasury in the slot.
     * @param treasuryWrapper The wrapper, never {@code null}.
     */
    private void asTreasury(final TreasuryWrapper treasuryWrapper) {
        Optional.of(treasuryWrapper.getItem())
                .ifPresent(this::asItem);
        // @todo generate tooltip.
    }

    /**
     * Displays a glider in the slot.
     * @param glider The glider, never {@code null}.
     */
    private void asGlider(final Glider glider) {
        glider.getIcon()
                .ifPresent(this::installIcon);
        // @todo generate tooltip.
    }

    /**
     * Displays a finisher in the slot.
     * @param finisher The finisher, never {@code null}.
     */
    private void asFinisher(final Finisher finisher) {
        finisher.getIcon()
                .ifPresent(this::installIcon);
        // @todo generate tooltip.
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Remove icon from to the slot.
     * @param url The url to the icon.
     */
    private void uninstallIcon(final URL url) {
        icon.setImage(null);
    }

    /**
     * Apply given URL to the slot.
     * @param url The url to the icon.
     */
    private void installIcon(final URL url) {
        final String path = url.toExternalForm();
        final Image image = ImageCache.INSTANCE.getImage(path);
        icon.setImage(image);
    }

    private void uninstallTooltip(final Tooltip tooltip) {
        Tooltip.uninstall(this, tooltip);
    }

    private void installTooltip(final Tooltip tooltip) {
        Tooltip.install(this, tooltip);
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * The value to show in this slot.
     */
    private final ObjectProperty value = new SimpleObjectProperty(this, "value", null); // NOI18N.

    public final Object getValue() {
        return value.get();
    }

    public final void setValue(final Object value) {
        this.value.set(value);
    }

    public final ObjectProperty valueProperty() {
        return value;
    }

    /**
     * If {@code true} the slot will display a tooltip with detailed information.
     */
    private final BooleanProperty showTooltip = new SimpleBooleanProperty(this, "showTooltip", true); // NOI18N.

    public final void setShowTooltip(final boolean value) {
        showTooltip.set(value);
    }

    public final boolean isShowTooltip() {
        return showTooltip.get();
    }

    public final BooleanProperty showTooltipProperty() {
        return showTooltip;
    }

    /**
     * If {@code true} the slot will display a colored border indicating the rarity of the object contained in the cell.
     */
    private final BooleanProperty showRarity = new SimpleBooleanProperty(this, "showRarity", false); // NOI18N.

    public final void setShowRarity(final boolean value) {
        showRarity.set(value);
    }

    public final boolean isShowRarity() {
        return showRarity.get();
    }

    public final BooleanProperty showRarityProperty() {
        return showRarity;
    }

    private final ObjectProperty<Tooltip> tooltip = new SimpleObjectProperty<>(this, "tooltip", null); // NOI18N.
}
