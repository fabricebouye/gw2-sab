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
            List<Node> description = Collections.EMPTY_LIST;
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
            descriptionFlow.getChildren().setAll(description);
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

    private List<Node> asArmor(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asBag(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asBack(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asConsumable(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asContainer(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asCraftingMaterial(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asGathering(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asGizmo(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asMinipet(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asTool(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asTrait(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asTrinket(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asTrophy(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asUpgradeComponent(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> asWeapon(final Item item) {
        List<Node> result = addDescription(item, null);
        result = addMerchantValue(item, result);
        return result;
    }

    private List<Node> addDescription(final Item item, final List<Node> list) {
        final List<Node> result = (list == null) ? new LinkedList<>() : list;
        item.getDescription().ifPresent(description -> {
            final String text = normalizeDescription(description);
            result.addAll(LabelUtils.INSTANCE.split(text));
        });
        return result;
    }

    private List<Node> addMerchantValue(final Item item, final List<Node> list) {
        final List<Node> result = (list == null) ? new LinkedList<>() : list;
        final CoinAmount vendorValue = item.getVendorValue();
        if (!vendorValue.equals(CoinAmount.ZERO)) {
            result.addAll(LabelUtils.INSTANCE.fromCopper(vendorValue.toCopper()));
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
