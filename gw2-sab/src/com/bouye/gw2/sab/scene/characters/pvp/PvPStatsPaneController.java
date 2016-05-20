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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

/**
 * FXML controller.
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

    private final Map<CharacterProfession, PieChart.Data> dataMap = new LinkedHashMap<>();

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        displayCombo.getItems().setAll(PvPStatsPane.ResultType.values());
        displayCombo.valueProperty().addListener(comboDisplayChangeListener);
        // Pre-allocate pie data.
        Arrays.stream(CharacterProfession.values())
                .filter(profession -> profession != CharacterProfession.UNKNOWN)
                .forEach(profession -> {
                    final PieChart.Data data = new PieChart.Data(profession.name(), 0);
                    dataMap.put(profession, data);
                });
        professionPieChart.getData().setAll(dataMap.values());
        // FB-2016-05-20: needs to be done after the data has been added to the pie chart.
        Arrays.stream(CharacterProfession.values())
                .filter(profession -> profession != CharacterProfession.UNKNOWN)
                .forEach(profession -> {
                    final PieChart.Data data = dataMap.get(profession);
                    final PseudoClass pseudoClass = LabelUtils.INSTANCE.toPseudoClass(profession);
                    data.getNode().pseudoClassStateChanged(pseudoClass, true);
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
            Arrays.stream(CharacterProfession.values())
                    .filter(profession -> profession != CharacterProfession.UNKNOWN)
                    .map(dataMap::get)
                    .forEach(data -> data.setPieValue(0));
        } else {
            rankLabel.setText(String.valueOf(stat.getPvpRank()));
            final Map<CharacterProfession, StatResult> professionResult = stat.getProfessions();
            final PvPStatsPane.ResultType resultType = node.get().getDisplay();
            Arrays.stream(CharacterProfession.values())
                    .filter(profession -> profession != CharacterProfession.UNKNOWN)
                    .forEach(profession -> {
                        final StatResult result = professionResult.get(profession);
                        if (result != null) {
                            final PieChart.Data data = dataMap.get(profession);
                            double value = 0;
                            switch (resultType) {
                                case BYES:
                                    value = result.getByes();
                                    break;
                                case DESERTIONS:
                                    value = result.getDesertions();
                                    break;
                                case FORFEITS:
                                    value = result.getForfeits();
                                    break;
                                case LOSSES:
                                    value = result.getLosses();
                                    break;
                                case WINS:
                                default:
                                    value = result.getWins();
                            }
                            data.setPieValue(value);
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
