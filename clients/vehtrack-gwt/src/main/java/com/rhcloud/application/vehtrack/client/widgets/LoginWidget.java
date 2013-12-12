package com.rhcloud.application.vehtrack.client.widgets;

import com.rhcloud.application.vehtrack.client.http.Authentification;
import com.rhcloud.application.vehtrack.client.rest.JsonRequest;
import com.rhcloud.application.vehtrack.client.rest.JsonResponse;
import com.rhcloud.application.vehtrack.client.rest.RestClient;
import com.rhcloud.application.vehtrack.client.jso.GeneralJSO;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

public class LoginWidget extends Window {

    public LoginWidget() {
        DynamicForm form = new DynamicForm();
        {
            TextItem login = new TextItem();
            {
                login.setTitle("Login");
                login.setRequired(Boolean.TRUE);
            }
            PasswordItem password = new PasswordItem();
            {
                password.setTitle("Password");
                password.setRequired(Boolean.TRUE);
            }
            ButtonItem submit = new ButtonItem();
            {
                submit.setTitle("Submit");
                submit.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        //Not used
                    }
                });
            }
            form.setFields(login, password, submit);
        }
        setHeaderControls(HeaderControls.HEADER_LABEL);
        addItem(form);
        setAutoSize(true);
        setTitle("Login");
        setIsModal(Boolean.TRUE);
        setShowModalMask(Boolean.TRUE);
        centerInPage();
    }
}
