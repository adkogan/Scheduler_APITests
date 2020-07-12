package com.scheduler.qa;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;

public class ApiTests {

    @Test
    public void loginUserTest() throws IOException {
        String response = apiLoginOrRegistration("adkogan@gmail.com", "5605105zxC");
        //  System.out.println(response);

        JsonElement parsed = new JsonParser().parse(response);
        JsonElement token = parsed.getAsJsonObject().get("token");
        JsonElement status = parsed.getAsJsonObject().get("status");
        JsonElement registration = parsed.getAsJsonObject().get("registration");

        System.out.println(token);
//        System.out.println(status);
//        System.out.println(registration);

        Assert.assertFalse(registration.getAsBoolean());
        Assert.assertEquals(status.toString(), "\"Login success\"");
    }

    @Test
    public void loginUserTestCodeResponse() throws IOException {
        String email= "adkogan@gmail.com";
        String password ="5605105zxC";
      int statusCode=  sendPostRequest("/api/login")
                .bodyString("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}", ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse()
                .getStatusLine()
                .getStatusCode();
        //  System.out.println(response);
        Assert.assertEquals(statusCode, 200);


    }


    @Test
    public void getRecordsPeriod() throws IOException {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFka29nYW5AZ21haWwuY29tIn0.sRNB6N4r_AtZSMP3d_xdKao5vHZAOJvKLgPtTYqrIc8";
        String res = Request.Get("http://super-scheduler-app.herokuapp.com/api/recordsPeriod")
                .addHeader("Authorization", token)
                .addHeader("Content-Type", "application/json")
                .execute()
                .returnContent()
                .asString();
        System.out.println(res);
        JsonElement parsed = new JsonParser().parse(res);
        String monthFrom = parsed.getAsJsonObject().get("monthFrom").toString();
        String monthTo = parsed.getAsJsonObject().get("monthTo").toString();
        String yearFrom = parsed.getAsJsonObject().get("yearFrom").toString();
        String yearTo = parsed.getAsJsonObject().get("yearTo").toString();


        String records = sendPostRequest("/api/records")
                .addHeader("Authorization", token)
                .addHeader("Content-Type", "application/json")
                .bodyString("{\"monthFrom\": " + monthFrom + ", \"monthTo\": " + monthTo + ", \"yearFrom\": " + yearFrom + ", \"yearTo\": " + yearTo + "}",
                        ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString();

        System.out.println(records);
    }

    private String apiLoginOrRegistration(String email, String password) throws IOException {
        return sendPostRequest("/api/login")
                .bodyString("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}", ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString();
    }

    private Request sendPostRequest(String controller) {
        return Request.Post("http://super-scheduler-app.herokuapp.com" + controller);
    }

    public int handleResponse(final HttpResponse response) throws IOException{
        StatusLine statusLine = response.getStatusLine();
       return statusLine.getStatusCode();
    }

}
