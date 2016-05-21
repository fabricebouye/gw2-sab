/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.characters.pvp;

import api.web.gw2.mapping.v2.characters.CharacterProfession;
import api.web.gw2.mapping.v2.pvp.stats.Stat;
import api.web.gw2.mapping.v2.pvp.stats.StatResult;
import com.bouye.gw2.sab.scene.SABControllerBase;
import com.bouye.gw2.sab.text.LabelUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.IntStream;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

/**
 * FXML controller.
 *
 * @author Fabrice Bouyé
 */
public final class PvPStatsPaneController extends SABControllerBase<PvPStatsPane> {

    @FXML
    private Label rankLabel;
    @FXML
    private ComboBox<PvPStatsPane.ResultType> displayCombo;
    @FXML
    private PieChart professionPieChart;

    /**
     * Creates a new instance.
     */
    public PvPStatsPaneController() {
    }

    /**
     * For some reason the pie chart in the pvp stats screen in game uses the
     * following profession order.
     */
    private final CharacterProfession[] professions = {
        CharacterProfession.GUARDIAN,
        CharacterProfession.WARRIOR,
        CharacterProfession.ENGINEER,
        CharacterProfession.RANGER,
        CharacterProfession.THIEF,
        CharacterProfession.ELEMENTALIST,
        CharacterProfession.MESMER,
        CharacterProfession.NECROMANCER,
        CharacterProfession.REVENANT,};
    private final Map<CharacterProfession, PieChart.Data> dataMap = new LinkedHashMap<>();

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        displayCombo.getItems().setAll(PvPStatsPane.ResultType.values());
        displayCombo.valueProperty().addListener(comboDisplayChangeListener);
        // Pre-allocate pie data.
        Arrays.stream(professions)
                .forEach(profession -> {
                    final PieChart.Data data = new PieChart.Data(profession.name(), 0);
                    dataMap.put(profession, data);
                });
        professionPieChart.getData().setAll(dataMap.values());
        professionPieChart.setStartAngle(90);
        // FB-2016-05-20: needs to be done after the data has been added to the pie chart.
        final Node legend = professionPieChart.lookup(".chart-legend"); // NOI18N.
        final Set<Node> legendItems = legend.lookupAll(".chart-legend-item");
        final Iterator<Node> legendItemsIterator = legendItems.iterator();
        IntStream.range(0, professions.length)
                .forEach(professionIndex -> {
                    final CharacterProfession profession = professions[professionIndex];
                    final Label legendLabel = (Label) legendItemsIterator.next();
                    final PieChart.Data data = dataMap.get(profession);
                    final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(profession);
                    final Node sector = data.getNode();
                    sector.pseudoClassStateChanged(pseudoClass, true);
                    legendLabel.pseudoClassStateChanged(pseudoClass, true);
                    // FB-2016-05-21: does not work when using CSS, forcing this through code.
                    legendLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    // @todo Set profession icon here.
                    final Tooltip tooltip = new Tooltip();
                    Tooltip.install(sector, tooltip);
                    sector.getProperties().put("tooltip", tooltip); // NOI18N.
                });
    }

    @Override
    protected void uninstallNode(final PvPStatsPane node) {
        valueEditing = true;
        try {
            node.statProperty().removeListener(statChangeListener);
            node.displayProperty().removeListener(displayChangeListener);
            displayCombo.getSelectionModel().select(null);
        } finally {
            valueEditing = false;
        }
    }

    @Override
    protected void installNode(final PvPStatsPane node) {
        valueEditing = true;
        try {
            node.statProperty().addListener(statChangeListener);
            node.displayProperty().addListener(displayChangeListener);
            displayCombo.getSelectionModel().select(node.getDisplay());
        } finally {
            valueEditing = false;
        }
    }

    @Override
    protected void updateUI() {
        final Optional<PvPStatsPane> node = parentNode();
        final Stat stat = (node.isPresent()) ? node.get().getStat() : null;
        if (stat == null) {
            rankLabel.setText(null);
            Arrays.stream(professions)
                    .map(dataMap::get)
                    .forEach(data -> {
                        data.setPieValue(0);
                        final Node sector = data.getNode();
                        final Tooltip tooltip = (Tooltip) sector.getProperties().get("tooltip"); // NOI18N.
                        tooltip.setText(null);
                    });
        } else {
            rankLabel.setText(String.valueOf(stat.getPvpRank()));
            final Map<CharacterProfession, StatResult> professionResult = stat.getProfessions();
            final PvPStatsPane.ResultType resultType = node.get().getDisplay();
            int totalValue = Arrays.stream(professions)
                    .mapToInt(profession -> {
                        final StatResult result = professionResult.get(profession);
                        int value = 0;
                        if (result != null) {
                            switch (resultType) {
                                case TOTAL_GAMES:
                                    value = result.getWins() + result.getLosses();
                                    break;
                                case WINS:
                                default:
                                    value = result.getWins();
                            }
                        }
                        return value;
                    })
                    .sum();
            Arrays.stream(professions)
                    .forEach(profession -> {
                        final StatResult result = professionResult.get(profession);
                        if (result != null) {
                            final PieChart.Data data = dataMap.get(profession);
                            int value = 0;
                            switch (resultType) {
                                case TOTAL_GAMES:
                                    value = result.getWins() + result.getLosses();
                                    break;
                                case WINS:
                                default:
                                    value = result.getWins();
                            }
                            double percent = 100 * value / (double) totalValue;
                            data.setPieValue(value);
                            final Node sector = data.getNode();
                            final Tooltip tooltip = (Tooltip) sector.getProperties().get("tooltip"); // NOI18N.
                            final String tip = String.format("%s - %s - %d - %.2f%% - %d%%", profession, resultType, value, percent, (int) Math.rint(percent));
                            tooltip.setText(tip);
                        }
                    });
        }
    }

    private final ChangeListener<Stat> statChangeListener = (observable, oldValue, newValue) -> updateUI();
    private final ChangeListener<PvPStatsPane.ResultType> displayChangeListener = (observable, oldValue, newValue) -> updateUI();

    private boolean valueEditing = false;

    /**
     * Called whenever the display changes in the combo box.
     */
    private final ChangeListener<PvPStatsPane.ResultType> comboDisplayChangeListener = (observable, oldValue, newValue) -> {
        if (valueEditing) {
            return;
        }
        final Optional<PvPStatsPane> node = parentNode();
        parentNode().ifPresent(n -> n.setDisplay(newValue));
    };
}
