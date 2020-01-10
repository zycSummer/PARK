package com.jet.cloud.deepmind.common.util;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Map;


public class EmailUtils {

    public EmailUtils() {
        this.from = "Jet";
    }

    private String from;

    public void setFrom(String from) {
        this.from = from;
    }

    public void sendText(String toEmail, String title, String content) throws SendMailException {

        OhMyEmail.subject(title)
                .from(from)
                .to(toEmail)
                .text(content)
                .send();
    }

    /**
     * @param toEmail
     * @param title
     * @param htmlContent <h1 font=red>信件内容</h1>
     * @throws SendMailException
     */
    public void sendHtml(String toEmail, String title, String htmlContent) throws SendMailException {
        OhMyEmail.subject(title)
                .from(from)
                .to(toEmail)
                .html(htmlContent)
                .send();
    }

    /**
     * 本地附件邮件
     *
     * @param toEmail
     * @param title
     * @param htmlContent
     * @param file
     * @param fileName
     * @throws SendMailException
     */
    public void sendLocalAttch(String toEmail, String title, String htmlContent, File file, String fileName) throws SendMailException {

        OhMyEmail.subject(title)
                .from(from)
                .to(toEmail)
                .html(htmlContent)
                .attach(file, fileName)
                .send();
    }

    public void sendNetAttach(String toEmail, String title, String htmlContent, URL url, String fileName) throws SendMailException {
        OhMyEmail.subject(title)
                .from(from)
                .to(toEmail)
                .html(htmlContent)
                .attachURL(url, fileName)
                .send();
    }

    /**
     * Pebble模板邮件
     *
     * @param toEmail
     * @param title
     * @param context      类似modelAndView
     * @param htmlFilePath
     * @throws PebbleException
     * @throws IOException
     * @throws SendMailException
     */
    public void sendHtmlTemplate(String toEmail, String title, Map<String, Object> context, String htmlFilePath) throws PebbleException, IOException, SendMailException {
        PebbleEngine engine = new PebbleEngine.Builder().build();
        PebbleTemplate compiledTemplate = engine.getTemplate(htmlFilePath);
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, context);
        String output = writer.toString();
        OhMyEmail.subject(title)
                .from(from)
                .to(toEmail)
                .html(output)
                .send();
    }
}
