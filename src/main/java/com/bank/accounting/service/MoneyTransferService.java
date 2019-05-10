package com.bank.accounting.service;

import com.bank.accounting.core.annotation.Bean;
import com.bank.accounting.dao.AccountDao;
import com.bank.accounting.entities.ErrorEntity;
import com.bank.accounting.model.AccountRequest;
import com.bank.accounting.model.ResponseModel;
import com.bank.accounting.threads.TransactionThread;
import com.google.gson.Gson;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/money")
public class MoneyTransferService {

    @Bean
    private static AccountDao accountDao;

    private Gson gson = new Gson();

    @POST
    @Path("/sendMoney")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ResponseModel getApplication(AccountRequest request) {
        TransactionThread transactionThread = new TransactionThread(request);
        transactionThread.start();
        ResponseModel responseModel = new ResponseModel();
        responseModel.setSuccess(true);
        return responseModel;
    }

    @POST
    @Path("/sendMoneyError")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ResponseModel sendMoneyError(AccountRequest request) {
        ErrorEntity errorEntity = accountDao.getErrorEntity(Long.parseLong(request.getId()));
        request = gson.fromJson(errorEntity.getJson(), AccountRequest.class);
        TransactionThread transactionThread = new TransactionThread(request, errorEntity);
        transactionThread.start();
        ResponseModel responseModel = new ResponseModel();
        responseModel.setSuccess(true);
        return responseModel;
    }

}
