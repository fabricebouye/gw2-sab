/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.wvw;

import api.web.gw2.mapping.v2.wvw.matches.MatchTeam;
import com.bouye.gw2.sab.SABControllerBase;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

/**
 * FXML Controller class
 * @author fabriceb
 */
public final class WvwSummaryPaneController extends SABControllerBase {

    @FXML
    private PieChart mainPieChart;
    @FXML
    private PieChart ebPieChart;
    @FXML
    private PieChart redChart;
    @FXML
    private PieChart bluePieChart;
    @FXML
    private PieChart greenPieChart;

    // Valid teams for match results.
    private final List<MatchTeam> teams = Arrays.stream(MatchTeam.values())
            .filter(team -> (team != MatchTeam.NEUTRAL) && (team != MatchTeam.UNKNOWN))
            .collect(Collectors.toList());

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        // Prepare pie charts.
        teams.stream()
                .forEach(team -> {
                    mainPieChart.getData().add(new PieChart.Data(team.name(), 0));
                    ebPieChart.getData().add(new PieChart.Data(team.name(), 0));
                    redChart.getData().add(new PieChart.Data(team.name(), 0));
                    bluePieChart.getData().add(new PieChart.Data(team.name(), 0));
                    greenPieChart.getData().add(new PieChart.Data(team.name(), 0));
                });
    }
}
