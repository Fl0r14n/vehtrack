package com.rhcloud.application.vehtrack.client.widgets;

import com.rhcloud.application.vehtrack.client.convertor.AccountConvertor;
import com.rhcloud.application.vehtrack.client.datasource.SpringDataREST;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.PromptStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class AccountWidget extends ContentWidget {

    public AccountWidget() {
        super("Accounts");
        final ListGrid grid = new ListGrid();
        {
            RPCManager.setPromptStyle(PromptStyle.CURSOR);           
            grid.setDataSource(SpringDataREST.getInstance().getDataSource("http://localhost:10000/account", new AccountConvertor()));            
            grid.setWidth(500);
            grid.setHeight(300);
            grid.setCanEdit(true);
            grid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
            grid.setAutoFetchData(true);
            grid.setShowFilterEditor(true);
            grid.setFilterOnKeypress(true);            
            
            ListGridField loginField = new ListGridField("Login");
            ListGridField passwordField = new ListGridField("Password");
            ListGridField rolesField = new ListGridField("Roles");
            ListGridField lockedField = new ListGridField("Locked");
            
            grid.setFields(loginField,passwordField,rolesField,lockedField);
                    
        }
        super.addSummary(grid);
        super.addDetails(null);
    }
}
