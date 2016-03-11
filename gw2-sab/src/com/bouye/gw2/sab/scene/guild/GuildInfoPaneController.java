/* 
 * Copyright (C) 2016 Fabrice Bouyé
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 */
package com.bouye.gw2.sab.scene.guild;

import api.web.gw2.mapping.v1.guilddetails.GuildDetails;
import api.web.gw2.mapping.v2.guild.id.log.LogEvent;
import api.web.gw2.mapping.v2.guild.id.log.LogEventType;
import api.web.gw2.mapping.v2.guild.id.members.Member;
import com.bouye.gw2.sab.SABControllerBase;
import com.bouye.gw2.sab.query.WebQuery;
import com.bouye.gw2.sab.scene.guild.log.LogEventListCell;
import com.bouye.gw2.sab.session.Session;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 * @author Fabrice Bouyé
 */
public class GuildInfoPaneController extends SABControllerBase<GuildInfoPane> {

    @FXML
    private Label guildNameLabel;
    @FXML
    private TextArea motdArea;
    @FXML
    private TableView<Member> rosterTableView;
    @FXML
    private TableColumn<Member, Boolean> onlineTableColumn;
    @FXML
    private TableColumn<Member, Object> rankTableColumn;
    @FXML
    private TableColumn<Member, String> nameTableColumn;
    @FXML
    private TableColumn<Member, Object> locationTableColumn;
    @FXML
    private TableColumn<Member, Object> lastOnlineTableColumn;
    @FXML
    private ComboBox<LogEventType> logsFilterCombo;
    @FXML
    private TextField logsSearchField;
    @FXML
    private ListView<LogEvent> logsListView;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        Bindings.select(nodeProperty(), "guildId").addListener((observable, oldValue, newValue) -> updateContent());
        nameTableColumn.setCellValueFactory(feature -> {
            final Member member = feature.getValue();
            return new SimpleStringProperty(member.getName());
        });
        //
        logsFilterCombo.getItems().setAll(LogEventType.values());
        logsFilterCombo.getItems().remove(LogEventType.UNKNOWN);
        logsFilterCombo.getItems().add(0, null);
        logsFilterCombo.valueProperty().addListener(observable -> applyLogsListFilter());
        //
        logsSearchField.textProperty().addListener(observable -> applyLogsListFilter());
        //
        logsListView.setItems(filteredLogs);
        logsListView.setCellFactory(listView -> new LogEventListCell());
    }

    @Override
    protected void clearContent(final GuildInfoPane parent) {
        guildNameLabel.setText(null);
    }

    @Override
    protected void installContent(final GuildInfoPane parent) {
        final Session session = parent.getSession();
        final String guildId = parent.getGuildId();
        guildNameLabel.setText(guildId);
        updateGuildDetailstAsync(session, guildId);
        updateGuildRosterAsync(session, guildId, 0);
        updateGuildLogsAsync(session, guildId, 0);
    }

    private void updateGuildDetailstAsync(final Session session, final String guildId) {
        final boolean isDemo = session.isDemo();
        final List<GuildDetails> guildDetails = WebQuery.INSTANCE.queryGuildDetails(isDemo, guildId);
        if (!guildDetails.isEmpty()) {
            final GuildDetails guild = guildDetails.get(0);
            guildNameLabel.setText(guild.getGuildName());
        }
    }

    private void updateGuildRosterAsync(final Session session, final String guildId, final int startIndex) {
        final boolean isDemo = session.isDemo();
        final String appKey = session.getAppKey();
        final List<Member> guildRoster = WebQuery.INSTANCE.queryGuildMembers(isDemo, appKey, guildId);
        rosterTableView.getItems().setAll(guildRoster);
    }

    private final ObservableList<LogEvent> logs = FXCollections.observableList(new LinkedList());
    private final FilteredList<LogEvent> filteredLogs = new FilteredList(logs);

    private void updateGuildLogsAsync(final Session session, final String guildId, final int startIndex) {
        final boolean isDemo = session.isDemo();
        final String appKey = session.getAppKey();
        final List<LogEvent> result = WebQuery.INSTANCE.queryGuildLogs(isDemo, appKey, guildId);
        logs.setAll(result);
    }

    private void applyLogsListFilter() {
        final LogEventType type = logsFilterCombo.getValue();
        Predicate<LogEvent> typeFilter = logEvent -> true;
        if (type != null) {
            typeFilter = logEvent -> logEvent.getType() == type;
        }
        final String search = logsSearchField.getText();
        if (search != null && !search.trim().isEmpty()) {
            final Predicate<LogEvent> searchFilter = logEvent -> {
                boolean result = false;
                final String user = logEvent.getUser();
                result |= user.contains(search);
                return result;
            };
            typeFilter = typeFilter.and(searchFilter);
        }
        filteredLogs.setPredicate(typeFilter);
    }
}
