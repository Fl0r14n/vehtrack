package com.rhcloud.application.vehtrack.client.datasource;

import com.google.gwt.core.client.JavaScriptObject;
import com.rhcloud.application.vehtrack.client.http.Authentification;
import com.rhcloud.application.vehtrack.client.http.Request;
import com.rhcloud.application.vehtrack.client.http.Response;
import com.rhcloud.application.vehtrack.client.jso.AccountJSO;
import com.rhcloud.application.vehtrack.client.rest.JsonRequest;
import com.rhcloud.application.vehtrack.client.rest.JsonResponse;
import com.rhcloud.application.vehtrack.client.rest.Marshaller;
import com.rhcloud.application.vehtrack.client.rest.RestClient;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSDataFormat;
import static com.smartgwt.client.types.DSOperationType.ADD;
import static com.smartgwt.client.types.DSOperationType.FETCH;
import static com.smartgwt.client.types.DSOperationType.REMOVE;
import static com.smartgwt.client.types.DSOperationType.UPDATE;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.TextMatchStyle;

public class SpringDataREST<T extends JavaScriptObject> {

    private SpringDataREST() {
    }
    private static SpringDataREST instance;

    public static synchronized SpringDataREST getInstance() {
        if (instance == null) {
            instance = new SpringDataREST();
        }
        return instance;
    }

    public DataSource getDataSource(final String url, final DataSourceConvertor dsc) {
        if (dsc == null || url == null) {
            throw new NullPointerException();
        }
        DataSource ds = new DataSource() {
            @Override
            protected Object transformRequest(DSRequest dsRequest) {
                DSResponse dsResponse = new DSResponse();
                dsResponse.setAttribute("clientContext", dsRequest.getAttributeAsObject("clientContext"));
                switch (dsRequest.getOperationType()) {
                    case FETCH: {
                        onRead(dsRequest, dsResponse);
                        break;
                    }
                    case ADD: {
                        onCreate(dsRequest, dsResponse);
                        break;
                    }
                    case UPDATE: {
                        onUpdate(dsRequest, dsResponse);
                        break;
                    }
                    case REMOVE: {
                        onDelete(dsRequest, dsResponse);
                        break;
                    }
                    default:
                        break;
                }

                return dsRequest.getData();
            }
            private RestClient rc = new RestClient();
            private Authentification auth = new Authentification();

            public void onRead(final DSRequest dsRequest, final DSResponse dsResponse) {
                //TODO
                //?page=2&limit=20
                //curl -v http://localhost:8080/people/search/nameStartsWith?name=K&sort=name&name.dir=desc
                SortSpecifier[] sortSpecifiers = dsRequest.getSortBy();
                dsRequest.getStartRow();
                dsRequest.getEndRow();
                Criteria criteria = dsRequest.getCriteria();
                TextMatchStyle textMatchStyle = dsRequest.getTextMatchStyle();
                dsRequest.getData();

                JsonRequest request = new JsonRequest(url);
                request.setAuthentification(auth);
                rc.request(request, new RestClient.JsonCallback<T>() {
                    @Override
                    public void handleJsonResponse(JsonResponse<T> response) {
                        if (Response.Status.OK.equals(response.getStatus())) {
                            Record[] data = dsc.toRecord(response.getJson());
                            if (data != null) {
                                dsResponse.setData(data);
                                //dsResponse.setStartRow(dsRequest.getStartRow());
                                //dsResponse.setEndRow(dsRequest.getEndRow());
                                //dsResponse.setTotalRows(data.length);
                            }
                            dsResponse.setStatus(RPCResponse.STATUS_SUCCESS);
                        } else {
                            dsResponse.setStatus(RPCResponse.STATUS_FAILURE);
                        }
                        processResponse(dsRequest.getRequestId(), dsResponse);
                    }
                });
            }

            public void onCreate(final DSRequest dsRequest, final DSResponse dsResponse) {
                JsonRequest request = new JsonRequest(url);
                request.setAuthentification(auth);
                request.setHttpMethod(Request.HttpMethod.POST);
                //TODO         
                rc.request(request, new RestClient.JsonCallback<T>() {
                    @Override
                    public void handleJsonResponse(JsonResponse<T> response) {
                        if (Response.Status.Created.equals(response.getStatus())) {
                            //TODO
                            dsResponse.setStatus(RPCResponse.STATUS_SUCCESS);
                        } else {
                            dsResponse.setStatus(RPCResponse.STATUS_FAILURE);
                        }
                        processResponse(dsRequest.getRequestId(), dsResponse);
                    }
                });
            }

            public void onUpdate(final DSRequest dsRequest, final DSResponse dsResponse) {
                printOldRecord(dsRequest.getOldValues());
                System.out.println(url + "/" + dsc.toID(dsRequest.getOldValues()));
                JsonRequest request = new JsonRequest(url + "/" + dsc.toID(dsRequest.getOldValues()));
                {
                    request.setAuthentification(auth);
                    request.setHttpMethod(Request.HttpMethod.PUT);
                    request.setJson(dsRequest.getData());
                    
                }
                System.out.println(Marshaller.marshall(request.getJson()));
                rc.request(request, new RestClient.JsonCallback<T>() {
                    @Override
                    public void handleJsonResponse(JsonResponse<T> response) {
                        if (Response.Status.NoContent.equals(response.getStatus())) {
                            System.out.println("HERE");
                            //TODO set data
                            dsResponse.setStatus(RPCResponse.STATUS_SUCCESS);
                        } else {
                            dsResponse.setStatus(RPCResponse.STATUS_FAILURE);
                        }
                        processResponse(dsRequest.getRequestId(), dsResponse);
                    }
                });
            }

            public void onDelete(final DSRequest dsRequest, final DSResponse dsResponse) {
                JsonRequest request = new JsonRequest(url);
                request.setAuthentification(auth);
                request.setHttpMethod(Request.HttpMethod.DELETE);
                //TODO
                rc.request(request, new RestClient.JsonCallback<T>() {
                    @Override
                    public void handleJsonResponse(JsonResponse<T> response) {
                        if (Response.Status.NoContent.equals(response.getStatus())) {
                            //TODO
                            dsResponse.setStatus(RPCResponse.STATUS_SUCCESS);
                        } else {
                            dsResponse.setStatus(RPCResponse.STATUS_FAILURE);
                        }
                        processResponse(dsRequest.getRequestId(), dsResponse);
                    }
                });
            }
        };
        ds.setDataProtocol(DSProtocol.CLIENTCUSTOM);
        ds.setDataFormat(DSDataFormat.CUSTOM);
        ds.setClientOnly(false);
        dsc.onInit(ds);
        return ds;
    }

    private void printOldRecord(Record record) {
        for (String key : record.getAttributes()) {
            String value = record.getAttributeAsString(key);
            System.out.println(key + " | " + value);
        }
    }
}
