package com.rhcloud.application.vehtrack.client.convertor;

import com.google.gwt.core.client.JsArray;
import com.rhcloud.application.vehtrack.client.datasource.DataSourceConvertor;
import com.rhcloud.application.vehtrack.client.jso.AccountJSO;
import com.rhcloud.application.vehtrack.client.jso.GeneralJSO;
import com.rhcloud.application.vehtrack.client.jso.ROLE;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceEnumField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class AccountConvertor implements DataSourceConvertor<GeneralJSO<AccountJSO>> {

    @Override
    public void onInit(DataSource ds) {
        DataSourceTextField login = new DataSourceTextField("Login");
        DataSourceTextField password = new DataSourceTextField("Password");
        DataSourceEnumField roles = new DataSourceEnumField("Roles");

        ROLE[] rolesEnum = ROLE.values();
        String[] rolesString = new String[rolesEnum.length];
        for (int i = 0; i < rolesString.length; i++) {
            rolesString[i] = rolesEnum[i].name();
        }
        roles.setValueMap(rolesString);

        DataSourceBooleanField locked = new DataSourceBooleanField("Locked");
        ds.setFields(login, password, roles, locked);
    }

    @Override
    public Record[] toRecord(GeneralJSO<AccountJSO> jso) {
        JsArray<AccountJSO> content = jso.getContent();
        ListGridRecord[] result = new ListGridRecord[content.length()];
        for (int i = 0; i < result.length; i++) {
            AccountJSO account = content.get(i);
            result[i] = new ListGridRecord();
            {
                //System.out.println(account.getLogin()+"|"+account.getPassword()+"|"+account.getRoles()+"|"+account.getLocked());                
                result[i].setAttribute("Login", account.getLogin());
                result[i].setAttribute("Password", account.getPassword());
                result[i].setAttribute("Roles", account.getRoles());
                result[i].setAttribute("Locked", account.getLocked());
            }
        }
        return result;
    }

    @Override
    public String toID(Record oldRecord) {
        return oldRecord.getAttribute("Login");
    }
}
