import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

public class TestResend {
    public static void main(String[] args) {
        try {
            Resend resend = new Resend("re_bUHXUpKS_6wGxGAR55PjjDUnY4tmM1v9D");
            CreateEmailOptions sendEmailRequest = CreateEmailOptions.builder()
                    .from("Wikka.lk <onboarding@resend.dev>")
                    .to("maleesha.pramud@gmail.com") // or any email
                    .subject("Test")
                    .html("Test HTML")
                    .build();
            resend.emails().send(sendEmailRequest);
            System.out.println("Success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
