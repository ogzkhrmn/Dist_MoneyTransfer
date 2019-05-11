package com.bank.accounting.threads;

import com.bank.accounting.core.ApplicationProperties;
import com.bank.accounting.core.annotation.Bean;
import com.bank.accounting.dao.AccountDao;
import com.bank.accounting.entities.ErrorEntity;
import com.bank.accounting.model.AccountRequest;
import com.bank.accounting.model.RequestModel;
import com.bank.accounting.model.ResponseModel;
import com.bank.accounting.model.SecuirtyServiceModel;
import com.bank.accounting.model.TCMBRequest;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransactionThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionThread.class);

    @Bean
    private static AccountDao accountDao;

    private Gson gson = new Gson();

    private AccountRequest accountRequest;

    private ErrorEntity error;

    private boolean getError = false;

    public TransactionThread(AccountRequest accountRequest) {
        this.accountRequest = accountRequest;
        error = new ErrorEntity();
        error.setCount(0);
    }

    public TransactionThread(AccountRequest accountRequest, ErrorEntity errorEntity) {
        this.accountRequest = accountRequest;
        this.error = errorEntity;
        error.setCount(error.getCount() + 1);
    }

    @Override
    public void run() {
        if (error.getCount() == 0) {
            callSendMail();
        }
        boolean hasLimit = true;
        try {
            hasLimit = accountDao.hasUserLimit(accountRequest.getFrom(), accountRequest.getAmount());
        } catch (Exception e) {
            getError = true;
            error.setJson(gson.toJson(accountRequest));
        }
        if (hasLimit && !getError) {
            error.setSuccess(true);
            if (!error.getErrorSecurity() && !callSecurity().isSuccess()) {
                callFailMail();
            } else if (callTcmb().isSuccess()) {
                callOkMail();
            } else {
                error.setJson(gson.toJson(accountRequest));
            }
        }

        if (hasLimit && getError && error.getCount() <= 3) {
            error.setSuccess(false);
        } else if (getError || !hasLimit) {
            if (error.getCount() == 4) {
                error.setSuccess(true);
                callSystemError();
            }
            callFailMail();
        }
        accountDao.saveError(error);
    }

    private void callFailMail() {
        try {
            URL url = new URL(ApplicationProperties.getProperty("app.error.mail.service"));
            callRestService(url);
        } catch (Exception e) {
            LOGGER.error("Transaction error " + accountRequest.getEmail() + " :", e);
        }
    }

    private void callSendMail() {
        try {
            URL url = new URL(ApplicationProperties.getProperty("app.send.mail.service"));
            callRestService(url);
        } catch (Exception e) {
            LOGGER.error("Transaction error " + accountRequest.getEmail() + " :", e);
        }
    }

    private void callOkMail() {
        try {
            URL url = new URL(ApplicationProperties.getProperty("app.sendok.mail.service"));
            callRestService(url);
        } catch (Exception e) {
            LOGGER.error("Transaction error " + accountRequest.getEmail() + " :", e);
        }
    }

    private void callSystemError() {
        try {
            URL url = new URL(ApplicationProperties.getProperty("app.systemerror.mail.service"));
            sendError(url);
        } catch (Exception e) {
            LOGGER.error("Transaction error " + accountRequest.getEmail() + " :", e);
        }
    }

    private ResponseModel callSecurity() {
        try {
            URL url = new URL(ApplicationProperties.getProperty("app.security.service"));
            return callSecurityService(url);
        } catch (Exception e) {
            LOGGER.error("Transaction error " + accountRequest.getEmail() + " :", e);
            error.setErrorSecurity(true);
            error.setJson(gson.toJson(accountRequest));
            getError = true;
        }
        ResponseModel responseModel = new ResponseModel();
        responseModel.setSuccess(true);
        return responseModel;
    }

    private ResponseModel callTcmb() {
        try {
            URL url = new URL(ApplicationProperties.getProperty("app.tcmb.service"));
            return callTcmb(url);
        } catch (Exception e) {
            LOGGER.error("Transaction error " + accountRequest.getEmail() + " :", e);
            error.setErrorTcmb(true);
            error.setJson(gson.toJson(accountRequest));
            getError = true;
        }
        ResponseModel responseModel = new ResponseModel();
        responseModel.setSuccess(false);
        return responseModel;
    }

    private ResponseModel callSecurityService(URL url) throws Exception {

        SecuirtyServiceModel requestModel = new SecuirtyServiceModel();
        requestModel.setTckn(accountRequest.getFrom());

        return getResponse(url, gson.toJson(requestModel));
    }

    private void callRestService(URL url) throws Exception {
        RequestModel requestModel = new RequestModel();
        requestModel.setAccountMail(accountRequest.getEmail());
        requestModel.setName(accountRequest.getName());
        requestModel.setSurname(accountRequest.getSurname());
        requestModel.setType(accountRequest.getType());
        requestModel.setModule("-");
        requestModel.setId("-");

        getResponse(url, gson.toJson(requestModel));
    }

    private ResponseModel sendError(URL url) throws Exception {
        RequestModel requestModel = new RequestModel();
        requestModel.setAccountMail(accountRequest.getEmail());
        requestModel.setName(accountRequest.getName());
        requestModel.setSurname(accountRequest.getSurname());
        requestModel.setType(accountRequest.getType());
        requestModel.setModule("ACCOUNT_SERVICE");
        requestModel.setId(error.getId() + "");

        return getResponse(url, gson.toJson(requestModel));
    }

    private ResponseModel callTcmb(URL url) throws Exception {
        TCMBRequest requestModel = new TCMBRequest();
        requestModel.setFrom(accountRequest.getFrom());
        requestModel.setTo(accountRequest.getTo());
        requestModel.setAmount(accountRequest.getAmount());
        requestModel.setError(false);

        return getResponse(url, gson.toJson(requestModel));
    }

    private ResponseModel getResponse(URL url, String s) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
        wr.write(s);
        wr.flush();
        wr.close();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOGGER.error("Servis Hatası : {} {}", conn.getResponseCode(), accountRequest.getFrom());
            throw new Exception();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

        String output = IOUtils.toString(br);

        conn.disconnect();
        return gson.fromJson(output, ResponseModel.class);
    }
}
