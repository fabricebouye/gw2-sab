/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.wvw;

import api.web.gw2.mapping.v2.wvw.matches.Match;
import api.web.gw2.mapping.v2.wvw.matches.MatchTeam;
import com.bouye.gw2.sab.scene.SABControllerBase;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public final class WvwMatchesRowController extends SABControllerBase {

    @FXML
    private GridPane rootPane;
    @FXML
    private Label rankLabel1;
    @FXML
    private Tooltip rankTip1;
    @FXML
    private TextFlow nameLabel1;
    @FXML
    private Label scoreLabel1;
    @FXML
    private Tooltip scoreTip1;
    @FXML
    private ProgressBar scoreProgressBar1;
    @FXML
    private Tooltip scoreProgressTip1;
    @FXML
    private Label incomeLabel1;
    @FXML
    private Tooltip incomeTip1;
    @FXML
    private Label campsLabel1;
    @FXML
    private Tooltip campsTip1;
    @FXML
    private Label towersLabel1;
    @FXML
    private Tooltip towersTip1;
    @FXML
    private Label keepsLabel1;
    @FXML
    private Tooltip keepsTip1;
    @FXML
    private Label castleLabel1;
    @FXML
    private Tooltip castleTip1;
    @FXML
    private Label rankLabel2;
    @FXML
    private Tooltip rankTip2;
    @FXML
    private TextFlow nameLabel2;
    @FXML
    private Label scoreLabel2;
    @FXML
    private Tooltip scoreTip2;
    @FXML
    private ProgressBar scoreProgressBar2;
    @FXML
    private Tooltip scoreProgressTip2;
    @FXML
    private Label incomeLabel2;
    @FXML
    private Tooltip incomeTip2;
    @FXML
    private Label campsLabel2;
    @FXML
    private Tooltip campsTip2;
    @FXML
    private Label towersLabel2;
    @FXML
    private Tooltip towersTip2;
    @FXML
    private Label keepsLabel2;
    @FXML
    private Tooltip keepsTip2;
    @FXML
    private Label castleLabel2;
    @FXML
    private Tooltip castleTip2;
    @FXML
    private Label rankLabel3;
    @FXML
    private Tooltip rankTip3;
    @FXML
    private TextFlow nameLabel3;
    @FXML
    private Label scoreLabel3;
    @FXML
    private Tooltip scoreTip3;
    @FXML
    private ProgressBar scoreProgressBar3;
    @FXML
    private Tooltip scoreProgressTip3;
    @FXML
    private Label incomeLabel3;
    @FXML
    private Tooltip incomeTip3;
    @FXML
    private Label campsLabel3;
    @FXML
    private Tooltip campsTip3;
    @FXML
    private Label towersLabel3;
    @FXML
    private Tooltip towersTip3;
    @FXML
    private Label keepsLabel3;
    @FXML
    private Tooltip keepsTip3;
    @FXML
    private Label castleLabel3;
    @FXML
    private Tooltip castleTip3;
    @FXML
    private PieChart incomePieChart;
    /**
    * In WvW pies, blue comes ahead of green.
    */
    private List<MatchTeam> teams = Collections.unmodifiableList(Arrays.asList(MatchTeam.BLUE, MatchTeam.GREEN, MatchTeam.RED));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearUIContent();
        incomePieChart.getData().setAll(teams.stream()
                .map(team -> new PieChart.Data(team.name(), 0))
                .collect(Collectors.toList()));
    }

    private void clearUIContent() {
        rankLabel1.setText(null);
        rankTip1.setText(null);
        nameLabel1.getChildren().clear();
        scoreLabel1.setText(null);
        scoreTip1.setText(null);
        scoreProgressTip1.setText(null);
        incomeLabel1.setText(null);
        incomeTip1.setText(null);
        campsLabel1.setText(null);
        campsTip1.setText(null);
        towersLabel1.setText(null);
        towersTip1.setText(null);
        keepsLabel1.setText(null);
        keepsTip1.setText(null);
        castleLabel1.setText(null);
        castleTip1.setText(null);
        //
        rankLabel2.setText(null);
        rankTip2.setText(null);
        nameLabel2.getChildren().clear();
        scoreLabel2.setText(null);
        scoreTip2.setText(null);
        scoreProgressTip2.setText(null);
        incomeLabel2.setText(null);
        incomeTip2.setText(null);
        campsLabel2.setText(null);
        campsTip2.setText(null);
        towersLabel2.setText(null);
        towersTip2.setText(null);
        keepsLabel2.setText(null);
        keepsTip2.setText(null);
        castleLabel2.setText(null);
        castleTip2.setText(null);
        //
        rankLabel3.setText(null);
        rankTip3.setText(null);
        nameLabel3.getChildren().clear();
        scoreLabel3.setText(null);
        scoreTip3.setText(null);
        scoreProgressTip3.setText(null);
        incomeLabel3.setText(null);
        incomeTip3.setText(null);
        campsLabel3.setText(null);
        campsTip3.setText(null);
        towersLabel3.setText(null);
        towersTip3.setText(null);
        keepsLabel3.setText(null);
        keepsTip3.setText(null);
        castleLabel3.setText(null);
        castleTip3.setText(null);
    }

    @Override
    protected void updateUI() {
        final Match match = getMatch();
        if (match == null) {
            clearUIContent();
        }
    }

    private final ObjectProperty<Match> match = new SimpleObjectProperty<>(this, "match", null); // NOI18N.

    public final Match getMatch() {
        return match.get();
    }

    public final void setMatch(final Match value) {
        match.set(value);
    }

    public final ObjectProperty<Match> matchProperty() {
        return match;
    }
}
