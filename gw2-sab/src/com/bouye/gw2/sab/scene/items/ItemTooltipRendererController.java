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
import api.web.gw2.mapping.v2.items.ItemArmorDetails;
import api.web.gw2.mapping.v2.items.ItemArmorType;
import api.web.gw2.mapping.v2.items.ItemBackDetails;
import api.web.gw2.mapping.v2.items.ItemFlag;
import api.web.gw2.mapping.v2.items.ItemInfixUpgrade;
import api.web.gw2.mapping.v2.items.ItemInfixUpgradeAttribute;
import api.web.gw2.mapping.v2.items.ItemInfusionSlot;
import api.web.gw2.mapping.v2.items.ItemInfusionSlotFlag;
import api.web.gw2.mapping.v2.items.ItemRarity;
import api.web.gw2.mapping.v2.items.ItemTrinketDetails;
import api.web.gw2.mapping.v2.items.ItemTrinketType;
import api.web.gw2.mapping.v2.items.ItemType;
import api.web.gw2.mapping.v2.items.ItemWeaponDamageType;
import api.web.gw2.mapping.v2.items.ItemWeaponDetails;
import api.web.gw2.mapping.v2.items.ItemWeaponType;
import com.bouye.gw2.sab.SABConstants;
import com.bouye.gw2.sab.query.ImageCache;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.text.LabelUtils;
import com.bouye.gw2.sab.wrappers.ItemWrapper;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
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
        StringBuilder result = addStats(item, null);
        result = addDescription(item, result);
        result = addRarity(item, result);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asBag(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asBack(final Item item) {
        StringBuilder result = addStats(item, null);
        result = addDescription(item, result);
        result = addRarity(item, result);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asConsumable(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asContainer(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asCraftingMaterial(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asGathering(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asGizmo(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asMinipet(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asTool(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asTrait(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asTrinket(final Item item) {
        StringBuilder result = addStats(item, null);
        result = addDescription(item, result);
        result = addRarity(item, result);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asTrophy(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asUpgradeComponent(final Item item) {
        StringBuilder result = addDescription(item, null);
        result = addItemType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private String asWeapon(final Item item) {
        StringBuilder result = addStats(item, null);
        result = addDescription(item, result);
        result = addRarity(item, result);
        result = addItemType(item, result);
        result = addDamageType(item, result);
        result = addLevelRequirement(item, result);
        result = addMerchantValue(item, result);
        return result.toString();
    }

    private StringBuilder addStats(final Item item, final StringBuilder builder) {
        final StringBuilder result = (builder == null) ? new StringBuilder() : builder;
        item.getDetails().ifPresent(details -> {
            Optional<ItemInfixUpgrade> infixUpgrade = Optional.empty();
            final ItemType itemType = item.getType();
            List<ItemInfusionSlot> infusionSlots = Collections.EMPTY_LIST;
            switch (itemType) {
                case ARMOR: {
                    final ItemArmorDetails armorDetails = (ItemArmorDetails) details;
                    final int defense = armorDetails.getDefense();
                    String defenseStr = LabelUtils.INSTANCE.toStatsUp(String.valueOf(defense));
                    defenseStr = String.format(SABConstants.I18N.getString("item-tip.armor-defense.label"), defenseStr); // NOI18N.
                    result.append(defenseStr);
                    result.append(LabelUtils.INSTANCE.lineBreak());
                    infixUpgrade = armorDetails.getInfixUpgrade();
                    infusionSlots = armorDetails.getInfusionSlots();
                }
                break;
                case BACK: {
                    final ItemBackDetails backDetails = (ItemBackDetails) details;
                    infixUpgrade = backDetails.getInfixUpgrade();
                    infusionSlots = backDetails.getInfusionSlots();
                }
                break;
                case TRINKET: {
                    final ItemTrinketDetails trinketDetails = (ItemTrinketDetails) details;
                    infixUpgrade = trinketDetails.getInfixUpgrade();
                    infusionSlots = trinketDetails.getInfusionSlots();
                }
                break;
                case WEAPON: {
                    final ItemWeaponDetails weaponDetails = (ItemWeaponDetails) details;
                    final int minPower = weaponDetails.getMinPower();
                    final int maxPower = weaponDetails.getMaxPower();
                    String strengthStr = String.format(SABConstants.I18N.getString("item-tip.weapon-strength.format"), minPower, maxPower); // NOI18N.
                    strengthStr = LabelUtils.INSTANCE.toStatsUp(strengthStr);
                    strengthStr = String.format(SABConstants.I18N.getString("item-tip.weapon-strength.label"), strengthStr);  // NOI18N.
                    result.append(strengthStr);
                    result.append(LabelUtils.INSTANCE.lineBreak());
                    infixUpgrade = weaponDetails.getInfixUpgrade();
                    infusionSlots = weaponDetails.getInfusionSlots();
                }
            }
            infixUpgrade.ifPresent(upgrade -> {
                final String text = upgrade.getAttributes()
                        .stream()
                        .map(value -> {
                            final int modifier = value.getModifier();
                            final ItemInfixUpgradeAttribute attribute = value.getAttribute();
                            String statsStr = LabelUtils.INSTANCE.fromItemInfixUpgradeAttribute(attribute);
                            statsStr = String.format(SABConstants.I18N.getString("item-tip.stats-up.format"), modifier, statsStr); // NOI18N.
                            statsStr = LabelUtils.INSTANCE.toStatsUp(statsStr);
                            return statsStr;
                        })
                        .collect(Collectors.joining(LabelUtils.INSTANCE.lineBreak()));
                result.append(text);
                result.append(LabelUtils.INSTANCE.lineBreak());
            });
            if (!infixUpgrade.isPresent()) {
                switch (itemType) {
                    case ARMOR:
                    case BACK:
                    case TRINKET:
                    case WEAPON:
                        result.append(SABConstants.I18N.getString("item-tip.select-stats.label")); // NOI18N.
                        result.append(LabelUtils.INSTANCE.lineBreak());
                        break;
                }
            }
            infusionSlots.stream()
                    .forEach(infusionSlot -> {
                        final ItemInfusionSlotFlag flag = infusionSlot.getFlags().isEmpty() ? ItemInfusionSlotFlag.AGONY : infusionSlot.getFlags().iterator().next();
                        result.append(LabelUtils.INSTANCE.fromItemInfusionSlotFlag(flag));
                        result.append(LabelUtils.INSTANCE.lineBreak());
                    });
        });
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
            final String requiredLevelStr = String.format(SABConstants.I18N.getString("item-tip.required-level.label"), level); // NOI18N.
            result.append(requiredLevelStr);
            result.append(LabelUtils.INSTANCE.lineBreak());
        }
        return result;
    }

    private StringBuilder addMerchantValue(final Item item, final StringBuilder builder) {
        final StringBuilder result = (builder == null) ? new StringBuilder() : builder;
        final CoinAmount vendorValue = item.getVendorValue();
        if (!vendorValue.equals(CoinAmount.ZERO) && !item.getFlags().contains(ItemFlag.NO_SELL)) {
            result.append(LabelUtils.INSTANCE.toCoins(vendorValue));
            result.append(LabelUtils.INSTANCE.lineBreak());
        }
        return result;
    }

    private StringBuilder addRarity(final Item item, final StringBuilder builder) {
        final StringBuilder result = (builder == null) ? new StringBuilder() : builder;
        final ItemRarity rarity = item.getRarity();
        result.append(LabelUtils.INSTANCE.fromItemRarity(rarity));
        result.append(LabelUtils.INSTANCE.lineBreak());
        return result;
    }

    private StringBuilder addItemType(final Item item, final StringBuilder builder) {
        final StringBuilder result = (builder == null) ? new StringBuilder() : builder;
        final ItemType type = item.getType();
        switch (type) {
            case ARMOR:
            case TRINKET:
            case WEAPON: {
                item.getDetails().ifPresent(details -> {
                    switch (type) {
                        case ARMOR: {
                            final ItemArmorDetails armorDetails = (ItemArmorDetails) details;
                            final ItemArmorType armorType = armorDetails.getType();
                            result.append(LabelUtils.INSTANCE.fromItemArmorType(armorType));
                        }
                        break;
                        case TRINKET: {
                            final ItemTrinketDetails trinketDetails = (ItemTrinketDetails) details;
                            final ItemTrinketType trinketType = trinketDetails.getType();
                            result.append(LabelUtils.INSTANCE.fromItemTrinketType(trinketType));
                        }
                        break;
                        case WEAPON: {
                            final ItemWeaponDetails weaponDetails = (ItemWeaponDetails) details;
                            final ItemWeaponType weaponType = weaponDetails.getType();
                            result.append(LabelUtils.INSTANCE.fromItemWeaponType(weaponType));
                            switch (weaponType) {
                                case GREATSWORD:
                                case HAMMER:
                                case LONG_BOW:
                                case RIFLE:
                                case SHORT_BOW:
                                case SPEARGUN:
                                case STAFF:
                                case TRIDENT: {
                                    result.append(LabelUtils.INSTANCE.lineBreak());
                                    final String text = SABConstants.I18N.getString("item-tip.two-handed.label"); // NOI18N.
                                    result.append(LabelUtils.INSTANCE.toNote(text));
                                }
                                break;
                            }
                        }
                        break;
                    }
                });
            }
            break;
            default:
                result.append(LabelUtils.INSTANCE.fromItemType(type));
        }
        result.append(LabelUtils.INSTANCE.lineBreak());
        return result;
    }

    private StringBuilder addDamageType(final Item item, final StringBuilder builder) {
        final StringBuilder result = (builder == null) ? new StringBuilder() : builder;
        final ItemType type = item.getType();
        item.getDetails().ifPresent(details -> {
            switch (type) {
                case WEAPON: {
                    final ItemWeaponDetails weaponDetails = (ItemWeaponDetails) details;
                    final ItemWeaponDamageType damageType = weaponDetails.getDamageType();
                    String damageStr = LabelUtils.INSTANCE.fromItemWeaponDamageType(damageType);
                    damageStr = String.format(SABConstants.I18N.getString("item-tip.damage-type.label"), damageStr); // NOI18N.
                    result.append(LabelUtils.INSTANCE.toNote(damageStr));
                    result.append(LabelUtils.INSTANCE.lineBreak());
                }
            }
        });
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
