package com.mobitech.eletromation;

import android.os.AsyncTask;

import com.mobitech.eletromation.Email.GmailSender;

public class EnviarEmailCodigo extends AsyncTask<String, Void, Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(String... params) {
        try {


            GmailSender sender = new GmailSender("exemplodoappeletromation@gmail.com", "eletromation2018");
            //subject, body, sender, to
            sender.sendMail("Recuperação de Senha Eletromation",
                    params[0],
                    params[1],params[2]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }
}
