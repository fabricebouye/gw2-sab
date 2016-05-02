/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.items;

import api.web.gw2.mapping.core.CoinAmount;
import api.web.gw2.mapping.v2.items.Item;
import api.web.gw2.mapping.v2.items.ItemRarity;
import api.web.gw2.mapping.v2.items.ItemType;
import com.bouye.gw2.sab.query.ImageCache;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.text.LabelUtils;
import com.bouye.gw2.sab.wrappers.ItemWrapper;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class.
 * @author Fabrice Bouyé
 */
public final class ItemTooltipRendererController extends SABControllerBase<ItemTooltipRenderer> {

    @FXML
    private StackPane iconContainer;
    @FXML
    private Label nameLabel;
    @FXML
    private TextFlow descriptionFlow;

    /**
     * Creates a new instance.
     */
    public ItemTooltipRendererController() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        nameLabel.setText(null);
        descriptionFlow.getChildren().clear();
    }

    @Override
    protected void uninstallNode(final ItemTooltipRenderer node) {
        node.itemProperty().removeListener(itemChangeListener);
    }

    @Override
    protected void installNode(final ItemTooltipRenderer node) {
        node.itemProperty().addListener(itemChangeListener);
    }

    @Override
    protected void updateUI() {
        final Optional<ItemTooltipRenderer> node = parentNode();
        final ItemWrapper wrapper = (!node.isPresent()) ? null : node.get().getItem();
        if (wrapper == null) {
            node.ifPresent(n -> Stream.of(ItemRarity.values())
                    .forEach(rarity -> {
                        final PseudoClass rarityPseudoClass = LabelUtils.INSTANCE.toPseudoClass(rarity);
                        n.pseudoClassStateChanged(rarityPseudoClass, false);
                    }));
            nameLabel.setText(null);
            descriptionFlow.getChildren().clear();
            iconContainer.getChildren().clear();
        } else {
            final Item item = wrapper.getItem();
            node.ifPresent(n -> {
                final ItemRarity rarity = item.getRarity();
                final PseudoClass rarityPseudoClass = LabelUtils.INSTANCE.toPseudoClass(rarity);
                n.pseudoClassStateChanged(rarityPseudoClass, true);
            });
            nameLabel.setText(item.getName());
            final ItemType type = item.getType();
            String description = "";
            switch (type) {
                case ARMOR:
                    description = asArmor(item);
                    break;
                case BAG:
                    description = asBag(item);
                    break;
                case BACK:
                    description = asBack(item);
                    break;
                case CONSUMABLE:
                    description = asConsumable(item);
                    break;
                case CONTAINER:
                    description = asContainer(item);
                    break;
                case CRAFTING_MATERIAL:
                    description = asCraftingMaterial(item);
                    break;
                case GATHERING:
                    description = asGathering(item);
                    break;
                case GIZMO:
                    description = asGizmo(item);
                    break;
                case MINI_PET:
                    description = asMinipet(item);
                    break;
                case TOOL:
                    description = asTool(item);
                    break;
                case TRAIT:
                    description = asTrait(item);
                    break;
                case TRINKET:
                    description = asTrinket(item);
                    break;
                case TROPHY:
                    description = asTrophy(item);
                    break;
                case UPGRADE_COMPONENT:
                    description = asUpgradeComponent(item);
                    break;
                case WEAPON:
                    description = asWeapon(item);
                    break;
                case UNKNOWN:
                default:
            }
            descriptionFlow.getChildren().setAll(LabelUtils.INSTANCE.split(description));
            item.getIcon().ifPresent(url -> {
                final Image image = ImageCache.INSTANCE.getImage(url.toExternalForm());
                final ImageView imageView = new ImageView();
                // @todo switch to CSS whenever those property become styleable.
                imageView.fitWidthProperty().bind(iconContainer.prefWidthProperty());
                imageView.fitHeightProperty().bind(iconContainer.prefHeightProperty());
                imageView.setImage(image);
                iconContainer.getChildren().add(imageView);
            });
        }
    }

    private String asArmor(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asBag(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asBack(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asConsumable(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asContainer(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asCraftingMaterial(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asGathering(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asGizmo(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asMinipet(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asTool(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asTrait(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asTrinket(final Item item) {
        StringBuilder result = addStats(item, null);
        result = addDescription(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asTrophy(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asUpgradeComponent(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asWeapon(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private StringBuilder addStats(final Item item, final StringBuilder builder) {
        final StringBuilder result = (builder == null) ? new StringBuilder() : builder;
        return result;
    }

    private StringBuilder addDescription(final Item item, final StringBuilder builder) {
        final StringBuilder result = (builder == null) ? new StringBuilder() : builder;
        item.getDescription().ifPresent(description -> {
            final String text = normalizeDescription(description);
            result.append(text);
        });
        return result;
    }

    private StringBuilder addLevelRequirement(final Item item, final StringBuilder builder) {
        final StringBuilder result = (builder == null) ? new StringBuilder() : builder;
        final int level = item.getLevel();
        if (level > 0) {
            // @todo Localize.
            result.append("Required Level: ");
            result.append(level);
        }
        return result;
    }

    private StringBuilder addMerchantValue(final Item item, final StringBuilder builder) {
        final StringBuilder result = (builder == null) ? new StringBuilder() : builder;
        final CoinAmount vendorValue = item.getVendorValue();
        if (!vendorValue.equals(CoinAmount.ZERO)) {
            result.append(LabelUtils.INSTANCE.toCoins(vendorValue));
        }
        return result;
    }

    private String normalizeDescription(final String description) {
        final StringBuilder builder = new StringBuilder();
        String text = description;
        text = text.replaceAll("\n", LabelUtils.INSTANCE.lineBreak()); // NOI18N.
        if (description.startsWith("<c=@flavor>")) { // NOI18N.
            text = text.replaceAll("<c=@flavor>", ""); // NOI18N.
            text = LabelUtils.INSTANCE.toQuote(text);
        }
        builder.append(text);
        builder.append(LabelUtils.INSTANCE.lineBreak());
        return builder.toString();
    }

    private final ChangeListener<ItemWrapper> itemChangeListener = (observable, oldValue, newValue) -> updateUI();
}
